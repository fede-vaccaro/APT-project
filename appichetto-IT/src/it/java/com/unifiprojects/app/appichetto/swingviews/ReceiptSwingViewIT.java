package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;

import javax.persistence.EntityManager;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.controllers.ReceiptController;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.modules.EntityManagerModule;
import com.unifiprojects.app.appichetto.modules.ReceiptModule;
import com.unifiprojects.app.appichetto.modules.RepositoriesModule;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepositoryHibernate;

public class ReceiptSwingViewIT extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private static MVCBaseTest baseTest = new MVCBaseTest();
	private HomepageSwingView homepageSwingView;
	private ReceiptSwingView receiptSwingView;
	private ReceiptController receiptController;
	private ReceiptRepository receiptRepository;
	private JTextComponentFixture nameBox;
	private JTextComponentFixture priceBox;
	private JTextComponentFixture quantityBox;
	private User pippo;
	private User pluto;
	private User mario;
	private static EntityManager entityManager;
	private static Injector injector;

	@BeforeClass
	public static void setupEntityManager() {

		Module repositoriesModule = new RepositoriesModule();
		
		Module entityManagerModule = new EntityManagerModule();

		Module receiptModule = new ReceiptModule();

		Injector persistenceInjector = Guice.createInjector(entityManagerModule);
		
		baseTest = persistenceInjector.getInstance(MVCBaseTest.class);
		entityManager = persistenceInjector.getInstance(EntityManager.class);
		
		injector = persistenceInjector.createChildInjector(repositoriesModule, receiptModule);
	}

	@AfterClass
	public static void closeEntityManager() {
		baseTest.closeEntityManager();
	}

	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);
		baseTest.wipeTablesBeforeTest();
		entityManager = baseTest.getEntityManager();

		GuiActionRunner.execute(() -> {
			pippo = new User("Pippo", "psw");
			pluto = new User("Pluto", "psw");
			mario = new User("Mario", "psw");
			entityManager.getTransaction().begin();
			entityManager.persist(pippo);
			entityManager.persist(pluto);
			entityManager.persist(mario);
			entityManager.getTransaction().commit();
			entityManager.clear();
			homepageSwingView = mock(HomepageSwingView.class);
			
			receiptRepository = injector.getInstance(ReceiptRepositoryHibernate.class);
			receiptSwingView = injector.getInstance(ReceiptSwingView.class);
			
			receiptController = (ReceiptController) receiptSwingView.getController();
			receiptController.setLoggedUser(pippo);
			receiptSwingView.setLinkedSwingView(homepageSwingView);
			receiptSwingView.setUsers();
			return receiptSwingView;
		});
		window = new FrameFixture(robot(), receiptSwingView.frame);
		window.show();
		nameBox = window.textBox("nameBox");
		priceBox = window.textBox("priceBox");
		quantityBox = window.textBox("quantityBox");
	}

	@Test
	public void testAddItem() {
		nameBox.enterText("Sugo");
		priceBox.enterText("2.2");
		quantityBox.enterText("2");
		window.list("usersList").selectItem(0);
		window.list("usersList").selectItem(1);
		window.list("usersList").selectItem(2);
		Item item = new Item("Sugo", 2.2, 2, Arrays.asList(pippo, pluto, mario));

		window.button(JButtonMatcher.withText("Save")).click();

		String[] listContents = window.list("itemsList").contents();
		assertThat(listContents).containsExactly(item.toString());
	}

	@Test
	public void testUpdateItem() {
		Item pasta = new Item("Pasta", 2.2, 2, Arrays.asList(pippo));
		Item sugo = new Item("Sugo", 2.2, 2, Arrays.asList(pippo, pluto, mario));
		Item updatedItem = new Item("Sugo", 1.1, 2, Arrays.asList(pippo, mario));

		GuiActionRunner.execute(() -> {
			receiptController.addItem(pasta);
			receiptController.addItem(sugo);
		});

		window.list("itemsList").selectItem(1);
		window.list("usersList").selectItem(1);
		priceBox.deleteText().enterText("1.1");

		window.button(JButtonMatcher.withText("Update")).click();

		String[] listContents = window.list("itemsList").contents();
		assertThat(listContents).containsExactlyInAnyOrder(pasta.toString(), updatedItem.toString());
	}

	@Test
	public void testDeleteItem() {
		Item pasta = new Item("Pasta", 2.2, 2, Arrays.asList(pippo));
		Item sugo = new Item("Sugo", 2.2, 2, Arrays.asList(pippo, pluto, mario));

		GuiActionRunner.execute(() -> {
			receiptController.addItem(pasta);
			receiptController.addItem(sugo);
		});

		window.list("itemsList").selectItem(1);

		window.button(JButtonMatcher.withText("Delete")).click();

		String[] listContents = window.list("itemsList").contents();
		assertThat(listContents).containsExactlyInAnyOrder(pasta.toString());
	}

	@Test
	public void testSaveReceipt() {
		Item sugo = new Item("Sugo", 2.2, 2, new ArrayList<>(Arrays.asList(pippo, pluto, mario)));
		Receipt receipt = new Receipt(pippo);
		receipt.addItem(sugo);
		receipt.addAccounting(new Accounting(pluto, 2.2));
		receipt.addAccounting(new Accounting(mario, 2.2));

		nameBox.enterText("Sugo");
		priceBox.enterText("2.2");
		quantityBox.enterText("2");
		window.list("usersList").selectItem(0);
		window.list("usersList").selectItem(1);
		window.list("usersList").selectItem(2);
		window.button(JButtonMatcher.withText("Save")).click();

		window.button(JButtonMatcher.withText("Save Receipt")).click();
		assertThat(receiptRepository.getAllReceiptsBoughtBy(pippo)).contains(receipt);
	}
}
