package com.unifiprojects.app.appichetto.controllers;

import com.unifiprojects.app.appichetto.models.User;

public abstract class UserController {
	
	User loggedUser;
	
	public void setLoggedUser(User user) {
		this.loggedUser = user;
	}
	
	public User getLoggedUser() {
		return loggedUser;
	}
	
	public abstract void update();
}
