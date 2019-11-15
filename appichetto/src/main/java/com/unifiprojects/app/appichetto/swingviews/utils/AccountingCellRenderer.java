package com.unifiprojects.app.appichetto.swingviews.utils;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.unifiprojects.app.appichetto.models.Accounting;

public class AccountingCellRenderer extends JLabel implements ListCellRenderer<Accounting> {

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

