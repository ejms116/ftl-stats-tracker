package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import net.blerf.ftl.parser.DataManager;

import java.lang.reflect.Field;

@XmlRootElement(name = "crewMember")
@XmlAccessorType(XmlAccessType.FIELD)
public class CrewMember extends AbstractBuildableTreeNode {
    @XmlAttribute(name = "amount")
    private Integer amount;

    @XmlAttribute(name = "id")
    private String id;

    @XmlAttribute(name = "weapons")
    private Integer weapons;

    @XmlAttribute(name = "shields")
    private Integer shields;

    @XmlAttribute(name = "pilot")
    private Integer pilot;

    @XmlAttribute(name = "engines")
    private Integer engines;

    @XmlAttribute(name = "combat")
    private Integer combat;

    @XmlAttribute(name = "repair")
    private Integer repair;

    @XmlAttribute(name = "all_skills")
    private Integer allSkills;

    @XmlAttribute(name = "class")
    private String crewClass;

    @XmlAttribute(name = "prop")
    private Float prop;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getWeapons() {
        return weapons;
    }

    public void setWeapons(Integer weapons) {
        this.weapons = weapons;
    }

    public Integer getShields() {
        return shields;
    }

    public void setShields(Integer shields) {
        this.shields = shields;
    }

    public Integer getPilot() {
        return pilot;
    }

    public void setPilot(Integer pilot) {
        this.pilot = pilot;
    }

    public Integer getEngines() {
        return engines;
    }

    public void setEngines(Integer engines) {
        this.engines = engines;
    }

    public Integer getCombat() {
        return combat;
    }

    public void setCombat(Integer combat) {
        this.combat = combat;
    }

    public Integer getRepair() {
        return repair;
    }

    public void setRepair(Integer repair) {
        this.repair = repair;
    }

    public Integer getAllSkills() {
        return allSkills;
    }

    public void setAllSkills(Integer allSkills) {
        this.allSkills = allSkills;
    }

    public String getCrewClass() {
        return crewClass;
    }

    public void setCrewClass(String crewClass) {
        this.crewClass = crewClass;
    }

    public Float getProp() {
        return prop;
    }

    public void setProp(Float prop) {
        this.prop = prop;
    }

    @Override
    public String getDisplayText(DataManager dataManager, BuildContext context){
        StringBuilder result = new StringBuilder("Crew ");

        Class<?> clazz = this.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true); // allows access to private fields
            try {
                Object value = field.get(this);
                if (value != null &&
                        (value instanceof String || value instanceof Integer || value instanceof Float)) {

                    result.append(field.getName())
                            .append("=")
                            .append(value.toString())
                            .append(", ");
                }
            } catch (IllegalAccessException e) {
                // Optional: handle or log
                e.printStackTrace();
            }
        }

        // Remove trailing comma and space if needed
        if (result.length() > 2) {
            result.setLength(result.length() - 2);
        }

        return result.toString();
    }

}
