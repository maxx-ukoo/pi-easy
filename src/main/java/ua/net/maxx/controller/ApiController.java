package ua.net.maxx.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformManager;

import io.micronaut.core.convert.format.ReadableBytes;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
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
	
	@Inject
	private WebSocketBroadcaster broadcaster;

	@Get("/message")
	public void message(@QueryValue("pin") String pin) {
		System.out.println(broadcaster);
		System.out.println(pin);
		broadcaster.broadcastSync("HIGH =>" + pin, isValid(pin));
	}
	
    private Predicate<WebSocketSession> isValid(String pin) {
    	return s -> pin.equalsIgnoreCase(s.getUriVariables().get("pin", String.class, null));
    }
	
	@Get("/pins/provisioned")
	public Collection<GpioPin> getProvisionedPins() {
		Collection<GpioPin> pins = gpioSevice.getProvisionedPins();
		return pins.stream().sorted(new Comparator<GpioPin>() {
			public int compare(GpioPin o1, GpioPin o2) {
				return Integer.valueOf(o1.getPin().getAddress()).compareTo(Integer.valueOf(o2.getPin().getAddress()));
			}
		}).collect(Collectors.toList());
	}

	@Get("/pins")
	public Map<String, Object> pins() {
		Map<String, Object> config = new HashMap<>();

		Pin[] pins = gpioSevice.allPins();
		Arrays.sort(pins, new Comparator<Pin>() {
			public int compare(Pin o1, Pin o2) {
				return Integer.valueOf(o1.getAddress()).compareTo(Integer.valueOf(o2.getAddress()));
			}
		});
		config.put("pins", pins);
		List<PinSettings> list =  gpioSevice.getPinsState().stream()
				.sorted(new Comparator<PinSettings>() {
					public int compare(PinSettings o1, PinSettings o2) {
						return Integer.valueOf(o1.getAddress()).compareTo(Integer.valueOf(o2.getAddress()));
					}
				}).collect(Collectors.toList());
		config.put("config", list);
		gpioSevice.getPinsState();
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
