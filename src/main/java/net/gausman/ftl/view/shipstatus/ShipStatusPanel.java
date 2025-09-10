package net.gausman.ftl.view.shipstatus;

import net.blerf.ftl.parser.SavedGameParser.SystemType;
import net.gausman.ftl.model.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShipStatusPanel extends JPanel {
    public static final int PREFERRED_WIDTH_1_DIGITS = 2;
    public static final int PREFERRED_WIDTH_3_DIGITS = 10;

    private SimpleTableModel runInfoTableModel;
    private SimpleTableModel resourcesTableModel;

    private ItemTableModel itemTableModel;
    private CrewTableModel crewTableModel;

    private SystemTableModel systemTableModel;


    public ShipStatusPanel() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setPreferredSize(new Dimension(900, 500));

        JPanel firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.Y_AXIS));
        firstCol.setPreferredSize(new Dimension(250, 500));
        add(firstCol);

        JPanel secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.Y_AXIS));
        secondCol.setPreferredSize(new Dimension(250, 500));
        add(secondCol);

        JPanel thirdCol = new JPanel();
        thirdCol.setLayout(new BoxLayout(thirdCol, BoxLayout.Y_AXIS));
        secondCol.setPreferredSize(new Dimension(350, 500));
        add(thirdCol);

        // Run Info
        runInfoTableModel = new SimpleTableModel();
        JTable runInfoTable = new JTable(runInfoTableModel);
        JScrollPane jScrollPaneRunInfo = new JScrollPane(runInfoTable);
        firstCol.add(jScrollPaneRunInfo);

        resourcesTableModel = new SimpleTableModel();
        JTable resourcesTable = new JTable(resourcesTableModel);
        JScrollPane jScrollPaneResources = new JScrollPane(resourcesTable);
        firstCol.add(jScrollPaneResources);

        // Systems
        systemTableModel = new SystemTableModel();
        JTable systemsTable = new JTable(systemTableModel);
        systemsTable.getColumnModel().getColumn(2).setPreferredWidth(PREFERRED_WIDTH_1_DIGITS);
        JScrollPane jScrollPane = new JScrollPane(systemsTable);
        secondCol.add(jScrollPane);


        // Items: Weapons/Drones/Augments
        itemTableModel = new ItemTableModel();
        JTable itemsTable = new JTable(itemTableModel);
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
        JScrollPane jScrollPaneItems = new JScrollPane(itemsTable);
        thirdCol.add(jScrollPaneItems);

        // Crew
        crewTableModel = new CrewTableModel();
        JTable crewTable = new JTable(crewTableModel);
//        crewTable.getColumnModel().getColumn(0).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
//        crewTable.getColumnModel().getColumn(1).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
//        crewTable.getColumnModel().getColumn(2).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
        crewTable.getColumnModel().getColumn(3).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
        crewTable.getColumnModel().getColumn(4).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
        JScrollPane jScrollPaneCrew = new JScrollPane(crewTable);
        thirdCol.add(jScrollPaneCrew);


        crewTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()){
                int selected = crewTable.getSelectedRow();
                if (selected != -1) {
                    Crew c = crewTableModel.getRowObject(selected);
                    System.out.println(c.toString());
                }
            }
        });

    }

    public void update(ShipStatusModel model){
        List<SimpleTableItem> runInfo = new ArrayList<>();

        for (Map.Entry<Constants.General, String> general : model.getGeneralInfoString().entrySet()){
            SimpleTableItem item = new SimpleTableItem(general.getKey().toString(), general.getValue());
            runInfo.add(item);
        }

        for (Map.Entry<Constants.General, Integer> general : model.getGeneralInfoInteger().entrySet()){
            SimpleTableItem item = new SimpleTableItem(general.getKey().toString(), general.getValue().toString());
            runInfo.add(item);
        }

        List<SimpleTableItem> resources = new ArrayList<>();
        for (Map.Entry<Constants.Resource, Integer> entry : model.getResources().entrySet()){
            SimpleTableItem item = new SimpleTableItem(entry.getKey().toString(), entry.getValue().toString());
            resources.add(item);
        }

        List<ShipSystem> shipSystems = new ArrayList<>();
        for (Map.Entry<Constants.Reactor, Integer> entry : model.getReactor().entrySet()){
            ShipSystem s = new ShipSystem(entry.getKey().toString(), "Reactor", "Other", false);
            s.setLevel(entry.getValue());
            shipSystems.add(s);
        }

        for (Map.Entry<SystemType, Integer> entry : model.getSystems().entrySet()){
            if (entry.getValue() < 1){
                continue;
            }
            ShipSystem s = new ShipSystem(entry.getKey().toString(), entry.getKey().getId(), entry.getKey().isSubsystem() ? "Subsystem" : "System", entry.getKey().isSubsystem());
            s.setLevel(entry.getValue());
            shipSystems.add(s);
        }

        runInfoTableModel.setContent(runInfo);
        resourcesTableModel.setContent(resources);
        systemTableModel.setSystems(shipSystems);
        itemTableModel.setItems(model.getItemList());
        List<Crew> fullCrewList = new ArrayList<>();
        fullCrewList.addAll(model.getCrewList());
        fullCrewList.addAll(model.getDeadCrewList());
        crewTableModel.setCrewList(fullCrewList);

    }

    private JPanel createPanelBoxTable(String title, JTable table){
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JScrollPane jScrollPane = new JScrollPane(table);


        // Add the list title as a border
        listPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                title,
                TitledBorder.LEADING,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12)
        ));

        listPanel.add(jScrollPane);


        // We wrap the panel in a panel with Flowlayout so that it shrinks...
        JPanel flowPanel = new JPanel();
        flowPanel.setLayout(new FlowLayout());
        flowPanel.add(listPanel);

        return flowPanel;
    }

    private JPanel createPanelBox(String title, Map<?, ShipStatusPanelRow> map){
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

        for (ShipStatusPanelRow row : map.values()){
            listPanel.add(row);
        }

        // We wrap the panel in a panel with Flowlayout so that it shrinks...
        JPanel flowPanel = new JPanel();
        flowPanel.setLayout(new FlowLayout());
        flowPanel.add(listPanel);

        return flowPanel;

    }

    private JPanel createSystemListPanel(List<SystemListItem> systems, String title) {
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

        if (systems.isEmpty()) {
            systems.add(new SystemListItem("none", 0));
        }

        for (SystemListItem systemListItem : systems) {
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new BorderLayout());
            rowPanel.setPreferredSize(new Dimension(140, 20));

            JLabel systemLabel = new JLabel(systemListItem.getName());
            systemLabel.setPreferredSize(new Dimension(120, 20));
            JLabel levelLabel = new JLabel();
            if (systemListItem.getLevel() > -1) {
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
