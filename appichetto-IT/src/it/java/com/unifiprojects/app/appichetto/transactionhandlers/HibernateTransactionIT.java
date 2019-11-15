package com.unifiprojects.app.appichetto.transactionhandlers;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.controllers.ReceiptGenerator;
import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;

public class HibernateTransactionIT {

	private static MVCBaseTest baseTest1 = new MVCBaseTest();
	private static MVCBaseTest baseTest2 = new MVCBaseTest();

	private HibernateTransaction transaction1;
	private HibernateTransaction transaction2;

	@BeforeClass
	public static void setupEntityManager() {
		// same DB, different entity managers; this is needed to emulate different
		// application opened at the same time

		baseTest1.setupEntityManager();
		baseTest2.setupEntityManager();
	}

	@AfterClass
	public static void closeEntityManager() {
		baseTest1.closeEntityManager();
		baseTest2.closeEntityManager();
	}

	@Before
	public void setUp() {
		baseTest1.wipeTablesBeforeTest();
		transaction1 = new HibernateTransaction(baseTest1.getEntityManager());
		transaction2 = new HibernateTransaction(baseTest2.getEntityManager());
	}

	@Test
	public void testDoInTransactionBetweenApps() {
		// Application 1 persist something
		User newUserByApp1 = new User("App1", "pw");
		transaction1.doInTransaction(() -> {
			baseTest1.getEntityManager().persist(newUserByApp1);
		});

		// Application 2 is able to find it
		User found = baseTest2.getEntityManager().find(User.class, newUserByApp1.getId());

		assertThat(found).isEqualTo(newUserByApp1);
	}

	@Test
	public void testConcurrencyHandlingWhenApp1TryToUpdateSomethingThatApp2Removed() {
		User buyer = new User("buyer", "pw");
		User participant = new User("participant", "pw");
		Receipt receipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(participant, buyer,
				new GregorianCalendar(2019, 9, 10));

		EntityManager entityManager1 = baseTest1.getEntityManager();
		EntityManager entityManager2 = baseTest2.getEntityManager();

		entityManager1.getTransaction().begin();
		entityManager1.persist(buyer);
		entityManager1.persist(participant);
		entityManager1.persist(receipt);
		entityManager1.getTransaction().commit();

		entityManager1.clear();

		// now, App2 retrieve the receipt...
		Receipt retrievedReceipt = baseTest2.getEntityManager().find(Receipt.class, receipt.getId());

		// after, App1 deletes it in a transaction
		transaction1.doInTransaction(() -> {
			Receipt mergedReceipt = entityManager1.merge(receipt);
			entityManager1.remove(mergedReceipt);
		});
		
		assertThat(entityManager1.find(Receipt.class, receipt.getId())).isNull();
		
		// but App2 doesn't know that; so it tries to update (i.e. pay) the single accounting in the receipt. But an
		// UncommittableTransactionException is thrown, because he's trying to update an accounting pointing to a deleted Receipt
		assertThatExceptionOfType(UncommittableTransactionException.class)
		.isThrownBy(() -> transaction2.doInTransaction(() -> {
			Accounting accounting = retrievedReceipt.getAccountings().get(0);
			accounting.setAmount(0.0);
			entityManager2.merge(accounting);
		}));
		
	}

	@Test
	public void testUncommittableExceptionIsThrownWhenRollbackExceptionOccurs() {
		User user = new User("user", "pw");
		Item item = new Item("randomItem", 10.0, Arrays.asList(user));

		baseTest1.getEntityManager().getTransaction().begin();
		baseTest1.getEntityManager().persist(user);
		baseTest1.getEntityManager().persist(item);
		baseTest1.getEntityManager().getTransaction().commit();

		baseTest1.getEntityManager().clear();

		assertThatExceptionOfType(UncommittableTransactionException.class)
				.isThrownBy(() -> transaction1.doInTransaction(() -> {
					// this should cause a Rollback exception, since it deletes user which is a
					// foreign key for Item
					User mergedUser = baseTest1.getEntityManager().merge(user);
					baseTest1.getEntityManager().remove(mergedUser);
				}));

		// now we check that user is still in the DB
		assertThat(baseTest1.getEntityManager().find(User.class, user.getId())).isEqualTo(user);

	}

}
