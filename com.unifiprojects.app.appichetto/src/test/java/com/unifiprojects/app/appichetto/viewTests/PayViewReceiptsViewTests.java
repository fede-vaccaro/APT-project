package com.unifiprojects.app.appichetto.viewTests;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.timing.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.views.CustomToStringReceipt;
import com.unifiprojects.app.appichetto.views.PayViewReceiptsViewSwing;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import org.assertj.swing.core.Settings;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;

@RunWith(GUITestRunner.class)
public class PayViewReceiptsViewTests extends AssertJSwingJUnitTestCase {

	private FrameFixture window;

	private PayViewReceiptsViewSwing payViewReceiptsSwing;

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			MockitoAnnotations.initMocks(this);
			payViewReceiptsSwing = new PayViewReceiptsViewSwing();
			return payViewReceiptsSwing;
		});
		window = new FrameFixture(robot(), payViewReceiptsSwing.getFrame());
		window.show(); // shows the frame to test
	}

	@Test
	@GUITest
	public void testInitialState() {
		window.label(JLabelMatcher.withText("User:"));
		window.label(JLabelMatcher.withText("Receipts:"));
		window.label(JLabelMatcher.withText("Items in receipt:"));
		window.label(JLabelMatcher.withText("Total debt to user:"));
		window.label(JLabelMatcher.withText("Total for this receipt:"));
		window.label("errorMsg");

		window.comboBox("userSelection").requireEnabled();
		window.list("receiptList").requireEnabled();
		window.list("itemList").requireEnabled();
		window.button("payButton").requireDisabled();
	}

	@Test
	@GUITest
	public void showErrorMsgWorksCorreclty() {
		GuiActionRunner.execute(() -> payViewReceiptsSwing.showErrorMsg("Testing error message."));
		window.label("errorMsg").requireText("Testing error message.");
	}

	@Test
	@GUITest
	public void testShowReceiptsVisualizeMessageWhenInputIsNull() {
		GuiActionRunner.execute(() -> payViewReceiptsSwing.showReceipts(null));
		window.label("errorMsg").requireText("You have no accounting.");
	}

	public Receipt generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(User loggedUser, User payerUser,
			GregorianCalendar timestamp, List<Item> itemList) {

		Receipt receipt = new Receipt();

		receipt.setTimestamp(timestamp);
		receipt.setBuyer(payerUser);

		// receipt setup: payerUser bought item1 and item2 but he shares them with
		// logged user...
		if (itemList == null) {
			Item item1 = new Item("Item1", 10., Arrays.asList(loggedUser, payerUser));
			Item item2 = new Item("Item2", 5., Arrays.asList(loggedUser, payerUser));
			itemList = Arrays.asList(item1, item2);
			receipt.setTotalPrice(item1.getPrice() + item2.getPrice());
		}
		receipt.setItems(itemList);
		// assuming each item is owned by buyer and logged user
		Double debtFromLoggedToPayerDouble = itemList.stream().mapToDouble(i -> i.getPrice() / 2.).sum();
		Accounting debtFromLoggedToPayer = new Accounting(loggedUser, debtFromLoggedToPayerDouble);
		debtFromLoggedToPayer.setReceipt(receipt);
		receipt.setAccountingList(Arrays.asList(debtFromLoggedToPayer));
		return receipt;
	}

	@Test
	@GUITest
	public void testShowReceiptsVisualizeTheOnlyReceipt() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");
		Receipt receipt = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 10), null);
		GuiActionRunner.execute(() -> payViewReceiptsSwing.showReceipts(Arrays.asList(receipt)));
		String[] receiptListString = window.list("receiptList").contents();
		assertThat(receiptListString).containsExactlyInAnyOrder(new CustomToStringReceipt(receipt).toString());
	}

	@Test
	@GUITest
	public void testShowReceiptsVisualizeTheThreeReceipts() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");
		Receipt receipt1 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);
		Receipt receipt2 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 2), null);
		Receipt receipt3 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 3), null);
		GuiActionRunner.execute(() -> payViewReceiptsSwing.showReceipts(Arrays.asList(receipt1, receipt2, receipt3)));
		String[] receiptListString = window.list("receiptList").contents();

		assertThat(receiptListString).containsExactlyInAnyOrder(new CustomToStringReceipt(receipt1).toString(),
				new CustomToStringReceipt(receipt2).toString(), new CustomToStringReceipt(receipt3).toString());

	}

	@Test
	@GUITest
	public void testShowReceiptsVisualizeTheBuyerInUserSelection() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");
		Receipt receipt = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);
		GuiActionRunner.execute(() -> payViewReceiptsSwing.showReceipts(Arrays.asList(receipt)));
		String[] userListString = window.comboBox().contents();
		assertThat(userListString).containsExactlyInAnyOrder(payer.toString());
	}

	@Test
	@GUITest
	public void testShowReceiptsDoesntVisualizeMultipleTimeTheSameUser() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");
		Receipt receipt1 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);
		Receipt receipt2 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);
		GuiActionRunner.execute(() -> payViewReceiptsSwing.showReceipts(Arrays.asList(receipt1, receipt2)));
		String[] userListString = window.comboBox().contents();
		assertThat(userListString).containsExactlyInAnyOrder(payer.toString());
	}

	@Test
	@GUITest
	public void testShowReceiptsVisualizeEachUser() {
		User logged = new User("logged", "pw");
		User payer1 = new User("payer1", "pw2");
		User payer2 = new User("payer2", "pw3");
		Receipt receipt1 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer1,
				new GregorianCalendar(2019, 8, 1), null);
		Receipt receipt2 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer2,
				new GregorianCalendar(2019, 8, 2), null);
		Receipt receipt3 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer2,
				new GregorianCalendar(2019, 8, 3), null);
		GuiActionRunner.execute(() -> payViewReceiptsSwing.showReceipts(Arrays.asList(receipt1, receipt2, receipt3)));
		String[] userListString = window.comboBox().contents();
		assertThat(userListString).containsExactlyInAnyOrder(payer1.toString(), payer2.toString());
	}

	@Test
	@GUITest
	public void testShowReceiptsVisualizeOnlyReceiptForTheFirstUser() {
		User logged = new User("logged", "pw");
		User payer1 = new User("payer1", "pw2");
		User payer2 = new User("payer2", "pw3");
		Receipt receipt1 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer1,
				new GregorianCalendar(2019, 8, 1), null);
		Receipt receipt2 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer2,
				new GregorianCalendar(2019, 8, 2), null);
		Receipt receipt3 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer2,
				new GregorianCalendar(2019, 8, 3), null);
		GuiActionRunner.execute(() -> payViewReceiptsSwing.showReceipts(Arrays.asList(receipt3, receipt2, receipt1)));
		String[] receiptListString = window.list("receiptList").contents();
		assertThat(receiptListString).containsExactlyInAnyOrder(new CustomToStringReceipt(receipt3).toString(),
				new CustomToStringReceipt(receipt2).toString());
	}

	@Test
	@GUITest
	public void testSelectingAnotherUserChangeTheDisplayedReceipt() {
		User logged = new User("logged", "pw");
		User payer1 = new User("payer1", "pw2");
		User payer2 = new User("payer2", "pw3");
		Receipt receipt1 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer1,
				new GregorianCalendar(2019, 8, 1), null);
		Receipt receipt2 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer2,
				new GregorianCalendar(2019, 8, 2), null);
		Receipt receipt3 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer2,
				new GregorianCalendar(2019, 8, 3), null);
		GuiActionRunner.execute(() -> payViewReceiptsSwing.showReceipts(Arrays.asList(receipt3, receipt2, receipt1)));
		window.comboBox("userSelection").selectItem("payer1");
		String[] receipt1ListString = window.list("receiptList").contents();
		assertThat(receipt1ListString).containsExactlyInAnyOrder(new CustomToStringReceipt(receipt1).toString());

		window.comboBox("userSelection").selectItem("payer2");
		String[] receipt1And2ListString = window.list("receiptList").contents();
		assertThat(receipt1And2ListString).containsExactlyInAnyOrder(new CustomToStringReceipt(receipt2).toString(),
				new CustomToStringReceipt(receipt3).toString());

	}

	@Test
	@GUITest
	public void testShowItemsFromShowReceiptsVisualizeTheOnlyReceiptItems() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");
		Item item1 = new Item("potatos", 5.0, Arrays.asList(logged, payer));
		Item item2 = new Item("tomatos", 5.0, Arrays.asList(logged, payer));
		Receipt receipt = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), Arrays.asList(item1, item2));
		GuiActionRunner.execute(() -> payViewReceiptsSwing.showReceipts(Arrays.asList(receipt)));
		String[] itemListString = window.list("itemList").contents();
		assertThat(itemListString).containsExactlyInAnyOrder(item1.toString(), item2.toString());
	}

	@Test
	@GUITest
	public void testSelectingAnotherUserChangeTheDisplayedItems() {
		User logged = new User("logged", "pw");

		User payer1 = new User("payer1", "pw2");
		User payer2 = new User("payer2", "pw3");

		Item item1 = new Item("potatos", 5.0, Arrays.asList(logged, payer1));
		Item item2 = new Item("tomatos", 5.0, Arrays.asList(logged, payer2));
		Item item3 = new Item("hamburgers", 5.0, Arrays.asList(logged, payer2));

		Receipt receipt1 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer1,
				new GregorianCalendar(2019, 8, 1), Arrays.asList(item1, item2));
		Receipt receipt2 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer2,
				new GregorianCalendar(2019, 8, 2), Arrays.asList(item2, item3));
		GuiActionRunner.execute(() -> payViewReceiptsSwing.showReceipts(Arrays.asList(receipt2, receipt1)));

		window.comboBox("userSelection").selectItem("payer1");
		String[] itemListStringByPayer1 = window.list("itemList").contents();
		assertThat(itemListStringByPayer1).containsExactlyInAnyOrder(item1.toString(), item2.toString());

		window.comboBox("userSelection").selectItem("payer2");
		String[] itemListStringByPayer2 = window.list("itemList").contents();
		assertThat(itemListStringByPayer2).containsExactlyInAnyOrder(item2.toString(), item3.toString());
		
		Pause.pause(20000);
	}

	@Test
	@GUITest
	public void testSelectingReceiptChangeItemsDisplayed() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");

		Item item1 = new Item("potatos", 5.0, Arrays.asList(logged, payer));
		Item item2 = new Item("tomatos", 5.0, Arrays.asList(logged, payer));
		Item item3 = new Item("hamburgers", 5.0, Arrays.asList(logged, payer));

		Receipt receipt1 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), Arrays.asList(item1, item2));
		Receipt receipt2 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), Arrays.asList(item2, item3));

		GuiActionRunner.execute(() -> payViewReceiptsSwing.showReceipts(Arrays.asList(receipt2, receipt1)));

		window.list("receiptList").selectItem(0);
		String[] itemListStringReceipt2 = window.list("itemList").contents();
		assertThat(itemListStringReceipt2).containsExactlyInAnyOrder(item3.toString(), item2.toString());

		window.list("receiptList").selectItem(1);
		String[] itemListStringReceipt1 = window.list("itemList").contents();
		assertThat(itemListStringReceipt1).containsExactlyInAnyOrder(item1.toString(), item2.toString());

		String[] receiptList = window.list("receiptList").contents();
		assertThat(receiptList).containsExactlyInAnyOrder(new CustomToStringReceipt(receipt2).toString(),
				new CustomToStringReceipt(receipt1).toString());

	}
	
	// TODO: testing the Pay button and the amount input

}
