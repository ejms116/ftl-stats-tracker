package net.gausman.ftl.view.shipstatus;

import net.gausman.ftl.model.Item;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ItemTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Item", "Type", "Origin", "State"};
    private List<Item> items = new ArrayList<>();

    public ItemTableModel(){

    }

    public void setItems(List<Item> items) {
        this.items = items;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Item item = items.get(rowIndex);

        return switch (columnIndex){
            case 0 -> item.getText();
            case 1 -> item.getItemType().toString();
            case 2 -> item.getOrigin().toString();
            case 3 -> item.getState().toString();
            default -> null;
        };
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}
