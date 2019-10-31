package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;

import javax.swing.JFrame;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.controllers.UserController;

@RunWith(GUITestRunner.class)
public class ObservableFrameSwingTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	@Mock
	private HomepageSwingView homepageSwingView;

	private LinkedSwingView observableFrame;
	
	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			MockitoAnnotations.initMocks(this);
			observableFrame = new LinkedSwingView() {

				JFrame frame = new JFrame();

				@Override
				public JFrame getFrame() {
					return frame;
				}

				@Override
				public UserController getController() {
					return null;
				}

				@Override
				public void updateData() {

				}
			};
			return observableFrame;
		});
		observableFrame.getFrame().getContentPane().add(observableFrame.getBtnHome());
		window = new FrameFixture(robot(), observableFrame.getFrame());
		window.show();
	}

	@Test
	public void testShowSetVisibilityToTrue() {
		observableFrame.show();

		assertThat(observableFrame.getFrame().isVisible()).isTrue();
	}

	@Test
	public void testWhenClickHomeButtonThemGoToHomePageIsCalled() {
		observableFrame.setLinkedSwingView(homepageSwingView);
		doNothing().when(homepageSwingView).show();

		window.button(JButtonMatcher.withName("homeBtn")).click();
		
		assertThat(observableFrame.getFrame().isDisplayable()).isFalse();
	}

}
