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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformManager;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import ua.net.maxx.dto.MQTTSettings;
import ua.net.maxx.dto.PinSettings;
import ua.net.maxx.service.GPIOSevice;
import ua.net.maxx.service.MQTTService;
import ua.net.maxx.storage.domain.GlobalConfiguration;
import ua.net.maxx.storage.service.StorageService;
import ua.net.maxx.utils.AppRestarter;

@Controller("/api")
public class ApiController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ApiController.class);

	@Inject
	private GPIOSevice gpioSevice;

	@Inject
	private StorageService storageService;

	@Inject
	private MQTTService mqttService;
	
	@Inject
	private WebSocketBroadcaster broadcaster;

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

	@Get("/mqtt/config")
	public List<MQTTSettings> getMQTTConfig() {
		return mqttService.getMQTTConfig().stream().map(item -> MQTTSettings.fromMQTTConfig(item)).collect(Collectors.toList());
	}

	@Post("/mqtt/config")
	public List<MQTTSettings> updateMQTTConfig(MQTTSettings mqttConfig) {
		mqttService.updateMQTTConfig(mqttConfig);
		return getMQTTConfig();
	}
	
	@Delete("/mqtt/config/{id}")
	public List<MQTTSettings> deleteMQTTConfig(Long id) {
		mqttService.deleteMQTTConfig(id);
		return getMQTTConfig();
	}

}
