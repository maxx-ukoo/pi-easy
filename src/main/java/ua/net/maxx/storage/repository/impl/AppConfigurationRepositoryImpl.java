package ua.net.maxx.storage.repository.impl;

import io.micronaut.configuration.hibernate.jpa.scope.CurrentSession;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.spring.tx.annotation.Transactional;
import ua.net.maxx.storage.domain.GlobalConfiguration;
import ua.net.maxx.storage.repository.AppConfigurationRepository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public class AppConfigurationRepositoryImpl implements AppConfigurationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public AppConfigurationRepositoryImpl(@CurrentSession EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GlobalConfiguration> findById(@NotNull Long id) {
        return Optional.ofNullable(entityManager.find(GlobalConfiguration.class, id));
    }

    @Override
    @Transactional
    public GlobalConfiguration save(@NotBlank GlobalConfiguration entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    @Transactional
    public void deleteById(@NotNull Long id) {
        findById(id).ifPresent(entity -> entityManager.remove(entity));
    }

    @Transactional(readOnly = true)
    public List<GlobalConfiguration> findAll() {
        String qlString = "SELECT g FROM GlobalConfiguration as g";
        TypedQuery<GlobalConfiguration> query = entityManager.createQuery(qlString, GlobalConfiguration.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void update(@NotNull GlobalConfiguration entity) {
        entityManager.merge(entity);
    }
}
