package com.unifiprojects.app.appichetto.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.exceptions.AlreadyExistentException;
import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionCommands;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;
import com.unifiprojects.app.appichetto.views.HomepageView;
import com.unifiprojects.app.appichetto.views.UserPanelView;

public class UserPanelControllerTest {

	@Captor
	private ArgumentCaptor<TransactionCommands> command;

	@Mock
	private TransactionHandler transaction;

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserPanelView userPanelView;

	@Mock
	private HomepageView homepageView;

	@InjectMocks
	private UserPanelController userPanelController;

	private User loggedUser;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		loggedUser = spy(new User("logged", "pw"));

		userPanelController.setHomepageView(homepageView);
		userPanelController.setLoggedUser(loggedUser);


	}

	@Test
	public void testShowUser() {
		User logged = userPanelController.getLoggedUser();

		userPanelController.showUser();

		verify(userPanelView).showUser(logged.getUsername());
	}

	private void verifyUserRepositorySaveIsCalledInTransaction() {
		verify(transaction).doInTransaction(command.capture());
		command.getValue().execute();
		verify(userRepository).save(loggedUser);
		
	}

	@Test
	public void testChangeCredential() {
		String newName = "newName";
		String newPassword = "newPassword";
		
		when(userRepository.findById(loggedUser.getId())).thenReturn(loggedUser);

		userPanelController.changeCredential(newName, newPassword);

		verify(loggedUser).setUsername(newName);
		verify(loggedUser).setPassword(newPassword);

		verifyUserRepositorySaveIsCalledInTransaction();

		verify(homepageView).setLoggedUser(loggedUser);
		verify(userPanelView).showUser(loggedUser.getUsername());

	}

	@Test
	public void testChangeCredentialWithOnlyUserName() {
		String newName = "newName";

		when(userRepository.findById(loggedUser.getId())).thenReturn(loggedUser);
		
		userPanelController.changeCredential(newName, null);

		verify(loggedUser).setUsername(newName);
		verify(loggedUser, never()).setPassword(anyString());

		verifyUserRepositorySaveIsCalledInTransaction();

		verify(homepageView).setLoggedUser(loggedUser);
		verify(userPanelView).showUser(loggedUser.getUsername());

	}

	@Test
	public void testChangeCredentialWithOnlyUserPassword() {
		String newPassword = "newPassword";
		
		when(userRepository.findById(loggedUser.getId())).thenReturn(loggedUser);
		
		userPanelController.changeCredential(null, newPassword);

		verify(loggedUser, never()).setUsername(anyString());
		verify(loggedUser).setPassword(newPassword);

		verifyUserRepositorySaveIsCalledInTransaction();

		verify(homepageView).setLoggedUser(loggedUser);
		verify(userPanelView).showUser(loggedUser.getUsername());

	}

	@Test
	public void testErrorMessageIsShownIfTheUserNameIsNotValid() {
		String newName = "newName";
		
		when(userRepository.findById(loggedUser.getId())).thenReturn(loggedUser);

		String exceptionMessage = "Invalid!";
		doThrow(new IllegalArgumentException(exceptionMessage)).when(transaction)
				.doInTransaction(any(TransactionCommands.class));

		userPanelController.changeCredential(newName, null);

		verify(loggedUser).setUsername(newName);
		verify(userPanelView).showErrorMsg(exceptionMessage);
		verify(userPanelView).showUser(loggedUser.getUsername());
	}

	@Test
	public void testSetLoggedUserIsNeverCalledIfAnyExceptionIsLaunched() {
		String newName = "newName";
		
		when(userRepository.findById(loggedUser.getId())).thenReturn(loggedUser);

		String exceptionMessage = "Invalid!";
		doThrow(new IllegalArgumentException(exceptionMessage)).when(transaction)
				.doInTransaction(any(TransactionCommands.class));

		userPanelController.changeCredential(newName, null);

		verifyNoMoreInteractions(homepageView);
		verify(userPanelView).showUser(loggedUser.getUsername());
	}

	@Test
	public void testUserIsAlwaysReloadedFromDb() {

		String oldName = loggedUser.getUsername();
		String oldPassword = loggedUser.getPassword();
		String newName = "newName";

		String exceptionMessage = "Invalid!";
		doThrow(new IllegalArgumentException(exceptionMessage)).when(transaction)
				.doInTransaction(any(TransactionCommands.class));
		when(userRepository.findById(loggedUser.getId())).thenReturn(new User(oldName, oldPassword));

		userPanelController.changeCredential(newName, null);

		verify(userRepository).findById(loggedUser.getId());
		verify(userPanelView).showErrorMsg(exceptionMessage);
		verify(userPanelView).showUser(oldName);

	}

	@Test
	public void testUsernameAlreadyExistentExceptionShowErrorMsg() {
		String newName = "newName";
		
		when(userRepository.findById(loggedUser.getId())).thenReturn(loggedUser);

		String exceptionMessage = "Invalid!";
		doThrow(new AlreadyExistentException(exceptionMessage)).when(transaction)
				.doInTransaction(any(TransactionCommands.class));

		userPanelController.changeCredential(newName, null);

		verify(userPanelView).showErrorMsg(exceptionMessage);
		verify(userPanelView).showUser(loggedUser.getUsername());
	}

	@Test
	public void testUncommittableExceptionShowErrorMsg() {
		String newName = "newName";
		
		when(userRepository.findById(loggedUser.getId())).thenReturn(loggedUser);
		
		String exceptionMessage = "Invalid!";
		doThrow(new UncommittableTransactionException(exceptionMessage)).when(transaction)
				.doInTransaction(any(TransactionCommands.class));

		userPanelController.changeCredential(newName, null);

		verify(userPanelView).showErrorMsg("Something went wrong while committing changes.");
		verify(userPanelView).showUser(loggedUser.getUsername());
	}

	@Test
	public void testRemoveUser() {
		userPanelController.deleteUser();

		verify(transaction).doInTransaction(command.capture());
		command.getValue().execute();
		verify(userRepository).removeUser(loggedUser);
	}

	@Test
	public void testGoToLoginView() {
		userPanelController.goToLoginView();

		verify(userPanelView).goToLoginView();
	}

	@Test
	public void testRemovingUserRedirectToLoginView() {
		userPanelController.deleteUser();

		verify(userPanelView).goToLoginView();
	}

	@Test
	public void testUncommittableExceptionRemovingUserShowErrorMsgAndNotRedirect() {
		String exceptionMessage = "Invalid!";
		doThrow(new UncommittableTransactionException(exceptionMessage)).when(transaction)
				.doInTransaction(any(TransactionCommands.class));

		userPanelController.deleteUser();

		verify(userPanelView).showErrorMsg("Something went wrong while committing changes.");
		verifyNoMoreInteractions(userPanelView);
	}

	@Test
	public void testUpdate() {
		userPanelController.update();

		verify(userPanelView).showUser(loggedUser.getUsername());
	}

}
