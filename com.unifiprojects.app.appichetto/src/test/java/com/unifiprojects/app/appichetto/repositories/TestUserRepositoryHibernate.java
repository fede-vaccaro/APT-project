package com.unifiprojects.app.appichetto.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.unifiprojects.app.appichetto.controllers.ReceiptGenerator;
import com.unifiprojects.app.appichetto.exceptions.AlreadyExistentException;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;

public class TestUserRepositoryHibernate {

	private UserRepository userRepository;
	private static EntityManagerFactory entityManagerFactory;
	private static EntityManager entityManager;

	@BeforeClass
	public static void setUp() {
		entityManagerFactory = Persistence.createEntityManagerFactory("test-persistence-unit");
		entityManager = entityManagerFactory.createEntityManager();
	}

	@AfterClass
	public static void tearDown() {
		entityManager.close();
		entityManagerFactory.close();
	}

	@Before
	public void createUserRepository() {
		entityManager.getTransaction().begin();
		entityManager.createNativeQuery("TRUNCATE SCHEMA public AND COMMIT").executeUpdate();
		entityManager.getTransaction().commit();

		userRepository = new UserRepositoryHibernate(entityManager);
	}

	@After
	public void closeTransaction() {
		EntityTransaction transaction = entityManager.getTransaction();
		if (transaction.isActive()) {
			transaction.commit();
		}
	}

	@Test
	public void testUserIsSavedForTheFirstTimeOnDB() {
		String username = "TestUser";
		String userPassword = "TestPassword";

		User testUser = new User(username, userPassword);

		entityManager.getTransaction().begin();
		userRepository.save(testUser);
		entityManager.getTransaction().commit();

		entityManager.clear();

		User retrievedUser = (User) entityManager.find(User.class, testUser.getId());
		assertThat(retrievedUser).isEqualTo(testUser);
	}

	@Test
	public void testUserIsUpdatedWhenSavedButNotReAddedToDB() {
		User testUser = persistGetTestUserAndClear(entityManager);

		String newUsername = "NewName";
		testUser.setUsername(newUsername);

		entityManager.getTransaction().begin();
		userRepository.save(testUser);
		entityManager.getTransaction().commit();

		entityManager.clear();

		List<User> extractedUserList = entityManager.createQuery("from users", User.class).getResultList();

		assertThat(extractedUserList).containsOnly(testUser);
		User extractedUser = extractedUserList.get(0);
		assertThat(extractedUser.getId()).isEqualTo(testUser.getId());
		assertThat(extractedUser.getUsername()).isEqualTo(testUser.getUsername());
	}

	@Test
	public void testUserCantBeSavedWhenUsernameIsAlreadyUsed() {
		User testUser = persistGetTestUserAndClear(entityManager);

		String username = testUser.getUsername();
		String userPassword = "testUser2Password";

		User testUserWithDuplicatedUserName = new User(username, userPassword);

		assertThatExceptionOfType(AlreadyExistentException.class).isThrownBy(() -> {
			entityManager.getTransaction().begin();
			userRepository.save(testUserWithDuplicatedUserName);
			entityManager.getTransaction().commit();
		}).withMessage(
				String.format("Username %s has been already picked.", testUserWithDuplicatedUserName.getUsername()));

	}

	static User persistGetTestUserAndClear(EntityManager entityManager) {
		String username = "TestUser";
		String userPassword = "TestPassword";

		User testUser = new User(username, userPassword);
		entityManager.getTransaction().begin();
		entityManager.persist(testUser);
		entityManager.getTransaction().commit();
		entityManager.clear();
		return testUser;
	}

	@Test
	public void testFindByIdWhenUserIsOnDB() {
		User testUser = persistGetTestUserAndClear(entityManager);

		User retrievedUser = userRepository.findById(testUser.getId());

		assertThat(retrievedUser).isEqualTo(testUser);
	}

	@Test
	public void testFindByIdWhenUserIsNotOnDbAndReturnNull() {
		Long testFakeId = 9999999l;

		User retrievedUser = userRepository.findById(testFakeId);

		assertThat(retrievedUser).isNull();
	}

	@Test
	public void testFindAllWhenUserTableIsFilled() {
		String username = "TestUser";
		String userPassword = "TestPassword";

		int userCount = 10;
		List<User> userList = new ArrayList<User>();

		entityManager.getTransaction().begin();
		for (int i = 0; i < userCount; i++) {
			User u = new User(username + i, userPassword + i);
			userList.add(u);
			entityManager.persist(u);
		}
		entityManager.getTransaction().commit();
		entityManager.clear();

		ArrayList<User> retrievedUsers = (ArrayList<User>) userRepository.findAll();
		assertThat(userList).isEqualTo(retrievedUsers).hasSize(userCount);
	}

	@Test
	public void testFindAllWhenUserTableIsEmpty() {
		ArrayList<User> retrievedUsers = (ArrayList<User>) userRepository.findAll();
		assertThat(retrievedUsers).isEmpty();
	}

	@Test
	public void testFindByUsernameWhenUserIsActuallyOnDb() {
		User testUser = persistGetTestUserAndClear(entityManager);

		User retrievedUser = userRepository.findByUsername(testUser.getUsername());

		assertThat(testUser).isEqualTo(retrievedUser);
	}

	@Test
	public void testFindByUsernameReturnNullWhenUserIsNotExistent() {
		String fakeName = "fakeName";
		User retrievedUser = userRepository.findByUsername(fakeName);

		assertThat(retrievedUser).isNull();
	}

	@Test
	public void testSavingUserWithEmptyUsernameLaunchException() {
		User user = new User("   ", "pw");

		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {

			entityManager.getTransaction().begin();
			userRepository.save(user);
			entityManager.getTransaction().commit();
		}).withMessage("You can't use empty string username.");
		if (entityManager.getTransaction().isActive())
			entityManager.getTransaction().commit();

	}

	@Test
	public void testRemoveUserWhenHeIsNotAttachedAndHasNoReceipts() {
		User deletingUser = persistGetTestUserAndClear(entityManager);

		entityManager.getTransaction().begin();
		userRepository.removeUser(deletingUser);
		entityManager.getTransaction().commit();

		assertThat(entityManager.find(User.class, deletingUser.getId())).isNull();

	}

	@Test
	public void testRemoveUserWhenHeIsAttachedAndHasNoReceipts() {
		User deletingUser = new User("testUser", "");

		entityManager.getTransaction().begin();
		entityManager.persist(deletingUser);
		entityManager.getTransaction().commit();

		entityManager.getTransaction().begin();
		userRepository.removeUser(deletingUser);
		entityManager.getTransaction().commit();

		assertThat(entityManager.find(User.class, deletingUser.getId())).isNull();
	}

	@Test
	public void testRemoveUserRemovesAllHisBoughtReceipts() {
		User deletingUser = persistGetTestUserAndClear(entityManager);

		User participant = new User("Participant", "");
		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(participant,
				deletingUser, new GregorianCalendar(2019, 8, 10));
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(participant,
				deletingUser, new GregorianCalendar(2019, 8, 11));

		entityManager.getTransaction().begin();
		entityManager.persist(participant);
		entityManager.persist(receipt1);
		entityManager.persist(receipt2);
		entityManager.getTransaction().commit();

		entityManager.clear();
		
		entityManager.getTransaction().begin();
		UserRepositoryHibernate userRepositoryHibernate = (UserRepositoryHibernate) userRepository;
		userRepositoryHibernate.deleteBoughtReceipts(deletingUser);
		entityManager.getTransaction().commit();

		assertThat(entityManager.find(User.class, participant.getId())).isNotNull();
		assertThat(entityManager.find(Receipt.class, receipt1.getId())).isNull();
		assertThat(entityManager.find(Receipt.class, receipt2.getId())).isNull();
	}

	@Test
	public void testRemoveUserRemovesAllHisAccountings() {
		User deletingUser = persistGetTestUserAndClear(entityManager);
		User user2 = new User("anotherParticipant", "");
		User buyer = new User("buyer", "");

		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(deletingUser, buyer,
				new GregorianCalendar(2019, 8, 10), Arrays.asList());
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(deletingUser, user2,
				new GregorianCalendar(2019, 8, 11), Arrays.asList());

		receipt1.setAccountingList(Arrays.asList(new Accounting(deletingUser, 10.0)));
		receipt2.setAccountingList(Arrays.asList(new Accounting(deletingUser, 10.0), new Accounting(user2, 5.0)));

		entityManager.getTransaction().begin();
		entityManager.persist(buyer);
		entityManager.persist(user2);
		entityManager.persist(receipt1);
		entityManager.persist(receipt2);
		entityManager.getTransaction().commit();

		entityManager.clear();
		
		entityManager.getTransaction().begin();
		UserRepositoryHibernate userRepositoryHibernate = (UserRepositoryHibernate) userRepository;
		userRepositoryHibernate.deleteAccountings(deletingUser);
		entityManager.getTransaction().commit();
		
		Receipt reloadedReceipt1 = entityManager.find(Receipt.class, receipt1.getId());
		Receipt reloadedReceipt2 = entityManager.find(Receipt.class, receipt2.getId());
		
		assertThat(entityManager.find(User.class, buyer.getId())).isNotNull();
		assertThat(entityManager.find(User.class, user2.getId())).isNotNull();
		assertThat(reloadedReceipt1).isNotNull();
		assertThat(reloadedReceipt2).isNotNull();

		for (Accounting accounting : reloadedReceipt1.getAccountings())
			assertThat(accounting).isNull();
		for (Accounting accounting : reloadedReceipt2.getAccountings())
			assertThat(accounting.getUser()).isEqualTo(user2);
	}

	@Test
	public void testRemoveUserRemovesAllHisItems() {
		User deletingUser = persistGetTestUserAndClear(entityManager);
		User user2 = new User("anotherParticipant", "");
		User buyer = new User("buyer", "");

		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(deletingUser, buyer,
				new GregorianCalendar(2019, 8, 10));
		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(deletingUser, buyer,
				new GregorianCalendar(2019, 8, 11));

		receipt1.setAccountingList(Arrays.asList(new Accounting(deletingUser, 10.0)));
		receipt2.setAccountingList(Arrays.asList(new Accounting(deletingUser, 10.0), new Accounting(user2, 5.0)));

		entityManager.getTransaction().begin();
		entityManager.persist(buyer);
		entityManager.persist(user2);
		entityManager.persist(receipt1);
		entityManager.persist(receipt2);
		entityManager.getTransaction().commit();

		entityManager.clear();
		
		entityManager.getTransaction().begin();
		UserRepositoryHibernate userRepositoryHibernate = (UserRepositoryHibernate) userRepository;
		userRepositoryHibernate.deleteItems(deletingUser);
		entityManager.getTransaction().commit();

		Receipt reloadedReceipt1 = entityManager.find(Receipt.class, receipt1.getId());
		Receipt reloadedReceipt2 = entityManager.find(Receipt.class, receipt2.getId());

		assertThat(entityManager.find(User.class, buyer.getId())).isNotNull();
		assertThat(entityManager.find(User.class, user2.getId())).isNotNull();
		assertThat(reloadedReceipt1).isNotNull();
		assertThat(reloadedReceipt2).isNotNull();

		List<Item> allItems = new ArrayList<>(reloadedReceipt1.getItems());
		allItems.addAll(reloadedReceipt2.getItems());

		assertThat(allItems).isNotEmpty();
		allItems.forEach(i -> assertThat(i.getOwners()).doesNotContain(deletingUser).isNotEmpty());

	}

	@Test
	public void testRemoveUser() {
		User deletingUser = persistGetTestUserAndClear(entityManager);
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
