package net.gausman.ftl.view.toolbar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ToolbarPanel extends JPanel {
    public static final String TRACKING_TOGGLE_BUTTON_OFF = "Off";
    public static final String TRACKING_TOGGLE_BUTTON_ON = "On";

    private final JButton trackingToggleButton = new JButton(TRACKING_TOGGLE_BUTTON_OFF);
    private final JButton testButton = new JButton("Test");
    private final JButton eventTreeBrowserButton = new JButton("Event Browser");
    private final JButton ftlDojoButton = new JButton("FTL Dojo");

    public ToolbarPanel() {
        trackingToggleButton.setBackground(Color.RED);
        add(trackingToggleButton);
        if (Boolean.getBoolean("ftl.testing")){
            add(testButton);
        }
        add(eventTreeBrowserButton);
        add(ftlDojoButton);
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

    public void setFtlDojoButton(ActionListener listener){
        ftlDojoButton.addActionListener(listener);
    }

    public void setTrackingToggleState(boolean isOn){
        trackingToggleButton.setText(isOn ? TRACKING_TOGGLE_BUTTON_ON : TRACKING_TOGGLE_BUTTON_OFF);
        trackingToggleButton.setBackground(isOn ? Color.GREEN : Color.RED);
    }
}
