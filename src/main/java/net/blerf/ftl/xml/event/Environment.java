package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.*;
import net.blerf.ftl.parser.DataManager;

@XmlRootElement(name = "environment")
@XmlAccessorType(XmlAccessType.FIELD)
public class Environment extends AbstractBuildableTreeNode {
    public enum Type {
        sun,
        asteroid,
        nebula,
        pulsar,
        storm,
        PDS
    }

    public enum Target {
        player,
        enemy,
        all
    }


    @XmlAttribute(name = "type")
    private Type type;

    @XmlElement(name = "target", required = false)
    private Target target;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public String getDisplayText(DataManager dataManager){
        String lineEnvironment = String.format("Environment: %s", type);
        if (type.equals(Environment.Type.PDS) && target != null){
            lineEnvironment += ", Target: " + target;
        }
        return lineEnvironment;
    }
}
