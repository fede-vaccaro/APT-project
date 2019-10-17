package com.unifiprojects.app.appichetto.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.unifiprojects.app.appichetto.controllers.PayReceiptsController;
import com.unifiprojects.app.appichetto.factories.PayReceiptsControllerFactory;
import com.unifiprojects.app.appichetto.swingviews.PayReceiptsViewSwing;
import com.unifiprojects.app.appichetto.views.PayReceiptsView;

public class PayReceiptsModule extends AbstractModule {
	@Override
	protected void configure() {
		install(new FactoryModuleBuilder().implement(PayReceiptsController.class, PayReceiptsController.class)
				.build(PayReceiptsControllerFactory.class));
	}

	@Provides
	public PayReceiptsView view(PayReceiptsControllerFactory controllerFactory) {
		PayReceiptsViewSwing view = new PayReceiptsViewSwing();
		view.setController(controllerFactory.create(view));
		return view;
	}

	@Provides
	PayReceiptsViewSwing viewSwing(PayReceiptsControllerFactory controllerFactory) {
		return (PayReceiptsViewSwing) view(controllerFactory);
	}
}
