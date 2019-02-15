package ua.net.maxx.storage.repository;


import ua.net.maxx.storage.domain.GPIOConfiguration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface GPIOConfigurationRepository {

    Optional<GPIOConfiguration> findById(@NotNull Long id);
    
    List<GPIOConfiguration> findByAddres(@NotNull Integer address);

    GPIOConfiguration save(@NotBlank GPIOConfiguration entity);

    void deleteById(@NotNull Long id);

    List<GPIOConfiguration> findAll();

    void update(@NotNull GPIOConfiguration entity);
}
