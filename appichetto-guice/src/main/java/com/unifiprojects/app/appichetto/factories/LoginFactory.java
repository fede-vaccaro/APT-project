package com.unifiprojects.app.appichetto.factories;

import com.unifiprojects.app.appichetto.controllers.LoginController;
import com.unifiprojects.app.appichetto.swingviews.HomepageSwingView;
import com.unifiprojects.app.appichetto.views.LoginView;

public interface LoginFactory {
	public LoginController createLoginController(LoginView loginView);
	public HomepageSwingView createHomePageView(LoginView loginView);
}
