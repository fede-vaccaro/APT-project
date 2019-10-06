package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

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
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.controllers.ReceiptController;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.User;

public class ReceiptSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private ReceiptSwingView receiptSwingView;
	private JTextComponentFixture nameBox;
	private JTextComponentFixture priceBox;
	private JTextComponentFixture quantityBox;

	private User addUserToListUserModel(String name, String psw) {
		User user = new User(name, psw);
		GuiActionRunner.execute(() -> receiptSwingView.getListUsersModel().addElement(user));
		return user;
	}

	private Item addItemToItemListModel(String name, Double price, Integer quantity, List<User> users) {
		Item item = new Item(name, price, quantity, users);

		GuiActionRunner.execute(() -> receiptSwingView.getListItemModel().addElement(item));
		return item;

	}

	@Mock
	private ReceiptController receiptController;

	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);
		GuiActionRunner.execute(() -> {
			receiptSwingView = new ReceiptSwingView();
			receiptSwingView.setReceiptController(receiptController);
			return receiptSwingView;
		});
		window = new FrameFixture(robot(), receiptSwingView.frame);
		window.show();
		nameBox = window.textBox("nameBox");
		priceBox = window.textBox("priceBox");
		quantityBox = window.textBox("quantityBox");
	}

	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("Name"));
		nameBox.requireEnabled();

		window.label(JLabelMatcher.withText("Price"));
		window.textBox("priceBox").requireEnabled();

		window.label(JLabelMatcher.withText("Quantity"));
		quantityBox.requireEnabled();

		// TODO test both scroll panels are present

		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();
		window.button(JButtonMatcher.withText("Delete")).requireDisabled();
		window.label("errorMsgLabel").requireText("");
	}

	@Test
	@GUITest
	public void testWhenFormIsNonEmptyThenAddButtonShouldBeEnabledCompilingFirstItemInfoThenUsersList() {
		nameBox.enterText("Sugo");
		window.textBox("priceBox").enterText("2.2");
		quantityBox.enterText("2");
		addUserToListUserModel("Pippo", "psw");

		window.list("usersList").selectItem(0);

		window.button(JButtonMatcher.withText("Save")).requireEnabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();
		window.button(JButtonMatcher.withText("Delete")).requireDisabled();
		window.button(JButtonMatcher.withText("Save Receipt")).requireDisabled();
		window.button(JButtonMatcher.withText("Home")).requireEnabled();
	}

	@Test
	@GUITest
	public void testWhenFormIsNonEmptyThenAddButtonShouldBeEnabledCompilingFirstUsersListThenItemInfo() {
		addUserToListUserModel("Pippo", "psw");
		window.list("usersList").selectItem(0);
		nameBox.enterText("Sugo");
		priceBox.enterText("2.2");
		quantityBox.enterText("2");

		window.button(JButtonMatcher.withText("Save")).requireEnabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();
		window.button(JButtonMatcher.withText("Delete")).requireDisabled();
	}

	@Test
	@GUITest
	public void testWhenNameIsBlanckAndUserIsSelectFirstThenSaveAndUpdateButtonsAreDisabled() {

		User user = addUserToListUserModel("Pippo", "psw");
		addItemToItemListModel("Sugo", 2.0, 2, Arrays.asList(user));

		window.list("usersList").selectItem(0);
		nameBox.enterText("  ");
		priceBox.enterText("");
		quantityBox.enterText("");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();

		window.textBox("priceBox").enterText("2.2");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();

		quantityBox.enterText("2");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();
	}

	@Test
	@GUITest
	public void testWhenNameIsBlanckAndUserIsSelectTheLastThenSaveAndUpdateButtonsAreDisabled() {

		User user = addUserToListUserModel("Pippo", "psw");
		addItemToItemListModel("Sugo", 2.0, 2, Arrays.asList(user));

		nameBox.enterText("  ");
		priceBox.enterText("");
		quantityBox.enterText("");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();

		window.textBox("priceBox").enterText("2.2");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();

		quantityBox.enterText("2");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();

		window.list("usersList").selectItem(0);
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();

	}

	@Test
	@GUITest
	public void testWhenPriceIsBlanckAndUserIsSelectFirstThenSaveAndUpdateButtonsAreDisabled() {

		User user = addUserToListUserModel("Pippo", "psw");
		addItemToItemListModel("Sugo", 2.0, 2, Arrays.asList(user));

		window.list("usersList").selectItem(0);
		nameBox.enterText("");
		priceBox.enterText("  ");
		quantityBox.enterText("");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();

		nameBox.enterText("Sugo");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();

		quantityBox.enterText("2");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();
	}

	@Test
	@GUITest
	public void testWhenPriceIsBlanckAndUserIsSelectTheLastThenSaveAndUpdateButtonsAreDisabled() {

		User user = addUserToListUserModel("Pippo", "psw");
		addItemToItemListModel("Sugo", 2.0, 2, Arrays.asList(user));

		nameBox.enterText("");
		priceBox.enterText("  ");
		quantityBox.enterText("");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();

		nameBox.enterText("Sugo");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();

		quantityBox.enterText("2");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();

		window.list("usersList").selectItem(0);
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();
	}

	@Test
	@GUITest
	public void testWhenQuantityIsBlanckAndUserIsSelectFirstThenSaveAndUpdateButtonsAreDisabled() {

		User user = addUserToListUserModel("Pippo", "psw");
		addItemToItemListModel("Sugo", 2.0, 2, Arrays.asList(user));

		window.list("usersList").selectItem(0);
		nameBox.enterText("");
		priceBox.enterText("");
		quantityBox.enterText("  ");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();

		nameBox.enterText("Sugo");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();

		priceBox.enterText("2.2");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();
	}

	@Test
	@GUITest
	public void testWhenQuantityIsBlanckAndUserIsSelectTheLastThenSaveAndUpdateButtonsAreDisabled() {

		User user = addUserToListUserModel("Pippo", "psw");
		addItemToItemListModel("Sugo", 2.0, 2, Arrays.asList(user));

		nameBox.enterText("");
		priceBox.enterText("");
		quantityBox.enterText("  ");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();

		nameBox.enterText("Sugo");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();

		priceBox.enterText("2.2");
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();

		window.list("usersList").selectItem(0);
		window.button(JButtonMatcher.withText("Save")).requireDisabled();
		window.button(JButtonMatcher.withText("Update")).requireDisabled();
	}

	@Test
	@GUITest
	public void testUploadButtonIsDisabledWhenAnItemIsSelectedAndThenNameIsEmpty() {

		User user = addUserToListUserModel("Pippo", "psw");
		addItemToItemListModel("Sugo", 2.0, 2, Arrays.asList(user));

		window.list("itemsList").selectItem(0);
		nameBox.setText("");
		nameBox.enterText("   ");

		window.button(JButtonMatcher.withText("Update")).requireDisabled();
	}

	@Test
	@GUITest
	public void testUploadButtonIsEnabledWhenAnItemIsSelectedAndThenNameIsModified() {

		User user = addUserToListUserModel("Pippo", "psw");
		addItemToItemListModel("Sugo", 2.0, 2, Arrays.asList(user));

		window.list("itemsList").selectItem(0);
		nameBox.setText("");
		nameBox.enterText("Sugo");

		window.button(JButtonMatcher.withText("Update")).requireEnabled();
	}

	@Test
	public void testSaveUpdateAndDeleteButtonsShouldBeEnabledOnlyWhenAnItemIsSelected() {
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));

		addItemToItemListModel("Sugo", 2.0, 2, users);
		window.list("itemsList").selectItem(0);
		JButtonFixture saveButton = window.button(JButtonMatcher.withText("Save"));
		JButtonFixture updateButton = window.button(JButtonMatcher.withText("Update"));
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Delete"));
		saveButton.requireEnabled();
		updateButton.requireEnabled();
		deleteButton.requireEnabled();
	}

	@Test
	public void testShowCurrentItemsListShouldAddItemDescriptionsToTheList() {
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));

		Item item1 = new Item("Sugo", 1., 1, users);
		Item item2 = new Item("Pasta", 2., 1, users);
		GuiActionRunner.execute(() -> receiptSwingView.showCurrentItemsList(Arrays.asList(item1, item2)));
		String[] listContents = window.list("itemsList").contents();
		assertThat(listContents).containsExactly(item1.toString(), item2.toString());
	}

	@Test
	public void testShowCurrentUsersListShouldAddUserDescriptionsToTheList() {
		User user1 = new User("Pippo", "psw");
		User user2 = new User("Pluto", "psw");

		GuiActionRunner.execute(() -> receiptSwingView.showCurrentUsers(Arrays.asList(user1, user2)));
		String[] listContents = window.list("usersList").contents();
		assertThat(listContents).containsExactly(user1.toString(), user2.toString());
	}

	@Test
	public void testShowErrorMessage() {
		GuiActionRunner.execute(() -> receiptSwingView.showError("Item name is null"));
		window.label("errorMsgLabel").requireText("Item name is null");
	}

	@Test
	public void testItemAddedShouldAddTheItemToTheListAndResetTheErrorLabel() {
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));
		Item item = new Item("Sugo", 1., 1, users);

		GuiActionRunner.execute(() -> receiptSwingView.itemAdded(item));
		String[] listContents = window.list("itemsList").contents();
		assertThat(listContents).containsExactly(item.toString());
		window.label("errorMsgLabel").requireText(" ");
	}

	@Test
	public void testItemDeletedShouldDeleteTheItemFromTheListAndResetTheErrorLabel() {
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));
		Item item1 = new Item("Sugo", 1., 1, users);
		Item item2 = new Item("Pasta", 1., 1, users);

		GuiActionRunner.execute(() -> {
			receiptSwingView.getListItemModel().addElement(item1);
			receiptSwingView.getListItemModel().addElement(item2);
		});

		GuiActionRunner.execute(() -> receiptSwingView.itemDeleted(item1));

		String[] listContents = window.list("itemsList").contents();
		assertThat(listContents).containsExactly(item2.toString());
		window.label("errorMsgLabel").requireText(" ");
	}

	@Test
	public void testItemUpdatedShouldUpdateTheItemFromTheListAndResetTheErrorLabel() {
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));
		Item item1 = new Item("Sugo", 1., 1, users);
		Item item2 = new Item("Pasta", 1., 1, users);

		GuiActionRunner.execute(() -> {
			receiptSwingView.getListItemModel().addElement(item1);
		});

		GuiActionRunner.execute(() -> receiptSwingView.itemUpdated(0, item2));

		String[] listContents = window.list("itemsList").contents();
		assertThat(listContents).containsExactly(item2.toString());
		window.label("errorMsgLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testWhenAnItemIsSelectedThenFormIsCompiledWithItsArguments() {
		JButtonFixture saveButton = window.button(JButtonMatcher.withText("Save"));
		JButtonFixture updateButton = window.button(JButtonMatcher.withText("Update"));
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Delete"));
		User user = addUserToListUserModel("Pippo", "psw");
		User user1 = addUserToListUserModel("Pluto", "psw");
		addItemToItemListModel("Sugo", 2.2, 2, Arrays.asList(user, user1));

		window.list("itemsList").selectItem(0);
		nameBox.requireText("Sugo");
		priceBox.requireText("2.2");
		quantityBox.requireText("2");
		assertTrue(receiptSwingView.getUsersList().getSelectedValuesList().containsAll(Arrays.asList(user, user1)));
		saveButton.requireEnabled();
		updateButton.requireEnabled();
		deleteButton.requireEnabled();
		window.list("itemsList").clearSelection();
		saveButton.requireDisabled();
		updateButton.requireDisabled();
		deleteButton.requireDisabled();
	}

	@Test
	public void testAddButtonShouldDelegateToReceiptControllerNewItemAndTheFormIsClear() {
		User user1 = addUserToListUserModel("Pippo", "psw");
		User user2 = addUserToListUserModel("Pluto", "psw");

		nameBox.enterText("Sugo");
		priceBox.enterText("2.0");
		quantityBox.enterText("2");
		window.list("usersList").selectItem(0);
		window.list("usersList").selectItem(1);

		window.button(JButtonMatcher.withText("Save")).click();

		verify(receiptController).addItem(new Item("Sugo", 2.0, 2, Arrays.asList(user1, user2)));
		nameBox.requireText("");
		priceBox.requireText("");
		quantityBox.requireText("");
		window.list("usersList").requireNoSelection();
		window.list("itemsList").requireNoSelection();
	}

	@Test
	public void testUpdateButtonShouldDelegateToReceiptControllerUpdateItem() {
		User user1 = addUserToListUserModel("Pippo", "psw");
		User user2 = addUserToListUserModel("Pluto", "psw");

		addItemToItemListModel("Sugo", 1., 1, Arrays.asList(user1, user2));
		addItemToItemListModel("Pasta", 1., 1, Arrays.asList(user1, user2));

		window.list("itemsList").selectItem(1);

		window.button(JButtonMatcher.withText("Update")).click();
		verify(receiptController).updateItem(new Item("Pasta", 1., 1, Arrays.asList(user1, user2)), 1);
		window.list("usersList").requireNoSelection();
		window.list("itemsList").requireNoSelection();
	}

	@Test
	public void testDeleteButtonShouldDelegateToReceiptControllerDeleteItem() {
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));
		Item item = addItemToItemListModel("Sugo", 1., 1, users);

		window.list("itemsList").selectItem(0);

		window.button(JButtonMatcher.withText("Delete")).click();
		verify(receiptController).deleteItem(item);

	}

	@Test
	public void testSaveReceiptIsEnabledWithAtLeastASaveIsDone() {
		addUserToListUserModel("Pippo", "psw");
		addUserToListUserModel("Pluto", "psw");

		nameBox.enterText("Sugo");
		priceBox.enterText("2.0");
		quantityBox.enterText("2");
		window.list("usersList").selectItem(0);
		window.list("usersList").selectItem(1);
		window.button(JButtonMatcher.withText("Save")).click();

		window.button(JButtonMatcher.withText("Save Receipt")).requireEnabled();
	}

	@Test
	public void testSaveReceiptDelegateToReceiptControllerToSaveReceipt() {
		addUserToListUserModel("Pippo", "psw");
		addUserToListUserModel("Pluto", "psw");

		nameBox.enterText("Sugo");
		priceBox.enterText("2.0");
		quantityBox.enterText("2");
		window.list("usersList").selectItem(0);
		window.list("usersList").selectItem(1);
		window.button(JButtonMatcher.withText("Save")).click();

		window.button(JButtonMatcher.withText("Save Receipt")).click();
		verify(receiptController).saveReceipt();
	}
	
	@Test
	public void testHomeButtonDisposeTheFrameAndNotifyObserver() {
		window.button(JButtonMatcher.withText("Home")).click();
		
		receiptSwingView.goToHome();
		assertThat(receiptSwingView.getFrame().isActive()).isFalse();
		//TODO test call notify observer
	}

}