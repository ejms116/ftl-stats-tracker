package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import net.blerf.ftl.parser.DataManager;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public class Item extends AbstractBuildableTreeNode {
    @XmlAttribute(name = "type")
    private String type;
    @XmlAttribute(name = "min")
    private Integer min;
    @XmlAttribute(name = "max")
    private Integer max;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public String getDisplayText(DataManager dataManager){
        return String.format("Item Modify: %s, min: %s, max: %s", type, min, max);
    }
}
