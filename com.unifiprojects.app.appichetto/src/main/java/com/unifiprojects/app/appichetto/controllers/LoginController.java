package com.unifiprojects.app.appichetto.controllers;

import com.unifiprojects.app.appichetto.repositories.AlreadyExistentException;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.repositories.UserRepositoryHibernate;
import com.unifiprojects.app.appichetto.views.LoginView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unifiprojects.app.appichetto.models.User;

public class LoginController {
	private UserRepository userRepository;
	private LoginView loginView;
	
	private static final Logger LOGGER = LogManager.getLogger(UserRepositoryHibernate.class);

	
	public LoginController(UserRepository userRepository, LoginView loginView) {
		this.userRepository = userRepository;
		this.loginView = loginView;
	}

	public void login(String username, String password) {
		User user = userRepository.findByUsername(username);
		if (user != null) {
			if (user.getPassword() == password) {
				loginView.goToHomePage();
				return;
			}else {
				loginView.showErrorMsg("Wrong password!");
			}
		}else {
			loginView.showErrorMsg("User not signed in yet.");
		}
	}

	public void signIn(String username, String password) {
		
		try {
			User newUser = new User(username, password);
			userRepository.save(newUser);
			loginView.goToHomePage();
		}catch(AlreadyExistentException e) {
			LOGGER.info(e.getMessage());
			loginView.showErrorMsg("Username already picked. Choice another username.");
		}catch(IllegalArgumentException e) {
			loginView.showErrorMsg("Password too short. Choice another password.");
		}
	}

}

