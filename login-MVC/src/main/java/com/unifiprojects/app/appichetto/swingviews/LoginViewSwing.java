package com.unifiprojects.app.appichetto.swingviews;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.google.inject.Inject;
import com.unifiprojects.app.appichetto.controllers.LoginController;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.swingviews.utils.LinkedSwingView;
import com.unifiprojects.app.appichetto.views.HomepageView;
import com.unifiprojects.app.appichetto.views.LoginView;

public class LoginViewSwing extends LinkedSwingView implements LoginView{
	private LoginController loginController;
	
	private JTextField usernameTextbox;
	private JPasswordField passwordField;
	private JLabel errorMsg;

	private HomepageView homepage;
	
	@Inject
	public LoginViewSwing() {
		initialize();
	}

	public void setLoginController(LoginController loginController) {
		this.loginController = loginController;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Login");
		frame.setName("Login");
		int width = 450;
		frame.setMinimumSize(new Dimension(width, 300));
		frame.setBounds(100, 100, width, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(115, 53, 72, 15);
		frame.getContentPane().add(lblUsername);
		
		usernameTextbox = new JTextField();
		usernameTextbox.setName("Username");
		usernameTextbox.setBounds(197, 51, 114, 19);
		frame.getContentPane().add(usernameTextbox);
		usernameTextbox.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setName("Password");
		passwordField.setBounds(197, 121, 114, 19);
		frame.getContentPane().add(passwordField);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(115, 121, 70, 15);
		frame.getContentPane().add(lblPassword);

		int buttonWidthHalf = (int) (118 / 2.);

		JButton btnLogin = new JButton("Log-in");
		btnLogin.addActionListener(
				e -> loginController.login(usernameTextbox.getText(), getPasswordFromPasswordField()));

		btnLogin.setEnabled(false);
		btnLogin.setBounds((int) (width * 1. / 3.) - buttonWidthHalf, 200, buttonWidthHalf * 2, 25);
		usernameTextbox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnLogin.setEnabled(!usernameTextbox.getText().isEmpty());
			}

		});

		frame.getContentPane().add(btnLogin);

		JButton btnSignin = new JButton("Sign-in");
		btnSignin.setBounds((int) (width * 2. / 3.) - buttonWidthHalf, 200, buttonWidthHalf * 2, 25);
		frame.getContentPane().add(btnSignin);

		btnSignin.addActionListener(
				e -> loginController.signIn(usernameTextbox.getText(), getPasswordFromPasswordField()));

		errorMsg = new JLabel("");
		errorMsg.setName("errorMsg");
		errorMsg.setForeground(Color.RED);
		errorMsg.setBounds(115, 162, 176, 15);
		frame.getContentPane().add(errorMsg);

	}

	private String getPasswordFromPasswordField() {
		return String.copyValueOf(passwordField.getPassword());
	}

	@Override
	public void showErrorMsg(String message) {
		SwingUtilities.invokeLater(() -> errorMsg.setText(message));
	}

	@Override
	public void goToHome(User loggedUser) {
		homepage.update(loggedUser);
		// frame.setVisible(false);
	}

	public void setHomepage(HomepageView homepage) {
		this.homepage = homepage;
	}

	@Override
	public JFrame getFrame() {
		return frame;
	}
}
