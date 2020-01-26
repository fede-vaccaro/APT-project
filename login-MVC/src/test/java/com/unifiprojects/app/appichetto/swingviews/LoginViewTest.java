package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.controllers.LoginController;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.views.HomepageView;

@RunWith(GUITestRunner.class)
public class LoginViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;

	private LoginViewSwing loginViewSwing;

	@Mock
	private LoginController loginController;

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			MockitoAnnotations.initMocks(this);
			loginViewSwing = new LoginViewSwing();
			loginViewSwing.setLoginController(loginController);
			return loginViewSwing;
		});
		window = new FrameFixture(robot(), loginViewSwing.getFrame());
		window.show(); // shows the frame to test
	}

	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("Username"));
		window.textBox("Username").requireEnabled();
		window.label(JLabelMatcher.withText("Password"));
		window.textBox("Password").requireEnabled();

		window.button(JButtonMatcher.withText("Log-in")).requireDisabled();
		window.button(JButtonMatcher.withText("Sign-in")).requireEnabled();
	}

	@Test
	@GUITest
	public void testLoginButtonEnabledWhenUsernameTextboxIsNotEmpty() {
		window.textBox("Username").enterText("user");
		window.textBox("Password").enterText("password");

		window.button(JButtonMatcher.withText("Log-in")).requireEnabled();
	}

	@Test
	@GUITest
	public void testLoginButtonDisabledWhenUsernameTextboxBecomesEmptyFromNonEmpty() {
		window.textBox("Username").enterText("user");
		window.textBox("Username").deleteText();

		window.button(JButtonMatcher.withText("Log-in")).requireDisabled();
	}

	@Test
	@GUITest
	public void testLoginControllerIsDelegatedWhenLogInButtonIsClicked() {
		String username = "user";
		String password = "password";

		window.textBox("Username").enterText(username);
		window.textBox("Password").enterText(password);
		window.button(JButtonMatcher.withText("Log-in")).click();

		verify(loginController).login(username, password);
	}

	@Test
	@GUITest
	public void testLoginControllerIsDelegatedWhenSignInButtonIsClicked() {
		String username = "newUser";
		String password = "pword";

		window.textBox("Username").enterText(username);
		window.textBox("Password").enterText(password);
		window.button(JButtonMatcher.withText("Sign-in")).click();

		verify(loginController).signIn(username, password);

	}

	@Test
	@GUITest
	public void testErrorMessageIsCorrectlyDisplayed() {
		String expectedMsg = "Testing error message.";
		loginViewSwing.showErrorMsg(expectedMsg);

		window.label("errorMsg").requireText(expectedMsg);
	}

	@Test
	@GUITest
	public void testGoToHome() {
		HomepageView homepage = mock(HomepageView.class);
		loginViewSwing.setHomepage(homepage);

		User user = new User("username", "pw");

		loginViewSwing.goToHome(user);

		verify(loginViewSwing.getHomepage()).setLoggedUser(user);
		verify(loginViewSwing.getHomepage()).show();
	}

	@Test
	public void testShowCleanTheField() {
		GuiActionRunner.execute(() -> {
			loginViewSwing.setUsernameTextbox("Pippo");
			loginViewSwing.setPasswordField("psw");
			loginViewSwing.setErrorMsg("Error");
			loginViewSwing.show();
		});
		
		assertThat(loginViewSwing.getUsernameTextbox()).isEqualTo("");
		assertThat(loginViewSwing.getPasswordFromPasswordField()).isEqualTo("");
		assertThat(loginViewSwing.getErrorMsg()).isEqualTo("");
	}

}
