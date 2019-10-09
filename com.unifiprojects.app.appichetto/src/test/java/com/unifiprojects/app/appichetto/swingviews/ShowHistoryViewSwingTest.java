package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

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

import com.unifiprojects.app.appichetto.controllers.ShowHistoryController;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.swingviews.utils.AccountingFormatter;

@RunWith(GUITestRunner.class)
public class ShowHistoryViewSwingTest extends AssertJSwingJUnitTestCase {

	ShowHistoryViewSwing showHistoryViewSwing;
	
	@Mock
	ShowHistoryController showHistoryController;

	private FrameFixture window;

	private User loggedUser = new User("buyer", "pw");

	private Receipt receipt0;
	private Receipt receipt1;

	private Accounting accounting1ToOtherOwner1;
	private Accounting accounting1ToOtherOwner2;
	private Accounting accounting2ToOtherOwner1;
	private Accounting accounting2ToOtherOwner2;

	private User otherOwner1;
	private User otherOwner2;

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			MockitoAnnotations.initMocks(this);
			showHistoryViewSwing = new ShowHistoryViewSwing();
			showHistoryViewSwing.setController(showHistoryController);
			return showHistoryViewSwing;
		});
		window = new FrameFixture(robot(), showHistoryViewSwing.getFrame());
		window.show(); // shows the frame to test
		
		otherOwner1 = new User("other1", "pw");
		otherOwner2 = new User("other2", "pw");

		// receipt1
		Item item0 = new Item("tomato", 12.0, Arrays.asList(loggedUser, otherOwner1));
		Item item1 = new Item("potato", 4.0, Arrays.asList(loggedUser, otherOwner2));

		
		receipt0 = new Receipt();
				
		receipt0.setBuyer(loggedUser);
		receipt0.setTimestamp(new GregorianCalendar(2019, 8, 20));
		receipt0.setItems(Arrays.asList(item0, item1));
		
		accounting1ToOtherOwner1 = new Accounting(otherOwner1, item0.getPricePerOwner());
		accounting1ToOtherOwner2 = new Accounting(otherOwner2, item1.getPricePerOwner());

		receipt0.setAccountingList(Arrays.asList(accounting1ToOtherOwner1, accounting1ToOtherOwner2));
		
		// receipt1
		Item item2 = new Item("hamburger", 15.0, Arrays.asList(otherOwner1, otherOwner2));
		Item item3 = new Item("bread", 5.0, Arrays.asList(loggedUser, otherOwner1, otherOwner2));
		
		receipt1 = new Receipt();
		
		receipt1.setBuyer(loggedUser);
		receipt1.setTimestamp(new GregorianCalendar(2019, 8, 15));
		receipt1.setItems(Arrays.asList(item2, item3));
		
		accounting2ToOtherOwner1 = new Accounting(otherOwner1, item2.getPricePerOwner() + item3.getPricePerOwner()); 
		accounting2ToOtherOwner2 = new Accounting(otherOwner2, item2.getPricePerOwner() + item3.getPricePerOwner());
		
		receipt1.setAccountingList(Arrays.asList(accounting2ToOtherOwner1, accounting2ToOtherOwner2));

	}
	

	@Test
	@GUITest
	public void testInititialState() {
		window.list("receiptList").requireEnabled();
		window.list("itemList").requireEnabled();
		window.list("accountingList").requireEnabled();
		window.list("totalAccountingList").requireEnabled();
		window.label(JLabelMatcher.withText("Receipts you bought:"));
		window.label(JLabelMatcher.withText("Items in selected receipt:"));
		window.label(JLabelMatcher.withText("Accountings:"));
		window.label(JLabelMatcher.withText("Total unpaid accountings:"));
		
		window.button("homeBtn").requireEnabled();
		window.label("errorMsg").requireText("");
	}

	@Test
	@GUITest
	public void testShowShoppingHistoryShowCorrectlyTheBoughtReceipts() {

		List<Receipt> history = Arrays.asList(receipt0);

		GuiActionRunner.execute(() -> showHistoryViewSwing.showShoppingHistory(history));

		String[] receiptListContent = window.list("receiptList").contents();

		assertThat(receiptListContent).containsExactlyInAnyOrder(receipt0.toString());
	}

	@Test
	@GUITest
	public void testShowShoppingHistoryShowTheSecondReceiptWhenCallingItAgain() {
		List<Receipt> history = Arrays.asList(receipt0);
		List<Receipt> updatedHistory = Arrays.asList(receipt0, receipt1);
		GuiActionRunner.execute(() -> {
			showHistoryViewSwing.showShoppingHistory(history);
			showHistoryViewSwing.showShoppingHistory(updatedHistory);
		});

		String[] receiptListContent = window.list("receiptList").contents();

		assertThat(receiptListContent).containsExactlyInAnyOrder(receipt0.toString(), receipt1.toString());
	}
	
	@Test
	@GUITest
	public void testShowShoppingHistoryEmptyEachListIfArgumentIsEmpty() {
		
		List<Receipt> history = Arrays.asList(receipt0);
		List<Receipt> updatedHistory = Arrays.asList();
		GuiActionRunner.execute(() -> {
			showHistoryViewSwing.showShoppingHistory(history);
			showHistoryViewSwing.showShoppingHistory(updatedHistory);
		});		
		String[] receiptListContent = window.list("receiptList").contents();
		String[] itemListContent = window.list("itemList").contents();
		String[] accountingListContent = window.list("accountingList").contents();

		assertThat(receiptListContent).isEmpty();
		assertThat(itemListContent).isEmpty();
		assertThat(accountingListContent).isEmpty();

	}
	@Test
	@GUITest
	public void testShowShoppingHistoryShowCorrectlyTheItems() {
		
		List<Receipt> history = Arrays.asList(receipt0);
		GuiActionRunner.execute(() -> showHistoryViewSwing.showShoppingHistory(history));
		
		window.list("receiptList").selectItem(0);
		
		String[] itemListContent = window.list("itemList").contents();
		
		Item item0 = receipt0.getItem(0);
		Item item1 = receipt0.getItem(1);
		
		assertThat(itemListContent).containsExactlyInAnyOrder(item0.toString(), item1.toString());

	}
	
	@Test
	@GUITest
	public void testShowShoppingHistoryShowCorrectlyTheAccountings() {
		List<Receipt> history = Arrays.asList(receipt0);
		GuiActionRunner.execute(() -> showHistoryViewSwing.showShoppingHistory(history));
		
		window.list("receiptList").selectItem(0);
		
		Accounting accounting0 = receipt0.getAccountings().get(0);
		Accounting accounting1 = receipt0.getAccountings().get(1);
		
		String[] accountingListContent = window.list("accountingList").contents();
		assertThat(accountingListContent).containsExactlyInAnyOrder(AccountingFormatter.format(accounting0), AccountingFormatter.format(accounting1));

	}

	@Test
	@GUITest
	public void selectingAnotherReceiptChangeItemsDisplayed() {
		List<Receipt> history = Arrays.asList(receipt0, receipt1);
		GuiActionRunner.execute(() -> showHistoryViewSwing.showShoppingHistory(history));
		
		window.list("receiptList").selectItem(0);
		window.list("receiptList").selectItem(1);

		Item item0 = receipt1.getItem(0);
		Item item1 = receipt1.getItem(1);
		
		String[] itemListContent = window.list("itemList").contents();
		assertThat(itemListContent).containsExactlyInAnyOrder(item0.toString(), item1.toString());
	}
	
	@Test
	@GUITest
	public void selectingAnotherReceiptChangeAccountingsDisplayed() {
		List<Receipt> history = Arrays.asList(receipt0, receipt1);
		GuiActionRunner.execute(() -> showHistoryViewSwing.showShoppingHistory(history));
		
		window.list("receiptList").selectItem(0);
		window.list("receiptList").selectItem(1);

		Accounting accounting0 = receipt1.getAccountings().get(0);
		Accounting accounting1 = receipt1.getAccountings().get(1);
		
		String[] accountingListContent = window.list("accountingList").contents();
		
		
		assertThat(accountingListContent).containsExactlyInAnyOrder(AccountingFormatter.format(accounting0), AccountingFormatter.format(accounting1));
	}
	
	@Test
	@GUITest
	public void testTotalUnpaidAccountingsAreEmptiedIfArgumentIsEmpty() {
		List<Receipt> history = Arrays.asList(receipt0, receipt1);
		List<Receipt> updatedHistory = Arrays.asList();
		GuiActionRunner.execute(() -> {
			showHistoryViewSwing.showShoppingHistory(history);
			showHistoryViewSwing.showShoppingHistory(updatedHistory);
		});
		
		String[] totalAccountingListContent = window.list("totalAccountingList").contents();
		
		assertThat(totalAccountingListContent).isEmpty();
		
	}
	
	@Test
	@GUITest
	public void testTotalUnpaidAccountingsAreShownWhenShowShoppingHistoryIsCalled() {
		List<Receipt> history = Arrays.asList(receipt0, receipt1);
		
		//accounting1ToOtherOwner1.setPaid(true); // this won't generate debt, so shouldn't be included in the computing of the total accounting
		accounting1ToOtherOwner1.setAmount(0.0); // this won't generate debt, so shouldn't be included in the computing of the total accounting
		
		
		Double totalCreditFromOwner1 = accounting2ToOtherOwner1.getAmount();
		Double totalCreditFromOwner2 = accounting1ToOtherOwner2.getAmount() + accounting2ToOtherOwner2.getAmount();
		

		GuiActionRunner.execute(() -> showHistoryViewSwing.showShoppingHistory(history));

		String[] accountingListContent = window.list("totalAccountingList").contents();

		String accountingFormat = "%s:	%.2f€"; 
		
		assertThat(accountingListContent).containsExactlyInAnyOrder(String.format(accountingFormat, otherOwner1.getUsername(), totalCreditFromOwner1), 
				String.format(accountingFormat, otherOwner2, totalCreditFromOwner2));
	}
	
	@Test
	@GUITest
	public void testErrorMsgDisplayTheMsg() {
		String msg = "Testing error messages.";
		
		GuiActionRunner.execute(() -> showHistoryViewSwing.showErrorMsg(msg));
		
		window.label("errorMsg").requireText(msg);
	}

	@Test
	@GUITest
	public void testRemoveReceiptDelegateToController() {
		List<Receipt> history = Arrays.asList(receipt0, receipt1);

		GuiActionRunner.execute(() -> showHistoryViewSwing.showShoppingHistory(history));
		
		window.list("receiptList").selectItem(receipt1.toString());
		window.button(JButtonMatcher.withText("Remove selected")).click();
		
		verify(showHistoryController).removeReceipt(receipt1);
	}
	
	@Test
	@GUITest
	public void testButtonIsDisabledWhenListIsEmpty() {
		List<Receipt> history = Arrays.asList();

		GuiActionRunner.execute(() -> showHistoryViewSwing.showShoppingHistory(history));
		
		window.button(JButtonMatcher.withText("Remove selected")).requireDisabled();

	}
	
	@Test
	@GUITest
	public void testButtonIsEnabledElementIsSelected() {
		List<Receipt> history = Arrays.asList(receipt0);

		GuiActionRunner.execute(() -> showHistoryViewSwing.showShoppingHistory(history));
		window.list("receiptList").selectItem(receipt0.toString());
		
		window.button(JButtonMatcher.withText("Remove selected")).requireEnabled();
	}
	
	@Test
	@GUITest
	public void testButtonIsDisabledWhenNoItemIsSelectedButListIsNotEmpty() {
		List<Receipt> history = Arrays.asList(receipt0, receipt1);

		GuiActionRunner.execute(() -> showHistoryViewSwing.showShoppingHistory(history));
		
		window.list("receiptList").clearSelection();
		
		window.button(JButtonMatcher.withText("Remove selected")).requireDisabled();
	}
	
	@Test
	@GUITest
	public void testClearingTheReceiptListWhenSomethingIsSelectedDisableTheRemoveButton() {
		List<Receipt> historyBefore = Arrays.asList(receipt0, receipt1);

		GuiActionRunner.execute(() -> showHistoryViewSwing.showShoppingHistory(historyBefore));

		window.list("receiptList").selectItem(0);
		
		List<Receipt> historyAfter = Arrays.asList();
		
		GuiActionRunner.execute(() -> showHistoryViewSwing.showShoppingHistory(historyAfter));

		window.button(JButtonMatcher.withText("Remove selected")).requireDisabled();
	}
	
}
