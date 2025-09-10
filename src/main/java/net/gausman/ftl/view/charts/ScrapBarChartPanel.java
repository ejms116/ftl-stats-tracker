package net.gausman.ftl.view.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

public class ScrapBarChartPanel extends JPanel {

    private final DefaultCategoryDataset dataset;

    public ScrapBarChartPanel() {
        setLayout(new BorderLayout());

        // --- Create dataset once ---
        dataset = new DefaultCategoryDataset();

        // Optional: add initial data (all zeros)
        for (int i = 1; i <= 8; i++) {
            String sector = "Sector " + i;
            dataset.addValue(0, "Normal", sector);
            dataset.addValue(0, "Free", sector);
        }

        // --- Create chart ---
        JFreeChart chart = ChartFactory.createStackedBarChart(
                "Scrap Gained per Sector",
                null,   // hide X-axis label
                null,   // hide Y-axis label
                dataset
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
        renderer.setSeriesPaint(0, new Color(70, 130, 180)); // Normal
        renderer.setSeriesPaint(1, new Color(46, 204, 113)); // Free

        // Show values on bars
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(
                new org.jfree.chart.labels.StandardCategoryItemLabelGenerator()
        );
        renderer.setDefaultItemLabelPaint(Color.WHITE);

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



        // Set Y-axis fixed range
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0, 400);
        rangeAxis.setTickLabelPaint(Color.WHITE);
        rangeAxis.setTickUnit(new org.jfree.chart.axis.NumberTickUnit(50));


        // --- Wrap chart ---
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 200));
        chartPanel.setMouseWheelEnabled(false);

        add(chartPanel, BorderLayout.CENTER);
    }

    /**
     * Updates the values for a given sector.
     *
     * @param sectorIndex 1–8 for "Sector 1" to "Sector 8"
     * @param normal      value for "Normal"
     * @param free        value for "Free"
     */
    public void updateSector(int sectorIndex, int normal, int free) {
        if (sectorIndex < 1 || sectorIndex > 8) {
            throw new IllegalArgumentException("Sector index must be 1–8");
        }
        String sector = "Sector " + sectorIndex;
        dataset.setValue(normal, "Normal", sector);
        dataset.setValue(free, "Free", sector);
    }

    // Demo usage
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Demo UI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            ScrapBarChartPanel chartPanel = new ScrapBarChartPanel();
            frame.add(chartPanel, BorderLayout.CENTER);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Simulate some updates
            new Timer(1000, e -> {
                int sector = (int) (Math.random() * 8) + 1;
                int normal = (int) (Math.random() * 300);
                int free = (int) (Math.random() * 100);
                chartPanel.updateSector(sector, normal, free);
            }).start();
        });
    }
}

