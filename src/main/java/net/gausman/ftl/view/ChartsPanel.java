package net.gausman.ftl.view;

import javax.swing.*;
import java.awt.*;

public class ChartsPanel extends JPanel {

    public ChartsPanel(){
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel scrapGainedChartPanel = new ScrapGainedChartPanel();
        JPanel scrapSpentChartPanel = new ScrapSpentChartPanel();

        add(scrapGainedChartPanel);
        add(scrapSpentChartPanel);
    }
}
