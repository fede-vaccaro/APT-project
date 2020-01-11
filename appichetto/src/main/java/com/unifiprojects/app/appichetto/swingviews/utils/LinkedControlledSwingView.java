package com.unifiprojects.app.appichetto.swingviews.utils;

import javax.swing.JButton;

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
	}

	public void updateData() {
		getController().update();
	}

	@Override
	public void show() {
		super.show();
		updateData();
	}
}
