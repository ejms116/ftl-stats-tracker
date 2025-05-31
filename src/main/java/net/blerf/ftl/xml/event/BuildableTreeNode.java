package net.blerf.ftl.xml.event;

import net.blerf.ftl.parser.DataManager;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Map;

public interface BuildableTreeNode {
    DefaultMutableTreeNode build(DataManager dataManager,  BuildContext context);
}
