package com.unifiprojects.app.appichetto.swingViews;

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

import com.unifiprojects.app.appichetto.controllers.LoginController;
import com.unifiprojects.app.appichetto.views.LoginView;

public class LoginViewSwing implements LoginView{
	private LoginController loginController;
	
	private JFrame frmAppichetto;
	private JTextField usernameTextbox;
	private JPasswordField passwordField;
	private JLabel errorMsg;
	
	public JFrame getFrame() {
		return frmAppichetto;
	}
	
	/**
	 * Launch the application.
	 */
	
	/*
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginViewSwing window = new LoginViewSwing();
					window.frmAppichetto.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	*/
	/**
	 * Create the application.
	 */
	public LoginViewSwing(LoginController loginController) {
		this.loginController = loginController;
		initialize();
	}
	
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
		frmAppichetto = new JFrame();
		frmAppichetto.setTitle("AppIchetto 0.0.1");
		int width = 450;
		frmAppichetto.setMinimumSize(new Dimension(width, 300));
		frmAppichetto.setBounds(100, 100, width, 300);
		frmAppichetto.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAppichetto.getContentPane().setLayout(null);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(115, 53, 72, 15);
		frmAppichetto.getContentPane().add(lblUsername);
		
		usernameTextbox = new JTextField();
		usernameTextbox.setName("usernameTextbox");
		usernameTextbox.setBounds(197, 51, 114, 19);
		frmAppichetto.getContentPane().add(usernameTextbox);
		usernameTextbox.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setName("passwordField");
		passwordField.setBounds(197, 121, 114, 19);
		frmAppichetto.getContentPane().add(passwordField);

		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(115, 121, 70, 15);
		frmAppichetto.getContentPane().add(lblPassword);
		
		int buttonWidthHalf = (int)(118/2.);
		
		JButton btnLogin = new JButton("Log-in");
		btnLogin.addActionListener(e -> loginController.login(usernameTextbox.getText(), getPasswordFromPasswordField()));
			
		
		btnLogin.setEnabled(false);
		btnLogin.setBounds((int)(width*1./3.) - buttonWidthHalf, 200, buttonWidthHalf*2, 25);
		usernameTextbox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnLogin.setEnabled(!usernameTextbox.getText().isEmpty());
			}

		});

		frmAppichetto.getContentPane().add(btnLogin);
		
		JButton btnSignin = new JButton("Sign-in");
		btnSignin.setBounds((int)(width*2./3.) - buttonWidthHalf, 200, buttonWidthHalf*2, 25);
		frmAppichetto.getContentPane().add(btnSignin);
		
		btnSignin.addActionListener(e -> loginController.signIn(usernameTextbox.getText(), getPasswordFromPasswordField()));
		
		errorMsg = new JLabel("");
		errorMsg.setName("errorMsg");
		errorMsg.setForeground(Color.RED);
		errorMsg.setBounds(115, 162, 176, 15);
		frmAppichetto.getContentPane().add(errorMsg);
		
	}
	
	private String getPasswordFromPasswordField() {
		return String.copyValueOf(passwordField.getPassword());
	}

	@Override
	public void goToHomePage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showErrorMsg(String message) {
		SwingUtilities.invokeLater(() -> errorMsg.setText(message));
	}


}
