package com.unifiprojects.app.appichetto.swingviews;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.google.inject.Inject;
import com.unifiprojects.app.appichetto.controllers.UserController;

public abstract class ObservableFrameSwing{

	protected JButton btnHome;
	
	@Inject
	protected HomepageSwingView homepageSwingView;
	
	public HomepageSwingView getHomepageSwingView() {
		return homepageSwingView;
		
	}
	
	public ObservableFrameSwing() {
		btnHome = new JButton("Home");
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
		homepageSwingView.update();
		getFrame().dispose();
	}

	public void show() {
		getFrame().setVisible(true);
		updateData();
	}
	
	public HomepageSwingView getHomepageView() {
		return homepageSwingView;
	}

	public void setHomepageSwingView(HomepageSwingView homepageSwingView) {
		this.homepageSwingView = homepageSwingView;
	}
	
	
}
