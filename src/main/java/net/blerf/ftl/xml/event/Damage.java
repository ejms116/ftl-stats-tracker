package net.blerf.ftl.xml.event;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import net.blerf.ftl.parser.DataManager;

@XmlRootElement(name = "damage")
@XmlAccessorType(XmlAccessType.FIELD)
public class Damage extends AbstractBuildableTreeNode {
    @XmlAttribute(name = "amount")
    private int amount;

    @XmlAttribute(name = "system")
    private String system;

    @XmlAttribute(name = "effect")
    private String effect;

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

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    @Override
    public String getDisplayText(DataManager dataManager, BuildContext context){
        return String.format("Damage: %s, System: %s, Effect: %s", amount, system, effect);
    }
}
