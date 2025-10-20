package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.*;
import net.blerf.ftl.parser.DataManager;

@XmlRootElement(name = "removeCrew")
@XmlAccessorType(XmlAccessType.FIELD)
public class RemoveCrew extends AbstractBuildableTreeNode {

    @XmlElement(name = "clone")
    private boolean clone;

    @XmlElement(name = "text")
    private FTLText text;

    public boolean isClone() {
        return clone;
    }

    public void setClone(boolean clone) {
        this.clone = clone;
    }

    public FTLText getText() {
        return text;
    }

    public void setText(FTLText text) {
        this.text = text;
    }

    @Override
    public String getDisplayText(DataManager dataManager, BuildContext context){
        return String.format("Crew loss, cloneable: %s", isClone());
    }
}
