package net.gausman.ftl.view.renderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

// Renderer for text + icon column
public class IconTextRenderer extends JLabel implements TableCellRenderer {
    private final Map<String, Icon> icons = new HashMap<>();

    public IconTextRenderer() {
        setOpaque(true);

        // Load your icons once (replace with real PNGs)
        icons.put("Fresh", loadIcon("/icons/icon_drones.png"));
        icons.put("Warning", loadIcon("/icons/icon_scrap.png"));
        icons.put("Expired", loadIcon("/icons/icon_missiles.png"));
    }

    private ImageIcon loadIcon(String path){
        java.net.URL url = getClass().getResource(path);
        if (url != null){
            return new ImageIcon(url);
        } else {
            return null;
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        String text = (value == null) ? "" : value.toString();
        setText(" " + text); // add space so text isn't glued to icon
        setIcon(icons.get(text));

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }
        return this;
    }
}
