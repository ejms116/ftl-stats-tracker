package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import net.blerf.ftl.parser.DataManager;

@XmlRootElement(name = "reveal_map")
@XmlAccessorType(XmlAccessType.FIELD)
public class RevealMap extends AbstractBuildableTreeNode {
    @Override
    public String getDisplayText(DataManager dataManager, BuildContext context){
        return "Map reveal";
    }
}
