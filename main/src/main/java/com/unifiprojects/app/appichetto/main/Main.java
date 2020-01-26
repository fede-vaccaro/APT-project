package com.unifiprojects.app.appichetto.main;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.unifiprojects.app.appichetto.modules.EntityManagerModule;
import com.unifiprojects.app.appichetto.modules.LoginModule;
import com.unifiprojects.app.appichetto.modules.PayReceiptsModule;
import com.unifiprojects.app.appichetto.modules.ReceiptModule;
import com.unifiprojects.app.appichetto.modules.RepositoriesModule;
import com.unifiprojects.app.appichetto.modules.ShowHistoryModule;
import com.unifiprojects.app.appichetto.modules.UserPanelModule;
import com.unifiprojects.app.appichetto.swingviews.LinkedSwingView;
import com.unifiprojects.app.appichetto.swingviews.LoginViewSwing;

public class Main {

	private static final Logger LOGGER = LogManager.getLogger(Main.class);

	public static void main(String[] args) {


		try {
			EventQueue.invokeAndWait(() -> {
				Injector persistenceInjector = Guice.createInjector(new EntityManagerModule());

				Injector injector = persistenceInjector.createChildInjector(new RepositoriesModule(),
						new PayReceiptsModule(), new ReceiptModule(), new ShowHistoryModule(persistenceInjector), new LoginModule(),
						new UserPanelModule());

				LoginViewSwing loginView = injector.getInstance(LoginViewSwing.class);
					LinkedSwingView.initializeMainFrame();
					loginView.show();
			});
		} catch (InvocationTargetException | InterruptedException e) {
			LOGGER.error(e);
			Thread.currentThread().interrupt();
		}
	}

}
