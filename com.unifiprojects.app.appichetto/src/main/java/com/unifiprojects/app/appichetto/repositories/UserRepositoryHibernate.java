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
		// TODO Auto-generated method stub

	}

	@Override
	public User findById(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findByUsername() {
		// TODO Auto-generated method stub
		return null;
	}

}
