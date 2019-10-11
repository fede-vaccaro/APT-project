package com.unifiprojects.app.appichetto.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.controllers.ReceiptGenerator;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.TestUserRepositoryHibernate;

public class UserRepositoryHibernateIT {

	private static MVCBaseTest baseTest = new MVCBaseTest();
	private static EntityManager entityManager;

	private UserRepositoryHibernate userRepository;

	@BeforeClass
	public static void setupEntityManager() {
		baseTest.setupEntityManager();
	}

	@AfterClass
	public static void closeEntityManager() {
		baseTest.closeEntityManager();
	}

	@Before
	public void setUp() {
		baseTest.wipeTablesBeforeTest();
		entityManager = baseTest.getEntityManager();
		userRepository = new UserRepositoryHibernate(entityManager);
	}

	@Test
	public void testSaveUser() {
		entityManager.getTransaction().begin();
		User user = new User("username", "password");
		userRepository.save(user);
		entityManager.getTransaction().commit();

		entityManager.clear();

		User saved = entityManager.find(User.class, user.getId());
		assertThat(saved.getId()).isEqualTo(user.getId());
		assertThat(saved).isEqualTo(user);
	}

	@Test
	public void testUpdateUser() {
		entityManager.getTransaction().begin();
		User user = new User("username", "password");
		entityManager.getTransaction().commit();

		entityManager.clear();

		user.setPassword("newPassword");
		entityManager.getTransaction().begin();
		userRepository.save(user);
		entityManager.getTransaction().commit();
		User saved = entityManager.find(User.class, user.getId());
		assertThat(saved.getId()).isEqualTo(user.getId());
		assertThat(saved).isEqualTo(user);
	}

	@Test
	public void testFindAll() {
		entityManager.getTransaction().begin();
		User user1 = new User("username1", "password1");
		User user2 = new User("username2", "password2");
		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.getTransaction().commit();

		entityManager.clear();

		List<User> allUsers = userRepository.findAll();

		assertThat(allUsers).containsExactlyInAnyOrder(user1, user2);
	}

	@Test
	public void testByUsername() {
		entityManager.getTransaction().begin();
		User user = new User("Heisenberg59", "password");
		entityManager.persist(user);
		entityManager.getTransaction().commit();

		entityManager.clear();

		User saved = userRepository.findByUsername(user.getUsername());
		assertThat(saved).isEqualTo(user);
		assertThat(saved.getId()).isEqualTo(user.getId());
	}

	@Test
	public void testRemoveUser() {
		User deletingUser = TestUserRepositoryHibernate.persistGetTestUserAndClear(entityManager);
		User user2 = new User("anotherParticipant", "");
		User user3 = new User("buyer", "");

		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(deletingUser, user3,
				new GregorianCalendar(2019, 8, 10));
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(deletingUser, user2,
				new GregorianCalendar(2019, 8, 11));
		Receipt receipt3 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(user2, deletingUser,
				new GregorianCalendar(2019, 8, 10));
		Receipt receipt4 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(user3, deletingUser,
				new GregorianCalendar(2019, 8, 11));

		receipt1.setAccountingList(Arrays.asList(new Accounting(deletingUser, 10.0)));
		receipt2.setAccountingList(Arrays.asList(new Accounting(deletingUser, 10.0), new Accounting(user2, 5.0)));

		entityManager.getTransaction().begin();
		entityManager.persist(user3);
		entityManager.persist(user2);
		entityManager.persist(receipt1);
		entityManager.persist(receipt2);
		entityManager.persist(receipt3);
		entityManager.persist(receipt4);
		entityManager.getTransaction().commit();

		entityManager.clear();

		entityManager.getTransaction().begin();
		userRepository.removeUser(deletingUser);
		entityManager.getTransaction().commit();

		Receipt reloadedReceipt1 = entityManager.find(Receipt.class, receipt1.getId());
		Receipt reloadedReceipt2 = entityManager.find(Receipt.class, receipt2.getId());

		assertThat(entityManager.find(Receipt.class, receipt3.getId())).isNull();
		assertThat(entityManager.find(Receipt.class, receipt4.getId())).isNull();
		assertThat(entityManager.find(User.class, user3.getId())).isNotNull();
		assertThat(entityManager.find(User.class, user2.getId())).isNotNull();
		assertThat(reloadedReceipt1).isNotNull();
		assertThat(reloadedReceipt2).isNotNull();

		for (Accounting accounting : reloadedReceipt1.getAccountings())
			assertThat(accounting).isNull();

		assertThat(reloadedReceipt2.getAccountings().get(0).getUser()).isEqualTo(user2);

		List<Item> allItems = new ArrayList<>(reloadedReceipt1.getItems());
		allItems.addAll(reloadedReceipt2.getItems());

		assertThat(allItems).isNotEmpty();
		allItems.forEach(i -> assertThat(i.getOwners()).doesNotContain(deletingUser));
		allItems.forEach(i -> assertThat(i.getOwners()).isNotEmpty());
	}

}
