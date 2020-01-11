package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.controllers.ReceiptGenerator;
import com.unifiprojects.app.appichetto.controllers.ShowHistoryController;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.modules.EntityManagerModule;
import com.unifiprojects.app.appichetto.modules.ReceiptModule;
import com.unifiprojects.app.appichetto.modules.RepositoriesModule;
import com.unifiprojects.app.appichetto.modules.ShowHistoryModule;
import com.unifiprojects.app.appichetto.swingviews.utils.LinkedSwingView;

@RunWith(GUITestRunner.class)
public class ShowHistoryReceiptViewIT extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private static MVCBaseTest baseTest = new MVCBaseTest();

	private ShowHistoryController showHistoryController;
	private ShowHistoryViewSwing showHistoryViewSwing;

	private User loggedUser;
	private User debtor1;
	private Receipt firstReceiptDebtor1;
	private Receipt secondReceiptDebtor1;
	private Receipt thirdReceiptDebtor1;
	private Receipt firstReceiptDebtor2;
	private User debtor2;

	private static EntityManager entityManager;
	private static Injector injector;

	@BeforeClass
	public static void setupEntityManager() {

		Module repositoriesModule = new RepositoriesModule();

		Module entityManagerModule = new EntityManagerModule();

		Module showHistoryModule = new ShowHistoryModule();

		Module receiptModule = new ReceiptModule();
		
		Injector persistenceInjector = Guice.createInjector(entityManagerModule);

		baseTest = persistenceInjector.getInstance(MVCBaseTest.class);
		entityManager = persistenceInjector.getInstance(EntityManager.class);

		injector = persistenceInjector.createChildInjector(repositoriesModule, showHistoryModule, receiptModule);
	}

	@AfterClass
	public static void closeEntityManager() {
		baseTest.closeEntityManager();
	}

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			baseTest.wipeTablesBeforeTest();
			loggedUser = new User("logged", "pw");

			entityManager.getTransaction().begin();
			entityManager.persist(loggedUser);
			entityManager.getTransaction().commit();

			showHistoryViewSwing = injector.getInstance(ShowHistoryViewSwing.class);
			showHistoryController = showHistoryViewSwing.getController();
			showHistoryController.setLoggedUser(loggedUser);

			debtor1 = new User("payer", "pw");
			debtor2 = new User("payer2", "pw");

			firstReceiptDebtor1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(debtor1,
					loggedUser, new GregorianCalendar(2019, 8, 10));
			secondReceiptDebtor1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(debtor1,
					loggedUser, new GregorianCalendar(2019, 8, 11));
			firstReceiptDebtor2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(debtor2,
					loggedUser, new GregorianCalendar(2019, 8, 12));

			Item tomato = new Item("tomato", 1.35, Arrays.asList(loggedUser, debtor1));
			Item hamburger = new Item("hamburger", 4.45, Arrays.asList(loggedUser, debtor1));
			Item bread = new Item("bread", 3.89, Arrays.asList(loggedUser, debtor1));
			thirdReceiptDebtor1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(debtor1,
					loggedUser, new GregorianCalendar(2019, 8, 11), Arrays.asList(tomato, bread, hamburger));

			entityManager.getTransaction().begin();
			entityManager.persist(debtor1);
			entityManager.persist(debtor2);
			entityManager.persist(firstReceiptDebtor2);

			entityManager.persist(firstReceiptDebtor1);
			entityManager.persist(secondReceiptDebtor1);
			entityManager.persist(thirdReceiptDebtor1);
			entityManager.getTransaction().commit();

			entityManager.clear();

			showHistoryController.showHistory();
			LinkedSwingView.initializeMainFrame();

			return showHistoryViewSwing;
		});
		window = new FrameFixture(robot(), showHistoryViewSwing.getFrame());
		window.show(); // shows the frame to test
	}

	@GUITest
	@Test
	public void testInitialState() {
		window.list("Receipts list").selectItem(0);
		String[] receiptListString = window.list("Receipts list").contents();
		String[] itemListString = window.list("Items list").contents();
		assertThat(receiptListString).containsExactlyInAnyOrder(firstReceiptDebtor2.toString(),
				thirdReceiptDebtor1.toString(), secondReceiptDebtor1.toString(), firstReceiptDebtor1.toString());
		assertThat(itemListString).containsExactlyInAnyOrder(firstReceiptDebtor2.getItem(0).toString(),
				firstReceiptDebtor2.getItem(1).toString());
	}

	public void removeReceipt(Receipt receipt) {
		Receipt toBeRemoved = receipt;
		if (!entityManager.contains(receipt)) {
			toBeRemoved = entityManager.merge(receipt);
		}

		entityManager.remove(toBeRemoved);
	}

	@GUITest
	@Test
	public void testShowErrorMessageWhenReceiptListIsEmpty() {

		GuiActionRunner.execute(() -> {

			entityManager.getTransaction().begin();
			removeReceipt(firstReceiptDebtor1);
			removeReceipt(secondReceiptDebtor1);
			removeReceipt(thirdReceiptDebtor1);
			removeReceipt(firstReceiptDebtor2);
			entityManager.getTransaction().commit();

			showHistoryController.showHistory();
		});

		window.label("errorMsg").requireText("You have no receipts in the history.");
	}

	@GUITest
	@Test
	public void testRemoveReceipt() {
		GuiActionRunner.execute(() -> {
			showHistoryController.showHistory();
		});

		window.list("Receipts list").selectItem(thirdReceiptDebtor1.toString());
		window.button(JButtonMatcher.withText("Remove selected")).click();

		String[] receiptList = window.list("Receipts list").contents();
		assertThat(receiptList).containsExactlyInAnyOrder(firstReceiptDebtor1.toString(),
				secondReceiptDebtor1.toString(), firstReceiptDebtor2.toString());

		window.label("errorMsg")
				.requireText(String.format("Receipt (from %s) deleted.", thirdReceiptDebtor1.getTimestamp().getTime()));
	}

	@GUITest
	@Test
	public void testRemoveAllReceipt() {
		GuiActionRunner.execute(() -> {
			showHistoryController.showHistory();
		});
		window.list("Receipts list").selectItem(firstReceiptDebtor1.toString());
		window.button(JButtonMatcher.withText("Remove selected")).click();
		window.list("Receipts list").selectItem(secondReceiptDebtor1.toString());
		window.button(JButtonMatcher.withText("Remove selected")).click();
		window.list("Receipts list").selectItem(thirdReceiptDebtor1.toString());
		window.button(JButtonMatcher.withText("Remove selected")).click();
		window.list("Receipts list").selectItem(firstReceiptDebtor2.toString());
		window.button(JButtonMatcher.withText("Remove selected")).click();

		String[] receiptList = window.list("Receipts list").contents();
		assertThat(receiptList).isEmpty();
		window.label("errorMsg").requireText("You have no receipts in the history.");
		window.button(JButtonMatcher.withText("Remove selected")).requireDisabled();
	}

	@GUITest
	@Test
	public void testUpdateReceipt() {
		GuiActionRunner.execute(() -> {
			showHistoryController.showHistory();
		});
		window.list("Receipts list").selectItem(firstReceiptDebtor1.toString());
		window.button(JButtonMatcher.withText("Update receipt")).click();
		assertThat(showHistoryViewSwing.getFrame().isDisplayable()).isFalse();
	}
}
