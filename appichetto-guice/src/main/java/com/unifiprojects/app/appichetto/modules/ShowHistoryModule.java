package com.unifiprojects.app.appichetto.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.unifiprojects.app.appichetto.controllers.ShowHistoryController;
import com.unifiprojects.app.appichetto.factories.ShowHistoryControllerFactory;
import com.unifiprojects.app.appichetto.swingviews.ReceiptSwingView;
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
		Injector persistenceInjector = Guice.createInjector(new EntityManagerModule());

		Injector injector = persistenceInjector.createChildInjector(new RepositoriesModule(),
				new ReceiptModule(), new UserPanelModule());
		
		ShowHistoryViewSwing view = new ShowHistoryViewSwing();
		view.setShowHistoryController(controllerFactory.create(view));
		view.setReceiptView(injector.getInstance(ReceiptSwingView.class));
		return view;
	}

}