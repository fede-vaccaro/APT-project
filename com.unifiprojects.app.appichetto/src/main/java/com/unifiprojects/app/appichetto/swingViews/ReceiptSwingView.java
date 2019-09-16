package com.unifiprojects.app.appichetto.swingViews;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.unifiprojects.app.appichetto.controls.ReceiptController;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.views.ReceiptView;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionListener;

import org.assertj.swing.edt.GuiActionRunner;

import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ReceiptSwingView extends JFrame implements ReceiptView {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtPrice;
	private JTextField txtName;
	private JTextField txtQuantity;
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

	public void setUsersList(JList<User> usersList) {
		this.usersList = usersList;
	}

	private DefaultListModel<User> listUsersModel;
	private JButton btnNewButton;
	private JButton btnUpdate;

	public void setReceiptController(ReceiptController receiptController) {
		this.receiptController = receiptController;
		receiptController.getUsers().stream().forEach(listUsersModel::addElement);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ReceiptSwingView frame = new ReceiptSwingView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

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
	public void showDoneMsg(String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showSelectedItem(Item item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showCurrentUsers(List<User> users) {
		// TODO Auto-generated method stub
	}

	@Override
	public void itemAdded(Item item) {
		listItemModel.addElement(item);
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

	/**
	 * Create the frame.
	 */
	public ReceiptSwingView() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new FormLayout(
				new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
						FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"), FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
						FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC,
						FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
						FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC,
						FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
						FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"), FormSpecs.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"), },
				new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, }));

		JLabel lblName = new JLabel("Name");
		getContentPane().add(lblName, "24, 2");

		txtName = new JTextField();
		txtName.setName("nameBox");
		getContentPane().add(txtName, "24, 4, fill, default");
		txtName.setColumns(10);

		JLabel lblPrice = new JLabel("Price");
		getContentPane().add(lblPrice, "24, 6");

		txtPrice = new JTextField();
		txtPrice.setName("priceBox");
		getContentPane().add(txtPrice, "24, 8, fill, default");
		txtPrice.setColumns(10);

		lblQuantity = new JLabel("Quantity");
		getContentPane().add(lblQuantity, "24, 10");

		txtQuantity = new JTextField();
		txtQuantity.setName("quantityBox");
		getContentPane().add(txtQuantity, "24, 12, fill, default");
		txtQuantity.setColumns(10);

		usersScrollPane = new JScrollPane();
		listUsersModel = new DefaultListModel<>();
		usersList = new JList<>(listUsersModel);
		usersList.setName("usersList");
		usersList.setSelectionModel(new DefaultListSelectionModel() {
			private int i0 = -1;
			private int i1 = -1;

			public void setSelectionInterval(int index0, int index1) {
				if (i0 == index0 && i1 == index1) {
					if (getValueIsAdjusting()) {
						setValueIsAdjusting(false);
						setSelection(index0, index1);
					}
				} else {
					i0 = index0;
					i1 = index1;
					setValueIsAdjusting(false);
					setSelection(index0, index1);
				}
			}

			private void setSelection(int index0, int index1) {
				if (super.isSelectedIndex(index0)) {
					super.removeSelectionInterval(index0, index1);
				} else {
					super.addSelectionInterval(index0, index1);
				}
			}
		});
		usersList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		usersList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				btnSave.setEnabled(!txtName.getText().trim().isEmpty() && !txtPrice.getText().trim().isEmpty()
						&& !txtQuantity.getText().trim().isEmpty() && !usersList.isSelectionEmpty());
			}
		});
		usersScrollPane.setViewportView(usersList);
		getContentPane().add(usersScrollPane, "24, 14, fill, fill");
		itemsScrollPane = new JScrollPane();

		listItemModel = new DefaultListModel<>();
		itemsList = new JList<>(listItemModel);
		itemsList.setName("itemsList");
		itemsScrollPane.setViewportView(itemsList);
		itemsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				Item item = itemsList.getSelectedValue();
				txtName.setText(item.getName());
				txtPrice.setText(item.getPrice().toString());
				txtQuantity.setText(item.getQuantity().toString());
				int[] indeces = item.getUsers().stream().mapToInt(user -> listUsersModel.indexOf(user)).toArray();
				usersList.setSelectedIndices(indeces);
				btnSave.setEnabled(itemsList.getSelectedIndex() != -1);
				btnUpdate.setEnabled(itemsList.getSelectedIndex() != -1);
				btnDelete.setEnabled(itemsList.getSelectedIndex() != -1);
			}
		});
		getContentPane().add(itemsScrollPane, "3, 8, 13, 7, fill, fill");

		errorMsgLabel = new JLabel("");
		errorMsgLabel.setName("errorMsgLabel");
		errorMsgLabel.setForeground(Color.RED);
		getContentPane().add(errorMsgLabel, "8, 15, 7, 1");

		btnSave = new JButton("Save");
		btnSave.addActionListener(e -> receiptController.addItem(txtName.getText(), txtPrice.getText(),
				txtQuantity.getText(), usersList.getSelectedValuesList()));
		btnSave.setEnabled(false);
		getContentPane().add(btnSave, "24, 17");

		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(e -> receiptController.deleteItem(itemsList.getSelectedValue()));

		btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(
				e -> receiptController.updateItem(txtName.getText(), txtPrice.getText(), txtQuantity.getText(),
						usersList.getSelectedValuesList(), listUsersModel.indexOf(itemsList.getSelectedValue())));
		btnUpdate.setEnabled(false);
		getContentPane().add(btnUpdate, "24, 19");

		btnDelete.setEnabled(false);
		getContentPane().add(btnDelete, "24, 21");

		KeyAdapter btnSaveEnabled = new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent arg0) {
				btnSave.setEnabled(!txtName.getText().trim().isEmpty() && !txtPrice.getText().trim().isEmpty()
						&& !txtQuantity.getText().trim().isEmpty() && !usersList.isSelectionEmpty());
			}
		};
		txtName.addKeyListener(btnSaveEnabled);
		txtPrice.addKeyListener(btnSaveEnabled);
		txtQuantity.addKeyListener(btnSaveEnabled);

		this.pack();

//		User user = new User("pippo", "psw");
//		User user1 = new User("pluto", "psw");
//		GuiActionRunner.execute(() -> this.getListUsersModel().addElement(user));
//		GuiActionRunner.execute(() -> this.getListUsersModel().addElement(user1));
//		usersList.setSelectedIndices(new int[] {0,1});
	}
}
