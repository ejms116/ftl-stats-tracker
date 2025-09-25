package net.gausman.ftl.view.renderer;

import net.gausman.ftl.model.Constants;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Set;

public class TagsRenderer extends JPanel implements TableCellRenderer {
    public TagsRenderer() {
        setOpaque(true);
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        removeAll();
        if (value instanceof Set<?>) {
            for (Object obj : (Set<?>) value) {
                if (obj instanceof Constants.EventTag tag) {
                    JLabel lbl = new JLabel(tag.name());
                    lbl.setOpaque(true);
                    lbl.setBackground(tag.getColor());
                    lbl.setForeground(Color.BLACK);
                    lbl.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
                    add(lbl);
                }
            }
        }
        if (isSelected) setBackground(table.getSelectionBackground());
        else setBackground(table.getBackground());
        return this;
    }
}
