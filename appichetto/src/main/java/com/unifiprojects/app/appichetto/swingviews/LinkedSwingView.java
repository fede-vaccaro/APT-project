package com.unifiprojects.app.appichetto.swingviews;

import javax.swing.JFrame;

public abstract class LinkedSwingView{

	protected JFrame frame;
	static JFrame mainFrame;

	public static void initializeMainFrame() {
		mainFrame = new JFrame();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public JFrame getFrame() {
		return frame;
	}

	public void show() {
		frame.setVisible(false);
		
		mainFrame.setVisible(true);
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
