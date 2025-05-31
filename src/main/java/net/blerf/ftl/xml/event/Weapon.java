package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.xml.WeaponBlueprint;

@XmlRootElement(name = "weapon")
@XmlAccessorType(XmlAccessType.FIELD)
public class Weapon extends AbstractBuildableTreeNode {
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
        WeaponBlueprint weaponBlueprint = dataManager.getWeapon(name);

        if (weaponBlueprint != null){
            return String.format("Weapon: %s", weaponBlueprint.getTitle().getTextValue());
        }
        return String.format("Weapon: %s", name);
    }
}
