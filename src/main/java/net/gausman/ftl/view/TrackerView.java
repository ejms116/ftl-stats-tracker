package net.gausman.ftl.view;

import net.gausman.ftl.view.shipstatus.CrewTableModel;
import net.gausman.ftl.view.shipstatus.ItemTableModel;
import net.gausman.ftl.view.shipstatus.ShipStatusPanel;
import net.gausman.ftl.view.table.EventTablePanel;

import javax.swing.*;
import java.awt.*;

public class TrackerView extends JFrame {
    private ToolbarPanel toolbarPanel;
    private JPanel leftPanel;
    private JPanel eventListPanel;
    private EventFilterPanel eventFilterPanel;
    private ShipStatusPanel shipStatusPanel;

    public TrackerView() {
        setTitle("FTL Stats Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1800, 1000);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        shipStatusPanel = new ShipStatusPanel();

        leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(shipStatusPanel, BorderLayout.NORTH);
        leftPanel.add(new ChartsPanel(), BorderLayout.CENTER);

        toolbarPanel = new ToolbarPanel();
        eventListPanel = new JPanel(new BorderLayout());
        eventListPanel.setPreferredSize(new Dimension(900, 1000));

        eventFilterPanel = new EventFilterPanel();
//        eventFilterPanel.setLayout(new BorderLayout());
        eventFilterPanel.setLayout(new BoxLayout(eventFilterPanel, BoxLayout.Y_AXIS));

        add(toolbarPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(eventListPanel, BorderLayout.CENTER);
        add(eventFilterPanel, BorderLayout.SOUTH);

        // Optional: menus, toolbars, status bar, etc.
    }

    public void setEventTablePanel(EventTablePanel eventTablePanel){
        eventListPanel.add(eventTablePanel);
    }

    public ToolbarPanel getToolbarPanel(){
        return toolbarPanel;
    }

    public EventFilterPanel getEventFilterPanel(){
        return eventFilterPanel;
    }

    public ShipStatusPanel getShipStatusPanel() {
        return shipStatusPanel;
    }
}
