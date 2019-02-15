package ua.net.maxx.storage.repository;

import ua.net.maxx.storage.domain.GlobalConfiguration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface AppConfigurationRepository {

    Optional<GlobalConfiguration> findById(@NotNull Long id);

    GlobalConfiguration save(@NotBlank GlobalConfiguration entity);

    void deleteById(@NotNull Long id);

    List<GlobalConfiguration> findAll();

    void update(@NotNull GlobalConfiguration entity);
}
