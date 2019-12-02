package com.unifiprojects.app.appichetto.swingviews.utils;

import com.unifiprojects.app.appichetto.controllers.UserController;
import com.unifiprojects.app.appichetto.swingviews.LinkedSwingView;
import com.unifiprojects.app.appichetto.views.ControlledView;

public abstract class LinkedControlledSwingView extends LinkedSwingView implements ControlledView {

	
	@Override
	public abstract UserController getController();

	@Override
	public void updateData() {
		getController().update();
	}
	
	@Override
	public void show() {
		getFrame().setVisible(true);
		updateData();
	}

}
