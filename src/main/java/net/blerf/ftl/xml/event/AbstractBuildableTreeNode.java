package net.blerf.ftl.xml.event;

import org.w3c.dom.Element;
import jakarta.xml.bind.annotation.XmlAnyElement;
import net.blerf.ftl.parser.DataManager;

import javax.swing.tree.DefaultMutableTreeNode;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

public class AbstractBuildableTreeNode implements BuildableTreeNode {
    @XmlAnyElement
    private List<Element> unknownElements;

    public List<Element> getUnknownElements() {
        return unknownElements;
    }

    public String getDisplayText(DataManager dataManager,  BuildContext context){
        return this.getClass().getSimpleName();
    }

    @Override
    public DefaultMutableTreeNode build(DataManager dataManager, BuildContext context) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(getDisplayText(dataManager, context));

        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(this);
                if (value instanceof BuildableTreeNode treeChild) {
                    DefaultMutableTreeNode childNode = treeChild.build(dataManager, context);
                    if (childNode != null){
                        node.add(childNode);
                    }

                } else if (value instanceof Collection<?> list) {
                    for (Object item : list) {
                        if (item instanceof BuildableTreeNode treeItem) {
                            DefaultMutableTreeNode childNode = treeItem.build(dataManager, context);
                            node.add(childNode);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return node;
    }
}
