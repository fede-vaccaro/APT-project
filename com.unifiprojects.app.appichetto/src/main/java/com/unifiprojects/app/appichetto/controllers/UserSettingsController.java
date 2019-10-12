package com.unifiprojects.app.appichetto.controllers;

import com.unifiprojects.app.appichetto.exceptions.AlreadyExistentException;
import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;
import com.unifiprojects.app.appichetto.views.UserSettingsView;

public class UserSettingsController {

	private User loggedUser;
	private UserRepository userRepository;
	private UserSettingsView userSettingsView;
	private TransactionHandler transaction;

	public void setLoggedUser(User user) {
		this.loggedUser = user;
	}

	public void loadUserCredential() {
		this.userSettingsView.loadUserCredential(loggedUser.getUsername(), loggedUser.getPassword());
	}

	void updateCredential(String newUsername, String newPassword) {
		try {
			loggedUser.setUsername(newUsername);
			loggedUser.setPassword(newPassword);

			userRepository.save(loggedUser);
		} catch (AlreadyExistentException e) {
			userSettingsView.showErrorMsg("Username already taken, choice another!");

			// undo the modification
			loggedUser = userRepository.findById(loggedUser.getId());
			this.loadUserCredential();
		}

	}

	public void updateUser(String newUsername, String newPassword) {
		try {
			transaction.doInTransaction(() -> updateCredential(newUsername, newPassword));
		} catch (UncommittableTransactionException e) {
			userSettingsView.showErrorMsg("Can't commit the changes to the DB.");
		}
	}

	public void deleteUser() {
		try {
			transaction.doInTransaction(() -> userRepository.removeUser(loggedUser));
			userSettingsView.closeApplication();
		} catch (UncommittableTransactionException e) {
			userSettingsView.showErrorMsg("Can't commit the changes to the DB.");
		}
	}

}
