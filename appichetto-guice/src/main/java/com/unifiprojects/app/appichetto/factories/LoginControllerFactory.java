package com.unifiprojects.app.appichetto.factories;

import com.unifiprojects.app.appichetto.controllers.LoginController;
import com.unifiprojects.app.appichetto.views.LoginView;

public interface LoginControllerFactory {
	public LoginController create(LoginView loginView);
}
