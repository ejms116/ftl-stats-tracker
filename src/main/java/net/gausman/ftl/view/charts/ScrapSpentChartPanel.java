package net.gausman.ftl.view.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;

public class ScrapSpentChartPanel extends JPanel {

    public ScrapSpentChartPanel(){
        JFreeChart pieChart = createPieChart();
        ChartPanel chartPanelPie = new ChartPanel(pieChart);
        chartPanelPie.setPreferredSize(new Dimension(400, 200));
        add(chartPanelPie);
    }



    private JFreeChart createPieChart(){
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

    private DefaultPieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Chrome", 63.59);
        dataset.setValue("Firefox", 4.18);
        dataset.setValue("Edge", 5.62);
        dataset.setValue("Safari", 19.23);
        return dataset;
    }

}
