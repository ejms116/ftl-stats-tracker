package net.gausman.ftl.view.charts;


import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.SectorInfo;
import net.gausman.ftl.model.SectorMetrics;
import net.gausman.ftl.model.record.Sector;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.RangeType;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ScrapGainedChartPanel extends JPanel {

    private final DefaultCategoryDataset defaultDataset;

    public ScrapGainedChartPanel() {
        defaultDataset = new DefaultCategoryDataset();
        initDatasetFromPos(0);

        setLayout(new BorderLayout());

        // --- Create chart ---
        JFreeChart chart = ChartFactory.createStackedBarChart(
                "Scrap Gained per Sector",
                null,   // hide X-axis label
                null,   // hide Y-axis label
                defaultDataset
        );

        // --- Customize plot ---
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        Color darkGray = new Color(60, 63, 65); // FlatLaf dark

        chart.setBackgroundPaint(darkGray);
        plot.setBackgroundPaint(darkGray);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Axis labels
        plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);

        // --- Renderer ---
        StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();
        for (int i = 0; i < Constants.flatLafDarkColors.length; i++) {
            renderer.setSeriesPaint(i, Constants.flatLafDarkColors[i]);
        }

        // Show values on bars
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new org.jfree.chart.labels.StandardCategoryItemLabelGenerator(){

            @Override
            public String generateLabel(CategoryDataset dataset, int row, int column) {
                Number value = dataset.getValue(row, column);
                if (value == null || value.intValue() == 0){
                    return null;
                }
                return this.generateLabelString(dataset, row, column);
            }

        });


        renderer.setDefaultPositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER)
        );

        renderer.setPositiveItemLabelPositionFallback(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER)
        );


        renderer.setDefaultItemLabelsVisible(true);

        plot.setRenderer(renderer);

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

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4) // 45° upwards
        );

        NumberAxis yAxis = (NumberAxis)plot.getRangeAxis();
        yAxis.setRangeType(RangeType.POSITIVE);
        yAxis.setAutoRangeMinimumSize(400);


        // --- Wrap chart ---
        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setPreferredSize(new Dimension(400, 200));
        chartPanel.setMouseWheelEnabled(false);

        add(chartPanel, BorderLayout.CENTER);
    }


    public void updateDataset(SectorMetrics sectorMetrics){
        defaultDataset.clear();
        for (Map.Entry<Sector, SectorInfo> outer : sectorMetrics.getData().entrySet()){
            String text = String.format("%s - %s", outer.getKey().getId(), outer.getKey().getSectorDot().getTitle());
//            String text = String.valueOf(outer.getKey().getId());
            for (Map.Entry<Constants.ScrapOrigin, Integer> innerEntry : outer.getValue().getScrapGained().entrySet()){
                defaultDataset.setValue(innerEntry.getValue(), innerEntry.getKey(), text);
            }
        }
        initDatasetFromPos(sectorMetrics.getData().size());

    }

    private void initDatasetFromPos(int pos){
        for (int i = pos + 1; i < 9; i++){
            for (Constants.ScrapOrigin origin : Constants.ScrapOrigin.values()){
                defaultDataset.setValue(0, origin, Integer.toString(i));
            }
        }
    }
}

