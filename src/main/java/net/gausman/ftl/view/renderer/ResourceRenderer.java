package net.gausman.ftl.view.renderer;

import net.gausman.ftl.model.Constants;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class ResourceRenderer extends JPanel implements TableCellRenderer {

    private final JLabel textLabel;

    public ResourceRenderer() {
        setOpaque(true);
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

        textLabel = new JLabel();
        add(textLabel);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        removeAll();

        if (value instanceof CellData cellData) {
            // Main text
            textLabel.setText(cellData.text());
            add(textLabel);

            // Now loop over the EnumMap<Resource, Integer>
            for (Map.Entry<Constants.Resource, Integer> entry : cellData.values().entrySet()) {
                Constants.Resource res = entry.getKey();
                Integer number = entry.getValue();

                JLabel lbl = new JLabel(number.toString(), res.getIcon(), JLabel.LEFT);
                lbl.setOpaque(false);

                // Colorize number
                if (number > 0) {
                    lbl.setForeground(Color.GREEN);
                } else if (number < 0) {
                    lbl.setForeground(Color.RED);
                } else {
                    lbl.setForeground(Color.LIGHT_GRAY);
                }

                add(lbl);
            }
        }

        // Selection background handling
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }

        return this;
    }

    // A little record to hold both text + map
    public record CellData(String text, EnumMap<Constants.Resource, Integer> values) {}
}

