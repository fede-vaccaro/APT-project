package com.unifiprojects.app.appichetto.services;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.unifiprojects.app.appichetto.controllers.ReceiptController;
import com.unifiprojects.app.appichetto.managers.ReceiptManager;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.swingviews.ReceiptSwingView;

public class UpdateReceiptServiceTest {
	@Mock
	private ReceiptRepository receiptRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@Spy
	private ReceiptManager receiptManager = new ReceiptManager(receiptRepository);
	
	@Spy
	private ReceiptSwingView receiptView = new ReceiptSwingView();

	@Spy
	private ReceiptController receiptController = new ReceiptController(receiptManager, receiptView, userRepository);
	
	
	@InjectMocks
	private UpdateReceiptService updateReceiptService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testWhenArgumentOfExecuteIsNotAReceiptThenExceptionIsThrown() {
		try {
			updateReceiptService.execute(new Object());
			fail("Wrong argument");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage()).isEqualTo("Argument must be Receipt");
		}
	}

	@Test
	public void testExcecuteInitializeUpdateReceiptContext() {
		updateReceiptService.execute(new Receipt());
		
		assertThat(receiptView).isNotNull();
		assertThat(receiptController).isNotNull();
		assertThat(receiptManager).isNotNull();
	}

	@Test
	public void testExcecuteCallUploadReceiptInReceiptManager() {
		Receipt receipt = spy(new Receipt());
		updateReceiptService.setReceiptManager(receiptManager);
		
		updateReceiptService.execute(receipt);

		verify(receiptManager).uploadReceipt(receipt);
	}

	@Test
	public void testExcecuteCallUploadReceiptManagerInReceiptController() {
		Receipt receipt = spy(new Receipt());
		updateReceiptService.setReceiptManager(receiptManager);
		updateReceiptService.setReceiptController(receiptController);
		
		updateReceiptService.execute(receipt);
		
		// verify(receiptController).uploadReceiptManager(receiptManager);
	}

	@Test
	public void testExcecuteCallShowReceiptViewAndSetUsers() {
		Receipt receipt = new Receipt();
		doNothing().when(receiptView).setUsers();
		
		updateReceiptService.setReceiptView(receiptView);
		
		updateReceiptService.execute(receipt);
		
		verify(receiptView).show();
		verify(receiptView).setUsers();
	}

}
