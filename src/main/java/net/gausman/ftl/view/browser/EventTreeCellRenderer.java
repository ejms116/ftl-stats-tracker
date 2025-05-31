package net.gausman.ftl.view.browser;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class EventTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        if (c instanceof JLabel && value instanceof DefaultMutableTreeNode) {
            Object userObj = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObj instanceof EventTreeNodeData) {
                EventTreeNodeData data = (EventTreeNodeData) userObj;
                ((JLabel) c).setText(data.toHtml());
            }
        }

        return c;
    }
}

