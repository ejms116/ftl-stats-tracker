package net.gausman.ftl.model.table;

import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.util.GausmanUtil;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.NavigableMap;
import java.util.TreeMap;

public class EventTableModel extends AbstractTableModel {
    private NavigableMap<Integer, Event> events = new TreeMap<>();
    private final String[] columnNames = {"Time", "S", "J", "Type", "Resources", "Tags", "Text"};
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

    public Event getEventById(int eventId){
        return events.get(eventId);
    }


    public Event getRowEvent(int rowIndex){
        int displayIndex = events.size() - 1 - rowIndex;

        Event e1 = events.get(rowIndex);
        Event e2 = events.get(displayIndex);
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
            case 2 -> event.getJump().getId();
            case 3 -> event.getEventFilterType();
            case 4 -> event.getResourceEffects();
            case 5 -> event.getTags();
            case 6 -> event.getDisplayText();

            default -> null;
        };
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    private int tryGetAmount(Object obj) {
        if (obj == null) return 0;

        try {
            Method method = obj.getClass().getMethod("getAmount");
            Object result = method.invoke(obj);

            if (result instanceof Number) {
                return ((Number) result).intValue();
            }
        } catch (NoSuchMethodException e) {
            // Method doesn't exist, return 0
        } catch (Exception e) {
            // Other exceptions like IllegalAccessException, InvocationTargetException
            e.printStackTrace();
        }

        return 0;
    }
}
