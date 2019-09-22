package com.unifiprojects.app.appichetto.repositories;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.unifiprojects.app.appichetto.exceptions.AlreadyExistentException;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.repositories.UserRepositoryHibernate;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

	@Test
	public void testUserIsSavedForTheFirstTimeOnDB() {
		String username = "TestUser";
		String userPassword = "TestPassword";

		User testUser = new User(username, userPassword);

		userRepository.save(testUser);

		entityManager.clear();

		User retrievedUser = (User) entityManager.find(User.class, testUser.getId());
		assertThat(retrievedUser).isEqualTo(testUser);
	}

	@Test
	public void testUserIsUpdatedWhenSavedButNotReAddedToDB() {
		User testUser = persistAndGetTestUser();

		String newUsername = "NewName";
		testUser.setUsername(newUsername);

		userRepository.save(testUser);
		
		entityManager.clear();
		
		List<User> extractedUserList = entityManager.createQuery("from User", User.class).getResultList();

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

		assertThatExceptionOfType(AlreadyExistentException.class)
				.isThrownBy(() -> userRepository.save(testUserWithDuplicatedUserName)).withMessage(String
						.format("Username %s has been already picked.", testUserWithDuplicatedUserName.getUsername()));

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

}
