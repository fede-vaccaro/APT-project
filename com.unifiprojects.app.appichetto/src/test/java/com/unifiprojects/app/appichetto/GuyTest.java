package com.unifiprojects.app.appichetto;

import org.junit.Test;
import org.junit.Before;

import com.unifiprojects.app.appichetto.Guy;

import static org.assertj.core.api.Assertions.*; 


public class GuyTest {

	Guy guy;
	
	public final String guyName = "Walter White";
	
	@Before
	public void setUp() {
		guy = new Guy(this.guyName);
	}
	
	@Test
	public void testSayMyNameSpellsTheCorrectName() {
		String guySays = this.guy.sayMyName();
		assertThat(guySays).isEqualTo("My name is: " + this.guyName);
	}
	
}
