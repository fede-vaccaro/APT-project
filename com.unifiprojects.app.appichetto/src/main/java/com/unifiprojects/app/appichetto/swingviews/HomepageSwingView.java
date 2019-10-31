package com.unifiprojects.app.appichetto.swingviews;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.unifiprojects.app.appichetto.controllers.UserController;
// import com.unifiprojects.app.appichetto.controllers.HomePageController;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;

@Singleton
public class HomepageSwingView extends LinkedSwingView {
	/**
	 * 
	 */
	private JFrame frame;

	// HomePageController homePageController;

	List<LinkedSwingView> views;

	LinkedSwingView loginView;

	LinkedSwingView receiptView;

	LinkedSwingView payReceiptsView;

	LinkedSwingView showHistoryView;

//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(() -> {
//			try {
//				HomepageSwingView frame = new HomepageSwingView();
//				frame.getFrame().setVisible(true);
//			} catch (Exception e) {
//
//			}
//		});
//	}

	public JFrame getFrame() {
		return frame;
	}

	public void setLoginView(LinkedSwingView loginView) {
		this.loginView = loginView;
	}

	public void setReceiptSwingView(LinkedSwingView receiptSwingView) {
		this.receiptView = receiptSwingView;
	}

	public void setPayViewReceiptsViewSwing(LinkedSwingView payViewReceiptsViewSwing) {
		this.payReceiptsView = payViewReceiptsViewSwing;
	}

	public void setHistoryViewSwing(LinkedSwingView historyViewSwing) {
		this.showHistoryView = historyViewSwing;
	}

	@Inject
	public HomepageSwingView(LoginViewSwing loginViewSwing, PayReceiptsViewSwing payReceiptsViewSwing,
			ShowHistoryViewSwing showHistoryViewSwing, ReceiptSwingView receiptSwingView) {
		this.payReceiptsView = payReceiptsViewSwing;
		this.showHistoryView = showHistoryViewSwing;
		this.receiptView = receiptSwingView;
		this.loginView = loginViewSwing;

		views = new ArrayList<>();
		// homePageController = new HomePageController();

		views.addAll(Arrays.asList(payReceiptsViewSwing, showHistoryViewSwing, receiptSwingView));

		views.forEach(v -> v.setLinkedSwingView(this));
		loginViewSwing.setLinkedSwingView(this);

		initialize();
	}

	/**
	 * Create the frame.
	 */
	public HomepageSwingView() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		JButton btnCreateReceipt = new JButton("Create Receipt");
		btnCreateReceipt.addActionListener(e -> {
			frame.setVisible(false);
			receiptView.show();
		});
		GridBagConstraints gbc_btnCreateReceipt = new GridBagConstraints();
		gbc_btnCreateReceipt.insets = new Insets(0, 0, 5, 5);
		gbc_btnCreateReceipt.gridx = 2;
		gbc_btnCreateReceipt.gridy = 2;
		frame.getContentPane().add(btnCreateReceipt, gbc_btnCreateReceipt);

		JButton btnPayReceipt = new JButton("Pay Receipt");
		btnPayReceipt.addActionListener(e -> {
			frame.setVisible(false);
			payReceiptsView.show();
		});
		GridBagConstraints gbc_btnPayReceipt = new GridBagConstraints();
		gbc_btnPayReceipt.insets = new Insets(0, 0, 5, 0);
		gbc_btnPayReceipt.gridx = 4;
		gbc_btnPayReceipt.gridy = 2;
		frame.getContentPane().add(btnPayReceipt, gbc_btnPayReceipt);

		JButton btnShowHistory = new JButton("Show History");
		btnShowHistory.addActionListener(e -> {
			frame.setVisible(false);
			showHistoryView.show();
		});
		GridBagConstraints gbc_btnShowHistory = new GridBagConstraints();
		gbc_btnShowHistory.insets = new Insets(0, 0, 5, 5);
		gbc_btnShowHistory.gridx = 2;
		gbc_btnShowHistory.gridy = 5;
		frame.getContentPane().add(btnShowHistory, gbc_btnShowHistory);

		JButton btnNewButton_4 = new JButton("New button");
		GridBagConstraints gbc_btnNewButton_4 = new GridBagConstraints();
		gbc_btnNewButton_4.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_4.gridx = 4;
		gbc_btnNewButton_4.gridy = 5;
		frame.getContentPane().add(btnNewButton_4, gbc_btnNewButton_4);

		JButton btnLogOut = new JButton("Log Out");
		btnLogOut.addActionListener(e -> {
			frame.setVisible(false);
			loginView.show();
		});
		GridBagConstraints gbc_btnLogOut = new GridBagConstraints();
		gbc_btnLogOut.insets = new Insets(0, 0, 0, 5);
		gbc_btnLogOut.gridx = 3;
		gbc_btnLogOut.gridy = 8;
		frame.getContentPane().add(btnLogOut, gbc_btnLogOut);
	}

	public void show() {
		this.frame.setVisible(true);
	}

	public void update(User loggedUser) {
		setLoggedUser(loggedUser);
		this.frame.setVisible(true);
	}

	//public void update(Receipt receipt) {
	//	startReceiptUpload(receipt);
	//}

	private void startReceiptUpload(Receipt receipt) {// TODO maybe in controller
		receiptView.show();
		((ReceiptSwingView) receiptView).getController().uploadReceipt(receipt);
	}

	public void setLoggedUser(User loggedUser) {
		views.stream().forEach(view -> view.getController().setLoggedUser(loggedUser));
	}

	public LinkedSwingView getLoginView() {
		return loginView;
	}

	@Override
	public UserController getController() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateData() {
		getController().update();		
	}
}
