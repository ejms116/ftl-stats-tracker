package net.gausman.ftl.view.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

public class ScrapGainedChartPanel extends JPanel {

    public ScrapGainedChartPanel(){
        // Create a bar chart
        JFreeChart barChart = createBarChart();
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(400, 200));  // Set chart size
        add(chartPanel);

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

}
