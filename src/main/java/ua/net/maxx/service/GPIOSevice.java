package ua.net.maxx.service;

import com.pi4j.io.gpio.BananaPiPin;
import com.pi4j.io.gpio.BananaProPin;
import com.pi4j.io.gpio.BpiPin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.NanoPiPin;
import com.pi4j.io.gpio.OdroidC1Pin;
import com.pi4j.io.gpio.OrangePiPin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinProvider;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.impl.GpioPinImpl;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformManager;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.discovery.event.ServiceStartedEvent;
import io.micronaut.scheduling.annotation.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.net.maxx.controller.WebSocketController;
import ua.net.maxx.dto.PinSettings;
import ua.net.maxx.events.PinEvent;
import ua.net.maxx.events.PinEventType;
import ua.net.maxx.events.StateChangeListener;
import ua.net.maxx.storage.domain.GPIOConfiguration;
import ua.net.maxx.storage.service.StorageService;
import ua.net.maxx.utils.GPIOCommand;
import ua.net.maxx.utils.SimulatedPin;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
public class GPIOSevice implements GpioPinListenerDigital, ApplicationEventListener<ServiceStartedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(GPIOSevice.class);

    private final StorageService storageService;

    private final List<StateChangeListener<PinEvent>> listeners = new ArrayList<>();
    private GpioController gpio;

    @Inject
    public GPIOSevice(StorageService storageService) {
        this.storageService = storageService;
    }

    @Async
    @Override
    public void onApplicationEvent(final ServiceStartedEvent event) {
        initialize();
    }

    private void initialize() {
        try {
            Platform platformType = storageService.getGlobalConfiguration().getPlatformType();
            PlatformManager.setPlatform(platformType);
            gpio = GpioFactory.getInstance();
            switch (platformType) {
                case BANANAPI: {
                    BananaPiPin.allPins();
                    break;
                }
                case BANANAPRO: {
                    BananaProPin.allPins();
                    break;
                }
                case BPI: {
                    BpiPin.allPins();
                    break;
                }
                case ODROID: {
                    OdroidC1Pin.allPins();
                    break;
                }
                case ORANGEPI: {
                    OrangePiPin.allPins();
                    break;
                }
                case NANOPI: {
                    NanoPiPin.allPins();
                    break;
                }
                case SIMULATED: {
                    SimulatedPin.allPins();
                    break;
                }
                default: {
                    // if a platform cannot be determine, then assume it's the default RaspberryPi
                    RaspiPin.allPins();
                }
            }
            List<GPIOConfiguration> list = storageService.getPinConfigForAll();
            LOG.debug("Got config for {} pins", list.size());
            list.stream().forEach(config -> {
                try {
                    configurePinInternal(config.getAddress(), config.getName(), config.getMode(), config.getPullUp());
                    LOG.info("Pin {} configured as {}", config.getAddress(), config.getMode());
                } catch (Exception e) {
                    LOG.error("Error during configure Pin {} as {}", config.getAddress(), config.getMode(), e);
                }
            });
        } catch (Throwable e) {
            LOG.error("Initialization error", e);
        }
    }

    public Collection<GpioPin> getProvisionedPins() {
        gpio.getProvisionedPins().stream()
                .forEach(pin -> {
                    System.out.println(pin.getClass());
                });
        return gpio.getProvisionedPins();
    }

    public Pin[] allPins() {
        return PinProvider.allPins();
    }

    private void configurePinInternal(int address, String pinName, PinMode pinMode, PinPullResistance pullResistance) {
        Pin pin = PinProvider.getPinByAddress(address);
        GpioPin provisionedPin = gpio.getProvisionedPin(pin);

        switch (pinMode) {
            case DIGITAL_INPUT:
                if (provisionedPin == null) {
                    provisionedPin = gpio.provisionDigitalInputPin(pin, pullResistance);
                } else {
                    provisionedPin.setMode(pinMode);
                }
                provisionedPin.addListener(this);
                break;
            case DIGITAL_OUTPUT:
                if (provisionedPin == null) {
                    gpio.provisionDigitalOutputPin(pin, pinName, PinState.LOW);
                } else {
                    provisionedPin.setMode(pinMode);
                }
                break;
            default:
                throw new UnsupportedOperationException(String.format("Operation %s not supported", pinMode));
        }
    }

    public PinSettings configurePin(int address, String pinName, PinMode pinMode, PinPullResistance pullResistance) {
        configurePinInternal(address, pinName, pinMode, pullResistance);
        GPIOConfiguration pinConfig = storageService.getPinConfig(address);
        if (pinConfig == null) {
            pinConfig = new GPIOConfiguration();
            pinConfig.setAddress(address);
        }
        pinConfig.setMode(pinMode);
        pinConfig.setName(pinName);
        pinConfig.setPullUp(pullResistance);
        storageService.setPinConfig(pinConfig);
        return PinSettings.fromPinConfig(pinConfig);
    }

    public void configurePin(PinSettings pinSettings) {
    	LOG.debug("Configuring pin: {}", pinSettings);
        configurePin(pinSettings.getAddress(), pinSettings.getName(), pinSettings.getPinMode(),
                pinSettings.getPullResistance());
        PinState pinState = getPinState(pinSettings.getAddress());

        PinEvent pinEvent = PinEvent.newBuilder()
                .setAddress(pinSettings.getAddress())
                .setPinEventType(PinEventType.INFO)
                .setPinName(pinSettings.getName())
                .setPinState(pinState)
                .addValue("mode", pinSettings.getPinMode() != null ? pinSettings.getPinMode().name() : "")
                .build();
        updateListeners(pinEvent);
    }

    private PinState getPinState(int address) {
        return getPinsState().stream().filter(settings -> settings.getAddress() == address)
                .map(settings -> settings.getPinState())
                .map(Optional::ofNullable).findFirst().flatMap(Function.identity())
                .orElse(null);
    }

    public Platform getCurrentPlatform() {
        return storageService.getGlobalConfiguration().getPlatformType();
    }

    public List<PinSettings> getPinsState() {
        return gpio.getProvisionedPins().stream().map(pin -> {
            PinSettings settings = PinSettings.fromGpioPin(pin);
            settings.setPinState(((GpioPinImpl) pin).getState());
            return settings;
        }).sorted((o1, o2) -> Integer.valueOf(o1.getAddress()).compareTo(Integer.valueOf(o2.getAddress()))).collect(Collectors.toList());
    }

    public void setState(int address, PinState pinState) {
        Pin pin = PinProvider.getPinByAddress(address);
        GpioPin gpioPin = gpio.getProvisionedPin(pin);
        if (pin == null) {
            throw new IllegalStateException(String.format("Pin %s not configured", address));
        }
        ((GpioPinImpl) gpioPin).setState(pinState);
        PinEvent pinEvent = PinEvent.newBuilder()
                .setAddress(address)
                .setPinEventType(PinEventType.INFO)
                .setPinName(pin.getName())
                .setPinState(pinState).build();
        updateListeners(pinEvent);
    }

    public void addListener(StateChangeListener<PinEvent> listener) {
        listeners.add(listener);
    }
    
	public void removeListener(StateChangeListener<PinEvent> listener) {
		listeners.remove(listener);
	}

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        try {
            GpioPin gpioPin = event.getPin();
            PinSettings settings = PinSettings.fromGpioPin(gpioPin);
            settings.setPinState(((GpioPinImpl) gpioPin).getState());

            PinEvent pinEvent = PinEvent.newBuilder()
                    .setAddress(event.getPin().getPin().getAddress())
                    .setPinEventType(PinEventType.INFO)
                    .setPinName(settings.getName())
                    .setPinState(((GpioPinImpl) gpioPin).getState()).build();
            updateListeners(pinEvent);
        } catch (Exception e) {
            LOG.error("Can't process GpioPinDigitalStateChangeEvent", e);
        }
    }

    private void updateListeners(PinEvent event) {
    	LOG.debug("Sending event to listeners[{}]: {}",listeners.size(), event);
        listeners.stream().forEach(listener -> {
            LOG.debug("Listener: {}", listener.getClass() );
            listener.event(event);
        });
    }



}
