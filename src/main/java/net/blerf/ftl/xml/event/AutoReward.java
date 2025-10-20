package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.*;
import net.blerf.ftl.parser.DataManager;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Map;


@XmlRootElement(name = "autoReward")
@XmlAccessorType(XmlAccessType.FIELD)
public class AutoReward implements BuildableTreeNode {


    public enum RewardLevel {
        LOW,
        MED,
        HIGH,
        RANDOM
    }

    @XmlAttribute(name = "level")
    private RewardLevel level;

    @XmlValue
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public RewardLevel getLevel() {
            return level;
        }

    public void setLevel(RewardLevel level) {
        this.level = level;
    }

    @Override
    public DefaultMutableTreeNode build(DataManager dataManager,  BuildContext context) {
        String text = String.format("Auto reward - Level: %s, Type: %s", level, value);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(text);
        return node;
    }


}
