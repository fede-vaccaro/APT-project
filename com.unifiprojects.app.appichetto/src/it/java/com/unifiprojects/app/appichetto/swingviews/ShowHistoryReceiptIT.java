package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.persistence.EntityManager;

import org.apache.commons.math3.util.Precision;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.timing.Pause;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.controllers.LoginController;
import com.unifiprojects.app.appichetto.controllers.PayViewReceiptsController;
import com.unifiprojects.app.appichetto.controllers.ReceiptGenerator;
import com.unifiprojects.app.appichetto.controllers.ShowHistoryController;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.AccountingRepository;
import com.unifiprojects.app.appichetto.repositories.AccountingRepositoryHibernate;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepositoryHibernate;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.repositories.UserRepositoryHibernate;
import com.unifiprojects.app.appichetto.transactionhandlers.HibernateTransaction;

import com.unifiprojects.app.appichetto.swingviews.utils.CustomToStringReceipt;

@RunWith(GUITestRunner.class)
public class ShowHistoryReceiptIT extends AssertJSwingJUnitTestCase {

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

	@BeforeClass
	public static void setupEntityManager() {
		baseTest.setupEntityManager();
		entityManager = baseTest.getEntityManager();
	}

	@AfterClass
	public static void closeEntityManager() {
		baseTest.closeEntityManager();
	}

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			baseTest.wipeTablesBeforeTest();
			ReceiptRepository receiptRepository = new ReceiptRepositoryHibernate(entityManager);

			showHistoryViewSwing = new ShowHistoryViewSwing();
			showHistoryController = new ShowHistoryController(receiptRepository, showHistoryViewSwing);
			
			loggedUser = new User("logged", "pw");
			entityManager.getTransaction().begin();
			entityManager.persist(loggedUser);
			entityManager.getTransaction().commit();
			
			showHistoryController.setLoggedUser(loggedUser);

			return showHistoryViewSwing;
		});
		window = new FrameFixture(robot(), showHistoryViewSwing.getFrame());
		window.show(); // shows the frame to test
	}

	@GUITest
	@Test
	public void testInitialState() {
		GuiActionRunner.execute(() -> {

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

			showHistoryController.showHistory();
		});

		window.list("receiptList").selectItem(0);
		String[] receiptListString = window.list("receiptList").contents();
		String[] itemListString = window.list("itemList").contents();
		assertThat(receiptListString)
				.isEqualTo(Arrays.asList(firstReceiptDebtor2.toString(), thirdReceiptDebtor1.toString(),
						secondReceiptDebtor1.toString(), firstReceiptDebtor1.toString()).toArray());
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
			showHistoryController.showHistory();
		});

		window.label("errorMsg").requireText("You have no receipts in the history.");
	}

}
