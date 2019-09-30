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

import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.controllers.LoginController;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.repositories.UserRepositoryHibernate;
import com.unifiprojects.app.appichetto.transactionhandlers.HibernateTransaction;

@RunWith(GUITestRunner.class)
public class LoginViewSwingIT extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private static MVCBaseTest baseTest = new MVCBaseTest();

	private LoginViewSwing loginViewSwing;

	private LoginController loginController;

	private UserRepository userRepository;

	private static EntityManager entityManager;

	@BeforeClass
	public static void setupEntityManager() {
		baseTest.setupEntityManager();
		entityManager = baseTest.getEntityManager();
	}

	@AfterClass
	public static void closeEntityManager() {
		baseTest.closeEntityManager();
	}

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			baseTest.wipeTablesBeforeTest();
			
			userRepository = new UserRepositoryHibernate(entityManager);
			loginViewSwing = new LoginViewSwing();

			loginController = new LoginController(new HibernateTransaction(entityManager), userRepository,
					loginViewSwing);
			loginViewSwing.setLoginController(loginController);
			return loginViewSwing;
		});
		window = new FrameFixture(robot(), loginViewSwing.getFrame());
		window.show(); // shows the frame to test
	}
	
	@GUITest
	@Test
	public void testSignInSaveTheUserOnDB() {
		String username = "newUser";
		String password = "newPassword";
				
		window.textBox("usernameTextbox").enterText(username);
		window.textBox("passwordField").enterText(password);
		window.button(JButtonMatcher.withText("Sign-in")).click();
		
		User newUser = entityManager
				.createQuery("from users where username=:username", User.class)
				.setParameter("username", username).getSingleResult();
		
		assertThat(newUser).isEqualTo(new User(username, password));
		
	}
	
	@GUITest
	@Test
	public void testSignInWhenUserIsAlreadyPickedShowError() {
		String username = "newUser";
		String password = "newPassword";
		
		entityManager.getTransaction().begin();
		entityManager.persist(new User(username, password));
		entityManager.getTransaction().commit();
		
		window.textBox("usernameTextbox").enterText(username);
		window.textBox("passwordField").enterText("otherPW");
		window.button(JButtonMatcher.withText("Sign-in")).click();

		window.label("errorMsg").requireText("Username already picked. Choice another username.");
	}
	
	@GUITest
	@Test
	public void testLogIn() {
		String username = "newUser";
		String password = "newPassword";
		
		entityManager.getTransaction().begin();
		entityManager.persist(new User(username, password));
		entityManager.getTransaction().commit();
		
		window.textBox("usernameTextbox").enterText(username);
		window.textBox("passwordField").enterText(password);
		window.button(JButtonMatcher.withText("Log-in")).click();

		//TODO: implement go to home page
	}

}