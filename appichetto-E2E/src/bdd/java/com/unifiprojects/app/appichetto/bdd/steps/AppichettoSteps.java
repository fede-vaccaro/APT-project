package com.unifiprojects.app.appichetto.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import javax.persistence.EntityManager;
import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
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

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AppichettoSteps {

	private UserRepositoryHibernate userRepository;
	private EntityManager entityManager;
	private FrameFixture window;

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
	}
	
	@After
	public void cleanAndTearDown() {
		wipeTablesBeforeTest();
//		window.close();
		window.cleanUp();
	}

	@Given("The database contains user {string} with {string} password")
	public void the_database_contains_user_with_password(String string, String string2) {
		entityManager.getTransaction().begin();
		userRepository.save(new User(string, string2));
		entityManager.getTransaction().commit();
	}

	@When("{string} view shows")
	public void view_shows(String string) {

		application("com.unifiprojects.app.appichetto.main.Main").start();

		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return string.equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(BasicRobot.robotWithCurrentAwtHierarchy());
	}

	@When("Write {string} in {string} text box")
	public void write_in_and_in(String string, String string2) {
		window.textBox(JTextComponentMatcher.withName(string2)).enterText(string);
	}

	@When("Click {string} button")
	public void click_button(String string) {
		window.button(JButtonMatcher.withText(string)).click();
	}

	@Then("The view contain the following message {string}")
	public void the_view_contain_the_following_message(String string) {
		assertThat(window.label("errorMsg").text()).contains(string);
	}
	
	@Then("{string} view disappear and {string} view shows")
	public void view_disappear_and_view_shows(String string, String string2) {
	}
}

//Scenario: The initial state
//Given The database contain the following users
//  | Giuseppe | Gpsw |
//  | Federico | Fpsw |
//  | Pasquale | Ppsw |
//  | Checco   | Cpsw |
//And The database contain a receipt bought by "Giuseppe" with the following items
//  | name        | price | quantity | list of users                     |
//  | pasta mista |   0.7 |        1 | Giuseppe Federico Pasquale Checco |
//  | fagioli     |   1.7 |        3 | Giuseppe Federico Pasquale Checco |
//  | detersivo   |   1.5 |        2 | Federico Pasquale Checco          |
//And The database contain a receipt bought by "Federico" with the following items
//  | name      | price | quantity | list of users              |
//  | spaghetti |   0.7 |        1 | Giuseppe Pasquale Checco   |
//  | carne     |   4.7 |        1 | Giuseppe Federico Checco   |
//  | sugo      |   1.5 |        2 | Giuseppe Federico Pasquale |
