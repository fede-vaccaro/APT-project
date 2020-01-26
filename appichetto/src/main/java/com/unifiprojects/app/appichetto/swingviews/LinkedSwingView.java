package com.unifiprojects.app.appichetto.swingviews;

import java.util.Objects;

import javax.swing.JFrame;

public abstract class LinkedSwingView{

	protected JFrame frame;
	static JFrame mainFrame;

	public LinkedSwingView() {
		if(Objects.isNull(mainFrame))
			initializeMainFrame();
	}
	
	public static void initializeMainFrame() {
		mainFrame = new JFrame();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocation(200, 200);
	}

	public JFrame getFrame() {
		return frame;
	}

	public void show() {
		frame.setVisible(false);
		
		mainFrame.getContentPane().removeAll();
		
		mainFrame.setTitle(frame.getTitle());
		mainFrame.setName(frame.getName());
		mainFrame.setBounds(frame.getBounds());
		mainFrame.getContentPane().setLayout(frame.getContentPane().getLayout());
		mainFrame.setContentPane(frame.getContentPane());

		mainFrame.revalidate();
		mainFrame.repaint();
		mainFrame.setVisible(true);
	}

}
