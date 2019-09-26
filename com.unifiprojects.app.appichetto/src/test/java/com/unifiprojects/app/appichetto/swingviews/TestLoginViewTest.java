package com.unifiprojects.app.appichetto.swingviews;

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

@RunWith(GUITestRunner.class)
public class TestLoginViewTest extends AssertJSwingJUnitTestCase {

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
		window.textBox("usernameTextbox").requireEnabled();
		window.label(JLabelMatcher.withText("Password"));
		window.textBox("passwordField").requireEnabled();

		window.button(JButtonMatcher.withText("Log-in")).requireDisabled();
		window.button(JButtonMatcher.withText("Sign-in")).requireEnabled();
	}

	@Test
	@GUITest
	public void testLoginButtonEnabledWhenUsernameTextboxIsNotEmpty() {
		window.textBox("usernameTextbox").enterText("user");
		window.textBox("passwordField").enterText("password");

		window.button(JButtonMatcher.withText("Log-in")).requireEnabled();
	}

	@Test
	@GUITest
	public void testLoginButtonDisabledWhenUsernameTextboxBecomesEmptyFromNonEmpty() {
		window.textBox("usernameTextbox").enterText("user");
		window.textBox("usernameTextbox").deleteText();

		window.button(JButtonMatcher.withText("Log-in")).requireDisabled();
	}
	
	@Test
	@GUITest
	public void testSchoolControllerIsDelegatedWhenLogInButtonIsClicked() {
		String username = "user";
		String password = "password";
		
		window.textBox("usernameTextbox").enterText(username);
		window.textBox("passwordField").enterText(password);
		window.button(JButtonMatcher.withText("Log-in")).click();
		
		verify(loginController).login(username, password);
	}
	
	@Test
	@GUITest
	public void testSchoolControllerIsDelegatedWhenSignInButtonIsClicked() {
		String username = "newUser";
		String password = "pword";
		
		window.textBox("usernameTextbox").enterText(username);
		window.textBox("passwordField").enterText(password);
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


}
