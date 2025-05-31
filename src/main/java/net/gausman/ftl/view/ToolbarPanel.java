package net.gausman.ftl.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ToolbarPanel extends JPanel {


    public static final String TRACKING_TOGGLE_BUTTON_OFF = "Off";
    public static final String TRACKING_TOGGLE_BUTTON_ON = "On";

    private final JButton trackingToggleButton = new JButton(TRACKING_TOGGLE_BUTTON_OFF);
    private final JButton testButton = new JButton("Test");
    private final JButton eventBrowserButton = new JButton("Event Browser");
    private final JButton eventTreeBrowserButton = new JButton("Event Tree Browser");

    public ToolbarPanel() {
        trackingToggleButton.setBackground(Color.RED);
        add(trackingToggleButton);
        add(testButton);
        add(eventBrowserButton);
        add(eventTreeBrowserButton);
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

    public void setTrackingToggleState(boolean isOn){
        trackingToggleButton.setText(isOn ? TRACKING_TOGGLE_BUTTON_ON : TRACKING_TOGGLE_BUTTON_OFF);
        trackingToggleButton.setBackground(isOn ? Color.GREEN : Color.RED);
    }
}
