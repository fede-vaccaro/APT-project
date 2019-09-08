package com.unifiprojects.app.appichetto;

public class Guy {
	private String name;

	public Guy(String name) {
		this.name = name;
	}
	
	public String sayMyName() {
		String sentence = "My name is: " + name;
		return sentence;
	}
	
}