package com.unifiprojects.app.appichetto.main;

import java.awt.EventQueue;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.unifiprojects.app.appichetto.modules.EntityManagerModule;
import com.unifiprojects.app.appichetto.modules.LoginModule;
import com.unifiprojects.app.appichetto.modules.PayReceiptsModule;
import com.unifiprojects.app.appichetto.modules.ReceiptModule;
import com.unifiprojects.app.appichetto.modules.RepositoriesModule;
import com.unifiprojects.app.appichetto.modules.ShowHistoryModule;
import com.unifiprojects.app.appichetto.modules.UserPanelModule;
import com.unifiprojects.app.appichetto.swingviews.HomepageSwingView;
import com.unifiprojects.app.appichetto.swingviews.LinkedSwingView;

public class Main {

	public static void main(String[] args) {

		Injector persistenceInjector = Guice.createInjector(new EntityManagerModule());

		Injector injector = persistenceInjector.createChildInjector(
				new RepositoriesModule(), 
				new PayReceiptsModule(),
				new ReceiptModule(), 
				new ShowHistoryModule(), 
				new LoginModule(), 
				new UserPanelModule());

		HomepageSwingView homepageSwingView = injector.getInstance(HomepageSwingView.class);

		EventQueue.invokeLater(() -> {
			try {
				LinkedSwingView.initializeMainFrame();
				homepageSwingView.getLoginView().show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
