package com.unifiprojects.app.appichetto.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.unifiprojects.app.appichetto.controllers.LoginController;
import com.unifiprojects.app.appichetto.factories.LoginFactory;
import com.unifiprojects.app.appichetto.swingviews.HomepageSwingView;
import com.unifiprojects.app.appichetto.swingviews.LoginViewSwing;
import com.unifiprojects.app.appichetto.views.HomepageView;
import com.unifiprojects.app.appichetto.views.LoginView;

public class LoginModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(LoginView.class).to(LoginViewSwing.class);
		install(new FactoryModuleBuilder().implement(LoginController.class, LoginController.class)
				.implement(HomepageView.class, HomepageSwingView.class).build(LoginFactory.class));
	}

	@Provides
	public LoginViewSwing view(LoginFactory controllerFactory) {
		LoginViewSwing view = new LoginViewSwing();
		view.setLoginController(controllerFactory.createLoginController(view));
		view.setHomepage(controllerFactory.createHomePageView(view));
		return view;
	}
}