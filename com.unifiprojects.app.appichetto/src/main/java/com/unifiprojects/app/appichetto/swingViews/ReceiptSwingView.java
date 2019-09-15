package com.unifiprojects.app.appichetto.swingViews;

import java.awt.EventQueue;

import javax.swing.JFrame;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.unifiprojects.app.appichetto.models.Item;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class ReceiptSwingView extends JFrame {
	private JTextField txtPrice;
	private JTextField txtName;
	private JTextField txtQuantity;
	private JLabel lblQuantity;
	private JScrollPane scrollPane;
	private JButton btnSave;
	private JButton btnDelete;
	private JList<Item> itemsList;
	private DefaultListModel<Item> listItemModel;

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
						FormSpecs.DEFAULT_ROWSPEC, }));

		JLabel lblName = new JLabel("Name");
		getContentPane().add(lblName, "24, 2");

		txtName = new JTextField();
		txtName.setName("nameBox");
		getContentPane().add(txtName, "24, 4, fill, default");
		txtName.setColumns(10);

		JLabel lblPrice = new JLabel("Price");
		getContentPane().add(lblPrice, "24, 6");

		listItemModel = new DefaultListModel<>();
		itemsList = new JList<>(listItemModel);
		itemsList.setName("itemsList");
		itemsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				btnSave.setEnabled(itemsList.getSelectedIndex() != -1);
			}
		});
		getContentPane().add(itemsList, "3, 8, 13, 7, fill, fill");

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

		KeyAdapter btnSaveEnabled = new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent arg0) {
				btnSave.setEnabled(!txtName.getText().trim().isEmpty() && !txtPrice.getText().trim().isEmpty()
						&& !txtQuantity.getText().trim().isEmpty());
			}
		};
		txtName.addKeyListener(btnSaveEnabled);
		txtPrice.addKeyListener(btnSaveEnabled);
		txtQuantity.addKeyListener(btnSaveEnabled);

		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "24, 14, fill, fill");

		btnSave = new JButton("Save");
		btnSave.setEnabled(false);
		getContentPane().add(btnSave, "24, 15");

		btnDelete = new JButton("Delete");
		btnDelete.setEnabled(false);
		getContentPane().add(btnDelete, "24, 16");
	}

}
