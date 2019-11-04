package com.unifiprojects.app.appichetto.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.unifiprojects.app.appichetto.controllers.UserPanelController;
import com.unifiprojects.app.appichetto.factories.UserPanelControllerFactory;
import com.unifiprojects.app.appichetto.swingviews.HomepageSwingView;
import com.unifiprojects.app.appichetto.swingviews.UserPanelViewSwing;
import com.unifiprojects.app.appichetto.views.HomepageView;
import com.unifiprojects.app.appichetto.views.UserPanelView;

public class UserPanelModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(HomepageView.class).to(HomepageSwingView.class);
		bind(UserPanelView.class).to(UserPanelViewSwing.class);
		install(new FactoryModuleBuilder().implement(UserPanelController.class, UserPanelController.class)
				.build(UserPanelControllerFactory.class));
	}
	
	@Provides
	public UserPanelViewSwing view(UserPanelControllerFactory controllerFactory) {
		UserPanelViewSwing view = new UserPanelViewSwing();
		view.setUserPanelController(controllerFactory.create(view));
		return view;
	}
}