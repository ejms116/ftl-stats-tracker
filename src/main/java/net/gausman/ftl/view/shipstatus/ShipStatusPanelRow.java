package net.gausman.ftl.view.shipstatus;

import javax.swing.*;
import java.awt.*;


public class ShipStatusPanelRow extends JPanel {
    private final JLabel textLabel;
    private JLabel valueLabel;

    public ShipStatusPanelRow(String text, String value){
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(140, 20));

        textLabel = new JLabel(text);
        textLabel.setPreferredSize(new Dimension(120, 20));
        valueLabel = new JLabel();
        valueLabel.setText(value);

        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        add(textLabel, BorderLayout.WEST);
        add(valueLabel, BorderLayout.EAST);
    }

    public void setTextLabel(String value) {
        this.valueLabel.setText(value);
    }
}
