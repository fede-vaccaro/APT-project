package com.unifiprojects.app.appichetto.swingviews;

import java.awt.Container;

import javax.swing.JFrame;

public abstract class LinkedSwingView implements IView {

	protected JFrame frame;
	protected static JFrame mainFrame = new JFrame();

	static {
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
	}

	public JFrame getFrame() {
		return frame;
	}

	@Override
	public void show() {
		frame.setVisible(false);
		
		mainFrame.getContentPane().removeAll();
		mainFrame.revalidate();
		mainFrame.getContentPane().add(frame.getContentPane());
		mainFrame.revalidate();
		mainFrame.repaint();
		
		mainFrame.setTitle(frame.getTitle());
		mainFrame.setName(frame.getName());
		mainFrame.setSize(frame.getSize());
		

	}

}
