package helper;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

public class CheckBoxCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 1L;
	protected JCheckBox checkBox;

	public CheckBoxCellEditor() {
		checkBox = new JCheckBox();
		checkBox.setHorizontalAlignment(SwingConstants.CENTER);
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (table.getValueAt(row, column).equals(true))
			table.setValueAt(false, row, column);
		else if (table.getValueAt(row, column).equals(false))
			table.setValueAt(true, row, column);

		return checkBox;
	}

	public Object getCellEditorValue() {
		return Boolean.valueOf(checkBox.isSelected());
	}
}