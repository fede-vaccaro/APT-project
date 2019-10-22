package com.unifiprojects.app.appichetto.factories;

import com.unifiprojects.app.appichetto.controllers.ShowHistoryController;
import com.unifiprojects.app.appichetto.views.ShowHistoryView;

public interface ShowHistoryControllerFactory {
	public ShowHistoryController create(ShowHistoryView view);
}
