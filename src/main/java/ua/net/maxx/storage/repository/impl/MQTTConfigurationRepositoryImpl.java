package ua.net.maxx.storage.repository.impl;

import io.micronaut.configuration.hibernate.jpa.scope.CurrentSession;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.spring.tx.annotation.Transactional;
import ua.net.maxx.storage.domain.MQTTConfiguration;
import ua.net.maxx.storage.repository.MQTTConfigurationRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public class MQTTConfigurationRepositoryImpl implements MQTTConfigurationRepository {

	private final ApplicationConfiguration applicationConfiguration;

	@PersistenceContext
	private EntityManager entityManager;

	public MQTTConfigurationRepositoryImpl(@CurrentSession EntityManager entityManager, ApplicationConfiguration applicationConfiguration) {
		this.entityManager = entityManager;
		this.applicationConfiguration = applicationConfiguration;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<MQTTConfiguration> findById(@NotNull Long id) {
		return Optional.ofNullable(entityManager.find(MQTTConfiguration.class, id));
	}

	@Override
	@Transactional
	public MQTTConfiguration save(@NotBlank MQTTConfiguration entity) {
		entityManager.persist(entity);
		return entity;
	}

	@Override
	@Transactional
	public void deleteById(@NotNull Long id) {
		findById(id).ifPresent(entity -> entityManager.remove(entity));
	}

	@Transactional(readOnly = true)
	public List<MQTTConfiguration> findAll() {
		String qlString = "SELECT g FROM MQTTConfiguration as g";
		TypedQuery<MQTTConfiguration> query = entityManager.createQuery(qlString, MQTTConfiguration.class);
		return query.getResultList();
	}

	@Override
	@Transactional
	public void update(@NotNull MQTTConfiguration entity) {
		entityManager.merge(entity);
	}

	@Override
	@Transactional
	public List<MQTTConfiguration> findByAddres(@NotNull Integer address) {
		return entityManager.createQuery("SELECT c FROM MQTTConfiguration c WHERE c.address LIKE :address", MQTTConfiguration.class)
				.setParameter("address", address)
				.setMaxResults(10)
				.getResultList();
	}
}
