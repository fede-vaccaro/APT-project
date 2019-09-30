package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;

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
public class PayReceiptViewSwingIT extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private static MVCBaseTest baseTest = new MVCBaseTest();

	private PayViewReceiptsViewSwing payViewReceiptsView;
	private PayViewReceiptsController payViewReceiptsController;

	private AccountingRepository accountingRepository;
	private ReceiptRepository receiptRepository;
	private User loggedUser;
	private User payerUser;
	private Receipt firstReceipt;
	private Receipt secondReceipt;
	private Receipt thirdReceipt;

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

			payViewReceiptsView = new PayViewReceiptsViewSwing();

			payViewReceiptsController = new PayViewReceiptsController(receiptRepository, accountingRepository,
					payViewReceiptsView);
			payViewReceiptsController.setTransactionHandler(new HibernateTransaction(entityManager));

			payViewReceiptsView.setController(payViewReceiptsController);

			loggedUser = new User("logged", "pw");
			payerUser = new User("payer", "pw");

			firstReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
					new GregorianCalendar(2019, 8, 10));
			secondReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
					payerUser, new GregorianCalendar(2019, 8, 11));

			Item tomato = new Item("tomato", 3., Arrays.asList(loggedUser, payerUser));
			Item hamburger = new Item("hamburger", 6., Arrays.asList(loggedUser, payerUser));
			Item bread = new Item("bread", 4., Arrays.asList(loggedUser, payerUser));
			thirdReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
					new GregorianCalendar(2019, 8, 12), Arrays.asList(tomato, bread, hamburger));

			entityManager.getTransaction().begin();
			entityManager.persist(loggedUser);
			entityManager.persist(payerUser);
			entityManager.persist(thirdReceipt);
			entityManager.persist(firstReceipt);
			entityManager.persist(secondReceipt);
			entityManager.getTransaction().commit();
			
			GuiActionRunner.execute(() -> payViewReceiptsController.showUnpaidReceiptsOfLoggedUser(loggedUser));
			
			return payViewReceiptsView;
		});
		window = new FrameFixture(robot(), payViewReceiptsView.getFrame());
		window.show(); // shows the frame to test
	}

	@GUITest
	@Test
	public void testInitialState() {

		String[] receiptListString = window.list("receiptList").contents();
		
		Pause.pause(20000);

		assertThat(receiptListString).isEqualTo(Arrays.asList((new CustomToStringReceipt(thirdReceipt)).toString(),
				(new CustomToStringReceipt(secondReceipt)).toString(),
				(new CustomToStringReceipt(firstReceipt)).toString()).toArray());
				
	}

}
