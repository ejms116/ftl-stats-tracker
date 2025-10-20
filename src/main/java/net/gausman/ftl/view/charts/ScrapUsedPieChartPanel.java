package net.gausman.ftl.view.charts;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.SectorInfo;
import net.gausman.ftl.model.SectorMetrics;
import net.gausman.ftl.model.record.Sector;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.EnumMap;
import java.util.Map;

public class ScrapUsedPieChartPanel extends JPanel {
    private enum InternalScrapUsedCategory {
        RESOURCES,
        REPAIR,
        SYSTEM_UPGRADE,
        SYSTEM_BUY,
        REACTOR,
        ITEMS,
        CREW,
    } 
    
    private final DefaultPieDataset<String> dataset;

    public ScrapUsedPieChartPanel() {
        dataset = new DefaultPieDataset<>();

        setLayout(new BorderLayout());

        // --- Create chart ---
        JFreeChart chart = ChartFactory.createPieChart(
                "Total Scrap Usage",
                dataset,
                true,   // legend
                true,   // tooltips
                false   // URLs
        );

        // --- Customize plot ---
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        Color darkGray = new Color(60, 63, 65);

        chart.setBackgroundPaint(darkGray);
        plot.setBackgroundPaint(darkGray);
        plot.setOutlineVisible(false);

        // Remove shadow/3D effects
        plot.setShadowPaint(null);
        plot.setSectionOutlinesVisible(false);

        // Title
        if (chart.getTitle() != null) {
            chart.getTitle().setPaint(Color.WHITE);
            chart.getTitle().setFont(new Font("Dialog", Font.BOLD, 14));
        }

        // Legend
        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(darkGray);
            chart.getLegend().setItemPaint(Color.WHITE);
        }

        // Colors per category
        int i = 0;
        for (InternalScrapUsedCategory cat : InternalScrapUsedCategory.values()) {
            if (i < Constants.flatLafDarkColors.length) {
                plot.setSectionPaint(cat.name(), Constants.flatLafDarkColors[i]);
            }
            i++;
        }

        // Labels: value + percentage, no category name
        plot.setLabelGenerator(
                new StandardPieSectionLabelGenerator(
                        "{0}: {1} ({2})",   // category: value (percentage)
                        new DecimalFormat("0"),     // integer values
                        new DecimalFormat("0.0%")   // percentage with 1 decimal
                )
        );


        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        add(chartPanel, BorderLayout.CENTER);
    }

    private InternalScrapUsedCategory convert(Constants.ScrapUsedCategory category){
        return switch (category){
            case FUEL, MISSILES, DRONE_PARTS -> InternalScrapUsedCategory.RESOURCES;
            case REPAIR -> InternalScrapUsedCategory.REPAIR;
            case SYSTEM_UPGRADE -> InternalScrapUsedCategory.SYSTEM_UPGRADE;
            case SYSTEM_BUY -> InternalScrapUsedCategory.SYSTEM_BUY;
            case REACTOR -> InternalScrapUsedCategory.REACTOR;
            case WEAPONS, DRONES, AUGMENTS -> InternalScrapUsedCategory.ITEMS;
            case CREW -> InternalScrapUsedCategory.CREW;
        };
    }

    public void updateDataset(SectorMetrics sectorMetrics) {

        // Sum values across all sectors
        Map<InternalScrapUsedCategory, Integer> totals =
                new EnumMap<>(InternalScrapUsedCategory.class);

        for (InternalScrapUsedCategory cat : InternalScrapUsedCategory.values()) {
            totals.put(cat, 0);
        }

        for (Map.Entry<Sector, SectorInfo> outer : sectorMetrics.getData().entrySet()) {
            for (Map.Entry<Constants.ScrapUsedCategory, Integer> inner :
                    outer.getValue().getScrapUsed().entrySet()) {

                InternalScrapUsedCategory cat = convert(inner.getKey());
                totals.put(cat, totals.get(cat) + inner.getValue());
            }
        }

        dataset.clear();
        for (Map.Entry<InternalScrapUsedCategory, Integer> entry : totals.entrySet()) {
            dataset.setValue(entry.getKey().name(), entry.getValue());
//            if (entry.getValue() > 0) {
//                dataset.setValue(entry.getKey().name(), entry.getValue());
//            }
        }
    }
}

