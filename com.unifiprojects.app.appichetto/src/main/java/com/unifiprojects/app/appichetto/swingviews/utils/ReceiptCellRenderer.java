package com.unifiprojects.app.appichetto.swingviews.utils;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Receipt;

public class ReceiptCellRenderer extends JLabel implements ListCellRenderer<Receipt> {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<? extends Receipt> list, Receipt value, int index,
			boolean isSelected, boolean cellHasFocus) {
		setText(ReceiptFormatter.format(value));
		return this;
	}
}
