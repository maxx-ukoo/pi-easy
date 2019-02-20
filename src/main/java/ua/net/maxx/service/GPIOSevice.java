package ua.net.maxx.service;

import com.pi4j.io.gpio.BananaPiPin;
import com.pi4j.io.gpio.BananaProPin;
import com.pi4j.io.gpio.BpiPin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.NanoPiPin;
import com.pi4j.io.gpio.OdroidC1Pin;
import com.pi4j.io.gpio.OrangePiPin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinProvider;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformManager;

import ua.net.maxx.controller.dto.PinSettings;
import ua.net.maxx.storage.service.StorageService;
import ua.net.maxx.utils.GPIOCommand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GPIOSevice {

	@Inject
	private StorageService storageService;
	GpioController gpio;

	private final Map<Pin, GpioPinDigitalOutput> outputMap = new HashMap<>();

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
				break;
			}
			default: {
				// if a platform cannot be determine, then assume it's the default RaspberryPi
				RaspiPin.allPins();
			}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public String executeCommand(GPIOCommand command) {
		GpioPinDigitalOutput currentPin = outputMap.get(command.getPin());
		if (currentPin == null) {
			throw new IllegalStateException(String.format("Pin %s not configured", command.getPin().getName()));
		}
		currentPin.setState(command.getState());
		return "OK";
	}

	public Collection<GpioPin> getPins() {
		return gpio.getProvisionedPins();
	}

	public Pin[] allPins() {
		return PinProvider.allPins();
	}

	public void configurePin(int addres, String pinName, PinMode pinMode, PinPullResistance pullResistance) {
		Pin pin = PinProvider.getPinByAddress(addres);

		System.out.println("pin: " + pin.getProvider());
		switch (pinMode) {
		case DIGITAL_INPUT:
			gpio.provisionDigitalInputPin(pin, pullResistance);
			break;
		case DIGITAL_OUTPUT:
			gpio.provisionDigitalOutputPin(pin, pinName, PinState.LOW);
			break;
		default:
			throw new UnsupportedOperationException(String.format("Operation %s not supported", pinMode));
		}
	}

	public void configurePin(PinSettings pinSettings) {
		configurePin(pinSettings.getAddress(), pinSettings.getName(), pinSettings.getPinMode(),
				pinSettings.getPullResistance());
	}

	public Collection<GpioPin> getProvisionedPins() {
		return gpio.getProvisionedPins();
	}

}
