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
import com.pi4j.io.gpio.impl.GpioPinImpl;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformManager;

import ua.net.maxx.controller.dto.PinSettings;
import ua.net.maxx.storage.domain.GPIOConfiguration;
import ua.net.maxx.storage.service.StorageService;
import ua.net.maxx.utils.GPIOCommand;
import ua.net.maxx.utils.SimulatedPin;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class GPIOSevice {

	private static final Logger LOG = LoggerFactory.getLogger(GPIOSevice.class);

	@Inject
	private StorageService storageService;

	GpioController gpio;

	@PostConstruct
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
			LOG.info("Got config for {} pins", list.size());
			list.stream().forEach(config -> {
				configurePinInternal(config.getAddress(), config.getName(), config.getMode(), config.getPullUp());
				LOG.info("Pin {} configured as {}", config.getAddress(), config.getMode());
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private GpioPin getProvisionedPin(int address) {
        Pin pin = PinProvider.getPinByAddress(address);
	    return gpio.getProvisionedPin(pin);
    }

	public String executeCommand(GPIOCommand command) {
        GpioPin pin = gpio.getProvisionedPin(command.getPin());
        System.out.println("executeCommand" + pin.getClass());
		if (pin == null) {
			throw new IllegalStateException(String.format("Pin %s not configured", command.getPin().getName()));
		}
        ((GpioPinImpl)pin).setState(command.getState());
		return "OK";
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
                gpio.provisionDigitalInputPin(pin, pullResistance);
            } else {
		        provisionedPin.setMode(pinMode);
            }
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

	public PinSettings configurePin(PinSettings pinSettings) {
		return configurePin(pinSettings.getAddress(), pinSettings.getName(), pinSettings.getPinMode(),
				pinSettings.getPullResistance());
	}

	public Platform getCurrentPlatform() {
		return storageService.getGlobalConfiguration().getPlatformType();
	}

	public List<PinSettings> getPinsState() {
        return gpio.getProvisionedPins().stream().map(pin -> {
            PinSettings settings = PinSettings.fromGpioPin(pin);
            settings.setPinState(((GpioPinImpl)pin).getState());
            return settings;
        }).sorted(new Comparator<PinSettings>() {
            public int compare(PinSettings o1, PinSettings o2) {
                return Integer.valueOf(o1.getAddress()).compareTo(Integer.valueOf(o2.getAddress()));
            }
        }).collect(Collectors.toList());
	}

}
