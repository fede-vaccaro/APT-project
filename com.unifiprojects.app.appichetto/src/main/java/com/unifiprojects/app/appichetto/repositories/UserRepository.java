package com.unifiprojects.app.appichetto.repositories;

import java.util.List;

import com.unifiprojects.app.appichetto.exceptions.AlreadyExistentException;
import com.unifiprojects.app.appichetto.models.User;

public interface UserRepository {
	public void save(User user) throws IllegalArgumentException, AlreadyExistentException;

	public User findById(Long id);

	public List<User> findAll();

	public User findByUsername(String username);
}
