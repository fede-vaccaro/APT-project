package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.timing.Pause;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.modules.EntityManagerModule;
import com.unifiprojects.app.appichetto.modules.LoginModule;
import com.unifiprojects.app.appichetto.modules.PayReceiptsModule;
import com.unifiprojects.app.appichetto.modules.ReceiptModule;
import com.unifiprojects.app.appichetto.modules.RepositoriesModule;
import com.unifiprojects.app.appichetto.modules.ShowHistoryModule;

@RunWith(GUITestRunner.class)

public class HomepageSwingViewIT extends AssertJSwingJUnitTestCase {

	private static Injector injector;

	private static EntityManager entityManager;

	private static MVCBaseTest baseTest;

	private HomepageSwingView homepageSwingView;

	private FrameFixture window;

	@Mock
	private LinkedSwingView loginView;
	@Mock
	private LinkedSwingView receiptView;
	@Mock
	private LinkedSwingView historyView;

	@BeforeClass
	public static void setUpInjectors() {

		Module repositoriesModule = new RepositoriesModule();

		Module entityManagerModule = new EntityManagerModule();

		Module payReceiptModule = new PayReceiptsModule();

		Injector persistenceInjector = Guice.createInjector(entityManagerModule);

		baseTest = persistenceInjector.getInstance(MVCBaseTest.class);
		entityManager = persistenceInjector.getInstance(EntityManager.class);

		injector = persistenceInjector.createChildInjector(repositoriesModule, payReceiptModule, new ReceiptModule(),
				new ShowHistoryModule(), new LoginModule());

	}

	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);

		baseTest.wipeTablesBeforeTest();

		GuiActionRunner.execute(() -> {
			homepageSwingView = injector.getInstance(HomepageSwingView.class);

			entityManager.getTransaction().begin();
			User logged = new User("Federico", "");
			User another = new User("Pasquale", "");
			entityManager.persist(logged);
			entityManager.persist(another);
			entityManager.getTransaction().commit();

			homepageSwingView.setLoggedUser(logged);

		});
		window = new FrameFixture(robot(), homepageSwingView.getFrame());
		window.show();

	}

	@GUITest
	@Test
	public void testInjection() {
		assertThat(homepageSwingView.payReceiptsView).isNotNull();
		PayReceiptsViewSwing payReceiptsViewSwing = (PayReceiptsViewSwing) homepageSwingView.payReceiptsView;
		assertThat(payReceiptsViewSwing.getController()).isNotNull();
	}

	@Test
	public void testHomepageViewIsEqualEverywhere() {
		for (LinkedSwingView view : homepageSwingView.views) {
			assertThat(view.getLinkedSwingView()).isEqualTo(homepageSwingView);
		}
	}

	//@Test
	public void test() {
		Pause.pause(1000000000);

		assertThat(true).isTrue();
	}

}
