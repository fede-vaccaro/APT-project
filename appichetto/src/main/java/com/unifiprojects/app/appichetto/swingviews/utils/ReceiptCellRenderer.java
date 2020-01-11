package com.unifiprojects.app.appichetto.swingviews.utils;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.unifiprojects.app.appichetto.models.Receipt;

public class ReceiptCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {

		DefaultListCellRenderer c = (DefaultListCellRenderer) super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);
		c.setText(ReceiptFormatter.format((Receipt) value));
		return c;

	}
}
