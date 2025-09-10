package net.gausman.ftl.view.eventtable;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Objects;

public class GroupRowColorJTable extends JTable {

    public GroupRowColorJTable(TableModel model) {
        super(model);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Only if model has data
        if (getRowCount() < 2) return;

        // Get the actual model column index for "Type" (assumed to be column 1 here)
        int typeCol = convertColumnIndexToView(3);
        TableModel model = getModel();

        for (int row = 0; row < getRowCount() - 1; row++) {
            Object currentType = getValueAt(row, typeCol);
            Object nextType = getValueAt(row + 1, typeCol);

            if (!Objects.equals(currentType, nextType)) {
                Rectangle rect = getCellRect(row, 0, true);
                int y = rect.y + rect.height - 1;

                g.setColor(Color.GRAY);  // Customize color here
                g.drawLine(0, y, getWidth(), y);

            }
        }
    }



//    @Override
//    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
//        Component comp = super.prepareRenderer(renderer, row, column);
//
//        // Get the type column to check group changes
//        int typeCol = convertColumnIndexToView(3); // Assuming column 1 is the type
//        TableModel model = getModel();
//
//        // Get current and next row "type" values
//        Object currentType = model.getValueAt(row, typeCol);
////        Object nextType = (row < getRowCount() - 1) ? model.getValueAt(row - 1, typeCol) : null;
//        Object nextType = getValueAt(row + 1, typeCol);
//
//        // Check if this row is at the start of a new group (change of type)
//        boolean isNewGroup = !Objects.equals(currentType, nextType);
//
//        // Color logic: alternate group colors (white, light blue, etc.)
//        Color groupColor;
//        if (isNewGroup) {
//            // Switch between two colors for different groups
//            groupColor = (row % 2 == 0) ? new Color(240, 248, 255) : Color.WHITE; // Light blue and white
//        } else {
//            // Keep the same color within the same group
//            groupColor = comp.getBackground();
//        }
//
//        comp.setBackground(groupColor);
//        return comp;
//    }
}
