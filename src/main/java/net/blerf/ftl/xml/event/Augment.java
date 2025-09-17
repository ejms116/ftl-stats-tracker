package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.xml.AugBlueprint;

@XmlRootElement(name = "augment")
@XmlAccessorType(XmlAccessType.FIELD)
public class Augment extends AbstractBuildableTreeNode {
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
        AugBlueprint augBlueprint = dataManager.getAugment(name);

        if (augBlueprint != null){
            return String.format("Augment: %s", augBlueprint.getTitle().getTextValue());
        }
        return String.format("Augment: %s", name);
    }
}
