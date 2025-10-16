package net.gausman.ftl.view.renderer;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.util.GausmanUtil;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;


public class MultiLineCellRenderer extends JPanel implements TableCellRenderer {
    public MultiLineCellRenderer() {
        setLayout(new GridLayout(3, 1)); // exactly 3 lines
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        removeAll(); // clear previous labels

        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        if (value instanceof SavedGameParser.StoreShelf shelf) {
            List<SavedGameParser.StoreItem> items = shelf.getItems();

            for (SavedGameParser.StoreItem item : items) {
                JLabel label = new JLabel(GausmanUtil.getTextToId(shelf.getItemType(), item.getItemId()));
                label.setOpaque(false);
                label.setForeground(!item.isAvailable()
                        ? new Color(255, 215, 0) // bright yellow
                        : table.getForeground());
                label.setToolTipText(GausmanUtil.getTextToId(shelf.getItemType(), item.getItemId())); // show full text on hover
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setPreferredSize(null); // reset
                label.putClientProperty("html.disable", Boolean.TRUE); // disable HTML to prevent wrapping
                add(label);
            }
        }

        if (value instanceof String text){
            JLabel label = new JLabel(text);
            add(label);
        }
        Color lineColor = UIManager.getColor("Table.gridColor");
        setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, lineColor));

        return this;
    }
}