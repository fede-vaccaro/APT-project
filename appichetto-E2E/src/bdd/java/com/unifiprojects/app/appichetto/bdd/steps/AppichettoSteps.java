package com.unifiprojects.app.appichetto.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import org.assertj.core.util.Arrays;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.ComponentLookupScope;
import org.assertj.swing.core.Robot;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JTextComponentMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.unifiprojects.app.appichetto.controllers.ReceiptGenerator;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.modules.EntityManagerModule;
import com.unifiprojects.app.appichetto.modules.RepositoriesModule;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepositoryHibernate;
import com.unifiprojects.app.appichetto.repositories.UserRepositoryHibernate;
import com.unifiprojects.app.appichetto.swingviews.PayReceiptsViewSwing;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AppichettoSteps {

	private Map<String, String> usersOnDb = new HashMap<String, String>();
	private UserRepositoryHibernate userRepository;
	private EntityManager entityManager;
	private FrameFixture window;
	Robot robot;
	private double receipt_debt;

	public void wipeTablesBeforeTest() {
		entityManager.getTransaction().begin();
		entityManager.createNativeQuery("do\n" + "$$\n" + "declare\n" + "  l_stmt text;\n" + "begin\n"
				+ "  select 'truncate ' || string_agg(format('%I.%I', schemaname, tablename), ',')\n"
				+ "    into l_stmt\n" + "  from pg_tables\n" + "  where schemaname in ('public');\n" + "\n"
				+ "  execute l_stmt;\n" + "end;\n" + "$$").executeUpdate();
		entityManager.getTransaction().commit();
		entityManager.clear();
	}

	@Before
	public void setUp() {
		Injector entityManagerInjector = Guice.createInjector(new EntityManagerModule());
		Injector repositoryInjector = entityManagerInjector.createChildInjector(new RepositoriesModule());
		entityManager = entityManagerInjector.getInstance(EntityManager.class);
		userRepository = repositoryInjector.getInstance(UserRepositoryHibernate.class);
		wipeTablesBeforeTest();
	}

	@After
	public void cleanAndTearDown() {
		window.cleanUp();
	}

	@Given("The database contains user")
	public void the_database_contains_user(List<User> users) {
		entityManager.getTransaction().begin();
		users.stream().forEach(user -> {
			userRepository.save(user);
			usersOnDb.putIfAbsent(user.getUsername(), user.getPassword());
		});
		entityManager.getTransaction().commit();
	}

	@When("Application start")
	public void application_start() {
		robot = BasicRobot.robotWithNewAwtHierarchy();
		robot.settings().componentLookupScope(ComponentLookupScope.ALL);
		application("com.unifiprojects.app.appichetto.main.Main").start();
	}

	@When("{string} view shows")
	public void view_shows(String viewToShow) {
		window = WindowFinder.findFrame(viewToShow).using(robot);
	}

	@When("{string} view shown")
	public void view_shown(String actualView) {
		assertThat(window.target().getTitle()).isEqualTo(actualView);
	}

	@When("Write {string} in {string} text box")
	public void write_in_and_in(String string, String string2) {
		window.textBox(JTextComponentMatcher.withName(string2)).deleteText().enterText(string);
	}

	@When("Click {string} button")
	public void click_button(String string) {
		window.button(JButtonMatcher.withText(string)).click();
//		if (string.equals("Save Receipt"))
//			window = WindowFinder.findFrame("Homepage").using(robot);
//		else if(string.equals("Update receipt"))
//			window = WindowFinder.findFrame("Create Receipt").using(robot);

	}

	@When("Click {string} button on homepage")
	public void click_button_on_homepage(String buttonName) {
		window.button(JButtonMatcher.withText(buttonName)).click();
		if (buttonName.equals("Log Out")) {
			buttonName = "Login";
			System.out.println(String.format("Changed 'Log Out' to %s", buttonName));
		}
		window = WindowFinder.findFrame(buttonName).using(robot);
	}

	@Then("The view contain the following message {string}")
	public void the_view_contain_the_following_message(String string) {
		assertThat(window.label("errorMsg").text()).contains(string);
	}

	@Then("debt to user is {float}")
	public void debt_to_user_is(float value) {
		assertThat(window.label("totalDebtToUser").text()).contains(String.format("Total debt to user: %.2f", value));
	}

	@Given("User {string} is logged")
	public void user_is_logged(String usernameToLog) {
		window.textBox(JTextComponentMatcher.withName("Username")).deleteText().enterText(usernameToLog);
		window.textBox(JTextComponentMatcher.withName("Password")).deleteText().enterText(usersOnDb.get(usernameToLog));
		window.button(JButtonMatcher.withText("Log-in")).click();
		window = WindowFinder.findFrame("Homepage").using(robot);
	}

	@When("Add new item")
	public void add_new_item(DataTable items) {
		List<Map<String, String>> itemsList = items.asMaps(String.class, String.class);

		itemsList.stream().forEach(item -> {
			window.textBox(JTextComponentMatcher.withName("nameBox")).enterText(item.get("name"));
			window.textBox(JTextComponentMatcher.withName("priceBox")).enterText(item.get("price"));
			window.textBox(JTextComponentMatcher.withName("quantityBox")).enterText(item.get("quantity"));
			List<Object> owners = new ArrayList<>(Arrays.asList(item.get("owners").split(" ")));
			owners.stream().forEach(owner -> window.list("usersList").selectItem(owner.toString()));
			window.button(JButtonMatcher.withText("Save")).click();
		});
	}

	@Then("{string} contains")
	public void contains(String listName, DataTable dataTable) {
		List<String> itemsString = dataTable.asList();
		String[] itemListContents = window.list(listName).contents();

		assertThat(itemListContents).containsExactlyInAnyOrderElementsOf(itemsString);
	}

	@Then("Going back")
	public void going_back() {
		window.button(JButtonMatcher.withText("Back")).click();
		window = WindowFinder.findFrame("Homepage").using(robot);
	}

	// @Then("Log out")
	@When("Log out")
	public void log_out() {
		window.button(JButtonMatcher.withText("Log Out")).click();
//		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
//			@Override
//			protected boolean isMatching(JFrame frame) {
//				return "Login".equals(frame.getTitle()) && frame.isShowing();
//			}
//		}).using(robot);
	}

	@Then("Set {string} in {string}")
	public void set_in(String user, String list) {
		window.comboBox(list).selectItem(user);
	}

	@When("Select the receipt")
	public void select_the_receipt() {
		window.list("Receipts list").clickItem(0);
	}

	@Given("The user {string} has a receipt shared with {string}")
	public void the_database_contains_receipt_of_with(String string, String string2) {
		User buyer = entityManager.createQuery("from users where username = :username", User.class)
				.setParameter("username", string).getSingleResult();
		User payer = entityManager.createQuery("from users where username = :username", User.class)
				.setParameter("username", string2).getSingleResult();
		Receipt receipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(payer, buyer);
		ReceiptRepository receiptRepository = new ReceiptRepositoryHibernate(entityManager);
		entityManager.getTransaction().begin();
		receiptRepository.saveReceipt(receipt);
		entityManager.getTransaction().commit();

		receipt_debt = receipt.getAccountings().get(0).getAmount();

	}

	@When("Write the import and pay")
	public void write_the_import_and_pay() {
		window.textBox("enterAmountField").enterText(String.format("%.2f", receipt_debt));
		click_button("Pay");
	}

	@When("Edit the items discharging {string}")
	public void edit_the_items_discharging_user(String string) {
		List<String> itemList = ImmutableList.copyOf(window.list("Items list").contents());
		List<String> usersList = ImmutableList.copyOf(window.list("usersList").contents());

		int indexOfUser = usersList.indexOf(string);

		itemList.forEach(item -> {
			window.list("Items list").selectItem(item);
			window.list("usersList").unselectItem(indexOfUser);
			click_button("Update");
		});
	}

	@Then("Has a refund receipt with the amount already paid with {string}")
	public void has_a_refund_receipt_with(String string) {
		window.comboBox("User selection").selectItem(string);
		assertThat(window.list("Receipts list").contents()[0]).contains("Refund");
		window.label("totalDebtToUser")
				.requireText(String.format(PayReceiptsViewSwing.TOTALDEBTTOUSERMESSAGE + "%.2f", receipt_debt));

	}

	@Then("The debt with {string} is paid")
	public void the_debt_with_user_is_paid(String string) {
		String[] accountingListStrings = window.list("accountingList").contents();
		assertThat(accountingListStrings[0]).contains(string).contains("paid").contains("true");
	}

	@Then("There is an unpaid debt from {string}")
	public void there_is_an_unpaid_debt_from(String user) {
		String[] accountingListStrings = window.list("accountingList").contents();
		assertThat(accountingListStrings[0]).contains(user).contains("paid").contains("false");
	}

	@Then("There is {string} in the users list")
	public void there_is_user_in_the_users_list(String user) {
		assertThat(window.list("usersList").contents()).contains(user);
	}

	@Then("A message is shown saying there are no more receipts to pay")
	public void a_message_is_shown_saying_no_receipts_to_pay() {
		window.label("errorMsg").requireText("You have no accountings.");
	}

	@Then("debt increased of {float}")
	public void the_debt_is_increased_of(float value) {
		window = WindowFinder.findFrame("Pay Receipt").using(robot);
		window.label("totalDebtToUser")
				.requireText(String.format(PayReceiptsViewSwing.TOTALDEBTTOUSERMESSAGE + "%.2f", receipt_debt + value));
	}
}
