package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.*;
import net.blerf.ftl.parser.DataManager;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

@XmlRootElement(name = "escape")
@XmlAccessorType(XmlAccessType.FIELD)
public class Escape extends AbstractBuildableTreeNode {
    @XmlAttribute(name = "chance")
    private Float chance;

    @XmlAttribute(name = "load")
    private String load;

    @XmlAttribute(name = "timer")
    private int timer;

    @XmlAttribute(name = "min")
    private int min;

    @XmlAttribute(name = "max")
    private int max;

    @XmlElement(name = "text")
    private FTLText text;

    @XmlElement(name = "ship")
    private ShipEvent ship;

    @XmlElement(name = "choice")
    List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public Float getChance() {
        if (chance == null){
            return 1.0F;
        }
        return chance;
    }

    public void setChance(Float chance) {
        this.chance = chance;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
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

    public FTLText getText() {
        return text;
    }

    public void setText(FTLText text) {
        this.text = text;
    }

    public ShipEvent getShip() {
        return ship;
    }

    public void setShip(ShipEvent ship) {
        this.ship = ship;
    }

    @Override
    public String getDisplayText(DataManager dataManager, BuildContext context){
        StringBuilder sb = new StringBuilder("<html><b>")
                .append("Escape")
                .append("</b> ")
                .append(text != null ? context.getTextForId(dataManager, text.getId()) : "")
                .append("</html>");
        return sb.toString();
    }

    @Override
    public DefaultMutableTreeNode build(DataManager dataManager, BuildContext context){
        DefaultMutableTreeNode node = super.build(dataManager, context);

        String treeChildText = String.format("Timer: %s, Value: %s-%s, Chance: %s", timer, min, max, getChance());
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
