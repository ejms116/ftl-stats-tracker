package net.gausman.ftl.view.charts;

import net.gausman.ftl.model.SectorMetrics;

import javax.swing.*;

public class ScrapUsageChartsPanel extends JSplitPane {
    private final ScrapUsedChartPanel scrapUsedChartPanel;
    private final ScrapUsedPieChartPanel scrapUsedPieChartPanel;

    public ScrapUsageChartsPanel(){
        setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        setResizeWeight(0.5);
        setDividerLocation(0.5);

        scrapUsedChartPanel = new ScrapUsedChartPanel();
        scrapUsedPieChartPanel = new ScrapUsedPieChartPanel();

        setLeftComponent(scrapUsedChartPanel);
        setRightComponent(scrapUsedPieChartPanel);
    }

    public void updateDatasets(SectorMetrics sectorMetrics){
        scrapUsedChartPanel.updateDataset(sectorMetrics);
        scrapUsedPieChartPanel.updateDataset(sectorMetrics);
    }
}
