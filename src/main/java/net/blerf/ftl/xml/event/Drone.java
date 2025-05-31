package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.xml.DroneBlueprint;

@XmlRootElement(name = "drone")
@XmlAccessorType(XmlAccessType.FIELD)
public class Drone extends AbstractBuildableTreeNode {
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
        DroneBlueprint droneBlueprint = dataManager.getDrone(name);

        if (droneBlueprint != null){
            return String.format("Drone: %s", droneBlueprint.getTitle().getTextValue());
        }
        return String.format("Drone: %s", name);
    }
}
