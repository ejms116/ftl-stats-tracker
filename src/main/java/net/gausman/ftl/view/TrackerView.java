package net.gausman.ftl.view;

import net.gausman.ftl.view.eventtable.EventTablePanel;
import net.gausman.ftl.view.shipstatus.ShipStatusPanel;
import net.gausman.ftl.view.toolbar.ToolbarPanel;

import javax.swing.*;
import java.awt.*;

public class TrackerView extends JFrame {
    private final ToolbarPanel toolbarPanel;
    private EventTablePanel eventTablePanel;
    private final ShipStatusPanel shipStatusPanel;

    private final JSplitPane main;

    public TrackerView() {
        setTitle("FTL Stats Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1800, 1000);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);


        shipStatusPanel = new ShipStatusPanel();

        toolbarPanel = new ToolbarPanel();

        main.setLeftComponent(shipStatusPanel);
        main.setResizeWeight(0.5);

        add(toolbarPanel, BorderLayout.NORTH);
        add(main, BorderLayout.CENTER);

    }

    public void setEventTablePanel(EventTablePanel eventTablePanel){
        this.eventTablePanel = eventTablePanel;
        this.eventTablePanel.setPreferredSize(new Dimension(900, 1000));
        main.setRightComponent(eventTablePanel);
    }

    public EventTablePanel getEventTablePanel() {
        return eventTablePanel;
    }

    public ToolbarPanel getToolbarPanel(){
        return toolbarPanel;
    }


    public ShipStatusPanel getShipStatusPanel() {
        return shipStatusPanel;
    }

}
