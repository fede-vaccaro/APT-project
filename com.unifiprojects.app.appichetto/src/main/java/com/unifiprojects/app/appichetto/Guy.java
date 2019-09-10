package com.unifiprojects.app.appichetto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Guy {
	private String name;
	private static final Logger LOGGER = LogManager.getLogger(Guy.class);

	public Guy(String name) {
		this.name = name;
	}

	public void sayMyName() {
		String sentence = "My name is: " + name;
		LOGGER.info(sentence);
	}

}