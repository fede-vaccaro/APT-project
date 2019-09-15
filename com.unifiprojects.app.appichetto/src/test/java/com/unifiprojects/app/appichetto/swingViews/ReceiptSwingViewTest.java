package com.unifiprojects.app.appichetto.swingViews;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.User;

@RunWith(GUITestRunner.class)
public class ReceiptSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private ReceiptSwingView receiptSwingView;

	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			receiptSwingView = new ReceiptSwingView();
			return receiptSwingView;
		});
		window = new FrameFixture(robot(), receiptSwingView);
		window.show(); // shows the frame to test
	}

	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("Name"));
		window.textBox("nameBox").requireEnabled();

		window.label(JLabelMatcher.withText("Price"));
		window.textBox("priceBox").requireEnabled();

		window.label(JLabelMatcher.withText("Quantity"));
		window.textBox("quantityBox").requireEnabled();

		// TODO test scroll panel is present

		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Delete")).requireDisabled();
	}

	@Test
	@GUITest
	public void testWhenFontsAreNonEmptyThenAddButtonShouldBeEnabled() {
		window.textBox("nameBox").enterText("Pippo");
		window.textBox("priceBox").enterText("2.2");
		window.textBox("quantityBox").enterText("2");
		window.button(JButtonMatcher.withText("Save")).requireEnabled();
	}

	@Test
	@GUITest
	public void testWhenOneOrMoreArgumentAreBlanckThenSaveButtonIsDisabled() {
		JTextComponentFixture nameBox = window.textBox("nameBox");
		JTextComponentFixture priceBox = window.textBox("priceBox");
		JTextComponentFixture quantityBox = window.textBox("quantityBox");

		nameBox.enterText("Pippo");
		priceBox.enterText("  ");
		quantityBox.enterText("2");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();

		nameBox.setText("");
		priceBox.setText("");
		quantityBox.setText("");

		nameBox.setText(" ");
		priceBox.setText("2.2");
		quantityBox.setText("  ");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();

	}

	@Test
	public void testSaveButtonShouldBeEnabledOnlyWhenAItemIsSelected() {
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));

		GuiActionRunner
				.execute(() -> receiptSwingView.getListItemModel().addElement(new Item("Pippo", "2.2", "2", users)));
		window.list("itemsList").selectItem(0);
		JButtonFixture saveButton = window.button(JButtonMatcher.withText("Save"));
		saveButton.requireEnabled();
		window.list("itemsList").clearSelection();
		saveButton.requireDisabled();
	}

}
