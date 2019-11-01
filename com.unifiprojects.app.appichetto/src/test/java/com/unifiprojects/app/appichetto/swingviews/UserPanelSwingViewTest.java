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
import com.unifiprojects.app.appichetto.controllers.UserPanelController;

@RunWith(GUITestRunner.class)
public class UserPanelSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	
	private UserPanelViewSwing UserPanelViewSwing;

	@Mock
	private UserPanelController userPanelController;

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
		});
		window = new FrameFixture(robot(), UserPanelViewSwing.getFrame());
		window.show(); // shows the frame to test
	}

	
	@Test
	@GUITest
	public void testControlsInitialStates() {
	}


}
