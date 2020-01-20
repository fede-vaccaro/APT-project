package com.unifiprojects.app.appichetto.bdd;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/bdd/resources/features", glue = "com.unifiprojects.app.appichetto.bdd.steps", monochrome = true)
public class AppichettoBDD {

	@BeforeClass
	public static void setUpOnce() {
		//This checks that all access to Swing components is performed in the EDT
//		FailOnThreadViolationRepaintManager.install();
	}
}
