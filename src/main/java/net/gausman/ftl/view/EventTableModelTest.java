package net.gausman.ftl.view;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class EventTableModelTest extends AbstractTableModel {
    private final List<EventListItem> events;
    private final String[] columnNames = {"Time", "Sec", "BId", "Jump", "Expl", "Category", "Type", "Amt", "Cost", "Id", "Text"};

    public EventTableModelTest(List<EventListItem> events) {
        this.events = events;
    }

    @Override
    public int getRowCount() {
        return events.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        EventListItem event = events.get(rowIndex);
        return switch (columnIndex){
            case 0 -> event.getTime();
            case 1 -> event.getSectorNumber();
            case 2 -> event.getCurrentBeaconId();
            case 3 -> event.getJumpNumber();
            case 4 -> event.getTotalBeaconsExplored();
            case 5 -> event.getItemType();
            case 6 -> event.getType();
            case 7 -> event.getAmount();
            case 8 -> event.getCost();
            case 9 -> event.getId();
            case 10 -> event.getText();
            case 11 -> event.getEventNumber();

            default -> null;
        };
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

}
