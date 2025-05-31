package net.gausman.ftl.view.browser;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class EventNodeTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Id", "Type"};
    private List<EventBrowserListItem> items = new ArrayList<>();

    public void setItems(List<EventBrowserListItem> items) {
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

    public EventBrowserListItem getRowItem(int rowIndex){
        return items.get(rowIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        EventBrowserListItem item = items.get(rowIndex);

        return switch (columnIndex){
            case 0 -> item.getId();
            case 1 -> item.getType();
            default -> null;
        };
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

}
