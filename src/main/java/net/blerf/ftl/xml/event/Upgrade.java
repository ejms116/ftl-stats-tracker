package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import net.blerf.ftl.parser.DataManager;

@XmlRootElement(name = "upgrade")
@XmlAccessorType(XmlAccessType.FIELD)
public class Upgrade extends AbstractBuildableTreeNode {
    @XmlAttribute(name = "amount")
    private int amount;

    @XmlAttribute(name = "system")
    private String system;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
    @Override
    public String getDisplayText(DataManager dataManager, BuildContext context){
        return String.format("Upgrade - System: %s, amount: %s", system, amount);
    }
}
