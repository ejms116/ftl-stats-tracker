package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.*;
import net.blerf.ftl.parser.DataManager;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

@XmlRootElement(name = "surrender")
@XmlAccessorType(XmlAccessType.FIELD)
public class Surrender extends AbstractBuildableTreeNode {
    @XmlAttribute(name = "chance")
    private float chance;

    @XmlAttribute(name = "min")
    private int min;

    @XmlAttribute(name = "max")
    private int max;

    @XmlAttribute(name = "load")
    private String load;

    @XmlElement(name = "choice")
    List<Choice> choices;

    @XmlElement(name = "ship")
    ShipEvent ship;

    @XmlElement(name = "autoReward")
    private AutoReward autoReward;

    @XmlElement(name = "crewMember" )
    private List<CrewMember> crewMembers;

    @XmlElement(name = "text")
    private FTLText text;

    public FTLText getText() {
        return text;
    }

    public void setText(FTLText text) {
        this.text = text;
    }

    public List<CrewMember> getCrewMembers() {
        return crewMembers;
    }

    public void setCrewMembers(List<CrewMember> crewMembers) {
        this.crewMembers = crewMembers;
    }

    public AutoReward getAutoReward() {
        return autoReward;
    }

    public void setAutoReward(AutoReward autoReward) {
        this.autoReward = autoReward;
    }

    public ShipEvent getShip() {
        return ship;
    }

    public void setShip(ShipEvent ship) {
        this.ship = ship;
    }

    public float getChance() {
        return chance;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    @Override
    public String getDisplayText(DataManager dataManager, BuildContext context){
        StringBuilder sb = new StringBuilder("<html><b>")
                .append("Surrender")
                .append("</b> ")
                .append(text != null ? dataManager.getTextForId(text.getId()) : "")
                .append("</html>");
        return sb.toString();
    }

    @Override
    public DefaultMutableTreeNode build(DataManager dataManager, BuildContext context){
        DefaultMutableTreeNode node = super.build(dataManager, context);

        String treeChildText = String.format("Chance: %s, Value: %s-%s", chance, min, max);
        DefaultMutableTreeNode treeChild = new DefaultMutableTreeNode(treeChildText);
        node.insert(treeChild, 0);

        if (load != null){
            FTLEventNode eventNode = context.getAllEvents().get(load);
            if (eventNode != null){
                DefaultMutableTreeNode treeNode = eventNode.build(dataManager, context);
                node.add(treeNode);
            }

        }
        return node;
    }
}
