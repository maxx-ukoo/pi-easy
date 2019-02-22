package ua.net.maxx.storage.service;

import com.pi4j.platform.Platform;

import ua.net.maxx.storage.domain.GPIOConfiguration;
import ua.net.maxx.storage.domain.GlobalConfiguration;
import ua.net.maxx.storage.repository.AppConfigurationRepository;
import ua.net.maxx.storage.repository.GPIOConfigurationRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class StorageService {

	private static final Platform DEFAULT_PLATFORM = Platform.SIMULATED;

	private AppConfigurationRepository appConfigurationRepository;
	private GPIOConfigurationRepository gpioConfigurationRepository;

	@Inject
	public StorageService(AppConfigurationRepository appConfigurationRepository, GPIOConfigurationRepository gpioConfigurationRepository) {
		this.appConfigurationRepository = appConfigurationRepository;
		this.gpioConfigurationRepository = gpioConfigurationRepository;
	}

	public GlobalConfiguration getGlobalConfiguration() {
		List<GlobalConfiguration> list = appConfigurationRepository.findAll();
		if (list.size() > 0) {
			return list.get(0);
		}

		GlobalConfiguration defaultConfig = new GlobalConfiguration(DEFAULT_PLATFORM);
		appConfigurationRepository.save(defaultConfig);
		return defaultConfig;
	}

	public GlobalConfiguration updateGlobalConfiguration(GlobalConfiguration config) {
		if (config.getId() == null) {
			return appConfigurationRepository.save(config);
		}
		appConfigurationRepository.update(config);
		return config;
	}

	public GPIOConfiguration getPinConfig(Integer address) {
		List<GPIOConfiguration> list = gpioConfigurationRepository.findByAddres(address);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	public GPIOConfiguration setPinConfig(GPIOConfiguration pinConfig) {
		if (pinConfig.getId() == null) {
			return gpioConfigurationRepository.save(pinConfig);
		}
		gpioConfigurationRepository.update(pinConfig);
		return pinConfig;
	}

	public List<GPIOConfiguration> getPinConfigForAll() {
		return gpioConfigurationRepository.findAll();
	}

}
