package net.gausman.ftl.view;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ExpandableJTableExample {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Expandable JTable Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);

            ExpandableTableModel model = new ExpandableTableModel();
            JTable table = new JTable(model);
            table.setRowHeight(25);

            // Toggle detail row on click
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row != -1) {
                        model.toggleRow(row);
                    }
                }
            });

            frame.add(new JScrollPane(table));
            frame.setVisible(true);
        });
    }

    static class ExpandableTableModel extends AbstractTableModel {
        private static class RowData {
            String name;
            String value;
            boolean isDetail;

            RowData(String name, String value, boolean isDetail) {
                this.name = name;
                this.value = value;
                this.isDetail = isDetail;
            }
        }

        private final List<RowData> rows = new ArrayList<>();
        private final Set<Integer> expandedMainRows = new HashSet<>();

        public ExpandableTableModel() {
            // Add some base rows
            for (int i = 1; i <= 5; i++) {
                rows.add(new RowData("Item " + i, "Value " + i, false));
            }
        }

        public void toggleRow(int rowIndex) {
            RowData row = rows.get(rowIndex);
            if (row.isDetail) return; // Do nothing for detail rows

            if (expandedMainRows.contains(rowIndex)) {
                // Collapse
                rows.remove(rowIndex + 3);
                rows.remove(rowIndex + 2);
                rows.remove(rowIndex + 1);
                expandedMainRows.remove(rowIndex);
            } else {
                // Expand
                rows.add(rowIndex + 1, new RowData("→ Details for " + row.name, "Extra info...", true));
                rows.add(rowIndex + 2, new RowData("→ Details for " + row.name, "Extra info...", true));
                rows.add(rowIndex + 3, new RowData("→ Details for " + row.name, "Extra info...", true));
                expandedMainRows.add(rowIndex);
            }
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            RowData row = rows.get(rowIndex);
            return columnIndex == 0 ? row.name : row.value;
        }

        @Override
        public String getColumnName(int column) {
            return column == 0 ? "Name" : "Value";
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
}

