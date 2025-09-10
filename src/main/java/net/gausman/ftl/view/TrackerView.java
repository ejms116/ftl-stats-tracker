package net.gausman.ftl.view;

import net.gausman.ftl.view.charts.ChartsPanel;
import net.gausman.ftl.view.shipstatus.ShipStatusPanel;
import net.gausman.ftl.view.table.EventTablePanel;

import javax.swing.*;
import java.awt.*;

public class TrackerView extends JFrame {
    private ToolbarPanel toolbarPanel;
    private JSplitPane leftPanel;
    private EventTablePanel eventTablePanel;
    private ShipStatusPanel shipStatusPanel;
//    private EventFilterPanel eventFilterPanel;

    // TODO add statusbar in the bottom

    public TrackerView() {
        setTitle("FTL Stats Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1800, 1000);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        shipStatusPanel = new ShipStatusPanel();

//        leftPanel = new JPanel(new BorderLayout());
//        leftPanel.add(shipStatusPanel, BorderLayout.NORTH);
//        leftPanel.add(new ChartsPanel(), BorderLayout.CENTER);

        leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, shipStatusPanel, new ChartsPanel());
        leftPanel.setResizeWeight(0.5);
        leftPanel.setDividerLocation(0.5);

//        leftPanel.setEnabled(false);
//        leftPanel.setDividerSize(5);

        toolbarPanel = new ToolbarPanel();


        add(toolbarPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
//        add(eventListPanel, BorderLayout.CENTER);
//        add(eventFilterPanel, BorderLayout.SOUTH);

        // Optional: menus, toolbars, status bar, etc.
    }

    public void setEventTablePanel(EventTablePanel eventTablePanel){
        this.eventTablePanel = eventTablePanel;
        this.eventTablePanel.setPreferredSize(new Dimension(900, 1000));
        add(eventTablePanel, BorderLayout.CENTER);
    }

    public EventTablePanel getEventTablePanel() {
        return eventTablePanel;
    }

    public ToolbarPanel getToolbarPanel(){
        return toolbarPanel;
    }

//    public EventFilterPanel getEventFilterPanel(){
//        return eventFilterPanel;
//    }


    public ShipStatusPanel getShipStatusPanel() {
        return shipStatusPanel;
    }
}
