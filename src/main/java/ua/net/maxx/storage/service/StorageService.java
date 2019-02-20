package ua.net.maxx.storage.service;

import com.pi4j.platform.Platform;
import ua.net.maxx.storage.domain.GlobalConfiguration;
import ua.net.maxx.storage.repository.AppConfigurationRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class StorageService {

    private static final Platform DEFAULT_PLATFORM = Platform.SIMULATED;

    private AppConfigurationRepository appConfigurationRepository;

    @Inject
    public StorageService(AppConfigurationRepository appConfigurationRepository) {
        this.appConfigurationRepository = appConfigurationRepository;
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

}
