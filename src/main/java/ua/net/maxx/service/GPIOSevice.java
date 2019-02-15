package ua.net.maxx.service;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import ua.net.maxx.utils.GPIOCommand;

import javax.inject.Singleton;

@Singleton
public class GPIOSevice {

    final GpioController gpio = GpioFactory.getInstance();

    public String executeCommand(GPIOCommand command) {
        Pin pin = command.getPin();
        pin.

    return "OK";
    }
}
