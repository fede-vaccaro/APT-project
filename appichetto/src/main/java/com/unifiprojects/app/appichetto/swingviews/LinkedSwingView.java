package com.unifiprojects.app.appichetto.swingviews;

import javax.swing.JButton;
import javax.swing.JFrame;

public abstract class LinkedSwingView{

	protected JButton btnHome;
	protected JFrame frame;
	protected LinkedSwingView previousLinkedSwingView;
	
	public void setLinkedSwingView(LinkedSwingView linkedSwingView) {
		this.previousLinkedSwingView = linkedSwingView;
	}

	public LinkedSwingView getLinkedSwingView() {
		return previousLinkedSwingView;
	}
	
	public LinkedSwingView() {
		btnHome = new JButton("Back");
		btnHome.setName("homeBtn");
		btnHome.addActionListener(e -> goBack());
	}

	public JFrame getFrame() {
		return frame;
	}

	public JButton getBtnHome() {
		return btnHome;
	}
	
	public void goBack() {
		previousLinkedSwingView.show();
		getFrame().dispose();
	}

	public void show() {
		getFrame().setVisible(true);
	}
	
}
