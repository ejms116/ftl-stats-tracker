package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import net.blerf.ftl.parser.DataManager;

@XmlRootElement(name = "remove")
@XmlAccessorType(XmlAccessType.FIELD)
public class Remove extends AbstractBuildableTreeNode {
    @XmlAttribute(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDisplayText(DataManager dataManager, BuildContext context){
        return String.format("Remove item: %s", name);
    }
}

