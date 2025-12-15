package com.epam.edu.dao.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.epam.edu.dao.BaseRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;

@Repository
public abstract class NewsPortalBaseRepository<T, ID> implements BaseRepository<T, ID> {

	@PersistenceContext
	protected EntityManager entityManager;
	private final Class<T> entityClass;

	protected NewsPortalBaseRepository(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	@Override
	public T save(T entity) {
		return entityManager.merge(entity); // не оптимально по производительности))
	}

	@Override
	public Optional<T> findById(ID id) {
		return Optional.ofNullable(entityManager.find(entityClass, id));
	}

	@Override
	public List<T> findAll() {
		CriteriaQuery<T> query = entityManager.getCriteriaBuilder().createQuery(entityClass);
		query.select(query.from(entityClass));
		return entityManager.createQuery(query).getResultList();
	}

	@Override
	public void delete(T entity) {
		entityManager.remove(entity);
	}
	
	@Override
	public boolean deleteById(ID id) {
		int deleted = entityManager.createQuery("DELETE FROM " + entityClass.getSimpleName() + " e WHERE e.id = :id")
				.setParameter("id", id).executeUpdate();
		return deleted > 0;
	}

	@Override
	public T update(T entity) {
		return entityManager.merge(entity);
	}

	@Override
	public void flush() {
		entityManager.flush();
	}

	@Override
	public void clear() {
		entityManager.clear();
	}
}
