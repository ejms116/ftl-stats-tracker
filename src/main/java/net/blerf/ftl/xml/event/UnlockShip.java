package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.*;
import net.blerf.ftl.parser.DataManager;

@XmlRootElement(name = "unlockShip")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnlockShip extends AbstractBuildableTreeNode {
    @XmlAttribute(name = "id")
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getDisplayText(DataManager dataManager, BuildContext context){
        return String.format("Ship unlock id: %s", id);
    }
}
