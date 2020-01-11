package com.unifiprojects.app.appichetto.swingviews.utils;

import javax.swing.JFrame;

public abstract class LinkedSwingView implements IView {

	protected JFrame frame;
	public static JFrame mainFrame;

//	static {
//		initializeMainFrame();
//	}

	public static void initializeMainFrame() {
		mainFrame = new JFrame();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public JFrame getFrame() {
		return frame;
	}

	@Override
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