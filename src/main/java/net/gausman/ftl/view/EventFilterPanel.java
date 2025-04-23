package net.gausman.ftl.view;

import net.gausman.ftl.model.table.EventFilter;

import javax.swing.*;
import java.util.EnumMap;
import java.util.Map;

public class EventFilterPanel extends JPanel {
    Map<EventFilter,JCheckBox> filterJCheckBoxMap = new EnumMap<>(EventFilter.class);

    public EventFilterPanel(){
        for (EventFilter eventFilter : EventFilter.values()){
            JCheckBox cb = new JCheckBox(eventFilter.getDisplayName(), true);
            filterJCheckBoxMap.put(eventFilter, cb);
            add(cb);
        }
    }

    public Map<EventFilter, JCheckBox> getFilterJCheckBoxMap() {
        return filterJCheckBoxMap;
    }

}
