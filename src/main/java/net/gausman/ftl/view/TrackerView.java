package net.gausman.ftl.view;

import net.gausman.ftl.view.charts.ChartsPanel;
import net.gausman.ftl.view.shipstatus.ShipStatusPanel;
import net.gausman.ftl.view.eventtable.EventTablePanel;
import net.gausman.ftl.view.toolbar.ToolbarPanel;

import javax.swing.*;
import java.awt.*;

public class TrackerView extends JFrame {
    private final ToolbarPanel toolbarPanel;
    private EventTablePanel eventTablePanel;
    private final ShipStatusPanel shipStatusPanel;
    private final ChartsPanel chartsPanel;

    private final JSplitPane main;

    public TrackerView() {
        setTitle("FTL Stats Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1800, 1000);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);


        shipStatusPanel = new ShipStatusPanel();
        chartsPanel = new ChartsPanel();

        JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, shipStatusPanel, chartsPanel);
        leftPanel.setResizeWeight(0.5);
        leftPanel.setDividerLocation(0.5);

        toolbarPanel = new ToolbarPanel();

        main.setLeftComponent(leftPanel);
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

    public ChartsPanel getChartsPanel() {
        return chartsPanel;
    }
}
