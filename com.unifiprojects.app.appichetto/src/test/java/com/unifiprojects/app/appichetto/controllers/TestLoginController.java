package com.unifiprojects.app.appichetto.controllerTests;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.controllers.LoginController;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.AlreadyExistentException;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.views.LoginView;
import static org.mockito.Mockito.*;

public class TestLoginController {
	
	private final String username = "testUsername";
	private final String password = "testPassword";
	
	@Mock
	LoginView loginView;

	@Mock
	UserRepository userRepository;

	@InjectMocks
	LoginController loginController;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testLoginGoToHomePageWhenUserIsExistent() {
		User testUser = new User(username, password);
		when(userRepository.findByUsername(username)).thenReturn(testUser);

		loginController.login(username, password);

		verify(loginView).goToHomePage();
	}

	@Test
	public void testLoginShowErrorMsgWhenUserIsExistentButPasswordIsWrong() {
		User testUser = new User(username, password);
		when(userRepository.findByUsername(username)).thenReturn(testUser);

		loginController.login(username, "WrongPassword!");

		verify(loginView).showErrorMsg("Wrong password!");
		verify(loginView, never()).goToHomePage();
	}

	@Test
	public void testLoginShowErrorMsgWhenUserIsNotExistent() {
		when(userRepository.findByUsername(username)).thenReturn(null);

		loginController.login(username, password);

		verify(loginView).showErrorMsg("User not signed in yet.");
		verify(loginView, never()).goToHomePage();

	}

	@Test
	public void testSignInGoToHomePageWhenUsernameIsAvailableThenView() {
		User user = new User(username, password);
		
		loginController.signIn(username, password);
		
		verify(userRepository).save(user);
		verify(loginView).goToHomePage();
	}

	@Test
	public void testSignInShowErrorMsgWhenUsernameIsNotAvailable() {
		User user = new User(username, password);
		
		doThrow(new AlreadyExistentException("Username already picked.")).when(userRepository).save(user);
	
		loginController.signIn(username, password);
		
		verify(loginView).showErrorMsg("Username already picked. Choice another username.");
		verify(loginView, never()).goToHomePage();
	}
	
	@Test
	public void testSignInShowErrorMsgWhenPasswordIsTooShort() {
		User user = new User(username, password);
		
		doThrow(new IllegalArgumentException()).when(userRepository).save(user);
	
		loginController.signIn(username, password);
		
		verify(loginView).showErrorMsg("Password too short. Choice another password.");
		verify(loginView, never()).goToHomePage();

	}
	
}
