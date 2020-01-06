package com.unifiprojects.app.appichetto.swingviews;

import javax.swing.JFrame;

public abstract class LinkedSwingView implements IView{

	protected JFrame frame;
		
	public JFrame getFrame() {
		return frame;
	}
	
	@Override
	public void show() {
		getFrame().setVisible(true);
	}

	
}
