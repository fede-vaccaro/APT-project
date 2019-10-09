package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;

import org.apache.commons.math3.util.Precision;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.controllers.PayReceiptsController;
import com.unifiprojects.app.appichetto.controllers.ReceiptGenerator;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.AccountingRepository;
import com.unifiprojects.app.appichetto.repositories.AccountingRepositoryHibernate;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepositoryHibernate;
import com.unifiprojects.app.appichetto.swingviews.utils.CustomToStringReceipt;
import com.unifiprojects.app.appichetto.transactionhandlers.HibernateTransaction;

@RunWith(GUITestRunner.class)
public class PayReceiptViewSwingIT extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private static MVCBaseTest baseTest = new MVCBaseTest();

	private PayReceiptsViewSwing payViewReceiptsView;
	private PayReceiptsController payViewReceiptsController;

	private AccountingRepository accountingRepository;
	private ReceiptRepository receiptRepository;
	private User loggedUser;
	private User payer1;
	private Receipt firstReceiptPayer1;
	private Receipt secondReceiptPayer1;
	private Receipt thirdReceiptPayer1;
	private Receipt firstReceiptPayer2;
	private User payer2;

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

			accountingRepository = new AccountingRepositoryHibernate(entityManager);
			receiptRepository = new ReceiptRepositoryHibernate(entityManager);

			payViewReceiptsView = new PayReceiptsViewSwing();

			payViewReceiptsController = new PayReceiptsController(receiptRepository, accountingRepository,
					payViewReceiptsView);
			payViewReceiptsController.setTransactionHandler(new HibernateTransaction(entityManager));

			payViewReceiptsView.setController(payViewReceiptsController);

			loggedUser = new User("logged", "pw");
			payer1 = new User("payer", "pw");
			payer2 = new User("payer2", "pw");

			firstReceiptPayer1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
					payer1, new GregorianCalendar(2019, 8, 10));
			secondReceiptPayer1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
					payer1, new GregorianCalendar(2019, 8, 11));
			firstReceiptPayer2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
					payer2, new GregorianCalendar(2019, 8, 10));

			Item tomato = new Item("tomato", 1.35, Arrays.asList(loggedUser, payer1));
			Item hamburger = new Item("hamburger", 4.45, Arrays.asList(loggedUser, payer1));
			Item bread = new Item("bread", 3.89, Arrays.asList(loggedUser, payer1));
			thirdReceiptPayer1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
					payer1, new GregorianCalendar(2019, 8, 12), Arrays.asList(tomato, bread, hamburger));

			entityManager.getTransaction().begin();
			entityManager.persist(loggedUser);
			entityManager.persist(payer1);
			entityManager.persist(payer2);
			entityManager.persist(firstReceiptPayer1);
			entityManager.persist(secondReceiptPayer1);
			entityManager.persist(thirdReceiptPayer1);
			entityManager.persist(firstReceiptPayer2);
			entityManager.getTransaction().commit();

			payViewReceiptsView.setLoggedUser(loggedUser);
			GuiActionRunner.execute(() -> payViewReceiptsController.showUnpaidReceiptsOfLoggedUser(loggedUser));

			return payViewReceiptsView;
		});
		window = new FrameFixture(robot(), payViewReceiptsView.getFrame());
		window.show(); // shows the frame to test
	}

	@GUITest
	@Test
	public void testInitialState() {
		window.comboBox("userSelection").selectItem("payer");
		String[] receiptListString = window.list("receiptList").contents();
		assertThat(receiptListString)
				.isEqualTo(Arrays.asList((new CustomToStringReceipt(thirdReceiptPayer1)).toString(),
						(new CustomToStringReceipt(secondReceiptPayer1)).toString(),
						(new CustomToStringReceipt(firstReceiptPayer2)).toString()).toArray());

		Double debtToPayer = Arrays.asList(thirdReceiptPayer1, secondReceiptPayer1, firstReceiptPayer1).stream()
				.mapToDouble(r -> r.getAccountings().get(0).getAmount()).sum();
		window.label("totalDebtToUser").requireText(String.format("Total debt to user: %.2f", debtToPayer));
	}

	@GUITest
	@Test
	public void testPayAllReceiptsToPayer1() {
		window.comboBox("userSelection").selectItem("payer");

		Double debtToPayer = Arrays.asList(thirdReceiptPayer1, secondReceiptPayer1, firstReceiptPayer1).stream()
				.mapToDouble(r -> r.getAccountings().get(0).getAmount()).sum();
		window.textBox("enterAmountField").enterText(String.format("%.2f", debtToPayer));
		window.button("payButton").click();

		String[] userComboBoxString = window.comboBox("userSelection").contents();

		assertThat(userComboBoxString).doesNotContain("payer");
	}

	@GUITest
	@Test
	public void testPayHalfReceiptsToPayer1() {
		window.comboBox("userSelection").selectItem("payer");

		Double debtToPayer = Arrays.asList(thirdReceiptPayer1, secondReceiptPayer1, firstReceiptPayer1).stream()
				.mapToDouble(r -> r.getAccountings().get(0).getAmount()).sum();
		window.textBox("enterAmountField").enterText(String.format("%.2f", debtToPayer / 2.0));
		window.button("payButton").click();

		window.comboBox("userSelection").selectItem("payer");
		window.label("totalDebtToUser").requireText(String.format("Total debt to user: %.2f",
				Precision.round(debtToPayer, 2) - Precision.round(debtToPayer / 2.0, 2)));

		String[] receiptListString = window.list("receiptList").contents();
		assertThat(receiptListString)
				.isEqualTo(Arrays.asList((new CustomToStringReceipt(thirdReceiptPayer1)).toString(),
						(new CustomToStringReceipt(secondReceiptPayer1)).toString()).toArray());

	}

	@GUITest
	@Test
	public void testPayEachUserShowMessage() {
		window.comboBox("userSelection").selectItem("payer");

		Double debtToPayer1 = Arrays.asList(thirdReceiptPayer1, secondReceiptPayer1, firstReceiptPayer1).stream()
				.mapToDouble(r -> r.getAccountings().get(0).getAmount()).sum();
		window.textBox("enterAmountField").enterText(String.format("%.2f", debtToPayer1));
		window.button("payButton").click();

		window.comboBox("userSelection").selectItem("payer2");

		Double debtToPayer2 = Arrays.asList(firstReceiptPayer2).stream()
				.mapToDouble(r -> r.getAccountings().get(0).getAmount()).sum();
		window.textBox("enterAmountField").enterText(String.format("%.2f", debtToPayer2));
		window.button("payButton").click();

		window.label("errorMsg").requireText("You have no accountings.");

	}

}
