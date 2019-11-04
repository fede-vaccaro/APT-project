package com.unifiprojects.app.appichetto.swingviews;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.unifiprojects.app.appichetto.controllers.UserController;

public abstract class LinkedSwingView{

	protected JButton btnHome;
	
	protected LinkedSwingView linkedSwingView;
	
	public void setLinkedSwingView(LinkedSwingView linkedSwingView) {
		this.linkedSwingView = linkedSwingView;
	}

	public LinkedSwingView getLinkedSwingView() {
		return linkedSwingView;
	}
	
	public LinkedSwingView() {
		btnHome = new JButton("Back");
		btnHome.setName("homeBtn");
		btnHome.addActionListener(e -> goToHome());
	}

	public abstract JFrame getFrame();
	public abstract UserController getController();
	public abstract void updateData();
	
	public JButton getBtnHome() {
		return btnHome;
	}
	
	public void goToHome() {
		linkedSwingView.show();
		getFrame().dispose();
	}

	public void show() {
		updateData();
		getFrame().setVisible(true);
	}
	
}
