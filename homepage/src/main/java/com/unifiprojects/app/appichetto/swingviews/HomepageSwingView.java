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
import com.google.inject.assistedinject.Assisted;
import com.unifiprojects.app.appichetto.controllers.UserPanelController;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.views.HomepageView;
import com.unifiprojects.app.appichetto.views.LoginView;

// @Singleton
public class HomepageSwingView extends LinkedSwingView implements HomepageView {

	List<LinkedControlledSwingView> views;

	LinkedSwingView loginView;

	LinkedControlledSwingView receiptView;
	LinkedControlledSwingView payReceiptsView;
	LinkedControlledSwingView showHistoryView;
	LinkedControlledSwingView userPanelView;

	@Inject
	public HomepageSwingView(PayReceiptsViewSwing payReceiptsViewSwing, ShowHistoryViewSwing showHistoryViewSwing,
			ReceiptSwingView receiptSwingView, UserPanelViewSwing userPanelViewSwing,@Assisted LoginView loginView) {

		this.payReceiptsView = payReceiptsViewSwing;
		this.showHistoryView = showHistoryViewSwing;
		this.userPanelView = userPanelViewSwing;
		this.receiptView = receiptSwingView;

		this.loginView = (LinkedSwingView) loginView;

		userPanelViewSwing.setLoginViewSwing((LinkedSwingView) loginView);
		((UserPanelController) userPanelViewSwing.getController()).setHomepageView(this);

		views = new ArrayList<>();
		views.addAll(Arrays.asList(payReceiptsViewSwing, showHistoryViewSwing, receiptSwingView, userPanelViewSwing));
		views.forEach(v -> v.setLinkedSwingView(this));

		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Homepage");
		frame.setName("Homepage");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		JButton btnCreateReceipt = new JButton("Create Receipt");
		btnCreateReceipt.addActionListener(e -> receiptView.show());
		GridBagConstraints gbc_btnCreateReceipt = new GridBagConstraints();
		gbc_btnCreateReceipt.insets = new Insets(0, 0, 5, 5);
		gbc_btnCreateReceipt.gridx = 2;
		gbc_btnCreateReceipt.gridy = 2;
		frame.getContentPane().add(btnCreateReceipt, gbc_btnCreateReceipt);

		JButton btnPayReceipt = new JButton("Pay Receipt");
		btnPayReceipt.addActionListener(e -> payReceiptsView.show());
		GridBagConstraints gbc_btnPayReceipt = new GridBagConstraints();
		gbc_btnPayReceipt.insets = new Insets(0, 0, 5, 0);
		gbc_btnPayReceipt.gridx = 4;
		gbc_btnPayReceipt.gridy = 2;
		frame.getContentPane().add(btnPayReceipt, gbc_btnPayReceipt);

		JButton btnShowHistory = new JButton("Show History");
		btnShowHistory.addActionListener(e -> showHistoryView.show());
		GridBagConstraints gbc_btnShowHistory = new GridBagConstraints();
		gbc_btnShowHistory.insets = new Insets(0, 0, 5, 5);
		gbc_btnShowHistory.gridx = 2;
		gbc_btnShowHistory.gridy = 5;
		frame.getContentPane().add(btnShowHistory, gbc_btnShowHistory);

		JButton btnUserPanel = new JButton("User panel");
		GridBagConstraints gbc_btnNewButton_4 = new GridBagConstraints();
		gbc_btnNewButton_4.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_4.gridx = 4;
		gbc_btnNewButton_4.gridy = 5;
		frame.getContentPane().add(btnUserPanel, gbc_btnNewButton_4);
		btnUserPanel.addActionListener(e -> userPanelView.show());

		JButton btnLogOut = new JButton("Log Out");
		btnLogOut.addActionListener(e -> loginView.show());
		GridBagConstraints gbc_btnLogOut = new GridBagConstraints();
		gbc_btnLogOut.insets = new Insets(0, 0, 0, 5);
		gbc_btnLogOut.gridx = 3;
		gbc_btnLogOut.gridy = 8;
		frame.getContentPane().add(btnLogOut, gbc_btnLogOut);
	}

	@Override
	public void setLoggedUser(User loggedUser) {
		views.stream().forEach(view -> view.getController().setLoggedUser(loggedUser));
	}

	public LinkedSwingView getLoginView() {
		return loginView;
	}
}
