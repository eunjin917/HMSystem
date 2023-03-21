package helper;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class MyTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (value.equals("0")) { // 이용 불가 : RED
			cell.setBackground(Color.RED);
			cell.setForeground(Color.RED);
		} else if (value.equals("1")) { // 이용 가능 : BLUE
			cell.setBackground(new Color(135, 206, 250));
			cell.setForeground(new Color(135, 206, 250));
		} else { // 그 외 : WHITE
			cell.setBackground(Color.WHITE);
			cell.setForeground(Color.BLACK);
		}

		return this;
	}
}