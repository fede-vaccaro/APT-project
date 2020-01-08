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

public class HomepageSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	//TODO
	@Mock
	private LoginViewSwing loginView;
	@Mock
	private PayReceiptsViewSwing payReceiptsView;
	@Mock
	private ShowHistoryViewSwing historyView;
	@Mock
	private UserPanelViewSwing userPanelView;
	@Mock
	private ReceiptSwingView receiptView;

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

//	@GUITest
//	@Test
//	public void testUpdateSetFrameVisible() {
//		homepageSwingView.show();
//	}

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
	}

	@GUITest
	@Test
	public void testPayDebtButtonShowOnlyPayDebtView() {
		window.button(JButtonMatcher.withText("Pay Receipt")).click();
		verify(payReceiptsView).show();
	}

	@GUITest
	@Test
	public void testLogOutButtonShowOnlyLogInView() {
		window.button(JButtonMatcher.withText("Log Out")).click();
		verify(loginView).show();
	}

	@GUITest
	@Test
	public void testUserPanelButtonShowOnlyUserPanelView() {
		window.button(JButtonMatcher.withText("User panel")).click();
		verify(userPanelView).show();
	}

	@GUITest
	@Test
	public void testShowHistoryButtonShowOnlyShowHistoryView() {
		window.button(JButtonMatcher.withText("Show History")).click();
		verify(historyView).show();
	}

	//TODO
//	@Test
//	public void testSetLoggedUserSetItInAllViews() {
//		User loggedUser = new User("loggedUser", "");
//	
//		homepageSwingView.setLoggedUser(loggedUser);
//
//		//TODO to test
//	}
//	
//	@Test
//	public void testUpdateMakeFrameTrue() {
//		//TODO to test
//	}
//	
	
	
}
