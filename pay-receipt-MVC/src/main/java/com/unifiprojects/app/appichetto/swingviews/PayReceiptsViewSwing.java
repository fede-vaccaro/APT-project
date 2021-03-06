package com.unifiprojects.app.appichetto.swingviews;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.commons.math3.util.Precision;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unifiprojects.app.appichetto.controllers.PayReceiptsController;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.swingviews.utils.ReceiptCellRenderer;
import com.unifiprojects.app.appichetto.views.PayReceiptsView;

public class PayReceiptsViewSwing extends LinkedControlledSwingView implements PayReceiptsView {

	private static final Logger LOGGER = LogManager.getLogger(PayReceiptsViewSwing.class);

	private static final String TOTALFORTHISRECEIPTMESSAGE = "Total for this receipt: ";
	public static final String TOTALDEBTTOUSERMESSAGE = "Total debt to user: ";

	private JTextField txtEnterAmount;
	private JLabel lblErrorMsg;
	private JList<Receipt> receiptList;
	DefaultListModel<Receipt> receiptListModel;
	private JComboBox<User> userSelection;
	DefaultComboBoxModel<User> userComboBoxModel;
	private DefaultListModel<Item> itemListModel;
	private JLabel lblTotalForThis;
	private JLabel lblTotaldebttouser;
	
	private Double enteredAmount;

	public List<Accounting> getAccountings() {
		return accountings;
	}

	public void setAccountings(List<Accounting> accountings) {
		this.accountings = accountings;
	}

	public List<Receipt> getUnpaids() {
		return unpaids;
	}

	public void setUnpaids(List<Receipt> unpaids) {
		this.unpaids = unpaids;
	}

	private List<Receipt> unpaids;
	private List<Accounting> accountings;

	public Double getEnteredValue() {
		return enteredAmount;
	}

	public PayReceiptsViewSwing() {
		initialize();
	}

	private boolean parseEnteredAmountAndCheckIfIsValid() {
		String enteredAmountString = txtEnterAmount.getText();

		try {
			enteredAmount = (double) Integer.parseInt(enteredAmountString);
			return true;
		} catch (NumberFormatException ex) {
			try {
				enteredAmount = Double.parseDouble(enteredAmountString);
				return true;
			} catch (NumberFormatException ex2) {
				return false;
			}

		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		Locale.setDefault(Locale.US);
		String fontName = "Dialog";
		
		Font dialogFontPlain = new Font(fontName, Font.PLAIN, 14);
		Font fialogFontBold = new Font(fontName, Font.BOLD, 14);
		
		frame = new JFrame();
		frame.setTitle("Pay and View receipts bought by others");
		frame.setName("Pay Receipt");
		frame.getContentPane().setFont(dialogFontPlain);
		frame.setBounds(100, 100, 450, 300);
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
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

		userComboBoxModel = new DefaultComboBoxModel<>();
		userSelection = new JComboBox<>(userComboBoxModel);
		userSelection.setName("User selection");

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

		JButton btnPay = new JButton("Pay");
		btnPay.setEnabled(false);
		btnPay.addActionListener(e -> {
			
			User loggedUser = getController().getLoggedUser();
			LOGGER.info(enteredAmount);
			LOGGER.info(loggedUser);
			LOGGER.info(userComboBoxModel.getSelectedItem());
			LOGGER.info(getController());
			((PayReceiptsController) getController()).payAmount(enteredAmount, loggedUser, (User) userComboBoxModel.getSelectedItem());
			txtEnterAmount.setText("");
		});

		GridBagConstraints gbc_btnBack = new GridBagConstraints();
		gbc_btnBack.insets = new Insets(0, 0, 5, 5);
		gbc_btnBack.gridx = 2;
		gbc_btnBack.gridy = 12;
		frame.getContentPane().add(getBtnBack(), gbc_btnBack);
		btnPay.setName("payButton");
		GridBagConstraints gbc_btnPay = new GridBagConstraints();
		gbc_btnPay.insets = new Insets(0, 0, 5, 5);
		gbc_btnPay.gridx = 1;
		gbc_btnPay.gridy = 10;
		frame.getContentPane().add(btnPay, gbc_btnPay);

		receiptListModel = new DefaultListModel<>();
		receiptList = new JList<>(receiptListModel);
		receiptList.setCellRenderer(new ReceiptCellRenderer());
		receiptList.setName("Receipts list");
		receiptList.addListSelectionListener(e -> {
			int selectedIndex = receiptList.getSelectedIndex();
			if (selectedIndex >= 0) {
				Receipt receipt = receiptListModel.get(selectedIndex);
				showItems(receipt.getItems());
				displayReceiptAmount(receipt);
			}
			User selectedUser = (User) userComboBoxModel.getSelectedItem();
			refreshTotalDebtToSelectedUser(selectedUser);
		});

		userSelection.addActionListener(e -> btnPay.setEnabled(getIfPayButtonShouldBeEnabled()));

		GridBagConstraints gbcReceiptList = new GridBagConstraints();
		gbcReceiptList.gridheight = 2;
		gbcReceiptList.insets = new Insets(0, 0, 5, 5);
		gbcReceiptList.fill = GridBagConstraints.BOTH;
		gbcReceiptList.gridx = 1;
		gbcReceiptList.gridy = 5;
		frame.getContentPane().add(receiptList, gbcReceiptList);

		itemListModel = new DefaultListModel<>();
		JList<Item> itemList = new JList<>(itemListModel);
		itemList.setName("Items list");
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

		lblTotaldebttouser = new JLabel(TOTALDEBTTOUSERMESSAGE);
		lblTotaldebttouser.setName("totalDebtToUser");
		lblTotaldebttouser.setFont(fialogFontBold);
		GridBagConstraints gbc_lblTotaldebttouser = new GridBagConstraints();
		gbc_lblTotaldebttouser.anchor = GridBagConstraints.WEST;
		gbc_lblTotaldebttouser.insets = new Insets(0, 0, 5, 5);
		gbc_lblTotaldebttouser.gridx = 1;
		gbc_lblTotaldebttouser.gridy = 7;
		frame.getContentPane().add(lblTotaldebttouser, gbc_lblTotaldebttouser);

		lblTotalForThis = new JLabel(TOTALFORTHISRECEIPTMESSAGE);
		lblTotalForThis.setFont(fialogFontBold);
		lblTotalForThis.setName("totalForSelectedReceipt");
		GridBagConstraints gbc_lblTotalForThis = new GridBagConstraints();
		gbc_lblTotalForThis.anchor = GridBagConstraints.WEST;
		gbc_lblTotalForThis.insets = new Insets(0, 0, 5, 0);
		gbc_lblTotalForThis.gridx = 3;
		gbc_lblTotalForThis.gridy = 7;
		frame.getContentPane().add(lblTotalForThis, gbc_lblTotalForThis);

		txtEnterAmount = new JTextField();
		txtEnterAmount.setName("enterAmountField");
		txtEnterAmount.setFont(dialogFontPlain);

		txtEnterAmount.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (parseEnteredAmountAndCheckIfIsValid() || txtEnterAmount.getText().isEmpty()) {
					btnPay.setEnabled(getIfPayButtonShouldBeEnabled());
					showErrorMsg("");
				} else {
					showErrorMsg("Not valid entered amount.");
				}
			}

		});

		GridBagConstraints gbc_txtEnterAmount = new GridBagConstraints();
		gbc_txtEnterAmount.insets = new Insets(0, 0, 5, 5);
		gbc_txtEnterAmount.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtEnterAmount.gridx = 1;
		gbc_txtEnterAmount.gridy = 9;
		frame.getContentPane().add(txtEnterAmount, gbc_txtEnterAmount);
		txtEnterAmount.setColumns(10);

		lblErrorMsg = new JLabel("");
		lblErrorMsg.setForeground(Color.RED);
		lblErrorMsg.setName("errorMsg");
		GridBagConstraints gbc_lblErrorMsg = new GridBagConstraints();
		gbc_lblErrorMsg.insets = new Insets(0, 0, 0, 5);
		gbc_lblErrorMsg.gridx = 1;
		gbc_lblErrorMsg.gridy = 11;
		frame.getContentPane().add(lblErrorMsg, gbc_lblErrorMsg);
	}

	private boolean getIfPayButtonShouldBeEnabled() {
		return (userSelection.getSelectedIndex() > -1) && parseEnteredAmountAndCheckIfIsValid()
				&& (enteredAmount <= Precision
						.round(getTotalDebtToSelectedUser((User) userComboBoxModel.getSelectedItem()), 2));
	}

	private void refreshReceiptList() {

		User selectedUser = (User) userComboBoxModel.getSelectedItem();
		receiptListModel.clear();
		unpaids.stream().filter(r -> r.getBuyer().equals(selectedUser))
				.forEach(receiptListModel::addElement);
		receiptList.setSelectedIndex(0);
	}

	private void refreshTotalDebtToSelectedUser(User selectedUser) {
		if (accountings != null) {
			double totalDebtToSelectedUser = getTotalDebtToSelectedUser(selectedUser);
			lblTotaldebttouser.setText(TOTALDEBTTOUSERMESSAGE + String.format("%.2f", totalDebtToSelectedUser));
		}
	}

	private double getTotalDebtToSelectedUser(User selectedUser) {
		return accountings.stream().filter(a -> a.getReceipt().getBuyer().equals(selectedUser))
				.mapToDouble(Accounting::getAmount).sum();
	}

	@Override
	public void showReceipts(List<Receipt> receipts) {
		receiptListModel.clear();
		itemListModel.clear();
		userComboBoxModel.removeAllElements();
		if (receipts.isEmpty()) {
			showErrorMsg("You have no accountings.");
		} else {
			this.unpaids = receipts;

			extractEachAccountingOfLoggedUser(receipts);

			Set<User> userSet = new HashSet<>();

			unpaids.stream().forEach(r -> userSet.add(r.getBuyer()));
			// each add element triggers a listener, which actually refresh the receipt list
			userSet.stream().forEach(userComboBoxModel::addElement);

		}
	}

	public String getLblErrorMsg() {
		return lblErrorMsg.getText();
	}

	public void setLblErrorMsg(String lblErrorMsg) {
		this.lblErrorMsg.setText(lblErrorMsg);
	}

	private void extractEachAccountingOfLoggedUser(List<Receipt> receipts) {
		accountings = new ArrayList<>();
		receipts.stream().forEach(r -> accountings.addAll(r.getAccountings()));
		User loggedUser = getController().getLoggedUser();
		accountings = accountings.stream().filter(a -> a.getUser().equals(loggedUser)).collect(Collectors.toList());
	}

	private void displayReceiptAmount(Receipt receipt) {
		lblTotalForThis.setText(TOTALFORTHISRECEIPTMESSAGE + String.format("%.2f", receipt.getTotalPrice()));
	}

	@Override
	public void showItems(List<Item> items) {
		itemListModel.clear();
		items.stream().forEach(itemListModel::addElement);
	}

	@Override
	public void showErrorMsg(String msg) {
		lblErrorMsg.setText(msg);
	}

	@Override
	public void updateData() {
		lblErrorMsg.setText("");
		getController().update();
	}

	public void setPayReceiptsController(PayReceiptsController payReceiptsController) {
		super.setController(payReceiptsController);	
	}
}
