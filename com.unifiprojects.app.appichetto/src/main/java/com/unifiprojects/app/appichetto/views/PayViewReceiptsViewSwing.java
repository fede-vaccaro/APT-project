package com.unifiprojects.app.appichetto.views;

import java.awt.EventQueue;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;

import java.awt.FlowLayout;
import javax.swing.JList;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.SwingConstants;

public class PayViewReceiptsViewSwing implements PayViewReceiptsView {

	public JFrame getFrame() {
		return frame;
	}

	private JFrame frame;
	private JTextField txtEnterAmount;
	private JLabel lblErrorMsg;
	private JList<CustomToStringReceipt> receiptList;
	private DefaultListModel<CustomToStringReceipt> receiptListModel;
	private JComboBox<User> userSelection;
	private DefaultComboBoxModel<User> userComboBoxModel;
	private JList<Item> itemList;
	private DefaultListModel<Item> itemListModels;// each add element triggers a listener, which actually refresh the
													// receipt lis
	private List<Receipt> unpaids;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PayViewReceiptsViewSwing window = new PayViewReceiptsViewSwing();
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
	public PayViewReceiptsViewSwing() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setFont(new Font("Dialog", Font.PLAIN, 14));
		frame.setBounds(100, 100, 1024, 768);
		frame.setMinimumSize(new Dimension(1024, 768));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		JLabel lblUser = new JLabel("User:");
		lblUser.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblUser = new GridBagConstraints();
		gbc_lblUser.anchor = GridBagConstraints.WEST;
		gbc_lblUser.insets = new Insets(0, 0, 5, 5);
		gbc_lblUser.gridx = 1;
		gbc_lblUser.gridy = 1;
		frame.getContentPane().add(lblUser, gbc_lblUser);

		userComboBoxModel = new DefaultComboBoxModel<User>();
		userSelection = new JComboBox<User>(userComboBoxModel);
		userSelection.setName("userSelection");

		userSelection.addActionListener(e -> refreshReceiptList());

		GridBagConstraints gbc_userSelection = new GridBagConstraints();
		gbc_userSelection.insets = new Insets(0, 0, 5, 5);
		gbc_userSelection.fill = GridBagConstraints.HORIZONTAL;
		gbc_userSelection.gridx = 1;
		gbc_userSelection.gridy = 2;
		frame.getContentPane().add(userSelection, gbc_userSelection);

		JLabel lblReceipts = new JLabel("Receipts:");
		GridBagConstraints gbc_lblReceipts = new GridBagConstraints();
		gbc_lblReceipts.anchor = GridBagConstraints.WEST;
		gbc_lblReceipts.insets = new Insets(0, 0, 5, 5);
		gbc_lblReceipts.gridx = 1;
		gbc_lblReceipts.gridy = 4;
		frame.getContentPane().add(lblReceipts, gbc_lblReceipts);

		JLabel lblItemsInReceipts = new JLabel("Items in receipt:");
		GridBagConstraints gbc_lblItemsInReceipts = new GridBagConstraints();
		gbc_lblItemsInReceipts.anchor = GridBagConstraints.WEST;
		gbc_lblItemsInReceipts.insets = new Insets(0, 0, 5, 0);
		gbc_lblItemsInReceipts.gridx = 3;
		gbc_lblItemsInReceipts.gridy = 4;
		frame.getContentPane().add(lblItemsInReceipts, gbc_lblItemsInReceipts);

		receiptListModel = new DefaultListModel<>();
		receiptList = new JList<>(receiptListModel);
		receiptList.setName("receiptList");
		receiptList.addListSelectionListener(e -> {
			int selectedIndex = receiptList.getSelectedIndex();
			if (selectedIndex >= 0) {
				showItems(receiptListModel.get(selectedIndex).getReceipt().getItems());
			}
		});

		GridBagConstraints gbc_receiptList = new GridBagConstraints();
		gbc_receiptList.gridheight = 2;
		gbc_receiptList.insets = new Insets(0, 0, 5, 5);
		gbc_receiptList.fill = GridBagConstraints.BOTH;
		gbc_receiptList.gridx = 1;
		gbc_receiptList.gridy = 5;
		frame.getContentPane().add(receiptList, gbc_receiptList);

		itemListModels = new DefaultListModel<>();
		itemList = new JList<>(itemListModels);
		itemList.setName("itemList");
		GridBagConstraints gbc_itemList = new GridBagConstraints();
		gbc_itemList.insets = new Insets(0, 0, 5, 0);
		gbc_itemList.gridheight = 2;
		gbc_itemList.fill = GridBagConstraints.BOTH;
		gbc_itemList.gridx = 3;
		gbc_itemList.gridy = 5;
		frame.getContentPane().add(itemList, gbc_itemList);

		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.insets = new Insets(0, 0, 5, 5);
		gbc_separator.gridx = 2;
		gbc_separator.gridy = 6;
		frame.getContentPane().add(separator, gbc_separator);

		JLabel lblTotaldebttouser = new JLabel("Total debt to user:");
		lblTotaldebttouser.setFont(new Font("Dialog", Font.BOLD, 14));
		GridBagConstraints gbc_lblTotaldebttouser = new GridBagConstraints();
		gbc_lblTotaldebttouser.anchor = GridBagConstraints.WEST;
		gbc_lblTotaldebttouser.insets = new Insets(0, 0, 5, 5);
		gbc_lblTotaldebttouser.gridx = 1;
		gbc_lblTotaldebttouser.gridy = 7;
		frame.getContentPane().add(lblTotaldebttouser, gbc_lblTotaldebttouser);

		JLabel lblTotalForThis = new JLabel("Total for this receipt:");
		GridBagConstraints gbc_lblTotalForThis = new GridBagConstraints();
		gbc_lblTotalForThis.anchor = GridBagConstraints.WEST;
		gbc_lblTotalForThis.insets = new Insets(0, 0, 5, 0);
		gbc_lblTotalForThis.gridx = 3;
		gbc_lblTotalForThis.gridy = 7;
		frame.getContentPane().add(lblTotalForThis, gbc_lblTotalForThis);

		txtEnterAmount = new JTextField();
		txtEnterAmount.setForeground(Color.LIGHT_GRAY);
		txtEnterAmount.setText("Enter amount...");
		txtEnterAmount.setFont(new Font("Dialog", Font.PLAIN, 14));
		GridBagConstraints gbc_txtEnterAmount = new GridBagConstraints();
		gbc_txtEnterAmount.insets = new Insets(0, 0, 5, 5);
		gbc_txtEnterAmount.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtEnterAmount.gridx = 1;
		gbc_txtEnterAmount.gridy = 8;
		frame.getContentPane().add(txtEnterAmount, gbc_txtEnterAmount);
		txtEnterAmount.setColumns(10);

		JButton btnPay = new JButton("Pay");
		btnPay.setEnabled(false);
		btnPay.setName("payButton");
		GridBagConstraints gbc_btnPay = new GridBagConstraints();
		gbc_btnPay.insets = new Insets(0, 0, 5, 5);
		gbc_btnPay.gridx = 1;
		gbc_btnPay.gridy = 9;
		frame.getContentPane().add(btnPay, gbc_btnPay);

		lblErrorMsg = new JLabel("");
		lblErrorMsg.setForeground(Color.RED);
		lblErrorMsg.setName("errorMsg");
		GridBagConstraints gbc_lblErrorMsg = new GridBagConstraints();
		gbc_lblErrorMsg.insets = new Insets(0, 0, 0, 5);
		gbc_lblErrorMsg.gridx = 1;
		gbc_lblErrorMsg.gridy = 10;
		frame.getContentPane().add(lblErrorMsg, gbc_lblErrorMsg);
	}

	private void refreshReceiptList() {

		User selectedUser = (User) userComboBoxModel.getSelectedItem();
		receiptListModel.clear();
		unpaids.stream().filter(r -> r.getBuyer().equals(selectedUser))
				.forEach(r -> receiptListModel.addElement(new CustomToStringReceipt(r)));
		receiptList.setSelectedIndex(0);
		// showItems(receiptListModel.get(0).getReceipt().getItems());
	}

	@Override
	public void showReceipts(List<Receipt> receipts) {
		if (receipts == null) {
			showErrorMsg("You have no accounting.");
		} else {
			this.unpaids = receipts;
			Set<User> userSet = new HashSet<>();

			unpaids.stream().forEach(r -> userSet.add(r.getBuyer()));
			// each add element triggers a listener, which actually refresh the receipt list
			userSet.stream().forEach(userComboBoxModel::addElement);

		}
	}

	@Override
	public void showItems(List<Item> items) {
		itemListModels.clear();
		items.stream().forEach(itemListModels::addElement);
	}

	@Override
	public void showErrorMsg(String msg) {
		lblErrorMsg.setText(msg);
	}
}
