package com.unifiprojects.app.appichetto.controllers;

import com.google.inject.Inject;
import com.unifiprojects.app.appichetto.exceptions.AlreadyExistentException;
import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;
import com.unifiprojects.app.appichetto.views.HomepageView;
import com.unifiprojects.app.appichetto.views.UserPanelView;

public class UserPanelController extends UserController {

	private UserPanelView userPanelView;
	private UserRepository userRepository;
	private TransactionHandler transaction;
	private HomepageView homepageView;

	public void setHomepageView(HomepageView homepageView) {
		this.homepageView = homepageView;
	}

	public HomepageView getHomepageView() {
		return homepageView;
	}

	@Inject
	public UserPanelController(UserPanelView userPanelView, UserRepository userRepository,
			TransactionHandler transaction) {
		this.userPanelView = userPanelView;
		this.userRepository = userRepository;
		this.transaction = transaction;
	}

	@Override
	public void update() {
		showUser();
	}

	public void showUser() {
		userPanelView.showUser(loggedUser.getUsername());
	}

	public void changeCredential(String newName, String newPassword) {
		if (newName != null)
			loggedUser.setUsername(newName);
		if (newPassword != null)
			loggedUser.setPassword(newPassword);
		try {
			transaction.doInTransaction(() -> userRepository.save(loggedUser));
			homepageView.setLoggedUser(loggedUser);
		} catch (IllegalArgumentException | AlreadyExistentException ex) {
			userPanelView.showErrorMsg(ex.getMessage());
		} catch (UncommittableTransactionException ex) {
			userPanelView.showErrorMsg("Something went wrong while committing changes.");
		} finally {
			this.loggedUser = userRepository.findById(loggedUser.getId());
			this.showUser();
		}
	}

	public void deleteUser() {
		try {
			transaction.doInTransaction(() -> userRepository.removeUser(loggedUser));
			goToLoginView();
		} catch (UncommittableTransactionException ex) {
			userPanelView.showErrorMsg("Something went wrong while committing changes.");

		}

	}

	void goToLoginView() {
		userPanelView.goToLoginView();
	}

}
