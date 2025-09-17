package net.gausman.ftl.view.shipstatus;

import net.blerf.ftl.parser.SavedGameParser.SystemType;
import net.gausman.ftl.model.*;
import net.gausman.ftl.view.charts.HullChartPanel;
import net.gausman.ftl.view.charts.ScrapUsedChartPanel;
import net.gausman.ftl.view.charts.ScrapUsedPieChartPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShipStatusPanel extends JSplitPane {
    public static final int PREFERRED_WIDTH_1_DIGITS = 2;
    public static final int PREFERRED_WIDTH_3_DIGITS = 10;

    private SimpleTableModel runInfoTableModel;
    private SimpleTableModel resourcesTableModel;

    private ItemTableModel itemTableModel;
    private CrewTableModel crewTableModel;

    private SectorTableModel sectorTableModel;
    private JTable sectorTable;
    private JScrollPane jScrollPaneSector;

    private SystemTableModel systemTableModel;

    private HullChartPanel hullChartPanel;
    private ScrapUsedChartPanel scrapUsedChartPanel;

    private final JSplitPane statusPanel;
    private final JSplitPane sectorPanel;

    public ShipStatusPanel() {
        super(JSplitPane.VERTICAL_SPLIT);
        statusPanel = new JSplitPane(HORIZONTAL_SPLIT);
        sectorPanel = new JSplitPane(HORIZONTAL_SPLIT);
        setPreferredSize(new Dimension(900, 500));

        JPanel firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.Y_AXIS));
        firstCol.setPreferredSize(new Dimension(250, 500));

        JPanel secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.Y_AXIS));

        statusPanel.setLeftComponent(firstCol);
        statusPanel.setRightComponent(secondCol);

        sectorTableModel = new SectorTableModel();
        sectorTable = new JTable(sectorTableModel);
        jScrollPaneSector = new JScrollPane(sectorTable);
        sectorPanel.setLeftComponent(jScrollPaneSector);
        sectorPanel.setDividerLocation(200);

        setTopComponent(statusPanel);
        setBottomComponent(sectorPanel);
        setResizeWeight(0.3);

        // Sector Charts

//        hullChartPanel = new HullChartPanel();
        scrapUsedChartPanel = new ScrapUsedChartPanel();
        JTabbedPane tabbedPaneSector = new JTabbedPane();
//        tabbedPaneSector.addTab("Hull", hullChartPanel);
//        tabbedPaneSector.addTab("Resources", new ScrapUsedPieChartPanel());
        tabbedPaneSector.addTab("Scrap usage", scrapUsedChartPanel);

        sectorPanel.setRightComponent(tabbedPaneSector);


        // Run Info
        runInfoTableModel = new SimpleTableModel();
        JTable runInfoTable = new JTable(runInfoTableModel);
        JScrollPane jScrollPaneRunInfo = new JScrollPane(runInfoTable);

        resourcesTableModel = new SimpleTableModel();
        JTable resourcesTable = new JTable(resourcesTableModel);
        JScrollPane jScrollPaneResources = new JScrollPane(resourcesTable);

        JTabbedPane tabbedPaneFirstCol = new JTabbedPane();
        tabbedPaneFirstCol.addTab("Stats", jScrollPaneRunInfo);
        tabbedPaneFirstCol.addTab("Resources", jScrollPaneResources);
        firstCol.add(tabbedPaneFirstCol);


        // Items: Weapons/Drones/Augments
        itemTableModel = new ItemTableModel();
        JTable itemsTable = new JTable(itemTableModel);
        JScrollPane jScrollPaneItems = new JScrollPane(itemsTable);

        // Crew
        crewTableModel = new CrewTableModel();
        JTable crewTable = new JTable(crewTableModel);
        JScrollPane jScrollPaneCrew = new JScrollPane(crewTable);

        // Systems
        systemTableModel = new SystemTableModel();
        JTable systemsTable = new JTable(systemTableModel);
        systemsTable.getColumnModel().getColumn(1).setPreferredWidth(PREFERRED_WIDTH_1_DIGITS);
        JScrollPane jScrollPaneSystems = new JScrollPane(systemsTable);

        JTabbedPane tabbedPaneSecondCol = new JTabbedPane();
        tabbedPaneSecondCol.addTab("Items", jScrollPaneItems);
        tabbedPaneSecondCol.addTab("Crew", jScrollPaneCrew);
        tabbedPaneSecondCol.addTab("Systems", jScrollPaneSystems);
        secondCol.add(tabbedPaneSecondCol);



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
        sectorTableModel.setSectorMetrics(model.getSectorMetrics());

        scrapUsedChartPanel.updateDataset(model.getSectorMetrics());
//        hullChartPanel.updateDataset(model.getSectorMetrics());

//        resizeColumnWidths(sectorTable, jScrollPaneSector);
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

    public void resizeColumnWidths(JTable table, JScrollPane scrollPane) {
        final TableColumnModel columnModel = table.getColumnModel();
        int totalWidth = 0;

        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // minimum width

            // calculate max width for column
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(width, comp.getPreferredSize().width + 1);
            }

            // consider header
            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(
                    table, table.getColumnName(column), false, false, 0, column);
            width = Math.max(width, headerComp.getPreferredSize().width + 1);

            columnModel.getColumn(column).setPreferredWidth(width);
            totalWidth += width;
        }

        // set table properties
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // now set scroll pane preferred & minimum sizes based on total column width + intercell spacing + insets
        int spacing = (table.getColumnCount() - 1) * table.getIntercellSpacing().width;
        int insets = table.getInsets().left + table.getInsets().right;
        int scrollWidth = totalWidth + spacing + insets;

        Dimension preferred = new Dimension(scrollWidth, scrollPane.getPreferredSize().height);
        scrollPane.setPreferredSize(preferred);
        scrollPane.setMinimumSize(preferred);
    }




}
