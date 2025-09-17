package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.*;

import java.util.List;

@XmlRootElement( name = "event" )
@XmlAccessorType( XmlAccessType.FIELD )
public class WeaponOverride extends AbstractBuildableTreeNode {
    @XmlElement(name = "name")
    private List<String> names;

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }
}
