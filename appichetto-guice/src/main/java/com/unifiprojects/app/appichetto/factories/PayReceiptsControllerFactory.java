package com.unifiprojects.app.appichetto.factories;

import com.unifiprojects.app.appichetto.controllers.PayReceiptsController;
import com.unifiprojects.app.appichetto.views.PayReceiptsView;

public interface PayReceiptsControllerFactory {
	public PayReceiptsController create(PayReceiptsView view);
}
