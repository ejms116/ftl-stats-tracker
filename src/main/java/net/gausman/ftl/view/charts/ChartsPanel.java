package net.gausman.ftl.view.charts;

import net.gausman.ftl.model.SectorMetrics;

import javax.swing.*;
import java.awt.*;


public class ChartsPanel extends JPanel {
    private final ScrapGainedChartPanel scrapGainedChartPanel;
    private final ScrapUsedPieChartPanel scrapUsedPieChartPanel;
//    private final ScrapUsedChartPanel scrapUsedChartPanel;

    public ChartsPanel(){
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        scrapGainedChartPanel = new ScrapGainedChartPanel();
        scrapUsedPieChartPanel = new ScrapUsedPieChartPanel();
//        scrapUsedChartPanel = new ScrapUsedChartPanel();

        add(scrapGainedChartPanel);
        add(scrapUsedPieChartPanel);
//        add(scrapUsedChartPanel);
    }

    public void updateDatasets(SectorMetrics sectorMetrics){
        scrapGainedChartPanel.updateDataset(sectorMetrics);
        scrapUsedPieChartPanel.updateDataset(sectorMetrics);
//        scrapUsedChartPanel.updateDataset(sectorMetrics);
    }

}
