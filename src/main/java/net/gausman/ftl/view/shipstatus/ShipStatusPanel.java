package net.gausman.ftl.view.shipstatus;

import net.blerf.ftl.parser.SavedGameParser.SystemType;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.util.GausmanUtil;
import net.gausman.ftl.view.SystemListItem;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ShipStatusPanel extends JPanel {
    private final Map<SystemType, ShipStatusPanelRow> subSystemPanelMap = new EnumMap<>(SystemType.class);
    private final Map<SystemType, ShipStatusPanelRow> systemPanelMap = new EnumMap<>(SystemType.class);

    public ShipStatusPanel() {

        for (SystemType type : SystemType.values()){
            ShipStatusPanelRow row = new ShipStatusPanelRow(GausmanUtil.getTextSystemId(type), "0");
            if (type.isSubsystem()){
                subSystemPanelMap.put(type, row);
            } else {
                systemPanelMap.put(type, row);
            }
            // todo connect to model
        }

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.Y_AXIS));

        JPanel firstflow = new JPanel();
        firstflow.setLayout(new FlowLayout());
        firstflow.add(firstCol);
        add(firstflow);
        add(firstCol);


        JPanel secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.Y_AXIS));
        add(secondCol);

        JPanel thirdCol = new JPanel();
        thirdCol.setLayout(new BoxLayout(thirdCol, BoxLayout.Y_AXIS));
        JPanel thirdFlow = new JPanel(new FlowLayout());
        add(thirdCol);

        JPanel fourthCol = new JPanel();
        fourthCol.setLayout(new BoxLayout(fourthCol, BoxLayout.Y_AXIS));
        add(fourthCol);


        // Run Info
        java.util.List<SystemListItem> runInfo = new ArrayList<>();
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
        java.util.List<SystemListItem> resourceList = new ArrayList<>();
        resourceList.add(new SystemListItem("HULL", 30));
        resourceList.add(new SystemListItem("FUEL", 19));
        resourceList.add(new SystemListItem("MISSILE", 45));
        resourceList.add(new SystemListItem("DRONE_PART", 17));
        resourceList.add(new SystemListItem("REACTOR", 24));
        firstCol.add(createSystemListPanel(resourceList, "Resources"));


        secondCol.add(createPanelBox("Subsystems", subSystemPanelMap));
        secondCol.add(createPanelBox("Systems", systemPanelMap));

        // WEAPONS
        java.util.List<SystemListItem> weaponList = new ArrayList<>();
        weaponList.add(new SystemListItem("Bust Laser II", -1));
        weaponList.add(new SystemListItem("Bust Laser II", -1));
        weaponList.add(new SystemListItem("Bust Laser II", -1));
        weaponList.add(new SystemListItem("Flak 1", -1));
        weaponList.add(new SystemListItem("Fire Bomb", -1));
        thirdCol.add(createSystemListPanel(weaponList, "Weapons"));

        // DRONES
        java.util.List<SystemListItem> droneList = new ArrayList<>();
        droneList.add(new SystemListItem("Combat Drone Mark 1", 1));
        droneList.add(new SystemListItem("Combat Drone Mark 1", 1));
        droneList.add(new SystemListItem("Defense Drone Mark 1", 1));
        thirdCol.add(createSystemListPanel(droneList, "Drones"));

        // AUGMENTS
        java.util.List<SystemListItem> augmentList = new ArrayList<>();
        augmentList.add(new SystemListItem("Weapon Pre-igniter", 1));
        augmentList.add(new SystemListItem("Long range Scanners", 1));
        augmentList.add(new SystemListItem("Zoltan Shield Bypass", 1));
        thirdCol.add(createSystemListPanel(augmentList, "Augments"));

        // CREW
        java.util.List<SystemListItem> crewList = new ArrayList<>();
        crewList.add(new SystemListItem("Human", 1));
        crewList.add(new SystemListItem("Human", 1));
        crewList.add(new SystemListItem("Human", 1));
        crewList.add(new SystemListItem("Engi", 1));
        crewList.add(new SystemListItem("Engi", 1));
        crewList.add(new SystemListItem("Engi", 1));
        crewList.add(new SystemListItem("Engi", 1));
        crewList.add(new SystemListItem("Rock", 1));
        fourthCol.add(createSystemListPanel(crewList, "Crew"));

    }

    public void update(ShipStatusModel model){
        for (Map.Entry<SystemType, Integer> entry : model.getSystems().entrySet()){
            systemPanelMap.get(entry.getKey()).setTextLabel(entry.getValue().toString());
        }
        for (Map.Entry<SystemType, Integer> entry : model.getSubSystems().entrySet()){
            subSystemPanelMap.get(entry.getKey()).setTextLabel(entry.getValue().toString());
        }
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
