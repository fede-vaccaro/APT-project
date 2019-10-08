package com.unifiprojects.app.appichetto.swingviews.utils;

import java.util.Observable;

import javax.swing.JFrame;

public abstract class ObservableFrame extends Observable {
	
	public abstract JFrame getFrame();	
	
	@Override
	protected synchronized void setChanged() {
		super.setChanged();
		getFrame().dispose();
	}

	public void goToHome() {
		setChanged();
		this.notifyObservers();
	}
	
	public void show() {
		getFrame().setVisible(true);
	}
}
