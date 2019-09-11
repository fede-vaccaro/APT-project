package com.unifiprojects.app.appichetto.repositories;

import java.util.List;

import javax.persistence.EntityManager;

import com.unifiprojects.app.appichetto.models.User;

public class UserRepositoryHibernate implements UserRepository {

	private EntityManager entityManager;

	public UserRepositoryHibernate(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public void save(User user) {
		entityManager.getTransaction().begin();
		if (user.getId() != null) {
			entityManager.merge(user);
		} else {
			entityManager.persist(user);
		}
		entityManager.getTransaction().commit();
	}

	@Override
	public User findById(Long id) {
		return entityManager.find(User.class, id);
	}

	@Override
	public List<User> findAll() {
		return entityManager.createQuery("from User", User.class).getResultList();
	}

	@Override
	public User findByUsername(String username) {
		return entityManager.createQuery("from User where username = :username and password = :pw", User.class).setParameter("username", username).getSingleResult();
	}

}
