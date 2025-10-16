package net.gausman.ftl.view.shipstatus;

import net.blerf.ftl.parser.SavedGameParser.SystemType;
import net.gausman.ftl.model.*;
import net.gausman.ftl.model.record.Sector;
import net.gausman.ftl.model.record.StoreInfo;
import net.gausman.ftl.view.charts.ScrapGainedChartPanel;
import net.gausman.ftl.view.charts.ScrapUsedChartPanel;
import net.gausman.ftl.view.charts.ScrapUsedPieChartPanel;
import net.gausman.ftl.view.renderer.MultiLineCellRenderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ShipStatusPanel extends JTabbedPane {
    public static final int PREFERRED_WIDTH_1_DIGITS = 2;
    public static final int PREFERRED_WIDTH_3_DIGITS = 10;

    private SimpleTableModel runInfoTableModel;
    private SimpleTableModel resourcesTableModel;

    private ItemTableModel itemTableModel;
    private CrewTableModel crewTableModel;

    private StoreTableModel storeTableModel;

    private SectorTableModel sectorTableModel;
//    private JTable sectorTable;
//    private JScrollPane jScrollPaneSector;

    private SystemTableModel systemTableModel;

    private ScrapGainedChartPanel scrapGainedChartPanel;
    private ScrapUsedChartPanel scrapUsedChartPanel;
    private ScrapUsedPieChartPanel scrapUsedPieChartPanel;

    private final JSplitPane statusPanel;
//    private final JSplitPane sectorPanel;

    private JScrollPane jScrollPaneStore;
    private JTable storeTable;

    private final JSplitPane main;

    public ShipStatusPanel() {
        main = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        statusPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//        sectorPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        setPreferredSize(new Dimension(900, 500));

        JPanel firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.Y_AXIS));
        firstCol.setPreferredSize(new Dimension(250, 500));

        JPanel secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.Y_AXIS));

        statusPanel.setLeftComponent(firstCol);
        statusPanel.setRightComponent(secondCol);
        statusPanel.setMinimumSize(new Dimension(250, 200));

        sectorTableModel = new SectorTableModel();
//        sectorTable = new JTable(sectorTableModel);
//        jScrollPaneSector = new JScrollPane(sectorTable);
//        sectorPanel.setLeftComponent(jScrollPaneSector);
//        resizeColumnWidths(sectorTable, jScrollPaneSector);

//        sectorTable.getModel().addTableModelListener(e -> {
//            int newWidth = getPreferredTableWidth(sectorTable);
//            Dimension tablePrefSize = sectorTable.getPreferredSize();
//            tablePrefSize.width = newWidth;
//            sectorTable.setPreferredScrollableViewportSize(tablePrefSize);
//            sectorPanel.setDividerLocation(newWidth);
//            resizeColumnWidths(sectorTable, jScrollPaneSector);
//        });

        main.setTopComponent(statusPanel);
//        main.setBottomComponent(sectorPanel);


        JTabbedPane tabbedPaneSector = new JTabbedPane();

        // Sector Charts
        scrapGainedChartPanel = new ScrapGainedChartPanel();
        scrapUsedChartPanel = new ScrapUsedChartPanel();
        scrapUsedPieChartPanel = new ScrapUsedPieChartPanel();
        tabbedPaneSector.addTab("Scrap gain", scrapGainedChartPanel);
        tabbedPaneSector.addTab("Scrap usage (sector)", scrapUsedChartPanel);
        tabbedPaneSector.addTab("Scrap usage", scrapUsedPieChartPanel);

        // add Store
        storeTableModel = new StoreTableModel();
        JTable storeInfoTable = new JTable(storeTableModel);
        JScrollPane jScrollPaneStoreInfo = new JScrollPane(storeInfoTable);
//        tabbedPaneSector.addTab("Stores", jScrollPaneStoreInfo);


//        sectorPanel.setRightComponent(tabbedPaneSector);
        main.setBottomComponent(tabbedPaneSector);

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

        addTab("Ship status", main);

        storeTableModel = new StoreTableModel();
        storeTable = new JTable(storeTableModel);
        TableCellRenderer multiLineRenderer = new MultiLineCellRenderer();
//        storeTable.getColumnModel().getColumn(6).setCellRenderer(multiLineRenderer);
//        storeTable.getColumnModel().getColumn(7).setCellRenderer(multiLineRenderer);
//        storeTable.getColumnModel().getColumn(8).setCellRenderer(multiLineRenderer);
//        storeTable.getColumnModel().getColumn(9).setCellRenderer(multiLineRenderer);
        storeTable.setDefaultRenderer(Object.class, multiLineRenderer);
        storeTable.setRowHeight(60);
        jScrollPaneStore = new JScrollPane(storeTable);
        addTab("Store overview", jScrollPaneStore);

        // Crew Table debugging
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
        ShipSystem reactor = new ShipSystem("Reactor", "Reactor", "Other", false);
        reactor.setLevel(model.getReactor());
        shipSystems.add(reactor);


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

        List<StoreInfo> storeInfoList = new ArrayList<>();

        for (Map.Entry<Sector, SectorInfo> entry : model.getSectorMetrics().getData().entrySet()){
            for (Map.Entry<Integer, StoreInfo> e2 : entry.getValue().getStoreInfoMap().entrySet()){
                storeInfoList.add(e2.getValue());
            }
        }
        if (!storeInfoList.isEmpty()){
            storeInfoList.sort(Comparator.comparingInt(info -> info.getVisitedOnJumps().getFirst()));
            storeTableModel.setStoreInfoList(storeInfoList);
        }

        scrapGainedChartPanel.updateDataset(model.getSectorMetrics());
        scrapUsedChartPanel.updateDataset(model.getSectorMetrics());
        scrapUsedPieChartPanel.updateDataset(model.getSectorMetrics());

        resizeColumnWidths(storeTable, jScrollPaneStore);
    }

    public static int getPreferredTableWidth(JTable table) {
        int total = 0;
        for (int col = 0; col < table.getColumnCount(); col++) {
            TableColumn column = table.getColumnModel().getColumn(col);

            int preferred = 0;

            // Header width
            TableCellRenderer headerRenderer = column.getHeaderRenderer();
            if (headerRenderer == null) {
                headerRenderer = table.getTableHeader().getDefaultRenderer();
            }
            Component headerComp = headerRenderer.getTableCellRendererComponent(
                    table, column.getHeaderValue(), false, false, 0, col);
            preferred = Math.max(preferred, headerComp.getPreferredSize().width);

            // Cell width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, col);
                Component comp = cellRenderer.getTableCellRendererComponent(
                        table, table.getValueAt(row, col), false, false, row, col);
                preferred = Math.max(preferred, comp.getPreferredSize().width);
            }

            preferred += 10; // add some margin
            total += preferred;
        }
        return total + table.getIntercellSpacing().width * (table.getColumnCount() - 1);
    }


    public void resizeColumnWidths(JTable table, JScrollPane scrollPane) {
        final TableColumnModel columnModel = table.getColumnModel();
        int totalWidth = 0;

        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 5; // minimum width

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

        int rowCount = table.getRowCount();
        int rowHeight = table.getRowHeight();
        int tableHeight = rowCount * rowHeight;

        int headerHeight = table.getTableHeader().getPreferredSize().height;
        Dimension prefSize = table.getPreferredSize();
        prefSize.height = tableHeight + headerHeight;
        table.setPreferredScrollableViewportSize(prefSize);

        scrollPane.revalidate();
    }

}
