package ua.net.maxx.service;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.OdroidGpioProvider;
import com.pi4j.io.gpio.OrangePiGpioProvider;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.SimulatedGpioProvider;

import ua.net.maxx.utils.GPIOCommand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

@Singleton
public class GPIOSevice {

	static {
		GpioFactory.setDefaultProvider(new SimulatedGpioProvider());
	}

	final GpioController gpio = GpioFactory.getInstance();

	private final Map<Pin, GpioPinDigitalOutput> outputMap = new HashMap<>();

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
		return RaspiPin.allPins();
	}
}
