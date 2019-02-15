package ua.net.maxx.storage.repository;


import ua.net.maxx.storage.domain.MQTTConfiguration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface MQTTConfigurationRepository {

    Optional<MQTTConfiguration> findById(@NotNull Long id);
    
    List<MQTTConfiguration> findByAddres(@NotNull Integer address);

    MQTTConfiguration save(@NotBlank MQTTConfiguration entity);

    void deleteById(@NotNull Long id);

    List<MQTTConfiguration> findAll();

    void update(@NotNull MQTTConfiguration entity);
}
