package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.unifiprojects.app.appichetto.swingviews.utils.ReceiptFormatter;

@RunWith(GUITestRunner.class)
public class PayReceiptsViewTests extends AssertJSwingJUnitTestCase {

	private FrameFixture window;

	@Mock
	private PayReceiptsController payReceiptsController;

	@InjectMocks
	private PayReceiptsViewSwing payReceiptsSwing;

	private User logged;

	private User payer1;

	private User payer2;

	private Item item1;

	private Item item2;

	private Item item3;

	private Receipt receipt1FromPayer1;

	private Receipt receipt1FromPayer2;

	private Receipt receipt2FromPayer1;

	private Receipt receipt3FromPayer1;

	private Receipt receipt2FromPayer2;

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			MockitoAnnotations.initMocks(this);
			payReceiptsSwing = new PayReceiptsViewSwing(payReceiptsController);
			return payReceiptsSwing;
		});
		
		logged = new User("logged", "pw");
		when(payReceiptsController.getLoggedUser()).thenReturn(logged);
		
		window = new FrameFixture(robot(), payReceiptsSwing.getFrame());
		window.show(); // shows the frame to test
	}

	private void setUpUsersAndReceipts() {

		payer1 = new User("payer1", "pw2");
		payer2 = new User("payer2", "pw3");

		item1 = new Item("potatos", 5.0, Arrays.asList(logged, payer1));
		item2 = new Item("tomatos", 5.0, Arrays.asList(logged, payer2));
		item3 = new Item("hamburgers", 5.0, Arrays.asList(logged, payer2));

		receipt1FromPayer1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer1,
				new GregorianCalendar(2019, 8, 1), Arrays.asList(item1, item2));
		receipt2FromPayer1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer1,
				new GregorianCalendar(2019, 8, 2), Arrays.asList(item2, item3));
		receipt3FromPayer1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer1,
				new GregorianCalendar(2019, 8, 3));
		receipt1FromPayer2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer2,
				new GregorianCalendar(2019, 8, 4), Arrays.asList(item2, item3));
		receipt2FromPayer2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(logged, payer2,
				new GregorianCalendar(2019, 8, 5), Arrays.asList(item2, item3));

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
		setUpUsersAndReceipts();
		Receipt receipt1 = receipt1FromPayer1;
		
		GuiActionRunner.execute(() -> payReceiptsSwing.showReceipts(Arrays.asList(receipt1)));
		String[] receiptListString = window.list("receiptList").contents();
		assertThat(receiptListString)
				.containsExactlyInAnyOrder(ReceiptFormatter.format(receipt1));
	}

	@Test
	@GUITest
	public void testShowReceiptsVisualizeTheThreeReceipts() {
		setUpUsersAndReceipts();

		Receipt receipt1 = receipt1FromPayer1;
		Receipt receipt2 = receipt2FromPayer1;
		Receipt receipt3 = receipt3FromPayer1;
		GuiActionRunner.execute(() -> payReceiptsSwing.showReceipts(Arrays.asList(receipt1, receipt2, receipt3)));
		String[] receiptListString = window.list("receiptList").contents();

		assertThat(receiptListString).containsExactlyInAnyOrder(ReceiptFormatter.format(receipt1),
				ReceiptFormatter.format(receipt2), ReceiptFormatter.format(receipt3));

	}

	@Test
	@GUITest
	public void testShowReceiptsVisualizeTheBuyerInUserSelection() {
		setUpUsersAndReceipts();

		Receipt receipt = receipt1FromPayer1;
		GuiActionRunner.execute(() -> payReceiptsSwing.showReceipts(Arrays.asList(receipt)));
		String[] userListString = window.comboBox().contents();
		assertThat(userListString).containsExactlyInAnyOrder(payer1.toString());
	}

	@Test
	@GUITest
	public void testShowReceiptsDoesntVisualizeMultipleTimeTheSameUser() {
		setUpUsersAndReceipts();
		Receipt receipt1 = receipt1FromPayer1;
		Receipt receipt2 = receipt2FromPayer1;

		GuiActionRunner.execute(() -> payReceiptsSwing.showReceipts(Arrays.asList(receipt1, receipt2)));
		String[] userListString = window.comboBox().contents();
		assertThat(userListString).containsExactlyInAnyOrder(payer1.toString());
	}

	@Test
	@GUITest
	public void testShowReceiptsVisualizeEachUser() {
		setUpUsersAndReceipts();

		Receipt receipt1 = receipt1FromPayer1;
		Receipt receipt2 = receipt2FromPayer1;
		Receipt receipt3 = receipt1FromPayer2;

		GuiActionRunner.execute(() -> payReceiptsSwing.showReceipts(Arrays.asList(receipt1, receipt2, receipt3)));
		String[] userListString = window.comboBox().contents();
		assertThat(userListString).containsExactlyInAnyOrder(payer1.toString(), payer2.toString());
	}

	@Test
	@GUITest
	public void testShowReceiptsVisualizeOnlyReceiptFromOnePayerAndNotFromTheOther() {
		setUpUsersAndReceipts();
		Receipt receipt1 = receipt1FromPayer2;
		Receipt receipt2 = receipt2FromPayer2;
		Receipt receipt3 = receipt1FromPayer1;

		GuiActionRunner.execute(() -> payReceiptsSwing.showReceipts(Arrays.asList(receipt3, receipt2, receipt1)));

		String[] receiptListString = window.list("receiptList").contents();
		assertThat(receiptListString)
				.containsExactlyInAnyOrder(ReceiptFormatter.format(receipt1),
						ReceiptFormatter.format(receipt2))
				.doesNotContain(ReceiptFormatter.format(receipt3));

	}

	@Test
	@GUITest
	public void testSelectingAnotherUserChangeTheDisplayedReceipts() {
		setUpUsersAndReceipts();
		Receipt receipt1 = receipt1FromPayer1;
		Receipt receipt2 = receipt1FromPayer2;
		Receipt receipt3 = receipt2FromPayer2;

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2, receipt3));

			payReceiptsSwing.receiptListModel.addElement(receipt1);
			payReceiptsSwing.receiptListModel.addElement(receipt2);
			payReceiptsSwing.receiptListModel.addElement(receipt3);

			payReceiptsSwing.userComboBoxModel.addElement(payer1);
			payReceiptsSwing.userComboBoxModel.addElement(payer2);
		});

		window.comboBox("userSelection").selectItem("payer1");
		String[] receipt1ListString = window.list("receiptList").contents();
		assertThat(receipt1ListString).containsExactlyInAnyOrder(ReceiptFormatter.format(receipt1));

		window.comboBox("userSelection").selectItem("payer2");
		String[] receipt1And2ListString = window.list("receiptList").contents();
		assertThat(receipt1And2ListString).containsExactlyInAnyOrder(ReceiptFormatter.format(receipt2),
				ReceiptFormatter.format(receipt3));

	}

	@Test
	@GUITest
	public void testShowItemsFromShowReceiptsVisualizeTheOnlyReceiptItems() {
		setUpUsersAndReceipts();
		Receipt receipt = receipt1FromPayer1;

		GuiActionRunner.execute(() -> payReceiptsSwing.showReceipts(Arrays.asList(receipt)));

		String[] itemListString = window.list("itemList").contents();
		assertThat(itemListString).containsExactlyInAnyOrder(item1.toString(), item2.toString());
	}

	@Test
	@GUITest
	public void testSelectingAnotherUserChangeTheDisplayedItems() {
		setUpUsersAndReceipts();

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1FromPayer1, receipt1FromPayer2));

			payReceiptsSwing.receiptListModel.addElement(receipt1FromPayer1);
			payReceiptsSwing.receiptListModel.addElement(receipt1FromPayer2);

			payReceiptsSwing.userComboBoxModel.addElement(payer1);
			payReceiptsSwing.userComboBoxModel.addElement(payer2);
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
		setUpUsersAndReceipts();
		Receipt receipt1 = receipt1FromPayer1;
		Receipt receipt2 = receipt2FromPayer1;

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));

			payReceiptsSwing.receiptListModel.addElement(receipt1);
			payReceiptsSwing.receiptListModel.addElement(receipt2);

			payReceiptsSwing.userComboBoxModel.addElement(payer1);
		});

		window.list("receiptList").selectItem(ReceiptFormatter.format(receipt2));
		String[] itemListStringReceipt2 = window.list("itemList").contents();
		assertThat(itemListStringReceipt2).containsExactlyInAnyOrder(item3.toString(), item2.toString());

		window.list("receiptList").selectItem(ReceiptFormatter.format(receipt1));
		String[] itemListStringReceipt1 = window.list("itemList").contents();
		assertThat(itemListStringReceipt1).containsExactlyInAnyOrder(item1.toString(), item2.toString());

		String[] receiptList = window.list("receiptList").contents();
		assertThat(receiptList).containsExactlyInAnyOrder(ReceiptFormatter.format(receipt2),
				ReceiptFormatter.format(receipt1));
	}

	@Test
	@GUITest
	public void testSelectedReceiptDisplayTheTotalAmount() {
		setUpUsersAndReceipts();
		Receipt receipt = receipt1FromPayer1;

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt));

			payReceiptsSwing.userComboBoxModel.addElement(payer1);

			payReceiptsSwing.receiptListModel.addElement(receipt);

		});

		window.label("totalForSelectedReceipt")
				.requireText(String.format("Total for this receipt: %.2f", receipt.getTotalPrice()));
	}

	@Test
	@GUITest
	public void testSelectingAnotherReceiptDisplayTheTotalAmountAccordingly() {
		setUpUsersAndReceipts();
		Receipt receipt1 = receipt1FromPayer1;
		Receipt receipt2 = receipt2FromPayer1;

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));

			payReceiptsSwing.receiptListModel.addElement(receipt1);
			payReceiptsSwing.receiptListModel.addElement(receipt2);

			payReceiptsSwing.userComboBoxModel.addElement(payer1);
		});
		window.list("receiptList").selectItem(ReceiptFormatter.format(receipt2));
		window.label("totalForSelectedReceipt")
				.requireText(String.format("Total for this receipt: %.2f", receipt2.getTotalPrice()));

		window.list("receiptList").selectItem(ReceiptFormatter.format(receipt1));
		window.label("totalForSelectedReceipt")
				.requireText(String.format("Total for this receipt: %.2f", receipt1.getTotalPrice()));
	}

	@Test
	@GUITest
	public void callingShowReceiptWithEmptyListArgumentAfterHavingCalledShowReceiptWithNotNullArgumentShouldClearTheLists() {
		setUpUsersAndReceipts();
		Receipt receipt1 = receipt1FromPayer1;
		Receipt receipt2 = receipt2FromPayer1;

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
		setUpUsersAndReceipts();
		Receipt receipt1 = receipt1FromPayer1;
		Receipt receipt2 = receipt2FromPayer1;

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));
			List<Accounting> accountings = new ArrayList<>();

			List<Receipt> unpaids = payReceiptsSwing.getUnpaids();
			for (Receipt r : unpaids) {
				accountings.addAll(r.getAccountings());
			}
			accountings = accountings.stream().filter(a -> a.getUser().equals(logged)).collect(Collectors.toList());
			payReceiptsSwing.setAccountings(accountings);

			payReceiptsSwing.userComboBoxModel.addElement(payer1);
		});
		double debt = receipt1.getTotalPrice() / 2.0 + receipt2.getTotalPrice() / 2.0;
		window.label("totalDebtToUser").requireText(String.format("Total debt to user: %.2f", debt));

	}

	@Test
	@GUITest
	public void ComputedTotalDebtToUserIsCorrectAfterCallingShowReceiptsWithOnlyOnePayer() {
		setUpUsersAndReceipts();
		Receipt receipt1 = receipt1FromPayer1;
		Receipt receipt2 = receipt2FromPayer1;

		double debtToPayer1 = (receipt1.getTotalPrice() + receipt2.getTotalPrice()) / 2.0;

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.showReceipts(Arrays.asList(receipt1, receipt2));
		});
		window.label("totalDebtToUser").requireText(String.format("Total debt to user: %.2f", debtToPayer1));
	}

	@Test
	@GUITest
	public void ComputedTotalDebtToUserIsCorrectAfterCallingShowReceiptsAndChangingUser() {
		setUpUsersAndReceipts();
		Receipt receipt1 = receipt1FromPayer1;
		Receipt receipt2 = receipt1FromPayer2;

		double debtToPayer1 = receipt1.getTotalPrice() / 2.0;
		double debtToPayer2 = receipt2.getTotalPrice() / 2.0;

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
		window.textBox("enterAmountField").enterText("not num");
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
		setUpUsersAndReceipts();
		Receipt receipt1 = receipt1FromPayer1;
		Receipt receipt2 = receipt2FromPayer1;

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));
			payReceiptsSwing.receiptListModel.addElement(receipt1);
			payReceiptsSwing.receiptListModel.addElement(receipt2);
			payReceiptsSwing.userComboBoxModel.addElement(payer1);

			payReceiptsSwing.setAccountings(new ArrayList<>());

			payReceiptsSwing.getAccountings().add(receipt1.getAccountings().get(0));
			payReceiptsSwing.getAccountings().add(receipt2.getAccountings().get(0));
		});

		payReceiptsSwing.userComboBoxModel.setSelectedItem(payer1);

		Double totalDebt = (receipt1.getTotalPrice() + receipt2.getTotalPrice()) / 2.0 - 2.0;

		window.textBox("enterAmountField").enterText(totalDebt.toString());

		window.button("payButton").requireEnabled();

	}

	@Test
	@GUITest
	public void buttonIsLockedIfThereIsAnAvailableUserAndSomeReceiptAndAnAmountHigherThanTotal() {
		setUpUsersAndReceipts();
		Receipt receipt1 = receipt1FromPayer1;
		Receipt receipt2 = receipt2FromPayer1;

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));
			payReceiptsSwing.receiptListModel.addElement(receipt1);
			payReceiptsSwing.receiptListModel.addElement(receipt2);
			payReceiptsSwing.userComboBoxModel.addElement(payer1);

			payReceiptsSwing.setAccountings(new ArrayList<>());

			payReceiptsSwing.getAccountings().add(receipt1.getAccountings().get(0));
			payReceiptsSwing.getAccountings().add(receipt2.getAccountings().get(0));

		});

		payReceiptsSwing.userComboBoxModel.setSelectedItem(payer1);

		Double total = receipt1.getTotalPrice() + receipt2.getTotalPrice() + 10.;

		window.textBox("enterAmountField").enterText(total.toString());

		window.button("payButton").requireDisabled();

	}

	@Test
	@GUITest
	public void buttonIsUnlockedIfThereIsAnAvailableUserAndSomeReceiptAndAnAmountEqualToTotal() {
		setUpUsersAndReceipts();
		Receipt receipt1 = receipt1FromPayer1;
		Receipt receipt2 = receipt2FromPayer1;

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));
			payReceiptsSwing.receiptListModel.addElement(receipt1);
			payReceiptsSwing.receiptListModel.addElement(receipt2);
			payReceiptsSwing.userComboBoxModel.addElement(payer1);

			payReceiptsSwing.setAccountings(new ArrayList<>());

			payReceiptsSwing.getAccountings().add(receipt1.getAccountings().get(0));
			payReceiptsSwing.getAccountings().add(receipt2.getAccountings().get(0));

		});

		payReceiptsSwing.userComboBoxModel.setSelectedItem(payer1);

		Double totalDebt = (receipt1.getTotalPrice() + receipt2.getTotalPrice()) / 2.0;

		window.textBox("enterAmountField").enterText(totalDebt.toString());

		window.button("payButton").requireEnabled();

	}

	@Test
	@GUITest
	public void buttonGetLockedFromUnlockedIfTheUserListIsCleared() {
		setUpUsersAndReceipts();
		Receipt receipt1 = receipt1FromPayer1;
		Receipt receipt2 = receipt2FromPayer1;

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));
			payReceiptsSwing.receiptListModel.addElement(receipt1);
			payReceiptsSwing.receiptListModel.addElement(receipt2);
			payReceiptsSwing.userComboBoxModel.addElement(payer1);

			payReceiptsSwing.setAccountings(new ArrayList<>());

			payReceiptsSwing.getAccountings().add(receipt1.getAccountings().get(0));
			payReceiptsSwing.getAccountings().add(receipt2.getAccountings().get(0));

		});

		payReceiptsSwing.userComboBoxModel.setSelectedItem(payer1);

		Double totalDebt = (receipt1.getTotalPrice() + receipt2.getTotalPrice()) / 2.0;

		window.textBox("enterAmountField").enterText(totalDebt.toString());

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.userComboBoxModel.removeAllElements();
			payReceiptsSwing.receiptListModel.clear();
			payReceiptsSwing.getAccountings().clear();
		});
		window.button("payButton").requireDisabled();

	}

	@Test
	@GUITest
	public void payButtonClickedDelegateToController() {
		setUpUsersAndReceipts();
		Receipt receipt1 = receipt1FromPayer1;
		Receipt receipt2 = receipt2FromPayer1;

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));
			payReceiptsSwing.receiptListModel.addElement(receipt1);
			payReceiptsSwing.receiptListModel.addElement(receipt2);
			payReceiptsSwing.userComboBoxModel.addElement(payer1);

			payReceiptsSwing.setAccountings(new ArrayList<>());

			payReceiptsSwing.getAccountings().add(receipt1.getAccountings().get(0));
			payReceiptsSwing.getAccountings().add(receipt2.getAccountings().get(0));

		});

		payReceiptsSwing.userComboBoxModel.setSelectedItem(payer1);

		Double totalDebt = (receipt1.getTotalPrice() + receipt2.getTotalPrice()) / 2.0;

		window.textBox("enterAmountField").enterText(totalDebt.toString());

		window.button("payButton").click();

		verify(payReceiptsController).payAmount(totalDebt, logged, payer1);

	}

	@Test
	@GUITest
	public void payButtonClickedEmptyTheText() {

		setUpUsersAndReceipts();
		Receipt receipt1 = receipt1FromPayer1;
		Receipt receipt2 = receipt2FromPayer1;

		GuiActionRunner.execute(() -> {
			payReceiptsSwing.setUnpaids(Arrays.asList(receipt1, receipt2));
			payReceiptsSwing.receiptListModel.addElement(receipt1);
			payReceiptsSwing.receiptListModel.addElement(receipt2);
			payReceiptsSwing.userComboBoxModel.addElement(payer1);

			payReceiptsSwing.setAccountings(new ArrayList<>());

			payReceiptsSwing.getAccountings().add(receipt1.getAccountings().get(0));
			payReceiptsSwing.getAccountings().add(receipt2.getAccountings().get(0));

		});

		payReceiptsSwing.userComboBoxModel.setSelectedItem(payer1);

		Double totalDebt = (receipt1.getTotalPrice() + receipt2.getTotalPrice()) / 2.0;

		window.textBox("enterAmountField").enterText(totalDebt.toString());

		window.button("payButton").click();

		window.textBox("enterAmountField").requireText("");

	}
	
	@Test
	public void testUpdatedData() {
		payReceiptsSwing.updateData();
		
		verify(payReceiptsController).update();
	}

}
