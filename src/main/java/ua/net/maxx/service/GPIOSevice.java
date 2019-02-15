package ua.net.maxx.service;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;

import ua.net.maxx.utils.GPIOCommand;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

@Singleton
public class GPIOSevice {

	final GpioController gpio = GpioFactory.getInstance();

	private final Map<Pin, GpioPinDigitalOutput> outputMap = new HashMap<>();

	// GpioPinDigitalInput myButton =
	// gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, // PIN NUMBER
	// "MyButton", // PIN FRIENDLY NAME (optional)
	// PinPullResistance.PULL_DOWN); // PIN RESISTANCE (optional)

	public String executeCommand(GPIOCommand command) {
		GpioPinDigitalOutput currentPin = outputMap.get(command.getPin());
		if (currentPin == null) {
			throw new IllegalStateException(String.format("Pin %s not configured", command.getPin().getName()));
		}
		currentPin.setState(command.getState());
		return "OK";
	}
}
