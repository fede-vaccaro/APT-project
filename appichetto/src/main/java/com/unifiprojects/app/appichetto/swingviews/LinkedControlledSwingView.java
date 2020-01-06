package com.unifiprojects.app.appichetto.swingviews;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.unifiprojects.app.appichetto.controllers.UserController;

public abstract class LinkedControlledSwingView extends LinkedSwingView {

	protected JButton btnBack;
	protected IView previousLinkedSwingView;
	
	public abstract UserController getController();

	public LinkedControlledSwingView() {
		btnBack = new JButton("Back");
		btnBack.setName("backBtn");
		btnBack.addActionListener(e -> goBack());
	}
	
	public void setLinkedSwingView(IView linkedSwingView) {
		this.previousLinkedSwingView = linkedSwingView;
	}

	public IView getLinkedSwingView() {
		return previousLinkedSwingView;
	}


	public JButton getBtnBack() {
		return btnBack;
	}
	
	public void goBack() {
		previousLinkedSwingView.show();
		getFrame().dispose();
	}


	public void updateData() {
		getController().update();
	}
	

	public JFrame getFrame() {
		return frame;
	}
	
	@Override
	public void show() {
		getFrame().setVisible(true);
		updateData();
	}	
}
	