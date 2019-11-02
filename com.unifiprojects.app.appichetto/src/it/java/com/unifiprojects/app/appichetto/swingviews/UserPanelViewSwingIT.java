package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;

import org.apache.commons.math3.util.Precision;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.timing.Pause;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.controllers.PayReceiptsController;
import com.unifiprojects.app.appichetto.controllers.ReceiptGenerator;
import com.unifiprojects.app.appichetto.controllers.UserPanelController;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.modules.EntityManagerModule;
import com.unifiprojects.app.appichetto.modules.LoginModule;
import com.unifiprojects.app.appichetto.modules.PayReceiptsModule;
import com.unifiprojects.app.appichetto.modules.RepositoriesModule;
import com.unifiprojects.app.appichetto.modules.UserPanelModule;
import com.unifiprojects.app.appichetto.swingviews.utils.ReceiptFormatter;
import com.unifiprojects.app.appichetto.views.HomepageView;
import com.unifiprojects.app.appichetto.views.PayReceiptsView;
import com.unifiprojects.app.appichetto.views.UserPanelView;

@RunWith(GUITestRunner.class)
public class UserPanelViewSwingIT extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private static MVCBaseTest baseTest = new MVCBaseTest();

	private UserPanelViewSwing userPanelViewSwing;
	private UserPanelController userPanelController;

	private User loggedUser;
	private User payer1;
	private Receipt firstReceiptPayer1;
	private Receipt secondReceiptPayer1;
	private Receipt thirdReceiptPayer1;
	private Receipt firstReceiptPayer2;
	private User payer2;

	private static EntityManager entityManager;
	private static Injector injector;

	@BeforeClass
	public static void setupEntityManager() {

		Module entityManagerModule = new EntityManagerModule();

		Module homepageModule = new AbstractModule() {
			@Override
			public void configure() {
				bind(HomepageView.class).to(HomepageSwingView.class).in(Singleton.class);
			}

		};

		Injector persistenceInjector = Guice.createInjector(entityManagerModule);

		baseTest = persistenceInjector.getInstance(MVCBaseTest.class);
		entityManager = persistenceInjector.getInstance(EntityManager.class);

		injector = persistenceInjector.createChildInjector(new RepositoriesModule(), new UserPanelModule(), new LoginModule(), homepageModule);
	}

	@AfterClass
	public static void closeEntityManager() {
		baseTest.closeEntityManager();
	}

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			baseTest.wipeTablesBeforeTest();

			loggedUser = new User("logged", "pw");
			payer1 = new User("payer", "pw");
			payer2 = new User("payer2", "pw");

			firstReceiptPayer1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
					payer1, new GregorianCalendar(2019, 8, 10));
			secondReceiptPayer1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
					payer1, new GregorianCalendar(2019, 8, 11));
			firstReceiptPayer2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
					payer2, new GregorianCalendar(2019, 8, 10));

			Item tomato = new Item("tomato", 1.35, Arrays.asList(loggedUser, payer1));
			Item hamburger = new Item("hamburger", 4.45, Arrays.asList(loggedUser, payer1));
			Item bread = new Item("bread", 3.89, Arrays.asList(loggedUser, payer1));
			thirdReceiptPayer1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
					payer1, new GregorianCalendar(2019, 8, 12), Arrays.asList(tomato, bread, hamburger));

			entityManager.getTransaction().begin();
			entityManager.persist(loggedUser);
			entityManager.persist(payer1);
			entityManager.persist(payer2);
			entityManager.persist(firstReceiptPayer1);
			entityManager.persist(secondReceiptPayer1);
			entityManager.persist(thirdReceiptPayer1);
			entityManager.persist(firstReceiptPayer2);
			entityManager.getTransaction().commit();

			entityManager.clear();

			userPanelViewSwing = (UserPanelViewSwing) injector.getInstance(UserPanelView.class);
			userPanelController = (UserPanelController) userPanelViewSwing.getController();
			userPanelController.setLoggedUser(loggedUser);

			// when the user is entering the view, the controller should call
			// showUnpaidReceipts
			GuiActionRunner.execute(() -> userPanelController.update());

			return userPanelViewSwing;
		});

		window = new FrameFixture(robot(), userPanelViewSwing.getFrame());
		window.show(); // shows the frame to test
	}

	@GUITest
	@Test
	public void testInitialState() {
		Pause.pause(1000000);
		
		window.label("userLabel").requireText("Hello logged!");
	}
	
	@GUITest
	@Test
	public void testGoBackHome() {
		HomepageSwingView homepage = (HomepageSwingView) injector.getInstance(HomepageView.class);
		userPanelViewSwing.setLinkedSwingView(homepage);
		
		window.button(JButtonMatcher.withText("Back")).click();
		assertThat(userPanelViewSwing.getFrame().isVisible()).isFalse();
		assertThat(homepage.getFrame().isVisible()).isTrue();
	}
	
	
	@GUITest
	@Test
	public void test() {
		HomepageSwingView homepage = (HomepageSwingView) injector.getInstance(HomepageView.class);
		userPanelViewSwing.setLinkedSwingView(homepage);
		
		Pause.pause(1000000);
	}

}
