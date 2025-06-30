package net.gausman.ftl.model.table;

import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.util.GausmanUtil;

import javax.swing.table.AbstractTableModel;
import java.time.Duration;
import java.time.Instant;
import java.util.NavigableMap;
import java.util.TreeMap;

public class EventTableModel extends AbstractTableModel {
    private NavigableMap<Integer, Event> events = new TreeMap<>();
    private final String[] columnNames = {"Time", "Sec", "BId", "Jump", "Expl", "Type", "Category", "Object", "Amt", "Scrap",  "Value"};
    private Instant startTime;


    public void setEvents(NavigableMap<Integer, Event> newEvents) {
        this.events = newEvents.descendingMap();
        fireTableDataChanged();
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    @Override
    public int getRowCount() {
        return events.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public Event getRowEvent(int rowIndex){
        int displayIndex = events.size() - 1 - rowIndex;

        return events.get(displayIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        int displayIndex = events.size() - 1 - rowIndex;

        Event event = events.get(displayIndex);
        if (event == null){
            return null;
        }
        return switch (columnIndex){
            case 0 -> GausmanUtil.formatDuration(Duration.between(startTime, event.getTs()));
            case 1 -> event.getJump().getSector().getId();
            case 2 -> event.getJump().getCurrentBeaconId();
            case 3 -> event.getJump().getId();
            case 4 -> event.getJump().getTotalBeaconsExplored();
            case 5 -> event.getEventType();
            case 6 -> event.getItemType();
            case 7 -> event.getDisplayText();
            case 8 -> event.getAmount();
            case 9 -> event.getScrap();
            case 10 -> event.getScrapChange();

            default -> null;
        };
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

}
