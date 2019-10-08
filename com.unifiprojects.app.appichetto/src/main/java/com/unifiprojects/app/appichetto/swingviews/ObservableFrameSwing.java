package com.unifiprojects.app.appichetto.swingviews;

import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JFrame;

public abstract class ObservableFrameSwing extends Observable {
	
	protected JButton btnHome;
	
	public ObservableFrameSwing() {
		btnHome = new JButton("Home");
		btnHome.setName("homeBtn");
		btnHome.addActionListener(e -> goToHome());
	}
	
	public abstract JFrame getFrame();

	public JButton getBtnHome() {
		return btnHome;
	}

	@Override
	protected synchronized void setChanged() {
		super.setChanged();
		getFrame().dispose();
	}

	public void goToHome() {
		setChanged();
		notifyObservers();
	}
	
	public void show() {
		getFrame().setVisible(true);
	}
}
