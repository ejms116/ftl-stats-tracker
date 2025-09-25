package net.gausman.ftl;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.view.renderer.TagsRenderer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TagsTableExampleWithIcons {

    static class MyTableModel extends AbstractTableModel {
        private final String[] columnNames = {"ID", "Name", "Tags", "Status"};
        private final List<Object[]> data = new ArrayList<>();

        public MyTableModel() {
            // Example data: ID, Name, Tags, Status text
            data.add(new Object[]{1, "Apple", EnumSet.of(Constants.EventTag.BUY), "Fresh"});
            data.add(new Object[]{2, "Banana", EnumSet.of(Constants.EventTag.SELL, Constants.EventTag.REWARD), "Warning"});
            data.add(new Object[]{3, "Cherry", EnumSet.of(Constants.EventTag.REPAIR), "Expired"});
        }

        @Override
        public int getRowCount() { return data.size(); }

        @Override
        public int getColumnCount() { return columnNames.length; }

        @Override
        public Object getValueAt(int row, int col) { return data.get(row)[col]; }

        @Override
        public String getColumnName(int col) { return columnNames[col]; }

        @Override
        public Class<?> getColumnClass(int col) {
            if (col == 2) return Set.class; // Tags
            return String.class; // others are text
        }
    }



    // Renderer for text + icon column
    static class IconTextRenderer extends JLabel implements TableCellRenderer {
        private final Map<String, Icon> icons = new HashMap<>();

        public IconTextRenderer() {
            setOpaque(true);

            // Load your icons once (replace with real PNGs)
            icons.put("Fresh", new ImageIcon("fresh.png"));
            icons.put("Warning", new ImageIcon("warning.png"));
            icons.put("Expired", new ImageIcon("expired.png"));
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
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }
            return this;
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Tags + Icons Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MyTableModel model = new MyTableModel();
        JTable table = new JTable(model);
        table.setRowHeight(30);

        // Tags column
        table.getColumnModel().getColumn(2).setCellRenderer(new TagsRenderer());

        // Status column (icon + text)
        table.getColumnModel().getColumn(3).setCellRenderer(new IconTextRenderer());

        frame.add(new JScrollPane(table));
        frame.setSize(600, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TagsTableExampleWithIcons::createAndShowGUI);
    }
}

