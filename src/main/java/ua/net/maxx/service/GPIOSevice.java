package ua.net.maxx.service;

import com.pi4j.io.gpio.BananaPiPin;
import com.pi4j.io.gpio.BananaProPin;
import com.pi4j.io.gpio.BpiPin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
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
import ua.net.maxx.storage.domain.GPIOConfiguration;
import ua.net.maxx.storage.service.StorageService;
import ua.net.maxx.utils.GPIOCommand;
import ua.net.maxx.utils.SimulatedPin;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private final Map<Integer, GpioPinDigitalOutput> outputMap = new HashMap<>();
	private final Map<Integer, GpioPinDigitalInput> inputMap = new HashMap<>();

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

	public String executeCommand(GPIOCommand command) {
		GpioPinDigitalOutput currentPin = outputMap.get(command.getPin().getAddress());
		if (currentPin == null) {
			throw new IllegalStateException(String.format("Pin %s not configured", command.getPin().getName()));
		}
		currentPin.setState(command.getState());
		return "OK";
	}

	public Collection<GpioPin> getProvisionedPins() {
		return gpio.getProvisionedPins();
	}

	public Pin[] allPins() {
		return PinProvider.allPins();
	}
	
	private void configurePinInternal(int address, String pinName, PinMode pinMode, PinPullResistance pullResistance) {
		Pin pin = PinProvider.getPinByAddress(address);
		inputMap.remove(pin.getAddress());
		outputMap.remove(pin.getAddress());
		switch (pinMode) {
			case DIGITAL_INPUT:
				inputMap.put(pin.getAddress(), gpio.provisionDigitalInputPin(pin, pullResistance));
				break;
			case DIGITAL_OUTPUT:
				outputMap.put(pin.getAddress(), gpio.provisionDigitalOutputPin(pin, pinName, PinState.LOW));
				break;
			default:
				throw new UnsupportedOperationException(String.format("Operation %s not supported", pinMode));
		}
	}

	public void configurePin(int address, String pinName, PinMode pinMode, PinPullResistance pullResistance) {
		//configurePinInternal(address, pinName, pinMode, pullResistance);
		
		GPIOConfiguration pinConfig = storageService.getPinConfig(address);
		if (pinConfig == null) {
			pinConfig = new GPIOConfiguration();
			pinConfig.setAddress(address);
		}
		pinConfig.setMode(pinMode);
		pinConfig.setName(pinName);
		pinConfig.setPullUp(pullResistance);
		storageService.setPinConfig(pinConfig);

	}

	public void configurePin(PinSettings pinSettings) {
		configurePin(pinSettings.getAddress(), pinSettings.getName(), pinSettings.getPinMode(),
				pinSettings.getPullResistance());
	}
	
	public Platform getCurrentPlatform() {
		return storageService.getGlobalConfiguration().getPlatformType();
	}

	/*public void getPinsState() {
		outputMap.entrySet().stream()
		.forEach(output -> {
			output.getValue().getState()
		});
		// TODO Auto-generated method stub
		
	}*/

}
