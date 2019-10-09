package com.unifiprojects.app.appichetto.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unifiprojects.app.appichetto.exceptions.AlreadyExistentException;
import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;
import com.unifiprojects.app.appichetto.views.LoginView;

public class LoginController {
	private UserRepository userRepository;
	private LoginView loginView;
	private TransactionHandler transaction;
	
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
				loginView.goToHome();
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
			transaction.doInTransaction(()->userRepository.save(newUser));
			loginView.goToHome();
		}catch(AlreadyExistentException e) {
			LOGGER.info(e.getMessage());
			loginView.showErrorMsg("Username already picked. Choice another username.");
		}catch(IllegalArgumentException e) {
			LOGGER.info(e.getMessage());
			loginView.showErrorMsg(e.getMessage());
		}catch(UncommittableTransactionException e) {
			LOGGER.info(e.getMessage());
			loginView.showErrorMsg("Something went wrong with the DB connection.");
		}
	}

	public void setTransactionHandler(TransactionHandler transactionHandler) {
		this.transaction = transactionHandler;
		
	}

}

