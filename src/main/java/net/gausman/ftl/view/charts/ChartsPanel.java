package net.gausman.ftl.view.charts;

import net.gausman.ftl.view.charts.ScrapBarChartPanel;
import net.gausman.ftl.view.charts.ScrapSpentChartPanel;

import javax.swing.*;
import java.awt.*;


public class ChartsPanel extends JPanel {

    public ChartsPanel(){
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);

//        JPanel scrapGainedChartPanel = new ScrapGainedChartPanel();
        JPanel scrapBarChartExample = new ScrapBarChartPanel();
        JPanel scrapSpentChartPanel = new ScrapSpentChartPanel();

//        add(scrapGainedChartPanel);
        add(scrapBarChartExample);
        add(scrapSpentChartPanel);
    }
}
