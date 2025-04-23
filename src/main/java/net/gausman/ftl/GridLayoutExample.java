package net.gausman.ftl;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import net.gausman.ftl.view.EventListItem;
import net.gausman.ftl.view.EventTableModelTest;
import net.gausman.ftl.view.SystemListItem;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class GridLayoutExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLaf.setup(new FlatLightLaf());
            JFrame frame = new JFrame("GridLayout with Table, Toolbar, and Bar Chart");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1800, 1000);

            // Set up BorderLayout
            frame.setLayout(new BorderLayout());

            // Create a toolbar with a few buttons
            JToolBar toolbar = new JToolBar();
            JButton button1 = new JButton("Button 1");
            JButton button2 = new JButton("Button 2");
            JButton button3 = new JButton("Button 3");

            // Add buttons to the toolbar
            toolbar.add(button1);
            toolbar.add(button2);
            toolbar.add(button3);

            // Create left panel with BoxLayout to arrange lists vertically
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            //leftPanel.setMinimumSize(new Dimension(800, 400));
            //leftPanel.setPreferredSize(new Dimension(800, 400)); // Set a preferred height

            JPanel topLeftPanel = new JPanel();
            topLeftPanel.setLayout(new BoxLayout(topLeftPanel, BoxLayout.X_AXIS));

            // Panel to hold multiple lists with different amounts of items
            JPanel systemListsPanel = new JPanel();
            systemListsPanel.setLayout(new BoxLayout(systemListsPanel, BoxLayout.X_AXIS));
            systemListsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            topLeftPanel.add(systemListsPanel);

            JPanel firstCol = new JPanel();
            firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.Y_AXIS));
//            JPanel firstflow = new JPanel();
//            firstflow.setLayout(new FlowLayout());
//            firstflow.add(firstCol);
//            systemListsPanel.add(firstflow);
            systemListsPanel.add(firstCol);

            JPanel secondCol = new JPanel();
            secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.Y_AXIS));
            systemListsPanel.add(secondCol);

            JPanel thirdCol = new JPanel();
            thirdCol.setLayout(new BoxLayout(thirdCol, BoxLayout.Y_AXIS));
            JPanel thirdFlow = new JPanel(new FlowLayout());
//            thirdFlow.add(thirdCol);
//            systemListsPanel.add(thirdFlow);
            systemListsPanel.add(thirdCol);

            JPanel fourthCol = new JPanel();
            fourthCol.setLayout(new BoxLayout(fourthCol, BoxLayout.Y_AXIS));
            systemListsPanel.add(fourthCol);

            //systemListsPanel.setLayout(new GridLayout(0,4,0,0));
//            systemListsPanel.setPreferredSize(new Dimension(1000, 600));

            // Run Info
            List<SystemListItem> runInfo = new ArrayList<>();
            runInfo.add(new SystemListItem("Ship", 4));
            runInfo.add(new SystemListItem("Difficulty", 8));
            runInfo.add(new SystemListItem("Sector", 8));
            runInfo.add(new SystemListItem("Jump", 91));
            runInfo.add(new SystemListItem("BeaconId", 4));
            runInfo.add(new SystemListItem("Beacons explored", 88));
            runInfo.add(new SystemListItem("Ships destroyed", 44));
            runInfo.add(new SystemListItem("Scrap collected", 1578));
            runInfo.add(new SystemListItem("Sector seed", 998405077));
            firstCol.add(createSystemListPanel(runInfo, "Run Info"));

            // RESOURCES+HULL+REACTOR?
            List<SystemListItem> resourceList = new ArrayList<>();
            resourceList.add(new SystemListItem("HULL", 30));
            resourceList.add(new SystemListItem("FUEL", 19));
            resourceList.add(new SystemListItem("MISSILE", 45));
            resourceList.add(new SystemListItem("DRONE_PART", 17));
            resourceList.add(new SystemListItem("REACTOR", 24));
            firstCol.add(createSystemListPanel(resourceList, "Resources"));

            // SUBSYSTEMS
            List<SystemListItem> subSystemList = new ArrayList<>();
            subSystemList.add(new SystemListItem("Piloting", 0));
            subSystemList.add(new SystemListItem("Sensors", 0));
            subSystemList.add(new SystemListItem("Door System", 0));
            subSystemList.add(new SystemListItem("Backup Battery", 0));
            secondCol.add(createSystemListPanel(subSystemList, "Subsystems"));

            // SYSTEMS
            List<SystemListItem> systemList = new ArrayList<>();
            systemList.add(new SystemListItem("Shields", 0));
            systemList.add(new SystemListItem("Engines", 0));
            systemList.add(new SystemListItem("Medbay", 0));
            systemList.add(new SystemListItem("Clone Bay", 0));
            systemList.add(new SystemListItem("Cloaking", 0));
            systemList.add(new SystemListItem("Oxygen", 0));
            systemList.add(new SystemListItem("Weapon Control", 0));
            systemList.add(new SystemListItem("Drone Control", 0));
            systemList.add(new SystemListItem("Hacking", 0));
            systemList.add(new SystemListItem("Mind Control", 0));
            systemList.add(new SystemListItem("Crew Teleporter", 0));
            systemList.add(new SystemListItem("Artillery Beam", 0));
            secondCol.add((createSystemListPanel(systemList, "Systems")));

            // WEAPONS
            List<SystemListItem> weaponList = new ArrayList<>();
            weaponList.add(new SystemListItem("Bust Laser II", -1));
//            weaponList.add(new SystemListItem("Bust Laser II", -1));
//            weaponList.add(new SystemListItem("Bust Laser II", -1));
//            weaponList.add(new SystemListItem("Flak 1", -1));
//            weaponList.add(new SystemListItem("Fire Bomb", -1));
            thirdCol.add(createSystemListPanel(weaponList, "Weapons"));

            // DRONES
            List<SystemListItem> droneList = new ArrayList<>();
            droneList.add(new SystemListItem("Combat Drone Mark 1", 1));
            droneList.add(new SystemListItem("Combat Drone Mark 1", 1));
            droneList.add(new SystemListItem("Defense Drone Mark 1", 1));
            thirdCol.add(createSystemListPanel(droneList, "Drones"));

            // AUGMENTS
            List<SystemListItem> augmentList = new ArrayList<>();
            augmentList.add(new SystemListItem("Weapon Pre-igniter", 1));
            augmentList.add(new SystemListItem("Long range Scanners", 1));
            augmentList.add(new SystemListItem("Zoltan Shield Bypass", 1));
            thirdCol.add(createSystemListPanel(augmentList, "Augments"));

            // CREW
            List<SystemListItem> crewList = new ArrayList<>();
            crewList.add(new SystemListItem("Human", 1));
            crewList.add(new SystemListItem("Human", 1));
            crewList.add(new SystemListItem("Human", 1));
            crewList.add(new SystemListItem("Engi", 1));
            crewList.add(new SystemListItem("Engi", 1));
            crewList.add(new SystemListItem("Engi", 1));
            crewList.add(new SystemListItem("Engi", 1));
            crewList.add(new SystemListItem("Rock", 1));
            fourthCol.add(createSystemListPanel(crewList, "Crew"));

            // Add the systemListsPanel to the left panel (top part)
//            JPanel systemsFlowPanel = new JPanel();
//            systemsFlowPanel.setLayout(new FlowLayout());
//            systemsFlowPanel.add(systemListsPanel);
//
//            leftPanel.add(systemsFlowPanel);

            Dimension preferredSize = systemListsPanel.getPreferredSize();
//            systemListsPanel.setMaximumSize(new Dimension(preferredSize.width, 600));
            leftPanel.add(topLeftPanel);

            // Bottom part of the left panel will hold the bar chart
            JPanel bottomLeftPanel = new JPanel();
            bottomLeftPanel.setLayout(new BorderLayout());

            // Create pie chart
            // Create Panel
            JFreeChart pieChart = createPieChart();
            ChartPanel chartPanelPie = new ChartPanel(pieChart);
            chartPanelPie.setPreferredSize(new java.awt.Dimension(560, 370));
//            bottomLeftPanel.add(chartPanelPie);
            topLeftPanel.add(chartPanelPie);

            // Create a bar chart
            JFreeChart barChart = createBarChart();
            ChartPanel chartPanel = new ChartPanel(barChart);
            chartPanel.setPreferredSize(new Dimension(800, 400));  // Set chart size
            bottomLeftPanel.add(chartPanel, BorderLayout.CENTER);


            // Add the bar chart panel to the left panel
            leftPanel.add(bottomLeftPanel);

            // Create the right panel with a JTable
            JPanel rightPanel = new JPanel(new BorderLayout());
            List<EventListItem> events = new ArrayList<>();
            events.addAll(getTestEvents());

            JTable table = new JTable(new EventTableModelTest(events));
            table.getColumnModel().getColumn(10).setPreferredWidth(200);

            // Make the table editable (optional)
//            table.setCellSelectionEnabled(false);
//            table.setRowSelectionAllowed(true);
//            table.setColumnSelectionAllowed(false);
//            table.setCellSelectionEnabled(false);
//            TableRenderer tr = new TableRenderer();
//            table.setDefaultRenderer(TableRenderer.class, tr);

//            table.addMouseListener(new MouseAdapter() {
//                @Override
//                public void mouseClicked(MouseEvent e) {
//                    int selectedRow = table.getSelectedRow();
//                    if (selectedRow != -1) {
//                        EventListItem ev = events.get(selectedRow);
//                        JOptionPane.showMessageDialog(table,
//                                "Selected: " + ev.getText(),
//                                "Row Clicked",
//                                JOptionPane.INFORMATION_MESSAGE);
//                    }
//                }
//            });


            // Add the table to a scroll pane
            JScrollPane scrollPane = new JScrollPane(table);
            rightPanel.add(scrollPane, BorderLayout.CENTER);

            // Add the toolbar and panels to the frame
            frame.add(toolbar, BorderLayout.NORTH);
            frame.add(leftPanel, BorderLayout.WEST);
            frame.add(rightPanel, BorderLayout.CENTER);


            frame.setResizable(false);
            frame.setVisible(true);
        });
    }

    private static List<EventListItem> getTestEvents(){
        List<EventListItem> events = new ArrayList<>();
        EventListItem event1 = new EventListItem();

        event1.setCost(30);
        event1.setAmount(1);
        event1.setId("BUREST");
        event1.setText("This is a test");
        event1.setTime("0:00:00");

        events.add(event1);

        EventListItem event2 = new EventListItem();

        event2.setCost(30);
        event2.setAmount(1);
        event2.setId("FUEL");
        event2.setText("More fuel");
        event2.setTime("0:00:00");

        events.add(event2);



        return events;
    }

    private static JFreeChart createPieChart(){
        // Create dataset
        DefaultPieDataset dataset = createDataset();

        // Create chart
        JFreeChart chart = ChartFactory.createPieChart(
                "Market Share",  // chart title
                dataset,         // data
                true,            // include legend
                true,
                false);

        // Customize chart
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Chrome", new Color(200, 100, 100));
        plot.setSectionPaint("Firefox", new Color(100, 150, 200));
        plot.setSectionPaint("Edge", new Color(150, 200, 150));
        plot.setSectionPaint("Safari", new Color(200, 200, 100));
        plot.setExplodePercent("Safari", 0.10);

        return chart;
    }

    private static DefaultPieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Chrome", 63.59);
        dataset.setValue("Firefox", 4.18);
        dataset.setValue("Edge", 5.62);
        dataset.setValue("Safari", 19.23);
        return dataset;
    }

    // Method to create a simple bar chart
    private static JFreeChart createBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Add sample data to the dataset
        dataset.addValue(100, "Scrap", "Civilian");
        dataset.addValue(10, "Free Stuff", "Civilian");

        dataset.addValue(120, "Scrap", "Rock");
        dataset.addValue(10, "Free Stuff", "Rock");

        dataset.addValue(167, "Scrap", "Engi");
        dataset.addValue(10, "Free Stuff", "Engi");

        dataset.addValue(106, "Scrap", "Abandoned");
        dataset.addValue(10, "Free Stuff", "Abandoned");

        dataset.addValue(140, "Scrap", "Civilian2");
        dataset.addValue(10, "Free Stuff", "Civilian2");

        dataset.addValue(277, "Scrap", "Slug Home");
        dataset.addValue(10, "Free Stuff", "Slug Home");

        dataset.addValue(301, "Scrap", "Zoltan");
        dataset.addValue(10, "Free Stuff", "Zoltan");

        dataset.addValue(55, "Scrap", "Last Stand");
        dataset.addValue(10, "Free Stuff", "Last Stand");



        // Create the bar chart
        JFreeChart barChart = ChartFactory.createBarChart(
                "Sample Bar Chart",   // Chart title
                "Item",               // X-axis label
                "Value",              // Y-axis label
                dataset,              // Dataset
                PlotOrientation.VERTICAL,  // Plot orientation
                true,                 // Include legend
                true,                 // Tooltips
                false                 // URLs
        );

        return barChart;
    }


    private static JPanel createSystemListPanel(List<SystemListItem> systems, String title) {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));


        // Add the list title as a border
        listPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                title,
                TitledBorder.LEADING,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12)
        ));

        if (systems.isEmpty()){
            systems.add(new SystemListItem("none",0));
        }

        for (SystemListItem systemListItem: systems){
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new BorderLayout());
            rowPanel.setPreferredSize(new Dimension(140, 20));

            JLabel systemLabel = new JLabel(systemListItem.getName());
            systemLabel.setPreferredSize(new Dimension(120,20));
            JLabel levelLabel = new JLabel();
            if (systemListItem.getLevel() > -1){
                levelLabel.setText(String.valueOf(systemListItem.getLevel()));
            }
//            levelLabel.setPreferredSize(new Dimension(20,20));
            levelLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            rowPanel.add(systemLabel, BorderLayout.WEST);
            rowPanel.add(levelLabel, BorderLayout.EAST);
            listPanel.add(rowPanel);
        }

        // We wrap the panel in a panel with Flowlayout so that it shrinks...
        JPanel flowPanel = new JPanel();
        flowPanel.setLayout(new FlowLayout());
        flowPanel.add(listPanel);

        return flowPanel;
    }
}
