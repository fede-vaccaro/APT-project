package com.unifiprojects.app.appichetto.swingviews;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.unifiprojects.app.appichetto.swingviews.utils.ObservableFrame;

public class HomepageSwingView implements Observer {
	/**
	 * 
	 */
	private JFrame frame;
	private ObservableFrame loginView;
	private ObservableFrame receiptView;
	private ObservableFrame payReceiptsView;
	private ObservableFrame showHistoryView;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HomepageSwingView frame = new HomepageSwingView();
					frame.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}



	public JFrame getFrame() {
		return frame;
	}

	public void setLoginView(ObservableFrame loginView) {
		this.loginView = loginView;
	}

	public void setReceiptSwingView(ObservableFrame receiptSwingView) {
		this.receiptView = receiptSwingView;
	}

	public void setPayViewReceiptsViewSwing(ObservableFrame payViewReceiptsViewSwing) {
		this.payReceiptsView = payViewReceiptsViewSwing;
	}

	public void setHistoryViewSwing(ObservableFrame historyViewSwing) {
		this.showHistoryView = historyViewSwing;
	}

	/**
	 * Create the frame.
	 */
	public HomepageSwingView() {
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
		btnCreateReceipt.addActionListener(
				e->{
					frame.setVisible(false);
					receiptView.show();
				});
		GridBagConstraints gbc_btnCreateReceipt = new GridBagConstraints();
		gbc_btnCreateReceipt.insets = new Insets(0, 0, 5, 5);
		gbc_btnCreateReceipt.gridx = 2;
		gbc_btnCreateReceipt.gridy = 2;
		frame.getContentPane().add(btnCreateReceipt, gbc_btnCreateReceipt);

		JButton btnPayReceipt = new JButton("Pay Receipt");
		btnPayReceipt.addActionListener(
				e->{
					frame.setVisible(false);
					payReceiptsView.show();
				});
		GridBagConstraints gbc_btnPayReceipt = new GridBagConstraints();
		gbc_btnPayReceipt.insets = new Insets(0, 0, 5, 0);
		gbc_btnPayReceipt.gridx = 4;
		gbc_btnPayReceipt.gridy = 2;
		frame.getContentPane().add(btnPayReceipt, gbc_btnPayReceipt);

		JButton btnShowHistory = new JButton("Show History");
		btnShowHistory.addActionListener(
				e->{
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
				btnLogOut.addActionListener(
						e->{
							frame.setVisible(false);
							loginView.show();
						});
				GridBagConstraints gbc_btnLogOut = new GridBagConstraints();
				gbc_btnLogOut.insets = new Insets(0, 0, 0, 5);
				gbc_btnLogOut.gridx = 3;
				gbc_btnLogOut.gridy = 8;
				frame.getContentPane().add(btnLogOut, gbc_btnLogOut);

	}

	@Override
	public void update(Observable o, Object arg) {
		this.frame.setVisible(true);
	}

}
