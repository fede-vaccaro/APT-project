package com.unifiprojects.app.appichetto.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.ArrayList;
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

import com.unifiprojects.app.appichetto.exceptions.AlreadyExistentException;
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
		if(transaction.isActive()) {
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
		User testUser = persistAndGetTestUser();

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
		User testUser = persistAndGetTestUser();

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

	private User persistAndGetTestUser() {
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
		User testUser = persistAndGetTestUser();

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
		User testUser = persistAndGetTestUser();

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

}
