package net.gausman.ftl.model.table;

import net.gausman.ftl.model.SimpleTableItem;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class JumpTableModel extends AbstractTableModel {
    private List<SimpleTableItem> list = new ArrayList<>();
    private final String[] columnNames = {"Event Text", "Choice"};

    public void setList(List<SimpleTableItem> list){
        this.list = list;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SimpleTableItem item = list.get(rowIndex);

        if (item == null){
            return null;
        }
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
