package com.unifiprojects.app.appichetto.managers;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;

public class ReceiptManagerTest {
	
	@Mock
	private User receiptOwner;

	@Mock
	private Receipt receipt;
	
	@Mock
	private List<User> users;
	
	@Mock
	private RepositoryManager repositoryManager; 
	
	@InjectMocks
	private ReceiptManager receiptManager;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testAddItemAddItemInItemsAndUpdateAccountings() {

	}

}
