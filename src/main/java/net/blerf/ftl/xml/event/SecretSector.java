package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import net.blerf.ftl.parser.DataManager;

@XmlRootElement(name = "secretSector")
@XmlAccessorType(XmlAccessType.FIELD)
public class SecretSector extends AbstractBuildableTreeNode {
    @Override
    public String getDisplayText(DataManager dataManager, BuildContext context){
        return "Secret Sector";
    }
}
