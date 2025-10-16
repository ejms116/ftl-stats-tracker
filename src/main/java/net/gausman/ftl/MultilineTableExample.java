package net.gausman.ftl;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class MultilineTableExample {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Multiline JTable Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            String[] columns = {"Name", "Description"};
            Object[][] data = {
                    {"Item 1", "Apple\nBanana\nCherry"},
                    {"Item 2", "Red\nGreen\nBlue"},
                    {"Item 3", "Short\nMedium\nVery long description text"}
            };

            JTable table = new JTable(data, columns);
            table.setRowHeight(60); // You can adjust dynamically too

            // Set custom renderer for all columns (or just specific ones)
            TableCellRenderer multiLineRenderer = new MultiLineCellRenderer();
            table.getColumnModel().getColumn(1).setCellRenderer(multiLineRenderer);

            frame.add(new JScrollPane(table));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // --- Custom renderer using JTextArea ---
    static class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {
        public MultiLineCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText(value == null ? "" : value.toString());

            // Apply JTable selection colors
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            // Adjust row height automatically
            setSize(table.getColumnModel().getColumn(column).getWidth(), Short.MAX_VALUE);
            int preferredHeight = getPreferredSize().height;
            if (table.getRowHeight(row) != preferredHeight) {
                table.setRowHeight(row, preferredHeight);
            }

            return this;
        }
    }
}

