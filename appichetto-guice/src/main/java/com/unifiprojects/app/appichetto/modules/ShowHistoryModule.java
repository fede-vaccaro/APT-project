package com.unifiprojects.app.appichetto.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.unifiprojects.app.appichetto.controllers.ShowHistoryController;
import com.unifiprojects.app.appichetto.factories.ShowHistoryControllerFactory;
import com.unifiprojects.app.appichetto.swingviews.ShowHistoryViewSwing;
import com.unifiprojects.app.appichetto.views.ShowHistoryView;

public class ShowHistoryModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(ShowHistoryView.class).to(ShowHistoryViewSwing.class);
		install(new FactoryModuleBuilder().implement(ShowHistoryController.class, ShowHistoryController.class)
				.build(ShowHistoryControllerFactory.class));
	}

	@Provides
	public ShowHistoryViewSwing view(ShowHistoryControllerFactory controllerFactory) {
		ShowHistoryViewSwing view = new ShowHistoryViewSwing();
		view.showHistoryController = controllerFactory.create(view);
		return view;
	}

}