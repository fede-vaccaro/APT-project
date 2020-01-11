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
		bind(PayReceiptsView.class).to(PayReceiptsViewSwing.class);
		install(new FactoryModuleBuilder().implement(PayReceiptsController.class, PayReceiptsController.class)
				.build(PayReceiptsControllerFactory.class));
	}

	@Provides
	public PayReceiptsViewSwing view(PayReceiptsControllerFactory controllerFactory) {
		PayReceiptsViewSwing view = new PayReceiptsViewSwing();
		view.setPayReceiptsController(controllerFactory.create(view));
		return view;
	}

}