package com.unifiprojects.app.appichetto.main;

import java.awt.EventQueue;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.unifiprojects.app.appichetto.modules.EntityManagerModule;
import com.unifiprojects.app.appichetto.modules.LoginModule;
import com.unifiprojects.app.appichetto.modules.PayReceiptsModule;
import com.unifiprojects.app.appichetto.modules.ReceiptModule;
import com.unifiprojects.app.appichetto.modules.RepositoriesModule;
import com.unifiprojects.app.appichetto.modules.ShowHistoryModule;
import com.unifiprojects.app.appichetto.modules.UserPanelModule;
import com.unifiprojects.app.appichetto.swingviews.HomepageSwingView;
import com.unifiprojects.app.appichetto.swingviews.LoginViewSwing;

public class Main {

	public static void main(String[] args) {
		
		Module repositoriesModule = new RepositoriesModule();

		Module entityManagerModule = new EntityManagerModule();

		Module payReceiptModule = new PayReceiptsModule();

		Injector persistenceInjector = Guice.createInjector(entityManagerModule);

		Injector injector = persistenceInjector.createChildInjector(repositoriesModule, payReceiptModule,
				new ReceiptModule(), new ShowHistoryModule(), new LoginModule(), new UserPanelModule());
		
		LoginViewSwing loginViewSwing = injector.getInstance(LoginViewSwing.class);
		HomepageSwingView homepageSwingView = injector.getInstance(HomepageSwingView.class);

		homepageSwingView.setLoginView(loginViewSwing);
		loginViewSwing.setHomepage(homepageSwingView);
		
		EventQueue.invokeLater(() -> {
			try {
				loginViewSwing.getFrame().setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
