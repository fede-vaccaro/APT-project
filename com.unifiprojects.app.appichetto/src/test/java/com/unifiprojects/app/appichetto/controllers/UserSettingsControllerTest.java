package com.unifiprojects.app.appichetto.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.verification.NoMoreInteractions;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.unifiprojects.app.appichetto.exceptions.AlreadyExistentException;
import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionCommands;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;
import com.unifiprojects.app.appichetto.views.UserSettingsView;

public class UserSettingsControllerTest {

	@InjectMocks
	private UserSettingsController userSettingsController;

	@Mock
	private TransactionHandler transactionHandler;

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserSettingsView userSettingsView;

	@Spy
	private User loggedUser = new User("logged", "pw");

	@Captor
	private ArgumentCaptor<TransactionCommands> commandCaptor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		userSettingsController.setLoggedUser(loggedUser);
	}

	@Test
	public void testLoadLoggedUserCredential() {
		userSettingsController.loadUserCredential();

		verify(userSettingsView).loadUserCredential(loggedUser.getUsername(), loggedUser.getPassword());
	}

	@Test
	public void testUpdateCredentialWithNewPasswordAndUsername() {
		String newUsername = "newUsername";
		String newPassword = "newPassword";

		userSettingsController.updateCredential(newUsername, newPassword);

		verify(loggedUser).setUsername(newUsername);
		verify(loggedUser).setPassword(newPassword);
		verify(userRepository).save(loggedUser);
	}

	@Test
	public void testUpdateCredentialShowErrorMsgWhenUserNameIsNotAvailable() {
		String newUsername = "newUsername";
		String newPassword = "newPassword";

		doThrow(AlreadyExistentException.class).when(userRepository).save(loggedUser);

		userSettingsController.updateCredential(newUsername, newPassword);

		verify(loggedUser).setUsername(newUsername);
		verify(loggedUser).setPassword(newPassword);

		verify(userSettingsView).showErrorMsg(anyString());

	}

	@Test
	public void testUpdateCredentialReloadTheUserWhenUserNameIsNotAvailable() {
		String newUsername = "newUsername";
		String newPassword = "newPassword";

		doThrow(AlreadyExistentException.class).when(userRepository).save(loggedUser);
		User notModifiedUser = new User("logged", "pw");
		when(userRepository.findById(loggedUser.getId())).thenReturn(notModifiedUser);

		userSettingsController.updateCredential(newUsername, newPassword);

		verify(loggedUser).setUsername(newUsername);
		verify(loggedUser).setPassword(newPassword);

		verify(userSettingsView).loadUserCredential(notModifiedUser.getUsername(), notModifiedUser.getPassword());
	}

	@Test
	public void testUpdateUserCallUpdateCredentialInTransaction() {
		String newUsername = "newUsername";
		String newPassword = "newPassword";

		userSettingsController.updateUser(newUsername, newPassword);

		verify(transactionHandler).doInTransaction(commandCaptor.capture());
		commandCaptor.getValue().execute();

		// a way to assert that the content of the transaction has been called
		verify(loggedUser).setUsername(newUsername);
		verify(loggedUser).setPassword(newPassword);
		verify(userRepository).save(loggedUser);
	}
	
	@Test
	public void testUpdateUserShowErrorMsgWhenUncommittableExceptionIsThrown() {
		String newUsername = "newUsername";
		String newPassword = "newPassword";

		doThrow(UncommittableTransactionException.class).when(transactionHandler).doInTransaction(any(TransactionCommands.class));
		userSettingsController.updateUser(newUsername, newPassword);

		verify(userSettingsView).showErrorMsg(anyString());
	}
	
	@Test
	public void testDeleteUser() {
		userSettingsController.deleteUser();
		
		verify(transactionHandler).doInTransaction(commandCaptor.capture());
		commandCaptor.getValue().execute();
		
		verify(userRepository).removeUser(loggedUser);
		verify(userSettingsView).closeApplication();
		verifyNoMoreInteractions(userSettingsView);
	}
	
	@Test
	public void testDeleteUserShowErrorMsgWhenUncommittableExceptionIsThrown() {

		doThrow(UncommittableTransactionException.class).when(transactionHandler).doInTransaction(any(TransactionCommands.class));

		userSettingsController.deleteUser();
		
		verify(transactionHandler).doInTransaction(commandCaptor.capture());
		commandCaptor.getValue().execute();
		
		verify(userSettingsView).showErrorMsg(anyString());
		verify(userRepository).removeUser(loggedUser);
		verifyNoMoreInteractions(userSettingsView);
		verifyNoMoreInteractions(userRepository);
	}

}
