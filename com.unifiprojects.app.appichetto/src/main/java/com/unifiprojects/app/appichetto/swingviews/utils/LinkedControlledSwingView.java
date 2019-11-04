package com.unifiprojects.app.appichetto.swingviews.utils;

import com.unifiprojects.app.appichetto.controllers.UserController;
import com.unifiprojects.app.appichetto.swingviews.LinkedSwingView;
import com.unifiprojects.app.appichetto.views.ControlledView;

public abstract class LinkedControlledSwingView extends LinkedSwingView implements ControlledView {

	UserController controller;
	
	@Override
	public UserController getController() {
		return controller;
	}

	@Override
	public void updateData() {
		controller.update();
	}

}
