package ua.net.maxx.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformManager;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import ua.net.maxx.controller.dto.PinSettings;
import ua.net.maxx.service.GPIOSevice;
import ua.net.maxx.storage.domain.GlobalConfiguration;
import ua.net.maxx.storage.service.StorageService;
import ua.net.maxx.utils.AppRestarter;

@Controller("/api")
public class ApiController {

	@Inject
	private GPIOSevice gpioSevice;

	@Inject
	private StorageService storageService;

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
	public Map<String, Object> pins() {
		Map<String, Object> config = new HashMap<>();
		config.put("pins", gpioSevice.allPins());
		Collection<GpioPin> provisionedPins = gpioSevice.getProvisionedPins();
		List<PinSettings> list = provisionedPins.stream().map(item -> PinSettings.fromGpioPin(item))
		.collect(Collectors.toList());		
		list.add(new PinSettings(2, "Pin 2", PinMode.DIGITAL_OUTPUT, PinPullResistance.PULL_DOWN));
		config.put("config", list);
		return config;
	}

	@Put("/pin/config")
	public void configurePin(@Body PinSettings pinSettings) {
		gpioSevice.configurePin(pinSettings);
	}

	@Get("/config")
	public Map<String, Object> getConfig() {
		Map<String, Object> config = new HashMap<>();
		config.put("platformID", storageService.getGlobalConfiguration());
		config.put("available", Platform.values());
		config.put("platformName", PlatformManager.getPlatform().getLabel());
		return config;
	}

	@Put("/config")
	public Map<String, Object> updateConfig(Platform platform) {
		GlobalConfiguration currentConfig = storageService.getGlobalConfiguration();
		currentConfig.setPlatformType(platform);
		storageService.updateGlobalConfiguration(currentConfig);
		new Thread(() -> {
			try {
				AppRestarter.restartApplication();
				Thread.sleep(2000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
		return getConfig();
	}

}
