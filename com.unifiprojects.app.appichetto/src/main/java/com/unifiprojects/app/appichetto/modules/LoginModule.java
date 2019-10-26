package com.unifiprojects.app.appichetto.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.unifiprojects.app.appichetto.controllers.LoginController;
import com.unifiprojects.app.appichetto.factories.LoginControllerFactory;
import com.unifiprojects.app.appichetto.swingviews.LoginViewSwing;
import com.unifiprojects.app.appichetto.views.LoginView;

public class LoginModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(LoginView.class).to(LoginViewSwing.class);
		install(new FactoryModuleBuilder().implement(LoginController.class, LoginController.class)
				.build(LoginControllerFactory.class));
	}
	
	@Provides
	public LoginViewSwing view(LoginControllerFactory controllerFactory) {
		LoginViewSwing view = new LoginViewSwing();
		view.setLoginController(controllerFactory.create(view));
		return view;
	}
}