package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import net.blerf.ftl.parser.DataManager;

import java.util.List;

@XmlRootElement(name = "crew")
@XmlAccessorType(XmlAccessType.FIELD)
public class Crew extends AbstractBuildableTreeNode {
    @XmlElement(name = "crewMember")
    private List<CrewMember> crewMembers;

    public List<CrewMember> getCrewMembers() {
        return crewMembers;
    }

    public void setCrewMembers(List<CrewMember> crewMembers) {
        this.crewMembers = crewMembers;
    }

}
