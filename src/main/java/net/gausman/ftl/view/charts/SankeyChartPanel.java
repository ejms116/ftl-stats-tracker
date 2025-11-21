package net.gausman.ftl.view.charts;

import net.gausman.ftl.model.SectorMetrics;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.flow.DefaultFlowDataset;

import javax.swing.*;
import java.awt.*;

public class SankeyChartPanel extends JPanel {
    private DefaultFlowDataset<String> flowDataset = new DefaultFlowDataset<>();
    private GausmanFlowPlot flowPlot;
    private JFreeChart chart;

    public SankeyChartPanel(){
        setLayout(new BorderLayout());

        flowPlot = new GausmanFlowPlot(flowDataset);

        chart = new JFreeChart("Scrap Flow", JFreeChart.DEFAULT_TITLE_FONT, flowPlot, true);

        Color darkGray = new Color(60, 63, 65); // FlatLaf dark
        chart.setBackgroundPaint(darkGray);
        flowPlot.setBackgroundPaint(darkGray);

        if (chart.getTitle() != null) {
            chart.getTitle().setPaint(Color.WHITE);      // change text color
            chart.getTitle().setFont(new Font("Dialog", Font.BOLD, 14)); // optional: change font
        }

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 200));
        chartPanel.setMouseWheelEnabled(false);

        add(chartPanel, BorderLayout.CENTER);
    }

    public void updateDataset(SectorMetrics sectorMetrics){
        flowDataset = sectorMetrics.getFlowDataset();
        flowPlot.setDataset(flowDataset);
        chart.fireChartChanged();
    }
}
