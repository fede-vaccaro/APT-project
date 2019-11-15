package com.unifiprojects.app.appichetto.views;

import com.unifiprojects.app.appichetto.controllers.UserController;

public interface ControlledView {
	public abstract UserController getController();

	public abstract void updateData();
}
