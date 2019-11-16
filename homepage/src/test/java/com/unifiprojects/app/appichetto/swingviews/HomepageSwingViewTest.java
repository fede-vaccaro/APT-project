package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.swingviews.utils.LinkedControlledSwingView;

public class HomepageSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	@Mock
	private LinkedSwingView loginView;
	@Mock
	private LinkedControlledSwingView receiptView;
	@Mock
	private LinkedControlledSwingView payReceiptsView;
	@Mock
	private LinkedControlledSwingView historyView;
	@Mock
	private LinkedControlledSwingView userPanelView;

	@InjectMocks
	private HomepageSwingView homepageSwingView;

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			MockitoAnnotations.initMocks(this);
			return homepageSwingView;
		});
		window = new FrameFixture(robot(), homepageSwingView.getFrame());
		window.show();
	}

	@GUITest
	@Test
	public void testUpdateSetFrameVisible() {
		homepageSwingView.show();
		assertThat(homepageSwingView.getFrame().isVisible()).isTrue();
	}

	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.button(JButtonMatcher.withText("Create Receipt")).requireEnabled();
		window.button(JButtonMatcher.withText("Pay Receipt")).requireEnabled();
		window.button(JButtonMatcher.withText("Show History")).requireEnabled();
		window.button(JButtonMatcher.withText("Log Out")).requireEnabled();
	}

	@GUITest
	@Test
	public void testCreateReceiptButtonShowOnlyCreateReceiptView() {
		window.button(JButtonMatcher.withText("Create Receipt")).click();
		verify(receiptView).show();
		assertThat(homepageSwingView.getFrame().isVisible()).isFalse();
	}

	@GUITest
	@Test
	public void testPayDebtButtonShowOnlyPayDebtView() {
		window.button(JButtonMatcher.withText("Pay Receipt")).click();
		verify(payReceiptsView).show();
		assertThat(homepageSwingView.getFrame().isVisible()).isFalse();
	}

	@GUITest
	@Test
	public void testLogOutButtonShowOnlyLogInView() {
		window.button(JButtonMatcher.withText("Log Out")).click();
		verify(loginView).show();
		assertThat(homepageSwingView.getFrame().isVisible()).isFalse();
	}

	@GUITest
	@Test
	public void testUserPanelButtonShowOnlyUserPanelView() {
		window.button(JButtonMatcher.withText("User panel")).click();
		verify(userPanelView).show();
		assertThat(homepageSwingView.getFrame().isVisible()).isFalse();
	}

	@GUITest
	@Test
	public void testShowHistoryButtonShowOnlyShowHistoryView() {
		window.button(JButtonMatcher.withText("Show History")).click();
		verify(historyView).show();
		assertThat(homepageSwingView.getFrame().isVisible()).isFalse();
	}
	
	@Test
	public void testSetLoggedUserSetItInAllViews() {
		User loggedUser = new User("loggedUser", "");
	
		//homepageSwingView.setLoggedUser(loggedUser);

		//TODO to test
	}
	
	@Test
	public void testUpdateMakeFrameTrue() {
		//TODO to test
	}
	
	
	
}
