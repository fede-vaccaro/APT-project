package com.unifiprojects.app.appichetto.swingviews;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.views.ShowHistoryView;

public class ShowHistoryViewSwing implements ShowHistoryView {

	private JFrame frame;
	private JList<Receipt> receiptList;
	private JList<Item> itemList;
	private JList<Accounting> accountingList;
	private JList<String> totalAccountingList;

	private DefaultListModel<Receipt> receiptListModel;
	private DefaultListModel<Item> itemListModel;
	private DefaultListModel<Accounting> accountingListModel;
	private DefaultListModel<String> totalAccountingsListModel;

	private JLabel lblTotalAccountings;
	private JLabel receiptLabel;
	private JButton btnHomepage;
	private JLabel message;

	/**
	 * Launch the application.
	 */

	public static class AccountingFormatter {
		public static String format(Accounting a) {
			if (a != null)
				return a.getUser().getUsername() + ": " + String.format("%.2f", a.getAmount()) + "; paid: "
						+ a.isPaid();
			return null;
		}
	}

	private class AccountingCellRenderer extends JLabel implements ListCellRenderer<Accounting> {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<? extends Accounting> list, Accounting value, int index,
				boolean isSelected, boolean cellHasFocus) {
			if (value.isPaid()) {
				setForeground(new Color(34, 139, 34));
			} else {
				setForeground(new Color(139, 0, 0));
			}
			setText(AccountingFormatter.format(value));
			return this;
		}
	}

	/**
	 * Create the application.
	 */
	public ShowHistoryViewSwing() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("History");
		frame.setBounds(100, 100, 450, 300);
		frame.setMinimumSize(new Dimension(800, 600));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		receiptLabel = new JLabel("Receipts you bought:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		frame.getContentPane().add(receiptLabel, gbc_lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Items in selected receipt:");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 0;
		frame.getContentPane().add(lblNewLabel_1, gbc_lblNewLabel_1);

		receiptListModel = new DefaultListModel<>();
		receiptList = new JList<>(receiptListModel);
		receiptList.setName("receiptList");

		receiptList.addListSelectionListener(e -> {
			Receipt selectedReceipt = receiptList.getSelectedValue();
			showItemList(selectedReceipt);
			showAccountingList(selectedReceipt);
		});

		GridBagConstraints gbc_receiptList = new GridBagConstraints();
		gbc_receiptList.gridheight = 6;
		gbc_receiptList.insets = new Insets(0, 0, 5, 5);
		gbc_receiptList.fill = GridBagConstraints.BOTH;
		gbc_receiptList.gridx = 0;
		gbc_receiptList.gridy = 2;
		frame.getContentPane().add(receiptList, gbc_receiptList);

		itemListModel = new DefaultListModel<>();
		itemList = new JList<>(itemListModel);
		itemList.setName("itemList");
		GridBagConstraints gbc_itemList = new GridBagConstraints();
		gbc_itemList.insets = new Insets(0, 0, 5, 0);
		gbc_itemList.fill = GridBagConstraints.BOTH;
		gbc_itemList.gridx = 1;
		gbc_itemList.gridy = 2;
		frame.getContentPane().add(itemList, gbc_itemList);

		JLabel lblUnpaidAccountings = new JLabel("Accountings:");
		GridBagConstraints gbc_lblUnpaidAccountings = new GridBagConstraints();
		gbc_lblUnpaidAccountings.insets = new Insets(0, 0, 5, 0);
		gbc_lblUnpaidAccountings.gridx = 1;
		gbc_lblUnpaidAccountings.gridy = 3;
		frame.getContentPane().add(lblUnpaidAccountings, gbc_lblUnpaidAccountings);

		accountingListModel = new DefaultListModel<>();
		accountingList = new JList<>(accountingListModel);
		accountingList.setForeground(new Color(51, 51, 51));

		accountingList.setCellRenderer(new AccountingCellRenderer());

		accountingList.setName("accountingList");
		GridBagConstraints gbc_accountingList = new GridBagConstraints();
		gbc_accountingList.insets = new Insets(0, 0, 5, 0);
		gbc_accountingList.fill = GridBagConstraints.BOTH;
		gbc_accountingList.gridx = 1;
		gbc_accountingList.gridy = 4;
		frame.getContentPane().add(accountingList, gbc_accountingList);

		lblTotalAccountings = new JLabel("Total unpaid accountings:");
		GridBagConstraints gbc_lblTotalAccountings = new GridBagConstraints();
		gbc_lblTotalAccountings.insets = new Insets(0, 0, 5, 0);
		gbc_lblTotalAccountings.gridx = 1;
		gbc_lblTotalAccountings.gridy = 5;
		frame.getContentPane().add(lblTotalAccountings, gbc_lblTotalAccountings);

		totalAccountingsListModel = new DefaultListModel<>();
		totalAccountingList = new JList<>(totalAccountingsListModel);
		totalAccountingList.setName("totalAccountingList");
		GridBagConstraints gbc_totalAccountingList = new GridBagConstraints();
		gbc_totalAccountingList.gridheight = 4;
		gbc_totalAccountingList.fill = GridBagConstraints.BOTH;
		gbc_totalAccountingList.gridx = 1;
		gbc_totalAccountingList.gridy = 6;
		frame.getContentPane().add(totalAccountingList, gbc_totalAccountingList);

		btnHomepage = new JButton("Homepage");
		btnHomepage.setName("homepageBtn");
		GridBagConstraints gbc_btnHomepage = new GridBagConstraints();
		gbc_btnHomepage.insets = new Insets(0, 0, 5, 5);
		gbc_btnHomepage.gridx = 0;
		gbc_btnHomepage.gridy = 8;
		frame.getContentPane().add(btnHomepage, gbc_btnHomepage);

		message = new JLabel("");
		message.setName("errorMsg");
		GridBagConstraints gbc_message = new GridBagConstraints();
		gbc_message.insets = new Insets(0, 0, 0, 5);
		gbc_message.gridx = 0;
		gbc_message.gridy = 9;
		frame.getContentPane().add(message, gbc_message);
	}

	@Override
	public void showShoppingHistory(List<Receipt> receipts) {
		receiptListModel.clear();
		if (receipts != null) {
			receipts.stream().forEach(receiptListModel::addElement);
		}
		computeTotalAccountings(receipts);
	}

	private void showItemList(Receipt receipt) {
		itemListModel.clear();
		if (receipt != null)
			receipt.getItems().stream().forEach(itemListModel::addElement);
	}

	private void showAccountingList(Receipt receipt) {
		accountingListModel.clear();
		if (receipt != null)
			receipt.getAccountings().stream().forEach(accountingListModel::addElement);
	}

	private void computeTotalAccountings(List<Receipt> history) {
		totalAccountingsListModel.clear();
		if (history != null) {
			Map<User, Double> totalAccounting = new HashMap<User, Double>();

			history.stream().forEach(r -> {
				r.getAccountings().stream().forEach(a -> {
					if (!a.isPaid()) {
						Double partialAccounting = totalAccounting.get(a.getUser());
						if (partialAccounting == null)
							partialAccounting = 0.0;
						partialAccounting += a.getAmount();
						totalAccounting.put(a.getUser(), partialAccounting);
					}
				});
			});

			String accountingFormat = "%s:	%.2f€";
			totalAccounting.keySet().stream().forEach(u -> totalAccountingsListModel
					.addElement(String.format(accountingFormat, u.getUsername(), totalAccounting.get(u))));
		}
	}

	public Frame getFrame() {
		return frame;
	}

	@Override
	public void showErrorMsg(String msg) {
		this.message.setText(msg);
	}
}
