package net.gausman.ftl.view.charts;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.SectorInfo;
import net.gausman.ftl.model.SectorMetrics;
import net.gausman.ftl.model.record.Sector;
import net.gausman.ftl.model.sector.DamageStat;
import net.gausman.ftl.model.sector.RepairStat;
import net.gausman.ftl.model.sector.SectorStat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class HullChartPanel extends JPanel {
    private final DefaultCategoryDataset dataset;

    public HullChartPanel() {
        setLayout(new BorderLayout());

        // Build dataset
        dataset = new DefaultCategoryDataset();

//        String[] dataPoints = {"P1","P2","P3","P4","P5","P6","P7","P8"};
//
//        for (String point : dataPoints) {
//            // Damage values
//            dataset.addValue(5, "Damage-Normal", point);
//            dataset.addValue(2, "Damage-Event", point);
//
//            // Repair values
//            dataset.addValue(3, "Repair-Store", point);
//            dataset.addValue(1, "Repair-Event", point);
//            dataset.addValue(4, "Repair-Drone", point);
//        }

        // Create chart
        JFreeChart chart = ChartFactory.createStackedBarChart(
                "Damage vs Repair",   // Chart title
                "Data Point",         // Domain axis label
                "Value",              // Range axis label
                dataset
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        Color darkGray = new Color(60, 63, 65); // FlatLaf dark

        chart.setBackgroundPaint(darkGray);
        plot.setBackgroundPaint(darkGray);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Axis labels
        plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);

        // Get the title object
        if (chart.getTitle() != null) {
            chart.getTitle().setPaint(Color.WHITE);      // change text color
            chart.getTitle().setFont(new Font("Dialog", Font.BOLD, 14)); // optional: change font
        }

        // Legend
        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(darkGray);
            chart.getLegend().setItemPaint(Color.WHITE);
        }

        // Grouped stacked renderer
        GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
        KeyToGroupMap map = new KeyToGroupMap("G1");

        // Assign Damage subcategories to group "Damage"
        map.mapKeyToGroup("Damage-Normal", "Damage");
        map.mapKeyToGroup("Damage-Event", "Damage");

        // Assign Repair subcategories to group "Repair"
        map.mapKeyToGroup("Repair-Store", "Repair");
        map.mapKeyToGroup("Repair-Event", "Repair");
        map.mapKeyToGroup("Repair-Drone", "Repair");

        renderer.setSeriesToGroupMap(map);
        renderer.setItemMargin(0.1); // spacing between bars
        plot.setRenderer(renderer);

        // Wrap chart in ChartPanel and add to this JPanel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        add(chartPanel, BorderLayout.CENTER);
    }

    private String convert(Object key, Constants.SectorStatsData sectorStatsData){
        return String.format("%s - %s", sectorStatsData.toString(), key.toString());
    }

    public void updateDataset(SectorMetrics sectorMetrics){
        initDataset();
        for (Map.Entry<Sector, SectorInfo> outer : sectorMetrics.getData().entrySet()){
            String text = String.valueOf(outer.getKey().getId());
            for (Map.Entry<Constants.SectorStatsData, SectorStat<?>> innerEntry : outer.getValue().getSectorStats().entrySet()){
                if (innerEntry.getValue() instanceof DamageStat || innerEntry.getValue() instanceof RepairStat){
                    for (Map.Entry<?, Integer> stat : innerEntry.getValue().getAll().entrySet()){
                        addOrSet(dataset, stat.getValue(), convert(stat.getKey(), innerEntry.getKey()), text);
                    }
                }
            }
        }
    }

    private void initDataset(){
        dataset.clear();
        for (int i = 1; i < 9; i++){
            for (Constants.DamageSub damageSub : Constants.DamageSub.values()){
                dataset.setValue(0, convert(damageSub, Constants.SectorStatsData.DAMAGE), Integer.toString(i));
            }
            for (Constants.RepairSub repairSub : Constants.RepairSub.values()){
                dataset.setValue(0, convert(repairSub, Constants.SectorStatsData.REPAIR), Integer.toString(i));
            }
        }
    }

    private void addOrSet(DefaultCategoryDataset dataset,
                          double value,
                          Comparable<?> rowKey,
                          Comparable<?> columnKey) {
        Number current;
        try {
            current = dataset.getValue(rowKey, columnKey);
        } catch (org.jfree.data.UnknownKeyException e) {
            current = null;
        }
        dataset.setValue((current == null ? 0.0 : current.doubleValue()) + value,
                rowKey,
                columnKey);
    }


}
