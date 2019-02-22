package ua.net.maxx.storage.repository.impl;

import io.micronaut.configuration.hibernate.jpa.scope.CurrentSession;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.spring.tx.annotation.Transactional;
import ua.net.maxx.storage.domain.GPIOConfiguration;
import ua.net.maxx.storage.repository.GPIOConfigurationRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public class GPIOConfigurationRepositoryImpl implements GPIOConfigurationRepository {

	private final ApplicationConfiguration applicationConfiguration;
	
	@PersistenceContext
	private EntityManager entityManager;

	public GPIOConfigurationRepositoryImpl(@CurrentSession EntityManager entityManager, ApplicationConfiguration applicationConfiguration) {
		this.entityManager = entityManager;
		this.applicationConfiguration = applicationConfiguration;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<GPIOConfiguration> findById(@NotNull Long id) {
		return Optional.ofNullable(entityManager.find(GPIOConfiguration.class, id));
	}

	@Override
	@Transactional
	public GPIOConfiguration save(@NotBlank GPIOConfiguration entity) {
		entityManager.persist(entity);
		return entity;
	}

	@Override
	@Transactional
	public void deleteById(@NotNull Long id) {
		findById(id).ifPresent(entity -> entityManager.remove(entity));
	}

	@Transactional(readOnly = true)
	public List<GPIOConfiguration> findAll() {
		String qlString = "SELECT g FROM GPIOConfiguration as g";
		TypedQuery<GPIOConfiguration> query = entityManager.createQuery(qlString, GPIOConfiguration.class);
		return query.getResultList();
	}

	@Override
	@Transactional
	public void update(@NotNull GPIOConfiguration entity) {
		entityManager.merge(entity);
	}

	@Override
	@Transactional
	public List<GPIOConfiguration> findByAddres(@NotNull Integer address) {
		return entityManager.createQuery("SELECT c FROM GPIOConfiguration c WHERE c.address LIKE :address", GPIOConfiguration.class)
				.setParameter("address", address)
				.setMaxResults(10)
				.getResultList();
	}
}
