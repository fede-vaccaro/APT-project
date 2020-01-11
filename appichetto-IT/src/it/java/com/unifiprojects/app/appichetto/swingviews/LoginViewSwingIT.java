package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
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
import com.unifiprojects.app.appichetto.swingviews.utils.LinkedSwingView;

@RunWith(GUITestRunner.class)
public class LoginViewSwingIT extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private static MVCBaseTest baseTest = new MVCBaseTest();

	private LoginViewSwing loginViewSwing;

	private HomepageSwingView homepageSwingView;

	private static EntityManager entityManager;
	private static Injector injector;

	@BeforeClass
	public static void setupEntityManager() {
		Module repositoriesModule = new RepositoriesModule();

		Module entityManagerModule = new EntityManagerModule();

		Module payReceiptModule = new PayReceiptsModule();

		Injector persistenceInjector = Guice.createInjector(entityManagerModule);

		baseTest = persistenceInjector.getInstance(MVCBaseTest.class);
		entityManager = persistenceInjector.getInstance(EntityManager.class);

		injector = persistenceInjector.createChildInjector(repositoriesModule, payReceiptModule, new ReceiptModule(),
				new ShowHistoryModule(), new LoginModule(), new UserPanelModule());

	}

	@AfterClass
	public static void closeEntityManager() {
		baseTest.closeEntityManager();
	}

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			baseTest.wipeTablesBeforeTest();

			loginViewSwing = injector.getInstance(LoginViewSwing.class);
			homepageSwingView = injector.getInstance(HomepageSwingView.class);

			homepageSwingView.loginView = loginViewSwing;
			loginViewSwing.setHomepage(homepageSwingView);
			LinkedSwingView.initializeMainFrame();

			return loginViewSwing;
		});
		window = new FrameFixture(robot(), loginViewSwing.getFrame());
		window.show(); // shows the frame to test
	}

	@GUITest
	@Test
	public void testSignInSaveTheUserOnDBAndGoToHomepage() {
		String username = "newUser";
		String password = "newPassword";

		window.textBox("Username").enterText(username);
		window.textBox("Password").enterText(password);
		window.button(JButtonMatcher.withText("Sign-in")).click();

		User newUser = entityManager.createQuery("from users where username=:username", User.class)
				.setParameter("username", username).getSingleResult();

		assertThat(newUser).isEqualTo(new User(username, password));

		assertThat(LinkedSwingView.mainFrame.getContentPane().getComponents()[0])
				.isEqualTo(homepageSwingView.getFrame().getContentPane());
		homepageSwingView.views.forEach(v -> assertThat(v.getController().getLoggedUser()).isEqualTo(newUser));
	}

	@GUITest
	@Test
	public void testSignInWhenUserIsAlreadyPickedShowError() {
		String username = "newUser";
		String password = "newPassword";

		entityManager.getTransaction().begin();
		entityManager.persist(new User(username, password));
		entityManager.getTransaction().commit();

		window.textBox("Username").enterText(username);
		window.textBox("Password").enterText("otherPW");
		window.button(JButtonMatcher.withText("Sign-in")).click();

		window.label("errorMsg").requireText("Username already picked. Choice another username.");
	}

	@GUITest
	@Test
	public void testLogIn() {
		String username = "newUser";
		String password = "newPassword";

		entityManager.getTransaction().begin();
		User newUser = new User(username, password);
		entityManager.persist(newUser);
		entityManager.getTransaction().commit();
		window.textBox("Username").enterText(username);
		window.textBox("Password").enterText(password);
		window.button(JButtonMatcher.withText("Log-in")).click();

		assertThat(newUser).isEqualTo(new User(username, password));

		assertThat(LinkedSwingView.mainFrame.getContentPane().getComponents()[0])
				.isEqualTo(homepageSwingView.getFrame().getContentPane());
		homepageSwingView.views.forEach(v -> assertThat(v.getController().getLoggedUser()).isEqualTo(newUser));
	}

}