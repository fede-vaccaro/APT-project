package com.unifiprojects.app.appichetto.views;

public interface UserPanelView {

	void showUser(String username);

	void showErrorMsg(String exceptionMessage);

	void goToLoginView();

}
