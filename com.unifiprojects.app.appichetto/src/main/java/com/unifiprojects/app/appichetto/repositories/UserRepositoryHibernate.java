package com.unifiprojects.app.appichetto.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unifiprojects.app.appichetto.exceptions.AlreadyExistentException;
import com.unifiprojects.app.appichetto.models.User;

public class UserRepositoryHibernate implements UserRepository {

	private EntityManager entityManager;
	private static final Logger LOGGER = LogManager.getLogger(UserRepositoryHibernate.class);

	public UserRepositoryHibernate(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public void save(User user) {
		if((user.getUsername().trim()).equals(""))
			throw new IllegalArgumentException("You can't use empty string username.");
		if (this.findByUsername(user.getUsername()) != null) {
			throw new AlreadyExistentException(
					String.format("Username %s has been already picked.", user.getUsername()));
		}
		if (user.getId() != null) {
			entityManager.merge(user);
		} else {
			entityManager.persist(user);
		}
	}

	@Override
	public User findById(Long id) {
		return entityManager.find(User.class, id);
	}

	@Override
	public List<User> findAll() {
		return entityManager.createQuery("from users", User.class).getResultList();
	}

	@Override
	public User findByUsername(String username) {
		try {
			return entityManager.createQuery("from users where username = :username", User.class)
					.setParameter("username", username).getSingleResult();
		} catch (NoResultException e) {
			LOGGER.debug(String.format("User with username %s not found.", username), e);
			return null;
		}
	}

}
