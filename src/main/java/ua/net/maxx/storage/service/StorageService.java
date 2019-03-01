package ua.net.maxx.storage.service;

import com.pi4j.platform.Platform;

import ua.net.maxx.controller.dto.MQTTSettings;
import ua.net.maxx.storage.domain.GPIOConfiguration;
import ua.net.maxx.storage.domain.GlobalConfiguration;
import ua.net.maxx.storage.domain.MQTTConfiguration;
import ua.net.maxx.storage.repository.AppConfigurationRepository;
import ua.net.maxx.storage.repository.GPIOConfigurationRepository;
import ua.net.maxx.storage.repository.MQTTConfigurationRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

//https://vladmihalcea.com/the-best-way-to-map-a-projection-query-to-a-dto-with-jpa-and-hibernate/

@Singleton
public class StorageService {

	private static final Platform DEFAULT_PLATFORM = Platform.SIMULATED;

	private AppConfigurationRepository appConfigurationRepository;
	private GPIOConfigurationRepository gpioConfigurationRepository;
	private MQTTConfigurationRepository mqttConfigurationRepository;

	@Inject
	public StorageService(AppConfigurationRepository appConfigurationRepository,
						  GPIOConfigurationRepository gpioConfigurationRepository,
						  MQTTConfigurationRepository mqttConfigurationRepository) {
		this.appConfigurationRepository = appConfigurationRepository;
		this.gpioConfigurationRepository = gpioConfigurationRepository;
		this.mqttConfigurationRepository = mqttConfigurationRepository;
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

    public List<MQTTConfiguration> getMQTTConfigs() {
		return mqttConfigurationRepository.findAll();
    }

	public MQTTConfiguration updateMQTTConfig(MQTTSettings mqttConfig) {
		MQTTConfiguration config;
		if (mqttConfig.getId() == null) {
			config = new MQTTConfiguration(mqttConfig.getPublisherId(), mqttConfig.getHost(),mqttConfig.getPort());
			return mqttConfigurationRepository.save(config);
		}
		config = mqttConfigurationRepository.findById(mqttConfig.getId()).get();
		config.setPublisherId(mqttConfig.getPublisherId());
		config.setHost(mqttConfig.getHost());
		config.setPort(mqttConfig.getPort());
		mqttConfigurationRepository.update(config);
		return config;
	}

	public void deleteMQTTConfig(Long id) {
		MQTTConfiguration config;
		if (id != null) {
			mqttConfigurationRepository.deleteById(id);
		}
	}
}
