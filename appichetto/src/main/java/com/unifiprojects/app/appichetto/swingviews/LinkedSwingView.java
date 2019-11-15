package com.unifiprojects.app.appichetto.swingviews;

import javax.swing.JButton;
import javax.swing.JFrame;

public abstract class LinkedSwingView{

	protected JButton btnHome;
	public JFrame frame;
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
		btnHome.addActionListener(e -> goBack());
	}

	public JFrame getFrame() {
		return frame;
	}

	public JButton getBtnHome() {
		return btnHome;
	}
	
	public void goBack() {
		linkedSwingView.show();
		getFrame().dispose();
	}

	public void show() {
		getFrame().setVisible(true);
	}
	
}
