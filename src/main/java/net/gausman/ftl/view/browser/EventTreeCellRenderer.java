package net.gausman.ftl.view.browser;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.xml.event.AbstractBuildableTreeNode;
import net.blerf.ftl.xml.event.BuildContext;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

class EventTreeCellRenderer extends DefaultTreeCellRenderer {
    private final DataManager dataManager;
    private final BuildContext context;

    public EventTreeCellRenderer(DataManager dataManager, BuildContext context) {
        this.dataManager = dataManager;
        this.context = context;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (value instanceof DefaultMutableTreeNode node) {
            Object userObject = node.getUserObject();

            if (userObject instanceof AbstractBuildableTreeNode buildable) {
                setText(buildable.getDisplayText(dataManager, context));
            } else if (userObject != null) {
                setText(userObject.toString());
            } else {
                setText("");
            }
        }

        return c;
    }
}


