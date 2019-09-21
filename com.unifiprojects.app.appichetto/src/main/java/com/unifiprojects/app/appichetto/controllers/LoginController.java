package com.unifiprojects.app.appichetto.controllers;

import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.transactionHandlers.ExecuteInTransaction;
import com.unifiprojects.app.appichetto.transactionHandlers.TransactionHandler;
import com.unifiprojects.app.appichetto.views.LoginView;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unifiprojects.app.appichetto.exceptions.AlreadyExistentException;
import com.unifiprojects.app.appichetto.exceptions.UncommittableTransaction;
import com.unifiprojects.app.appichetto.models.User;

public class LoginController {
	private TransactionHandler transaction;
	private UserRepository userRepository;
	private LoginView loginView;

	private static final Logger LOGGER = LogManager.getLogger(LoginController.class);

	public LoginController(TransactionHandler transaction, UserRepository userRepository, LoginView loginView) {
		this.transaction = transaction;
		this.userRepository = userRepository;
		this.loginView = loginView;
	}

	public void login(String username, String password) {
		User user = userRepository.findByUsername(username);
		if (user != null) {
			if (user.getPassword().equals(password)) {
				loginView.goToHomePage();
			} else {
				loginView.showErrorMsg("Wrong password!");
			}
		} else {
			loginView.showErrorMsg("User not signed in yet.");
		}
	}

	public void signIn(String username, String password) {

		try { 
			User newUser = new User(username, password);
			transaction.doInTransaction(() -> userRepository.save(newUser));
			loginView.goToHomePage();
		} catch (AlreadyExistentException e) {
			LOGGER.log(Level.INFO, e.getMessage());
			loginView.showErrorMsg("Username already picked. Choice another username.");
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.INFO, e.getMessage());
			loginView.showErrorMsg("Password too short. Choice another password.");
		} catch (UncommittableTransaction e) {
			LOGGER.log(Level.INFO, e.getMessage());
			loginView.showErrorMsg("Something went wrong with the DB connection.");
		}
	}

	public void setTransactionHandler(TransactionHandler transaction) {
		this.transaction = transaction;
	}

}
