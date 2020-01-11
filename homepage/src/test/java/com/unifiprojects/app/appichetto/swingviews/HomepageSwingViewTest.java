package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.controllers.UserController;
import com.unifiprojects.app.appichetto.controllers.UserPanelController;

public class HomepageSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	// TODO
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
	@Mock
	private UserPanelController userController;

	private HomepageSwingView homepageSwingView;

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			MockitoAnnotations.initMocks(this);
			when(userPanelView.getController()).thenReturn(userController);
			homepageSwingView = new HomepageSwingView(payReceiptsView, historyView, receiptView, userPanelView,
					loginView);

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

	// TODO
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
