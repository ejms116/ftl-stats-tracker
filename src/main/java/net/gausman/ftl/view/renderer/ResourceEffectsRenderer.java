package net.gausman.ftl.view.renderer;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.util.IconUtils;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class ResourceEffectsRenderer extends JPanel implements TableCellRenderer {

    public ResourceEffectsRenderer() {
        setOpaque(true);
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {

        removeAll();

        if (value instanceof EnumMap<?, ?> map) {
            @SuppressWarnings("unchecked")
            EnumMap<Constants.Resource, Integer> resourceMap = (EnumMap<Constants.Resource, Integer>) map;

            int rowHeight = table.getRowHeight(row);

            for (Map.Entry<Constants.Resource, Integer> entry : resourceMap.entrySet()) {
                Constants.Resource resource = entry.getKey();
                int amount = entry.getValue();

                Color color = amount > 0 ? Color.GREEN : amount < 0 ? Color.RED : Color.GRAY;

                ImageIcon baseIcon = resource.getIcon();
                ImageIcon tintedIcon = IconUtils.tintIcon(baseIcon, color, rowHeight - 4);

                JLabel lbl = new JLabel(String.valueOf(amount), tintedIcon, JLabel.LEFT);
                lbl.setHorizontalTextPosition(SwingConstants.LEFT);
                lbl.setIconTextGap(2);
                lbl.setOpaque(false);
                lbl.setForeground(color);

                add(lbl);
            }
        }

        // handle selection background from FlatLaf properly:
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }

        return this;
    }
}

