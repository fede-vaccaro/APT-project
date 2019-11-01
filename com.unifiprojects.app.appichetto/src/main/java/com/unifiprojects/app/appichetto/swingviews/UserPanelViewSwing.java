package com.unifiprojects.app.appichetto.swingviews;

import java.awt.EventQueue;

import javax.swing.JFrame;

import com.unifiprojects.app.appichetto.controllers.UserController;
import com.unifiprojects.app.appichetto.controllers.UserPanelController;
import com.unifiprojects.app.appichetto.views.UserPanelView;

public class UserPanelViewSwing extends LinkedSwingView implements UserPanelView{

	private JFrame frame;
	
	private UserPanelController userPanelController;
	
	private LoginViewSwing loginViewSwing;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UserPanelViewSwing window = new UserPanelViewSwing();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UserPanelViewSwing() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void showUser(String username) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showErrorMsg(String exceptionMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void goToLoginView() {
		this.frame.dispose();
		loginViewSwing.show();
	}

	@Override
	public JFrame getFrame() {
		return frame;
	}

	@Override
	public UserController getController() {
		return userPanelController;
	}

	@Override
	public void updateData() {
		userPanelController.update();
	}

}
