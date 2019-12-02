package com.unifiprojects.app.appichetto.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.ArrayList;
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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.modules.EntityManagerModule;
import com.unifiprojects.app.appichetto.modules.RepositoriesModule;
import com.unifiprojects.app.appichetto.repositories.UserRepositoryHibernate;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AppichettoSteps {

	private UserRepositoryHibernate userRepository;
	private EntityManager entityManager;
	private FrameFixture window;
	Robot robot;

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
		});
		entityManager.getTransaction().commit();
	}

	@When("{string} view shows")
	public void view_shows(String viewToShow) {
		robot = BasicRobot.robotWithNewAwtHierarchy();
		robot.settings().componentLookupScope(ComponentLookupScope.ALL);
		application("com.unifiprojects.app.appichetto.main.Main").start();
		window = WindowFinder.findFrame(viewToShow).using(robot);
	}

	@When("{string} view shown")
	public void view_shown(String actualView) {
		//TODO
	}

	@When("Write {string} in {string} text box")
	public void write_in_and_in(String string, String string2) {
		window.textBox(JTextComponentMatcher.withName(string2)).enterText(string);
	}

	@When("Click {string} button")
	public void click_button(String string) {
		window.button(JButtonMatcher.withText(string)).click();
		if(string.equals("Save Receipt"))
			window = WindowFinder.findFrame("Homepage").using(robot);
	}

	@When("Click {string} button on homepage")
	public void click_button_on_homepage(String buttonName) {
		window.button(JButtonMatcher.withText(buttonName)).click();
		window = WindowFinder.findFrame(buttonName).using(robot);
	}

	@Then("The view contain the following message {string}")
	public void the_view_contain_the_following_message(String string) {
		assertThat(window.label("errorMsg").text()).contains(string);
	}

	@Given("User {string} is logged")
	public void user_is_logged(String usernameToLog) {
		User userToLog = userRepository.findByUsername(usernameToLog);

		window.textBox(JTextComponentMatcher.withName("Username")).enterText(userToLog.getUsername());
		window.textBox(JTextComponentMatcher.withName("Password")).enterText(userToLog.getPassword());
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
}
