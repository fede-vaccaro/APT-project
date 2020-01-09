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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.controllers.PayReceiptsController;
import com.unifiprojects.app.appichetto.controllers.ReceiptGenerator;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.modules.EntityManagerModule;
import com.unifiprojects.app.appichetto.modules.PayReceiptsModule;
import com.unifiprojects.app.appichetto.modules.RepositoriesModule;
import com.unifiprojects.app.appichetto.swingviews.utils.ReceiptFormatter;
import com.unifiprojects.app.appichetto.views.PayReceiptsView;

@RunWith(GUITestRunner.class)
public class PayReceiptViewSwingIT extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private static MVCBaseTest baseTest = new MVCBaseTest();

	private PayReceiptsViewSwing payReceiptsView;
	private PayReceiptsController payReceiptsController;

	private User loggedUser;
	private User payer1;
	private Receipt firstReceiptPayer1;
	private Receipt secondReceiptPayer1;
	private Receipt thirdReceiptPayer1;
	private Receipt firstReceiptPayer2;
	private User payer2;

	private static EntityManager entityManager;
	private static Injector injector;

	@BeforeClass
	public static void setupEntityManager() {

		Module repositoriesModule = new RepositoriesModule();

		Module entityManagerModule = new EntityManagerModule();

		Module payReceiptModule = new PayReceiptsModule();

		Injector persistenceInjector = Guice.createInjector(entityManagerModule);

		baseTest = persistenceInjector.getInstance(MVCBaseTest.class);
		entityManager = persistenceInjector.getInstance(EntityManager.class);

		injector = persistenceInjector.createChildInjector(repositoriesModule, payReceiptModule);
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

			entityManager.clear();

			payReceiptsView = (PayReceiptsViewSwing) injector.getInstance(PayReceiptsView.class);
			payReceiptsController = payReceiptsView.getController();
			payReceiptsController.setLoggedUser(loggedUser);

			// when the user is entering the view, the controller should call
			// showUnpaidReceipts
			GuiActionRunner.execute(() -> payReceiptsController.showUnpaidReceipts(loggedUser));

			return payReceiptsView;
		});

		window = new FrameFixture(robot(), payReceiptsView.getFrame());
		window.show(); // shows the frame to test
	}

	@GUITest
	@Test
	public void testInitialState() {
		window.comboBox("User selection").selectItem("payer");
		String[] receiptListString = window.list("Receipts list").contents();
		assertThat(receiptListString).isEqualTo(Arrays.asList(ReceiptFormatter.format(thirdReceiptPayer1),
				ReceiptFormatter.format(secondReceiptPayer1), ReceiptFormatter.format(firstReceiptPayer2)).toArray());

		Double debtToPayer = Arrays.asList(thirdReceiptPayer1, secondReceiptPayer1, firstReceiptPayer1).stream()
				.mapToDouble(r -> r.getAccountings().get(0).getAmount()).sum();
		window.label("totalDebtToUser").requireText(String.format("Total debt to user: %.2f", debtToPayer));
	}

	@GUITest
	@Test
	public void testPayAllReceiptsToPayer1() {
		window.comboBox("User selection").selectItem("payer");

		Double debtToPayer = Arrays.asList(thirdReceiptPayer1, secondReceiptPayer1, firstReceiptPayer1).stream()
				.mapToDouble(r -> r.getAccountings().get(0).getAmount()).sum();
		window.textBox("enterAmountField").enterText(String.format("%.2f", debtToPayer));
		window.button("payButton").click();

		String[] userComboBoxString = window.comboBox("User selection").contents();

		assertThat(userComboBoxString).doesNotContain("payer");
	}

	@GUITest
	@Test
	public void testPayHalfReceiptsToPayer1() {
		window.comboBox("User selection").selectItem("payer");

		Double debtToPayer = Arrays.asList(thirdReceiptPayer1, secondReceiptPayer1, firstReceiptPayer1).stream()
				.mapToDouble(r -> r.getAccountings().get(0).getAmount()).sum();
		window.textBox("enterAmountField").enterText(String.format("%.2f", debtToPayer / 2.0));
		window.button("payButton").click();

		window.comboBox("User selection").selectItem("payer");
		window.label("totalDebtToUser").requireText(String.format("Total debt to user: %.2f",
				Precision.round(debtToPayer, 2) - Precision.round(debtToPayer / 2.0, 2)));

		String[] receiptListString = window.list("Receipts list").contents();
		assertThat(receiptListString).isEqualTo(
				Arrays.asList(ReceiptFormatter.format(thirdReceiptPayer1), ReceiptFormatter.format(secondReceiptPayer1))
						.toArray());

	}

	@GUITest
	@Test
	public void testPayEachUserShowMessage() {
		window.comboBox("User selection").selectItem("payer");

		Double debtToPayer1 = Arrays.asList(thirdReceiptPayer1, secondReceiptPayer1, firstReceiptPayer1).stream()
				.mapToDouble(r -> r.getAccountings().get(0).getAmount()).sum();
		window.textBox("enterAmountField").enterText(String.format("%.2f", debtToPayer1));
		window.button("payButton").click();

		window.comboBox("User selection").selectItem("payer2");

		Double debtToPayer2 = Arrays.asList(firstReceiptPayer2).stream()
				.mapToDouble(r -> r.getAccountings().get(0).getAmount()).sum();
		window.textBox("enterAmountField").enterText(String.format("%.2f", debtToPayer2));
		window.button("payButton").click();

		window.label("errorMsg").requireText("You have no accountings.");

	}

	@GUITest
	@Test
	public void testPayingDeleteReceiptShowsMessage() {
		window.comboBox("User selection").selectItem("payer");

		Double debtToPayer1 = Arrays.asList(thirdReceiptPayer1, secondReceiptPayer1, firstReceiptPayer1).stream()
				.mapToDouble(r -> r.getAccountings().get(0).getAmount()).sum();

		GuiActionRunner.execute(() -> {
			entityManager.getTransaction().begin();
			firstReceiptPayer1 = entityManager.merge(firstReceiptPayer1);
			entityManager.remove(firstReceiptPayer1);
			
			secondReceiptPayer1 = entityManager.merge(secondReceiptPayer1);
			entityManager.remove(secondReceiptPayer1);
			
			thirdReceiptPayer1 = entityManager.merge(thirdReceiptPayer1);
			entityManager.remove(thirdReceiptPayer1);

			entityManager.getTransaction().commit();
		});
		
		window.textBox("enterAmountField").enterText(String.format("%.2f", debtToPayer1));
		window.button("payButton").click();

		window.label("errorMsg").requireText("enteredAmount (19.85) should be lower than the amount to pay (0.00).");

	}

}
