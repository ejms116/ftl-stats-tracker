package net.gausman.ftl.view.toolbar;

import net.gausman.ftl.model.table.EventFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import java.util.Map;

public class ToolbarPanel extends JPanel {


    public static final String TRACKING_TOGGLE_BUTTON_OFF = "Off";
    public static final String TRACKING_TOGGLE_BUTTON_ON = "On";

    private final JButton trackingToggleButton = new JButton(TRACKING_TOGGLE_BUTTON_OFF);
    private final JButton testButton = new JButton("Test");
    private final JButton eventBrowserButton = new JButton("Event Browser");
    private final JButton eventTreeBrowserButton = new JButton("Event Tree Browser");
    private final JButton openEventInBrowserButton = new JButton("Open in Event Browser");

    private final JButton dropdownButton = new JButton("Options");
    private final JPopupMenu dropdownMenu = new JPopupMenu();

    Map<EventFilter,JCheckBoxMenuItem> filterJCheckBoxMap = new EnumMap<>(EventFilter.class);

    public ToolbarPanel() {
        trackingToggleButton.setBackground(Color.RED);
        add(trackingToggleButton);
        add(testButton);
        add(eventBrowserButton);
        add(eventTreeBrowserButton);
        add(openEventInBrowserButton);
        add(dropdownButton);

        // Example checkboxes
        for (EventFilter eventFilter : EventFilter.values()){
            JCheckBoxMenuItem cb = new JCheckBoxMenuItem(eventFilter.getDisplayName(), false);
            filterJCheckBoxMap.put(eventFilter, cb);
            dropdownMenu.add(cb);
        }

        // You can also add non-checkbox items:
        JMenuItem normalItem = new JMenuItem("Test");
        dropdownMenu.addSeparator(); // adds a horizontal line
        dropdownMenu.add(normalItem);

        // Show popup when button is clicked
        dropdownButton.addActionListener(e -> dropdownMenu.show(dropdownButton, 0, dropdownButton.getHeight()));

    }

    public Map<EventFilter, JCheckBoxMenuItem> getFilterJCheckBoxMap() {
        return filterJCheckBoxMap;
    }

    public void setEventBrowserButtonListener(ActionListener listener){
        eventBrowserButton.addActionListener(listener);
    }

    public void setEventTreeBrowserButtonListener(ActionListener listener){
        eventTreeBrowserButton.addActionListener(listener);
    }

    public void setTestButtonListener(ActionListener listener){
        testButton.addActionListener(listener);
    }

    public void setTrackingToggleListener(ActionListener listener){
        trackingToggleButton.addActionListener(listener);
    }

    public void setOpenEventInBrowserButton(ActionListener listener){
        openEventInBrowserButton.addActionListener(listener);
    }

    public void setTrackingToggleState(boolean isOn){
        trackingToggleButton.setText(isOn ? TRACKING_TOGGLE_BUTTON_ON : TRACKING_TOGGLE_BUTTON_OFF);
        trackingToggleButton.setBackground(isOn ? Color.GREEN : Color.RED);
    }
}
