package ua.net.maxx.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.platform.PlatformManager;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import ua.net.maxx.controller.dto.PinSettings;
import ua.net.maxx.service.GPIOSevice;

@Controller("/api")
public class ApiController {

	@Inject
	private GPIOSevice gpioSevice;

	@Get("/sysinfo")
	public Map<String, String> index() {
		Map<String, String> info = new HashMap<>();
		info.put("platformName", PlatformManager.getPlatform().getLabel());
		info.put("platformID", PlatformManager.getPlatform().getId());
		return info;
	}

	@Get("/pins/provisioned")
	public Collection<GpioPin> getProvisionedPins() {
		Collection<GpioPin> pins = gpioSevice.getProvisionedPins();
		pins.stream().forEach(pin -> {
			System.out.println(pin.getName());
			System.out.println(pin);
		});
		return pins;
		// Arrays.sort(pins, Comparator.comparingInt(Pin::getAddress));
		// return pins;
	}

	@Get("/pins")
	public Pin[] pins() {
		Pin[] pins = gpioSevice.allPins();
		Arrays.sort(pins, Comparator.comparingInt(Pin::getAddress));
		return pins;
	}

	@Put("/pin/config")
	public void configurePin(@Body PinSettings pinSettings) {
		gpioSevice.configurePin(pinSettings);
	}

}
