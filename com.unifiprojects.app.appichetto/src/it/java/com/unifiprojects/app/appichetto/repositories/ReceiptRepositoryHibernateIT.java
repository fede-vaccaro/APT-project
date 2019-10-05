package com.unifiprojects.app.appichetto.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;

public class ReceiptRepositoryHibernateIT {
	private static MVCBaseTest baseTest = new MVCBaseTest();
	private static EntityManager entityManager;

	private ReceiptRepositoryHibernate receiptRepositoryHibernate;

	@BeforeClass
	public static void setupEntityManager() {
		baseTest.setupEntityManager();
	}

	@AfterClass
	public static void close() {
		baseTest.closeEntityManager();
	}

	@Before
	public void setup() {
		baseTest.wipeTablesBeforeTest();
		entityManager = baseTest.getEntityManager();
		receiptRepositoryHibernate = new ReceiptRepositoryHibernate(entityManager);
	}

	@Test
	public void testSaveReceiptSaveCorrectlyTheReceiptWithItsAccountingsAndItems() {
		User debtorUser = new User("user1", "pw");
		User creditorUser = new User("user2", "pw");

		entityManager.getTransaction().begin();
		entityManager.persist(debtorUser);
		entityManager.persist(creditorUser);
		entityManager.getTransaction().commit();

		Item item1 = new Item("potato", 10.0, Arrays.asList(debtorUser, creditorUser));
		Item item2 = new Item("tomato", 5.0, Arrays.asList(debtorUser, creditorUser));

		Accounting accountingToUser1 = new Accounting(debtorUser, item1.getPrice() / 2.0 + item2.getPrice() / 2.0);

		Receipt receipt1 = new Receipt();
		receipt1.setBuyer(creditorUser);
		receipt1.setTimestamp(new GregorianCalendar(2019, 8, 10));
		receipt1.setItems(Arrays.asList(item1, item2));
		receipt1.setAccountingList(Arrays.asList(accountingToUser1));
		receipt1.setTotalPrice(item1.getPrice() + item2.getPrice());

		entityManager.clear();

		receiptRepositoryHibernate.saveReceipt(receipt1);

		entityManager.clear();
		Receipt foundReceipt = entityManager.find(Receipt.class, receipt1.getId());
		assertThat(foundReceipt).isEqualTo(receipt1);

	}

	@Test
	public void testGetAllUnpaidReceiptOfReturnTheReceipt() {
		User debtorUser = new User("user1", "pw");
		User creditorUser = new User("user2", "pw");

		Item item1 = new Item("potato", 10.0, Arrays.asList(debtorUser, creditorUser));
		Item item2 = new Item("tomato", 5.0, Arrays.asList(debtorUser, creditorUser));

		Receipt receipt = new Receipt();
		Accounting accountingToDebtor = new Accounting(debtorUser, item1.getPrice() / 2.0 + item2.getPrice() / 2.0);

		receipt.setBuyer(creditorUser);
		receipt.setTimestamp(new GregorianCalendar(2019, 8, 10));
		receipt.setItems(Arrays.asList(item1, item2));
		receipt.setAccountingList(Arrays.asList(accountingToDebtor));
		receipt.setTotalPrice(item1.getPrice() + item2.getPrice());

		entityManager.getTransaction().begin();
		entityManager.persist(debtorUser);
		entityManager.persist(creditorUser);

		entityManager.persist(item1);
		entityManager.persist(item2);

		entityManager.persist(accountingToDebtor);
		entityManager.persist(receipt);

		entityManager.getTransaction().commit();

		entityManager.clear();

		List<Receipt> unpaids = receiptRepositoryHibernate.getAllUnpaidReceiptsOf(debtorUser);
		assertThat(unpaids).hasSize(1);
		Receipt foundReceipt = unpaids.get(0);

		assertThat(foundReceipt).isEqualTo(receipt);

	}

	@Test
	public void testFindByIdReturnsTheCorrectReceipt() {
		User user = new User("user", "pw");
		Receipt receipt = new Receipt();
		receipt.setBuyer(user);
		receipt.setTimestamp(new GregorianCalendar(2019, 8, 10));
		receipt.setTotalPrice(10.0);

		entityManager.getTransaction().begin();
		entityManager.persist(user);
		entityManager.persist(receipt);
		entityManager.getTransaction().commit();

		entityManager.clear();

		Receipt foundReceipt = receiptRepositoryHibernate.getById(receipt.getId());
		assertThat(foundReceipt).isEqualTo(receipt);

	}

	@Test
	public void testGetAllReceiptBoughtByReturnsCorrectReceipts() {
		User debtorUser1 = new User("user1", "pw");
		User creditorUser1 = new User("user2", "pw");
		User creditorUser2 = new User("user3", "pw");

		Item item1 = new Item("potato", 10.0, Arrays.asList(debtorUser1, creditorUser1));
		Item item2 = new Item("tomato", 5.0, Arrays.asList(debtorUser1, creditorUser2));

		Receipt receipt1 = new Receipt();
		Receipt receipt2 = new Receipt();
		Accounting accountingToDebtorUserForReceipt1 = new Accounting(debtorUser1, item1.getPrice() / 2.0);
		Accounting accountingToDebtorUserForReceipt2 = new Accounting(debtorUser1, item2.getPrice() / 2.0);

		receipt1.setBuyer(creditorUser1);
		receipt1.setTimestamp(new GregorianCalendar(2019, 8, 10));
		receipt1.setItems(Arrays.asList(item1));
		receipt1.setAccountingList(Arrays.asList(accountingToDebtorUserForReceipt1));
		receipt1.setTotalPrice(item1.getPrice());

		receipt2.setBuyer(creditorUser2);
		receipt2.setTimestamp(new GregorianCalendar(2019, 8, 12));
		receipt2.setItems(Arrays.asList(item2));
		receipt2.setAccountingList(Arrays.asList(accountingToDebtorUserForReceipt1));
		receipt2.setTotalPrice(item2.getPrice());

		entityManager.getTransaction().begin();
		entityManager.persist(debtorUser1);
		entityManager.persist(creditorUser2);
		entityManager.persist(creditorUser1);

		entityManager.persist(item1);
		entityManager.persist(item2);

		entityManager.persist(accountingToDebtorUserForReceipt1);
		entityManager.persist(accountingToDebtorUserForReceipt2);
		entityManager.persist(receipt1);
		entityManager.persist(receipt2);
		entityManager.getTransaction().commit();

		entityManager.clear();

		List<Receipt> receiptsBoughtByCreditorUser1 = receiptRepositoryHibernate.getAllReceiptsBoughtBy(creditorUser1);
		List<Receipt> receiptsBoughtByCreditorUser2 = receiptRepositoryHibernate.getAllReceiptsBoughtBy(creditorUser2);

		assertThat(receiptsBoughtByCreditorUser1).containsExactlyInAnyOrder(receipt1);
		assertThat(receiptsBoughtByCreditorUser2).containsExactlyInAnyOrder(receipt2);

	}

	@Test
	public void testRemovingAReceiptWhenNotAttachedRemovesItsItemsAndAccountingsButNotTheUsers() {
		User debtorUser = new User("user1", "pw");
		User creditorUser = new User("user2", "pw");

		Item item1 = new Item("potato", 10.0, Arrays.asList(debtorUser, creditorUser));
		Item item2 = new Item("tomato", 5.0, Arrays.asList(debtorUser, creditorUser));

		Accounting accountingToUser1 = new Accounting(debtorUser, item1.getPrice() / 2.0 + item2.getPrice() / 2.0);

		Receipt receipt = new Receipt();
		receipt.setBuyer(creditorUser);
		receipt.setTimestamp(new GregorianCalendar(2019, 8, 10));
		receipt.setItems(Arrays.asList(item1, item2));
		receipt.setAccountingList(Arrays.asList(accountingToUser1));
		receipt.setTotalPrice(item1.getPrice() + item2.getPrice());

		entityManager.getTransaction().begin();
		entityManager.persist(debtorUser);
		entityManager.persist(creditorUser);
		entityManager.persist(receipt);
		entityManager.getTransaction().commit();

		entityManager.clear();

		entityManager.getTransaction().begin();
		receiptRepositoryHibernate.removeReceipt(receipt);
		entityManager.getTransaction().commit();

		
		assertThat(entityManager.find(User.class, creditorUser.getId())).isEqualTo(creditorUser);
		assertThat(entityManager.find(User.class, debtorUser.getId())).isEqualTo(debtorUser);
		assertThat(entityManager.find(Item.class, item1.getId())).isNull();
		assertThat(entityManager.find(Item.class, item2.getId())).isNull();
		assertThat(entityManager.find(Accounting.class, accountingToUser1.getId())).isNull();
		assertThat(entityManager.find(Receipt.class, receipt.getId())).isNull();
		System.out.println(receipt.getId());
	}

}
