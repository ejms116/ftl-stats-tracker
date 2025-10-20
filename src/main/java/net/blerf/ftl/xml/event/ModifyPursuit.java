package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import net.blerf.ftl.parser.DataManager;

@XmlRootElement(name = "modifyPursuit")
@XmlAccessorType(XmlAccessType.FIELD)
public class ModifyPursuit extends AbstractBuildableTreeNode {
    @XmlAttribute(name = "amount")
    private Integer amount;


    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public String getDisplayText(DataManager dataManager, BuildContext context){
        return String.format("Modify Pursuit: %s", amount);
    }
}
