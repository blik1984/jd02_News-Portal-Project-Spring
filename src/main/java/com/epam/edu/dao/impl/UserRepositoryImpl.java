package com.epam.edu.dao.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.epam.edu.dao.UserRepository;
import com.epam.edu.entity.User;

@Repository
public class UserRepositoryImpl extends NewsPortalBaseRepository<User, Long> implements UserRepository {

	protected UserRepositoryImpl() {
		super(User.class);
	}

	@Override
	public Optional<User> findByEmail(String email) {

		Session session = entityManager.unwrap(Session.class);
		Query<User> query = session.createQuery("FROM User u WHERE u.email = :email", User.class);
		query.setParameter("email", email);
		User user = query.uniqueResult();
		return Optional.ofNullable(user);
	}

	@Override
	public boolean existsByEmail(String email) {

		Session session = entityManager.unwrap(Session.class);
		Query<User> query = session.createQuery("FROM User u WHERE u.email = :email", User.class);
		query.setParameter("email", email);
		query.setMaxResults(1);
		User user = query.uniqueResult();
		return user != null;
	}

	@Override
	public List<User> findAllAuthors() {

		Session session = entityManager.unwrap(Session.class);
		Query<User> query = session.createQuery("SELECT u FROM User u WHERE u.author = true", User.class);
		return query.getResultList();
	}
}
