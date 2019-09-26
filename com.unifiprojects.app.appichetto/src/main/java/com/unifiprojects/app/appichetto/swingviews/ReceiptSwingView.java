package com.unifiprojects.app.appichetto.swingviews;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import com.unifiprojects.app.appichetto.controllers.ReceiptController;
import com.unifiprojects.app.appichetto.listselectionmodel.ItemsListSelectionModel;
import com.unifiprojects.app.appichetto.listselectionmodel.UsersListSelectionModel;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.views.ReceiptView;

public class ReceiptSwingView extends JFrame implements ReceiptView {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFormattedTextField txtPrice;
	private NumberFormat priceFormat;
	private NumberFormatter priceFormatter;
	private JTextField txtName;
	private JFormattedTextField txtQuantity;
	private NumberFormat quantityFormat;
	private NumberFormatter quantityFormatter;
	private JLabel lblQuantity;
	private JScrollPane usersScrollPane;
	private JScrollPane itemsScrollPane;
	private JButton btnSave;
	private JButton btnDelete;
	private JList<Item> itemsList;
	private DefaultListModel<Item> listItemModel;
	private JLabel errorMsgLabel;

	private ReceiptController receiptController;
	private JList<User> usersList;

	private DefaultListModel<User> listUsersModel;
	private JButton btnUpdate;
	private JTextField textField;
	private JTextField textField_1;
	private JLabel lblDescription;
	private JLabel lblDate;
	private JButton btnSaveReceipt;
	private JButton btnHome;
	private JButton btnExit;
	private JLabel lblItem;
	private JLabel lblReceipt;
	private JLabel lblUsersList;
	private JLabel lblItemsList;

	public void setReceiptController(ReceiptController receiptController) {
		this.receiptController = receiptController;
		receiptController.getUsers().stream().forEach(listUsersModel::addElement);
	}

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(() -> {
//			try {
//				ReceiptSwingView frame = new ReceiptSwingView();
//				frame.setReceiptController(new ReceiptController(new Receipt(), frame));
//				frame.setVisible(true);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
//	}

	public DefaultListModel<Item> getListItemModel() {
		return listItemModel;
	}

	public DefaultListModel<User> getListUsersModel() {
		return listUsersModel;
	}

	public JList<User> getUsersList() {
		return usersList;
	}

	@Override
	public void showCurrentItemsList(List<Item> items) {
		items.stream().forEach(listItemModel::addElement);
	}

	@Override
	public void showError(String string) {
		errorMsgLabel.setText(string);
	}

	@Override
	public void showCurrentUsers(List<User> users) {
		users.stream().forEach(listUsersModel::addElement);
	}

	@Override
	public void itemAdded(Item item) {
		listItemModel.addElement(item);
		resetErrorLabel();
	}

	@Override
	public void itemUpdated(int index, Item item) {
		listItemModel.set(index, item);
		resetErrorLabel();
	}

	@Override
	public void itemDeleted(Item item) {
		listItemModel.removeElement(item);
		resetErrorLabel();
	}

	private void resetErrorLabel() {
		errorMsgLabel.setText(" ");
	}

	private void clearForm() {
		txtName.setText("");
		txtPrice.setText("");
		txtQuantity.setText("");
		usersList.clearSelection();
	}

	/**
	 * Create the frame.
	 */
	public ReceiptSwingView() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 245, 54, 216, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 15, 15, 19, 15, 19, 19, 22, 19, 0, 93, 1, 25, 25, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		KeyAdapter btnSaveEnabled = null;
		KeyAdapter btnUpdateEnabled = null;
		listItemModel = new DefaultListModel<>();
		listUsersModel = new DefaultListModel<>();

		lblReceipt = new JLabel("RECEIPT");
		GridBagConstraints gbc_lblReceipt = new GridBagConstraints();
		gbc_lblReceipt.insets = new Insets(0, 0, 5, 5);
		gbc_lblReceipt.gridx = 1;
		gbc_lblReceipt.gridy = 0;
		getContentPane().add(lblReceipt, gbc_lblReceipt);

		lblItem = new JLabel("ITEM");
		GridBagConstraints gbc_lblItem = new GridBagConstraints();
		gbc_lblItem.insets = new Insets(0, 0, 5, 5);
		gbc_lblItem.gridx = 3;
		gbc_lblItem.gridy = 0;
		getContentPane().add(lblItem, gbc_lblItem);

		JLabel lblName = new JLabel("Name");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.NORTH;
		gbc_lblName.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 3;
		gbc_lblName.gridy = 1;
		getContentPane().add(lblName, gbc_lblName);

		lblDescription = new JLabel("Description");
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblDescription.insets = new Insets(0, 0, 5, 5);
		gbc_lblDescription.gridx = 1;
		gbc_lblDescription.gridy = 1;
		getContentPane().add(lblDescription, gbc_lblDescription);

		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.anchor = GridBagConstraints.NORTH;
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 2;
		getContentPane().add(textField, gbc_textField);
		textField.setColumns(10);

		txtName = new JTextField();
		txtName.setName("nameBox");
		GridBagConstraints gbc_txtName = new GridBagConstraints();
		gbc_txtName.anchor = GridBagConstraints.NORTH;
		gbc_txtName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtName.insets = new Insets(0, 0, 5, 5);
		gbc_txtName.gridx = 3;
		gbc_txtName.gridy = 2;
		getContentPane().add(txtName, gbc_txtName);
		txtName.setColumns(10);

		lblDate = new JLabel("Date");
		GridBagConstraints gbc_lblDate = new GridBagConstraints();
		gbc_lblDate.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblDate.insets = new Insets(0, 0, 5, 5);
		gbc_lblDate.gridx = 1;
		gbc_lblDate.gridy = 3;
		getContentPane().add(lblDate, gbc_lblDate);

		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.anchor = GridBagConstraints.NORTH;
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 4;
		getContentPane().add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);

		JLabel lblPrice = new JLabel("Price");
		GridBagConstraints gbc_lblPrice = new GridBagConstraints();
		gbc_lblPrice.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblPrice.insets = new Insets(0, 0, 5, 5);
		gbc_lblPrice.gridx = 3;
		gbc_lblPrice.gridy = 3;
		getContentPane().add(lblPrice, gbc_lblPrice);

		priceFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
		priceFormatter = new NumberFormatter(priceFormat);
		priceFormatter.setValueClass(Double.class);
		priceFormatter.setMinimum(0.0);
		priceFormatter.setAllowsInvalid(true);
		txtPrice = new JFormattedTextField(priceFormatter);
		txtPrice.setName("priceBox");

		GridBagConstraints gbc_txtPrice = new GridBagConstraints();
		gbc_txtPrice.anchor = GridBagConstraints.NORTH;
		gbc_txtPrice.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPrice.insets = new Insets(0, 0, 5, 5);
		gbc_txtPrice.gridx = 3;
		gbc_txtPrice.gridy = 4;
		getContentPane().add(txtPrice, gbc_txtPrice);
		txtPrice.setColumns(10);

		itemsScrollPane = new JScrollPane();
		itemsList = new JList<>(listItemModel);
		itemsList.setName("itemsList");
		itemsList.setSelectionModel(new ItemsListSelectionModel());
		itemsScrollPane.setViewportView(itemsList);
		itemsList.addListSelectionListener(arg0 -> {
			if (!itemsList.isSelectionEmpty()) {
				Item item = itemsList.getSelectedValue();
				txtName.setText(item.getName());
				txtPrice.setText(item.getPrice().toString());
				txtQuantity.setText(item.getQuantity().toString());
				int[] indeces = item.getOwners().stream().mapToInt(user -> listUsersModel.indexOf(user)).toArray();
				usersList.setSelectedIndices(indeces);
			} else {
				clearForm();
			}
			btnSave.setEnabled(itemsList.getSelectedIndex() != -1);
			btnUpdate.setEnabled(itemsList.getSelectedIndex() != -1);
			btnDelete.setEnabled(itemsList.getSelectedIndex() != -1);
		});

		lblItemsList = new JLabel("Items list");
		GridBagConstraints gbc_lblItemsList = new GridBagConstraints();
		gbc_lblItemsList.anchor = GridBagConstraints.WEST;
		gbc_lblItemsList.insets = new Insets(0, 0, 5, 5);
		gbc_lblItemsList.gridx = 1;
		gbc_lblItemsList.gridy = 5;
		getContentPane().add(lblItemsList, gbc_lblItemsList);
		GridBagConstraints gbc_itemsScrollPane = new GridBagConstraints();
		gbc_itemsScrollPane.fill = GridBagConstraints.BOTH;
		gbc_itemsScrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_itemsScrollPane.gridheight = 4;
		gbc_itemsScrollPane.gridx = 1;
		gbc_itemsScrollPane.gridy = 6;
		getContentPane().add(itemsScrollPane, gbc_itemsScrollPane);

		lblQuantity = new JLabel("Quantity");
		GridBagConstraints gbc_lblQuantity = new GridBagConstraints();
		gbc_lblQuantity.anchor = GridBagConstraints.SOUTH;
		gbc_lblQuantity.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblQuantity.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuantity.gridx = 3;
		gbc_lblQuantity.gridy = 5;
		getContentPane().add(lblQuantity, gbc_lblQuantity);

		quantityFormat = NumberFormat.getInstance();
		quantityFormat.setGroupingUsed(false);
		quantityFormatter = new NumberFormatter(quantityFormat);
		quantityFormatter.setValueClass(Integer.class);

		quantityFormatter.setMinimum(0);
		quantityFormatter.setAllowsInvalid(true);
		txtQuantity = new JFormattedTextField(quantityFormatter);
		txtQuantity.setName("quantityBox");
		txtQuantity.setColumns(10);

		GridBagConstraints gbc_txtQuantity = new GridBagConstraints();
		gbc_txtQuantity.anchor = GridBagConstraints.NORTH;
		gbc_txtQuantity.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtQuantity.insets = new Insets(0, 0, 5, 5);
		gbc_txtQuantity.gridx = 3;
		gbc_txtQuantity.gridy = 6;
		getContentPane().add(txtQuantity, gbc_txtQuantity);

		usersScrollPane = new JScrollPane();
		usersList = new JList<>(listUsersModel);
		usersList.setName("usersList");
		usersList.setSelectionModel(new UsersListSelectionModel());

		usersList.addListSelectionListener(arg0 -> {
			btnSave.setEnabled(!txtName.getText().trim().isEmpty() && !txtPrice.getText().isEmpty()
					&& !txtQuantity.getText().isEmpty() && !usersList.isSelectionEmpty());
			btnUpdate.setEnabled(!txtName.getText().trim().isEmpty() && !txtPrice.getText().isEmpty()
					&& !txtQuantity.getText().isEmpty() && !usersList.isSelectionEmpty()
					&& !itemsList.isSelectionEmpty());
		});

		lblUsersList = new JLabel("Users list");
		GridBagConstraints gbc_lblUsersList = new GridBagConstraints();
		gbc_lblUsersList.anchor = GridBagConstraints.WEST;
		gbc_lblUsersList.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsersList.gridx = 3;
		gbc_lblUsersList.gridy = 8;
		getContentPane().add(lblUsersList, gbc_lblUsersList);
		usersScrollPane.setViewportView(usersList);
		GridBagConstraints gbc_usersScrollPane = new GridBagConstraints();
		gbc_usersScrollPane.fill = GridBagConstraints.BOTH;
		gbc_usersScrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_usersScrollPane.gridx = 3;
		gbc_usersScrollPane.gridy = 9;
		getContentPane().add(usersScrollPane, gbc_usersScrollPane);

		errorMsgLabel = new JLabel("");
		errorMsgLabel.setName("errorMsgLabel");
		errorMsgLabel.setForeground(Color.RED);
		GridBagConstraints gbc_errorMsgLabel = new GridBagConstraints();
		gbc_errorMsgLabel.anchor = GridBagConstraints.NORTH;
		gbc_errorMsgLabel.insets = new Insets(0, 0, 5, 5);
		gbc_errorMsgLabel.gridx = 1;
		gbc_errorMsgLabel.gridy = 10;
		getContentPane().add(errorMsgLabel, gbc_errorMsgLabel);

		btnSave = new JButton("Save");
		btnSave.addActionListener(e -> {
			receiptController.addItem(new Item(txtName.getText(), Double.valueOf(txtPrice.getText()),
					Integer.valueOf(txtQuantity.getText()), usersList.getSelectedValuesList()));
			btnSaveReceipt.setEnabled(true);
			clearForm();
			itemsList.clearSelection();
		});

		btnSaveReceipt = new JButton("Save Receipt");
		btnSaveReceipt.setEnabled(false);
		GridBagConstraints gbc_btnSaveReceipt = new GridBagConstraints();
		gbc_btnSaveReceipt.fill = GridBagConstraints.BOTH;
		gbc_btnSaveReceipt.insets = new Insets(0, 0, 5, 5);
		gbc_btnSaveReceipt.gridx = 1;
		gbc_btnSaveReceipt.gridy = 11;
		getContentPane().add(btnSaveReceipt, gbc_btnSaveReceipt);
		btnSaveReceipt.addActionListener(e -> {
			receiptController.saveReceipt();
		});
		btnSave.setEnabled(false);
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.anchor = GridBagConstraints.NORTH;
		gbc_btnSave.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSave.insets = new Insets(0, 0, 5, 5);
		gbc_btnSave.gridx = 3;
		gbc_btnSave.gridy = 11;
		getContentPane().add(btnSave, gbc_btnSave);

		btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(e -> {
			receiptController.updateItem(
					new Item(txtName.getText(), Double.valueOf(txtPrice.getText()),
							Integer.valueOf(txtQuantity.getText()), usersList.getSelectedValuesList()),
					itemsList.getSelectedIndex());
			btnSaveReceipt.setEnabled(true);
			clearForm();
			itemsList.clearSelection();
		});

		btnHome = new JButton("Home");
		GridBagConstraints gbc_btnHome = new GridBagConstraints();
		gbc_btnHome.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnHome.insets = new Insets(0, 0, 5, 5);
		gbc_btnHome.gridx = 1;
		gbc_btnHome.gridy = 12;
		getContentPane().add(btnHome, gbc_btnHome);
		btnUpdate.setEnabled(false);
		GridBagConstraints gbc_btnUpdate = new GridBagConstraints();
		gbc_btnUpdate.anchor = GridBagConstraints.NORTH;
		gbc_btnUpdate.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnUpdate.insets = new Insets(0, 0, 5, 5);
		gbc_btnUpdate.gridx = 3;
		gbc_btnUpdate.gridy = 12;
		getContentPane().add(btnUpdate, gbc_btnUpdate);

		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(e -> {
			receiptController.deleteItem(itemsList.getSelectedValue());
			btnSaveReceipt.setEnabled(true);
		});

		btnExit = new JButton("Exit");

		GridBagConstraints gbc_btnExit = new GridBagConstraints();
		gbc_btnExit.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnExit.insets = new Insets(0, 0, 5, 5);
		gbc_btnExit.gridx = 1;
		gbc_btnExit.gridy = 13;

		getContentPane().add(btnExit, gbc_btnExit);

		btnDelete.setEnabled(false);
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.insets = new Insets(0, 0, 5, 5);
		gbc_btnDelete.anchor = GridBagConstraints.NORTH;
		gbc_btnDelete.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnDelete.gridx = 3;
		gbc_btnDelete.gridy = 13;
		getContentPane().add(btnDelete, gbc_btnDelete);

		btnSaveEnabled = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				btnSave.setEnabled(!txtName.getText().trim().isEmpty() && !txtPrice.getText().isEmpty()
						&& !txtQuantity.getText().isEmpty() && !usersList.isSelectionEmpty());
			}
		};

		btnUpdateEnabled = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				btnUpdate.setEnabled(!txtName.getText().trim().isEmpty() && !txtPrice.getText().isEmpty()
						&& !txtQuantity.getText().isEmpty() && !usersList.isSelectionEmpty()
						&& !itemsList.isSelectionEmpty());
			}
		};
		txtQuantity.addKeyListener(btnSaveEnabled);
		txtQuantity.addKeyListener(btnUpdateEnabled);
		txtPrice.addKeyListener(btnSaveEnabled);
		txtPrice.addKeyListener(btnUpdateEnabled);
		txtName.addKeyListener(btnSaveEnabled);
		txtName.addKeyListener(btnUpdateEnabled);

		this.pack();

	}

	@Override
	public void goToHome() {
		// TODO Auto-generated method stub
	}
}