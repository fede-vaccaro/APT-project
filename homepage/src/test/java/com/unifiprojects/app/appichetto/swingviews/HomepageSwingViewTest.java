package com.unifiprojects.app.appichetto.swingviews;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.controllers.UserController;
import com.unifiprojects.app.appichetto.controllers.UserPanelController;
import com.unifiprojects.app.appichetto.models.User;

public class HomepageSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;

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
	private UserPanelController userPanelController;
	@Mock
	private UserController userController;

	private HomepageSwingView homepageSwingView;

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			MockitoAnnotations.initMocks(this);
			when(userPanelView.getController()).thenReturn((UserPanelController)userPanelController);
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

	@Test
	public void testSetLoggedUserSetItInAllViews() {
		User loggedUser = new User("loggedUser", "");
	
		homepageSwingView.views.forEach(view -> when(view.getController()).thenReturn(userController));
	
		homepageSwingView.setLoggedUser(loggedUser);

		verify(userController, times(4)).setLoggedUser(loggedUser);
	}

	
//	
//	@Test
//	public void testUpdateMakeFrameTrue() {
//		//TODO to test
//	}
//	

}
