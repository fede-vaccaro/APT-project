package com.unifiprojects.app.appichetto.swingviews;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.unifiprojects.app.appichetto.controllers.UserPanelController;
import com.unifiprojects.app.appichetto.views.UserPanelView;

public class UserPanelViewSwing extends LinkedControlledSwingView implements UserPanelView {

	private UserPanelController userPanelController;

	private IView loginViewSwing;

	public void setLoginViewSwing(IView loginView) {
		this.loginViewSwing = loginView;
	}

	private JLabel lblUsername;

	private JLabel lblErrorMessage;

	private JFormattedTextField passwordTextField;

	private JFormattedTextField nameTextField;

	private JButton btnUpdateCredential;

	private JButton btnRemoveUser;

	private JButton btnYesButton;

	private JLabel lblConfirm;

	private JButton btnNoButton;
	/**
	 * Launch the application.
	 */
	
	/*
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
*/
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
		frame.setTitle("User panel");
		frame.setName("User panel");

		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		lblUsername = new JLabel("Hello user!");
		lblUsername.setName("userLabel");
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsername.gridx = 3;
		gbc_lblUsername.gridy = 2;
		frame.getContentPane().add(lblUsername, gbc_lblUsername);

		JLabel lblNewUsername = new JLabel("New username:");
		GridBagConstraints gbc_lblNewUsername = new GridBagConstraints();
		gbc_lblNewUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewUsername.gridx = 2;
		gbc_lblNewUsername.gridy = 5;
		frame.getContentPane().add(lblNewUsername, gbc_lblNewUsername);

		nameTextField = new JFormattedTextField();
		nameTextField.setName("newName");
		
		KeyAdapter updateCredentialKeyListener = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (!(nameTextField.getText().isEmpty() && passwordTextField.getText().isEmpty())) {
					btnUpdateCredential.setEnabled(true);
				} else {
					btnUpdateCredential.setEnabled(false);
				}
			}
		};
		
		nameTextField.addKeyListener(updateCredentialKeyListener);
		GridBagConstraints gbc_formattedTextField_1 = new GridBagConstraints();
		gbc_formattedTextField_1.insets = new Insets(0, 0, 5, 5);
		gbc_formattedTextField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_formattedTextField_1.gridx = 3;
		gbc_formattedTextField_1.gridy = 5;
		frame.getContentPane().add(nameTextField, gbc_formattedTextField_1);

		JLabel lblNewPassword = new JLabel("New password:");
		GridBagConstraints gbc_lblNewPassword = new GridBagConstraints();
		gbc_lblNewPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewPassword.anchor = GridBagConstraints.EAST;
		gbc_lblNewPassword.gridx = 2;
		gbc_lblNewPassword.gridy = 7;
		frame.getContentPane().add(lblNewPassword, gbc_lblNewPassword);

		passwordTextField = new JFormattedTextField();
		passwordTextField.addKeyListener(updateCredentialKeyListener);
		passwordTextField.setName("newPW");
		GridBagConstraints gbc_formattedTextField = new GridBagConstraints();
		gbc_formattedTextField.insets = new Insets(0, 0, 5, 5);
		gbc_formattedTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_formattedTextField.gridx = 3;
		gbc_formattedTextField.gridy = 7;
		frame.getContentPane().add(passwordTextField, gbc_formattedTextField);

		lblErrorMessage = new JLabel("Error message");
		lblErrorMessage.setName("errorMsg");
		lblErrorMessage.setText("");
		lblErrorMessage.setForeground(Color.RED);
		GridBagConstraints gbc_lblErrorMessage = new GridBagConstraints();
		gbc_lblErrorMessage.insets = new Insets(0, 0, 5, 5);
		gbc_lblErrorMessage.gridx = 3;
		gbc_lblErrorMessage.gridy = 8;
		frame.getContentPane().add(lblErrorMessage, gbc_lblErrorMessage);

		btnUpdateCredential = new JButton("Update credential");
		btnUpdateCredential.setEnabled(false);
		btnUpdateCredential.addActionListener(e -> changeCredential());
		
		// new JButton("Back");
		GridBagConstraints gbc_btnBack = new GridBagConstraints();
		gbc_btnBack.insets = new Insets(0, 0, 5, 5);
		gbc_btnBack.gridx = 2;
		gbc_btnBack.gridy = 9;
		frame.getContentPane().add(super.btnBack, gbc_btnBack);
		
		GridBagConstraints gbc_btnUpdateCredential = new GridBagConstraints();
		gbc_btnUpdateCredential.insets = new Insets(0, 0, 5, 5);
		gbc_btnUpdateCredential.gridx = 3;
		gbc_btnUpdateCredential.gridy = 9;
		frame.getContentPane().add(btnUpdateCredential, gbc_btnUpdateCredential);

		btnRemoveUser = new JButton("Remove user");
		GridBagConstraints gbc_btnRemoveUser = new GridBagConstraints();
		gbc_btnRemoveUser.insets = new Insets(0, 0, 5, 5);
		gbc_btnRemoveUser.gridx = 3;
		gbc_btnRemoveUser.gridy = 10;
		frame.getContentPane().add(btnRemoveUser, gbc_btnRemoveUser);

		btnYesButton = new JButton("Yes");
		btnYesButton.setForeground(Color.RED);
		GridBagConstraints gbc_btnYesButton = new GridBagConstraints();
		gbc_btnYesButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnYesButton.gridx = 2;
		gbc_btnYesButton.gridy = 11;
		frame.getContentPane().add(btnYesButton, gbc_btnYesButton);

		lblConfirm = new JLabel("Do you confirm?");
		GridBagConstraints gbc_lblConfirm = new GridBagConstraints();
		gbc_lblConfirm.insets = new Insets(0, 0, 5, 5);
		gbc_lblConfirm.gridx = 3;
		gbc_lblConfirm.gridy = 11;
		frame.getContentPane().add(lblConfirm, gbc_lblConfirm);

		btnNoButton = new JButton("No");
		GridBagConstraints gbc_btnNoButton = new GridBagConstraints();
		gbc_btnNoButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNoButton.gridx = 4;
		gbc_btnNoButton.gridy = 11;
		frame.getContentPane().add(btnNoButton, gbc_btnNoButton);

		btnYesButton.setVisible(false);
		btnNoButton.setVisible(false);
		lblConfirm.setVisible(false);
		
		btnRemoveUser.addActionListener(e -> {
			showConfirm(true);
		});
		
		btnNoButton.addActionListener(e -> {
			showConfirm(false);
		});
		
		btnYesButton.addActionListener(e -> {
			userPanelController.deleteUser();
			loginViewSwing.show();
			frame.dispose();
		});

	}

	private void showConfirm(boolean showConfirm) {
		btnYesButton.setVisible(showConfirm);
		btnNoButton.setVisible(showConfirm);
		btnRemoveUser.setVisible(!showConfirm);
		lblConfirm.setVisible(showConfirm);
	}

	private void changeCredential() {

		String newName = nameTextField.getText();
		String newPassword = passwordTextField.getText();

		if (newName.equals("")) {
			newName = null;
		}
		if (newPassword.equals("")) {
			newPassword = null;
		}

		userPanelController.changeCredential(newName, newPassword);
	}

	@Override
	public void showUser(String username) {
		lblUsername.setText("Hello " + username + "!");
	}

	@Override
	public void showErrorMsg(String exceptionMessage) {
		lblErrorMessage.setText(exceptionMessage);
	}

	@Override
	public void goToLoginView() {
		loginViewSwing.show();
	}

	@Override
	public UserPanelController getController() {
		return userPanelController;
	}

	@Override
	public void updateData() {
		userPanelController.update();
	}

	public void setUserPanelController(UserPanelController controller) {
		this.userPanelController = controller;
	}

}
