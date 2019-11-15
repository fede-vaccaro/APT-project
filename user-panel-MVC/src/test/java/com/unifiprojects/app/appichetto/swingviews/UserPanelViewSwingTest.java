package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.controllers.UserPanelController;

@RunWith(GUITestRunner.class)
public class UserPanelViewSwingTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;

	@InjectMocks
	private UserPanelViewSwing userPanelViewSwing;

	@Mock
	private UserPanelController userPanelController;

	@Mock
	private LinkedSwingView loginSwingView;

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			MockitoAnnotations.initMocks(this);
		});
		window = new FrameFixture(robot(), userPanelViewSwing.getFrame());
		window.show(); // shows the frame to test
	}

	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.button(JButtonMatcher.withText("Yes")).requireNotVisible();
		window.button(JButtonMatcher.withText("No")).requireNotVisible();
		window.button(JButtonMatcher.withText("Update credential")).requireDisabled();
		window.label(JLabelMatcher.withText("Do you confirm?")).requireNotVisible();
	}

	@Test
	@GUITest
	public void testShowUser() {
		String username = "TestName";
		GuiActionRunner.execute(() -> userPanelViewSwing.showUser(username));

		window.label("userLabel").requireText("Hello TestName!");
	}

	@Test
	@GUITest
	public void testErrorMsg() {
		String testMsg = "test";
		GuiActionRunner.execute(() -> userPanelViewSwing.showErrorMsg(testMsg));

		window.label("errorMsg").requireText(testMsg);
	}

	@Test
	@GUITest
	public void testUpdateCredentialDelegateToController() {
		String testNewName = "newName";
		String testNewPassword = "newPW";

		window.textBox("newName").enterText(testNewName);
		window.textBox("newPW").enterText(testNewPassword);

		window.button(JButtonMatcher.withText("Update credential")).click();

		verify(userPanelController).changeCredential(testNewName, testNewPassword);

	}

	@Test
	@GUITest
	public void testPasswordFieldIsSentAsNullToTheControllerIfTheStringIsEmptyWhenUpdateCredential() {
		String testNewName = "newName";

		window.textBox("newName").enterText(testNewName);

		window.button(JButtonMatcher.withText("Update credential")).click();

		verify(userPanelController).changeCredential(testNewName, null);

	}

	@Test
	@GUITest
	public void testUsernameFieldIsSentAsNullToTheControllerIfTheStringIsEmptyWhenUpdateCredential() {
		String testNewPassword = "newPW";

		window.textBox("newPW").enterText(testNewPassword);

		window.button(JButtonMatcher.withText("Update credential")).click();

		verify(userPanelController).changeCredential(null, testNewPassword);
	}

	@Test
	@GUITest
	public void testUnlockUpdateButtonIfThereIsSomethingWrittenInUsernameField() {
		String testNewName = "newName";

		window.textBox("newName").enterText(testNewName);

		window.button(JButtonMatcher.withText("Update credential")).requireEnabled();

	}

	@Test
	@GUITest
	public void testUnlockUpdateButtonIfThereIsSomethingWrittenInPWField() {
		String testNewPassword = "newPW";

		window.textBox("newPW").enterText(testNewPassword);

		window.button(JButtonMatcher.withText("Update credential")).requireEnabled();
	}

	@Test
	@GUITest
	public void testRelockWhenBothFieldsBecomesEmpty() {
		String testNewPassword = "newPw";

		window.textBox("newPW").enterText(testNewPassword);

		window.textBox("newPW").deleteText();

		window.button(JButtonMatcher.withText("Update credential")).requireDisabled();
	}

	@Test
	public void testUpdateData() {
		GuiActionRunner.execute(() -> userPanelViewSwing.updateData());
		
		verify(userPanelController).update();
	}
	
	@Test
	@GUITest
	public void testRemoveUserShowYesAndNoButton() {
		window.button(JButtonMatcher.withText("Remove user")).click();
		
		window.button(JButtonMatcher.withText("Yes")).requireVisible();
		window.button(JButtonMatcher.withText("No")).requireVisible();
		
		window.button(JButtonMatcher.withText("Remove user")).requireNotVisible();
		window.label(JLabelMatcher.withText("Do you confirm?")).requireVisible();
	}
	
	@Test
	@GUITest
	public void testClickNoButtonShowRemoveUserAgain() {
		window.button(JButtonMatcher.withText("Remove user")).click();
		window.button(JButtonMatcher.withText("No")).click();
		
		window.button(JButtonMatcher.withText("Remove user")).requireVisible();
		
		window.button(JButtonMatcher.withText("Yes")).requireNotVisible();
		window.button(JButtonMatcher.withText("No")).requireNotVisible();
		
		window.label(JLabelMatcher.withText("Do you confirm?")).requireNotVisible();
	}
	
	@Test
	@GUITest
	public void testClickYesButtonDelegateControllerAndGoToLoginPage() {
		window.button(JButtonMatcher.withText("Remove user")).click();
		window.button(JButtonMatcher.withText("Yes")).click();

		verify(userPanelController).deleteUser();
		verify(loginSwingView).show();
		
		assertThat(userPanelViewSwing.getFrame().isVisible()).isFalse();
	}

}
