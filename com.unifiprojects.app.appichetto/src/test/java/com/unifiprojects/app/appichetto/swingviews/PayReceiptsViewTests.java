package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.swing.annotation.GUITest;
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

import com.unifiprojects.app.appichetto.controllers.PayReceiptsController;
import com.unifiprojects.app.appichetto.controllers.ReceiptGenerator;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.swingviews.utils.CustomToStringReceipt;

@RunWith(GUITestRunner.class)
public class PayReceiptsViewTests extends AssertJSwingJUnitTestCase {

	private FrameFixture window;

	@Mock
	private PayReceiptsController payReceiptsController;

	@InjectMocks
	private PayReceiptsViewSwing payReceiptsSwing;

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			MockitoAnnotations.initMocks(this);
			payReceiptsSwing = new PayReceiptsViewSwing(payReceiptsController);
			return payReceiptsSwing;
		});
		window = new FrameFixture(robot(), payReceiptsSwing.getFrame());
		window.show(); // shows the frame to test
	}

	@Test
	@GUITest
	public void testInitialState() {
		window.label(JLabelMatcher.withText("User:"));
		window.label(JLabelMatcher.withText("Receipts:"));
		window.label(JLabelMatcher.withText("Items in receipt:"));
		window.label(JLabelMatcher.withText("Total debt to user: "));
		window.label(JLabelMatcher.withText("Total for this receipt: "));
		window.label("errorMsg");

		window.comboBox("userSelection").requireEnabled();
		window.list("receiptList").requireEnabled();
		window.list("itemList").requireEnabled();
		window.button("payButton").requireDisabled();
	}

	@Test
	@GUITest
	public void showErrorMsgWorksCorreclty() {
		GuiActionRunner.execute(() -> payReceiptsSwing.showErrorMsg("Testing error message."));
		window.label("errorMsg").requireText("Testing error message.");
	}

	@Test
	@GUITest
	public void testShowReceiptsVisualizeMessageWhenInputIsEmpty() {
		GuiActionRunner.execute(() -> payReceiptsSwing.showReceipts(Arrays.asList()));
		window.label("errorMsg").requireText("You have no accountings.");
	}

	@Test
	@GUITest
	public void testShowReceiptsVisualizeTheOnlyReceipt() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");
		Receipt receipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 10), null);
		GuiActionRunner.execute(() -> payReceiptsSwing.showReceipts(Arrays.asList(receipt)));
		String[] receiptListString = window.list("receiptList").contents();
		assertThat(receiptListString).containsExactlyInAnyOrder(new CustomToStringReceipt(receipt).toString());
	}

	@Test
	@GUITest
	public void testShowReceiptsVisualizeTheThreeReceipts() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");
		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 2), null);
		Receipt receipt3 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 3), null);
		GuiActionRunner.execute(() -> payReceiptsSwing.showReceipts(Arrays.asList(receipt1, receipt2, receipt3)));
		String[] receiptListString = window.list("receiptList").contents();

		assertThat(receiptListString).containsExactlyInAnyOrder(new CustomToStringReceipt(receipt1).toString(),
				new CustomToStringReceipt(receipt2).toString(), new CustomToStringReceipt(receipt3).toString());

	}

	@Test
	@GUITest
	public void testShowReceiptsVisualizeTheBuyerInUserSelection() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");
		Receipt receipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);
		GuiActionRunner.execute(() -> payReceiptsSwing.showReceipts(Arrays.asList(receipt)));
		String[] userListString = window.comboBox().contents();
		assertThat(userListString).containsExactlyInAnyOrder(payer.toString());
	}

	@Test
	@GUITest
	public void testShowReceiptsDoesntVisualizeMultipleTimeTheSameUser() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");
		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);
		GuiActionRunner.execute(() -> payReceiptsSwing.showReceipts(Arrays.asList(receipt1, receipt2)));
		String[] userListString = window.comboBox().contents();
		assertThat(userListString).containsExactlyInAnyOrder(payer.toString());
	}

	@Test
	@GUITest
	public void testShowReceiptsVisualizeEachUser() {
		User logged = new User("logged", "pw");
		User payer1 = new User("payer1", "pw2");
		User payer2 = new User("payer2", "pw3");
		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer1,
				new GregorianCalendar(2019, 8, 1), null);
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer2,
				new GregorianCalendar(2019, 8, 2), null);
		Receipt receipt3 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer2,
				new GregorianCalendar(2019, 8, 3), null);
		GuiActionRunner.execute(() -> payReceiptsSwing.showReceipts(Arrays.asList(receipt1, receipt2, receipt3)));
		String[] userListString = window.comboBox().contents();
		assertThat(userListString).containsExactlyInAnyOrder(payer1.toString(), payer2.toString());
	}

	@Test
	@GUITest
	public void testShowReceiptsVisualizeOnlyReceiptForTheFirstUser() {
		User logged = new User("logged", "pw");
		User payer1 = new User("payer1", "pw2");
		User payer2 = new User("payer2", "pw3");
		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer1,
				new GregorianCalendar(2019, 8, 1), null);
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer2,
				new GregorianCalendar(2019, 8, 2), null);
		Receipt receipt3 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer2,
				new GregorianCalendar(2019, 8, 3), null);
		GuiActionRunner.execute(() -> payReceiptsSwing.showReceipts(Arrays.asList(receipt3, receipt2, receipt1)));
		String[] receiptListString = window.list("receiptList").contents();
		assertThat(receiptListString).containsExactlyInAnyOrder(new CustomToStringReceipt(receipt3).toString(),
				new CustomToStringReceipt(receipt2).toString());
	}

	@Test
	@GUITest
	public void testSelectingAnotherUserChangeTheDisplayedReceipts() {
		User logged = new User("logged", "pw");
		User payer1 = new User("payer1", "pw2");
		User payer2 = new User("payer2", "pw3");
		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer1,
				new GregorianCalendar(2019, 8, 1), null);
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer2,
				new GregorianCalendar(2019, 8, 2), null);
		Receipt receipt3 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer2,
				new GregorianCalendar(2019, 8, 3), null);

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2, receipt3));

			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt1));
			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt2));
			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt3));

			payReceiptsSwing.getUserComboBoxModel().addElement(payer1);
			payReceiptsSwing.getUserComboBoxModel().addElement(payer2);
		});

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
		Receipt receipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), Arrays.asList(item1, item2));
		GuiActionRunner.execute(() -> payReceiptsSwing.showReceipts(Arrays.asList(receipt)));
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

		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer1,
				new GregorianCalendar(2019, 8, 1), Arrays.asList(item1, item2));
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer2,
				new GregorianCalendar(2019, 8, 2), Arrays.asList(item2, item3));
		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));

			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt1));
			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt2));

			payReceiptsSwing.getUserComboBoxModel().addElement(payer1);
			payReceiptsSwing.getUserComboBoxModel().addElement(payer2);
		});
		window.comboBox("userSelection").selectItem("payer1");
		String[] itemListStringByPayer1 = window.list("itemList").contents();
		assertThat(itemListStringByPayer1).containsExactlyInAnyOrder(item1.toString(), item2.toString());

		window.comboBox("userSelection").selectItem("payer2");
		String[] itemListStringByPayer2 = window.list("itemList").contents();
		assertThat(itemListStringByPayer2).containsExactlyInAnyOrder(item2.toString(), item3.toString());
	}

	@Test
	@GUITest
	public void testSelectingReceiptChangeItemsDisplayed() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");

		Item item1 = new Item("potatos", 5.0, Arrays.asList(logged, payer));
		Item item2 = new Item("tomatos", 5.0, Arrays.asList(logged, payer));
		Item item3 = new Item("hamburgers", 5.0, Arrays.asList(logged, payer));

		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), Arrays.asList(item1, item2));
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 2), Arrays.asList(item2, item3));

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));

			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt1));
			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt2));

			payReceiptsSwing.getUserComboBoxModel().addElement(payer);
		});

		window.list("receiptList").selectItem(new CustomToStringReceipt(receipt2).toString());
		String[] itemListStringReceipt2 = window.list("itemList").contents();
		assertThat(itemListStringReceipt2).containsExactlyInAnyOrder(item3.toString(), item2.toString());

		window.list("receiptList").selectItem(new CustomToStringReceipt(receipt1).toString());
		String[] itemListStringReceipt1 = window.list("itemList").contents();
		assertThat(itemListStringReceipt1).containsExactlyInAnyOrder(item1.toString(), item2.toString());

		String[] receiptList = window.list("receiptList").contents();
		assertThat(receiptList).containsExactlyInAnyOrder(new CustomToStringReceipt(receipt2).toString(),
				new CustomToStringReceipt(receipt1).toString());
	}

	@Test
	@GUITest
	public void testSelectedReceiptDisplayTheTotalAmount() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");
		Receipt receipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 10), null);
		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt));

			payReceiptsSwing.getUserComboBoxModel().addElement(payer);

			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt));

		});

		window.label("totalForSelectedReceipt")
				.requireText(String.format("Total for this receipt: %.2f", receipt.getTotalPrice()));
	}

	@Test
	@GUITest
	public void testSelectingAnotherReceiptDisplayTheTotalAmountAccordingly() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");

		Item item1 = new Item("potatos", 5.0, Arrays.asList(logged, payer));
		Item item2 = new Item("tomatos", 2.0, Arrays.asList(logged, payer));
		Item item3 = new Item("hamburgers", 5.0, Arrays.asList(logged, payer));

		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), Arrays.asList(item1, item2));
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 2), Arrays.asList(item2, item3));

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));

			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt1));
			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt2));

			payReceiptsSwing.getUserComboBoxModel().addElement(payer);
		});
		window.list("receiptList").selectItem(new CustomToStringReceipt(receipt2).toString());
		window.label("totalForSelectedReceipt")
				.requireText(String.format("Total for this receipt: %.2f", receipt2.getTotalPrice()));

		window.list("receiptList").selectItem(new CustomToStringReceipt(receipt1).toString());
		window.label("totalForSelectedReceipt")
				.requireText(String.format("Total for this receipt: %.2f", receipt1.getTotalPrice()));
	}

	@Test
	@GUITest
	public void callingShowReceiptWithEmptyListArgumentAfterHavingCalledShowReceiptWithNotNullArgumentShouldClearTheLists() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");

		Item item1 = new Item("potatos", 5.0, Arrays.asList(logged, payer));
		Item item2 = new Item("tomatos", 2.0, Arrays.asList(logged, payer));
		Item item3 = new Item("hamburgers", 5.0, Arrays.asList(logged, payer));

		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), Arrays.asList(item1, item2));
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), Arrays.asList(item2, item3));

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.showReceipts(Arrays.asList(receipt2, receipt1));
			payReceiptsSwing.showReceipts(Arrays.asList());
		});

		window.label("errorMsg").requireText("You have no accountings.");

		String[] receiptStringList = window.list("receiptList").contents();
		String[] itemStringList = window.list("itemList").contents();
		String[] userComboBoxStringList = window.comboBox("userSelection").contents();

		assertThat(receiptStringList).isEmpty();
		assertThat(itemStringList).isEmpty();
		assertThat(userComboBoxStringList).isEmpty();
	}

	@Test
	@GUITest
	public void ComputedTotalDebtToUserIsCorrect() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");

		Item item1 = new Item("potatos", 5.0, Arrays.asList(logged, payer));
		Item item2 = new Item("tomatos", 2.0, Arrays.asList(logged, payer));
		Item item3 = new Item("hamburgers", 10.0, Arrays.asList(logged, payer));

		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), Arrays.asList(item1, item2));
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 2), Arrays.asList(item2, item3));

		payReceiptsSwing.setLoggedUser(logged);

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));
			List<Accounting> accountings = new ArrayList<>();

			List<Receipt> unpaids = payReceiptsSwing.getUnpaids();
			for (Receipt r : unpaids) {
				accountings.addAll(r.getAccountings());
			}
			accountings = accountings.stream().filter(a -> a.getUser().equals(logged)).collect(Collectors.toList());
			payReceiptsSwing.setAccountings(accountings);

			payReceiptsSwing.getUserComboBoxModel().addElement(payer);
		});
		double debt = receipt1.getTotalPrice() / 2.0 + receipt2.getTotalPrice() / 2.0;
		window.label("totalDebtToUser").requireText(String.format("Total debt to user: %.2f", debt));

	}

	@Test
	@GUITest
	public void ComputedTotalDebtToUserIsCorrectAfterCallingShowReceiptsWithOnlyOnePayer() {
		User logged = new User("logged", "pw");

		User payer1 = new User("payer1", "pw2");

		Item item1 = new Item("potatos", 2.0, Arrays.asList(logged, payer1));
		Item item2 = new Item("hamburgers", 4.0, Arrays.asList(logged, payer1));
		Item item3 = new Item("bread", 4.0, Arrays.asList(logged, payer1));

		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer1,
				new GregorianCalendar(2019, 8, 1), Arrays.asList(item1, item2));
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer1,
				new GregorianCalendar(2019, 8, 2), Arrays.asList(item2, item3));

		double debtToPayer1 = (receipt1.getTotalPrice() + receipt2.getTotalPrice()) / 2.0;

		payReceiptsSwing.setLoggedUser(logged);

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.showReceipts(Arrays.asList(receipt1, receipt2));
		});
		window.label("totalDebtToUser").requireText(String.format("Total debt to user: %.2f", debtToPayer1));
	}

	@Test
	@GUITest
	public void ComputedTotalDebtToUserIsCorrectAfterCallingShowReceiptsAndChangingUser() {
		User logged = new User("logged", "pw");

		User payer1 = new User("payer1", "pw2");
		User payer2 = new User("payer2", "pw3");

		Item item1 = new Item("potatos", 2.0, Arrays.asList(logged, payer1));
		Item item2 = new Item("tomatos", 3.0, Arrays.asList(logged, payer2));
		Item item3 = new Item("hamburgers", 4.0, Arrays.asList(logged, payer2));

		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer1,
				new GregorianCalendar(2019, 8, 1), Arrays.asList(item1, item2));
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer2,
				new GregorianCalendar(2019, 8, 2), Arrays.asList(item2, item3));

		double debtToPayer1 = receipt1.getTotalPrice() / 2.0;
		double debtToPayer2 = receipt2.getTotalPrice() / 2.0;

		payReceiptsSwing.setLoggedUser(logged);

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.showReceipts(Arrays.asList(receipt1, receipt2));
		});

		window.comboBox("userSelection").selectItem("payer1");
		window.label("totalDebtToUser").requireText(String.format("Total debt to user: %.2f", debtToPayer1));

		window.comboBox("userSelection").selectItem("payer2");
		window.label("totalDebtToUser").requireText(String.format("Total debt to user: %.2f", debtToPayer2));

	}

	@Test
	@GUITest
	public void enterAmountFieldShowErrorIfNonDoubleAreWritten() {
		window.textBox("enterAmountField").deleteText();
		window.textBox("enterAmountField").enterText("this is a not-numeric text");
		window.label("errorMsg").requireText("Not valid entered amount.");
	}

	@Test
	@GUITest
	public void errorMsgDisappearIfEnteredAmountIsEmptied() {
		window.textBox("enterAmountField").enterText("not num");
		window.textBox("enterAmountField").deleteText();
		window.label("errorMsg").requireText("");
	}

	@Test
	@GUITest
	public void intValueIsCorrectlyParsedWhenEnteredInTextBox() {
		window.textBox("enterAmountField").enterText("42");
		double enteredValue = payReceiptsSwing.getEnteredValue();
		assertThat(enteredValue).isEqualTo(42.0);
		window.label("errorMsg").requireText("");
	}

	@Test
	@GUITest
	public void doubleValueIsCorrectlyParsedWhenEnteredInTextBox() {
		window.textBox("enterAmountField").enterText("42.75");
		double enteredValue = payReceiptsSwing.getEnteredValue();
		assertThat(enteredValue).isEqualTo(42.75);
		window.label("errorMsg").requireText("");
	}

	@Test
	@GUITest
	public void buttonIsUnlockedIfThereIsAnAvailableUserAndSomeReceiptAndAnAmountLesserThanTotal() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");
		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));
			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt1));
			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt2));
			payReceiptsSwing.getUserComboBoxModel().addElement(payer);

			payReceiptsSwing.setAccountings(new ArrayList<>());

			payReceiptsSwing.getAccountings().add(receipt1.getAccountings().get(0));
			payReceiptsSwing.getAccountings().add(receipt2.getAccountings().get(0));
		});

		payReceiptsSwing.getUserComboBoxModel().setSelectedItem(payer);

		Double totalDebt = (receipt1.getTotalPrice() + receipt2.getTotalPrice()) / 2.0 - 2.0;

		window.textBox("enterAmountField").enterText(totalDebt.toString());

		window.button("payButton").requireEnabled();

	}

	@Test
	@GUITest
	public void buttonIsLockedIfThereIsAnAvailableUserAndSomeReceiptAndAnAmountHigherThanTotal() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");
		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));
			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt1));
			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt2));
			payReceiptsSwing.getUserComboBoxModel().addElement(payer);

			payReceiptsSwing.setAccountings(new ArrayList<>());

			payReceiptsSwing.getAccountings().add(receipt1.getAccountings().get(0));
			payReceiptsSwing.getAccountings().add(receipt2.getAccountings().get(0));

		});

		payReceiptsSwing.getUserComboBoxModel().setSelectedItem(payer);

		Double total = receipt1.getTotalPrice() + receipt2.getTotalPrice() + 10.;

		window.textBox("enterAmountField").enterText(total.toString());

		window.button("payButton").requireDisabled();

	}

	@Test
	@GUITest
	public void buttonIsUnlockedIfThereIsAnAvailableUserAndSomeReceiptAndAnAmountEqualToTotal() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");
		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));
			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt1));
			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt2));
			payReceiptsSwing.getUserComboBoxModel().addElement(payer);

			payReceiptsSwing.setAccountings(new ArrayList<>());

			payReceiptsSwing.getAccountings().add(receipt1.getAccountings().get(0));
			payReceiptsSwing.getAccountings().add(receipt2.getAccountings().get(0));

		});

		payReceiptsSwing.getUserComboBoxModel().setSelectedItem(payer);

		Double totalDebt = (receipt1.getTotalPrice() + receipt2.getTotalPrice()) / 2.0;

		window.textBox("enterAmountField").enterText(totalDebt.toString());

		window.button("payButton").requireEnabled();

	}

	@Test
	@GUITest
	public void buttonGetLockedFromUnlockedIfTheUserListIsCleared() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");
		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));
			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt1));
			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt2));
			payReceiptsSwing.getUserComboBoxModel().addElement(payer);

			payReceiptsSwing.setAccountings(new ArrayList<>());

			payReceiptsSwing.getAccountings().add(receipt1.getAccountings().get(0));
			payReceiptsSwing.getAccountings().add(receipt2.getAccountings().get(0));

		});

		payReceiptsSwing.getUserComboBoxModel().setSelectedItem(payer);

		Double totalDebt = (receipt1.getTotalPrice() + receipt2.getTotalPrice()) / 2.0;

		window.textBox("enterAmountField").enterText(totalDebt.toString());

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.getUserComboBoxModel().removeAllElements();
			payReceiptsSwing.getReceiptListModel().clear();
			payReceiptsSwing.getAccountings().clear();
		});
		window.button("payButton").requireDisabled();

	}

	@Test
	@GUITest
	public void payButtonClickedDelegateToController() {
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");
		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));
			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt1));
			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt2));
			payReceiptsSwing.getUserComboBoxModel().addElement(payer);

			payReceiptsSwing.setAccountings(new ArrayList<>());

			payReceiptsSwing.getAccountings().add(receipt1.getAccountings().get(0));
			payReceiptsSwing.getAccountings().add(receipt2.getAccountings().get(0));

			payReceiptsSwing.setLoggedUser(logged);
		});

		payReceiptsSwing.getUserComboBoxModel().setSelectedItem(payer);

		Double totalDebt = (receipt1.getTotalPrice() + receipt2.getTotalPrice()) / 2.0;

		window.textBox("enterAmountField").enterText(totalDebt.toString());

		window.button("payButton").click();

		verify(payReceiptsController).payAmount(totalDebt, logged, payer);

	}

	@Test
	@GUITest
	public void payButtonClickedEmptyTheText() {
		
		User logged = new User("logged", "pw");
		User payer = new User("payer", "pw2");
		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer,
				new GregorianCalendar(2019, 8, 1), null);

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));
			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt1));
			payReceiptsSwing.getReceiptListModel().addElement(new CustomToStringReceipt(receipt2));
			payReceiptsSwing.getUserComboBoxModel().addElement(payer);

			payReceiptsSwing.setAccountings(new ArrayList<>());

			payReceiptsSwing.getAccountings().add(receipt1.getAccountings().get(0));
			payReceiptsSwing.getAccountings().add(receipt2.getAccountings().get(0));

			payReceiptsSwing.setLoggedUser(logged);
		});

		payReceiptsSwing.getUserComboBoxModel().setSelectedItem(payer);

		Double totalDebt = (receipt1.getTotalPrice() + receipt2.getTotalPrice()) / 2.0;

		window.textBox("enterAmountField").enterText(totalDebt.toString());

		window.button("payButton").click();

		window.textBox("enterAmountField").requireText("");

	}

}