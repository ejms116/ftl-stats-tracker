package net.gausman.ftl.view.shipstatus;

import net.gausman.ftl.model.SimpleTableItem;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SimpleTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Name", "Value"};
    private List<SimpleTableItem> content = new ArrayList<>();

    public void setContent(List<SimpleTableItem> content) {
        this.content = content;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return content.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SimpleTableItem item = content.get(rowIndex);

        return switch (columnIndex){
            case 0 -> item.getKey();
            case 1 -> item.getValue();
            default -> null;
        };
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}
