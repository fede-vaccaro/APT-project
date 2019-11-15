package com.unifiprojects.app.appichetto.views;

import com.unifiprojects.app.appichetto.models.User;

public interface LoginView {

	void goToHome(User loggedUser);

	void showErrorMsg(String string);

}
