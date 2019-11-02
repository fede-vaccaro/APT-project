package com.unifiprojects.app.appichetto.factories;

import com.unifiprojects.app.appichetto.controllers.UserPanelController;
import com.unifiprojects.app.appichetto.views.UserPanelView;

public interface UserPanelControllerFactory {
	public UserPanelController create(UserPanelView view);
}
