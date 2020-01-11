package com.unifiprojects.app.appichetto.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.unifiprojects.app.appichetto.controllers.ReceiptController;
import com.unifiprojects.app.appichetto.factories.ReceiptControllerFactory;
import com.unifiprojects.app.appichetto.swingviews.ReceiptSwingView;
import com.unifiprojects.app.appichetto.views.ReceiptView;

public class ReceiptModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(ReceiptView.class).to(ReceiptSwingView.class);
		install(new FactoryModuleBuilder().implement(ReceiptController.class, ReceiptController.class)
				.build(ReceiptControllerFactory.class));
	}

	@Provides
	public ReceiptSwingView view(ReceiptControllerFactory controllerFactory) {
		ReceiptSwingView receiptView = new ReceiptSwingView();
		receiptView.receiptController = controllerFactory.create(receiptView);
		return receiptView;
	}
}
