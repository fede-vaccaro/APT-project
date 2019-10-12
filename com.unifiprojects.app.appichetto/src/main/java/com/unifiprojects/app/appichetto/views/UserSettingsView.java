package com.unifiprojects.app.appichetto.views;

public interface UserSettingsView {
	
	public void showErrorMsg(String msg);

	public void loadUserCredential(String username, String password);

	public void closeApplication();

}
