package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.*;
import net.blerf.ftl.parser.DataManager;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

@XmlRootElement(name = "deadCrew")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeadCrew extends AbstractBuildableTreeNode {
    @XmlAttribute(name = "load")
    private String load;

    @XmlElement(name = "text")
    private FTLText text;

    @XmlElement(name = "autoReward")
    private AutoReward autoReward;

    @XmlElement(name = "choice")
    List<Choice> choices;

    @XmlElement(name = "item_modify")
    private ItemModify itemModify;

    @XmlElement(name = "damage")
    private Damage damage;

    @XmlElement(name = "store")
    private Store store;

    @XmlElement(name = "weapon")
    private Weapon weapon;

    @XmlElement(name = "drone")
    private Weapon drone;

    @XmlElement(name = "augment")
    private Weapon augment;

    @XmlElement(name = "status")
    List<Status> statusList;

    @XmlElement(name = "quest")
    private Quest quest;

    @XmlElement(name = "modifyPursuit")
    private ModifyPursuit modifyPursuit;

    @XmlElement(name = "unlockShip")
    private UnlockShip unlockShip;

    public UnlockShip getUnlockShip() {
        return unlockShip;
    }

    public void setUnlockShip(UnlockShip unlockShip) {
        this.unlockShip = unlockShip;
    }

    public ModifyPursuit getModifyPursuit() {
        return modifyPursuit;
    }

    public void setModifyPursuit(ModifyPursuit modifyPursuit) {
        this.modifyPursuit = modifyPursuit;
    }

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    public List<Status> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Status> statusList) {
        this.statusList = statusList;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public Weapon getDrone() {
        return drone;
    }

    public void setDrone(Weapon drone) {
        this.drone = drone;
    }

    public Weapon getAugment() {
        return augment;
    }

    public void setAugment(Weapon augment) {
        this.augment = augment;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Damage getDamage() {
        return damage;
    }

    public void setDamage(Damage damage) {
        this.damage = damage;
    }

    public ItemModify getItemModify() {
        return itemModify;
    }

    public void setItemModify(ItemModify itemModify) {
        this.itemModify = itemModify;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public FTLText getText() {
        return text;
    }

    public void setText(FTLText text) {
        this.text = text;
    }

    public AutoReward getAutoReward() {
        return autoReward;
    }

    public void setAutoReward(AutoReward autoReward) {
        this.autoReward = autoReward;
    }

    @Override
    public String getDisplayText(DataManager dataManager, BuildContext context){
        StringBuilder sb = new StringBuilder("<html><b>")
                .append("DeadCrew")
                .append("</b> ")
                .append(text != null ? context.getTextForId(dataManager, text.getId()) : "")
                .append("</html>");
        return sb.toString();
    }

    @Override
    public DefaultMutableTreeNode build(DataManager dataManager, BuildContext context){
        DefaultMutableTreeNode node = super.build(dataManager, context);

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
