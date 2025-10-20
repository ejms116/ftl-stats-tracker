package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;
import net.blerf.ftl.parser.DataManager;

@XmlRootElement(name = "boarders")
@XmlAccessorType(XmlAccessType.FIELD)
public class Boarders extends AbstractBuildableTreeNode {

    @XmlAttribute(name = "min")
    private int min;

    @XmlAttribute(name = "max")
    private int max;

    @XmlAttribute(name = "class")
    private String clazz;  // 'class' is a reserved word in Java

    // Getters and setters
    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getDisplayText(DataManager dataManager, BuildContext context){
        return String.format("Boarders - amount: %s-%s, class: %s", min, max, clazz);
    }
}

