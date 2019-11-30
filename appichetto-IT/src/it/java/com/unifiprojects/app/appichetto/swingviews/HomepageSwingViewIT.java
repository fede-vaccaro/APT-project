package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.modules.EntityManagerModule;
import com.unifiprojects.app.appichetto.modules.LoginModule;
import com.unifiprojects.app.appichetto.modules.PayReceiptsModule;
import com.unifiprojects.app.appichetto.modules.ReceiptModule;
import com.unifiprojects.app.appichetto.modules.RepositoriesModule;
import com.unifiprojects.app.appichetto.modules.ShowHistoryModule;
import com.unifiprojects.app.appichetto.modules.UserPanelModule;

@RunWith(GUITestRunner.class)
public class HomepageSwingViewIT extends AssertJSwingJUnitTestCase {

	private static Injector injector;

	private static EntityManager entityManager;

	private static MVCBaseTest baseTest;

	private HomepageSwingView homepageSwingView;

	private FrameFixture window;

	@BeforeClass
	public static void setUpInjectors() {

		Module repositoriesModule = new RepositoriesModule();

		Module entityManagerModule = new EntityManagerModule();

		Module payReceiptModule = new PayReceiptsModule();

		Module userPanelModule = new UserPanelModule();

		Injector persistenceInjector = Guice.createInjector(entityManagerModule);

		baseTest = persistenceInjector.getInstance(MVCBaseTest.class);
		entityManager = persistenceInjector.getInstance(EntityManager.class);

		injector = persistenceInjector.createChildInjector(repositoriesModule, payReceiptModule, new ReceiptModule(),
				userPanelModule, new ShowHistoryModule(), new LoginModule());

	}

	@Override
	protected void onSetUp() {
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
	
	/*
	@Test
	@GUITest
	public void testCreateReceiptButtonShowOnlyCreateReceiptView() {
		window.button(JButtonMatcher.withText("Create Receipt")).click();
		assertThat(homepageSwingView.getFrame().isVisible()).isFalse();
		assertThat(homepageSwingView.receiptView.getFrame().isVisible()).isTrue();

	}
	@Test
	@GUITest
	public void testPayDebtButtonShowOnlyPayDebtView() {
		window.button(JButtonMatcher.withText("Pay Receipt")).click();
		assertThat(homepageSwingView.getFrame().isVisible()).isFalse();
		assertThat(homepageSwingView.payReceiptsView.getFrame().isVisible()).isTrue();

	}
	@Test
	@GUITest
	public void testLogOutButtonShowOnlyLogInView() {
		window.button(JButtonMatcher.withText("Log Out")).click();
		assertThat(homepageSwingView.getFrame().isVisible()).isFalse();
		assertThat(homepageSwingView.loginView.getFrame().isVisible()).isTrue();

	}
	@Test
	@GUITest
	public void testUserPanelButtonShowOnlyUserPanelView() {
		window.button(JButtonMatcher.withText("User panel")).click();
		assertThat(homepageSwingView.getFrame().isVisible()).isFalse();
		assertThat(homepageSwingView.userPanelView.getFrame().isVisible()).isTrue();

	}

	@Test
	@GUITest
	public void testShowHistoryButtonShowOnlyShowHistoryView() {
		window.button(JButtonMatcher.withText("Show History")).click();
		assertThat(homepageSwingView.getFrame().isVisible()).isFalse();
		assertThat(homepageSwingView.showHistoryView.getFrame().isVisible()).isTrue();

	}
	*/
	
	//@Test
	public void test() {
		//Pause.pause(1000000000);

		assertThat(true).isTrue();
	}

}