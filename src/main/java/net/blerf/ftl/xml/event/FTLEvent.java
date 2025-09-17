package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.*;
import net.blerf.ftl.parser.DataManager;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlRootElement( name = "event" )
@XmlAccessorType( XmlAccessType.FIELD )
public class FTLEvent extends AbstractFTLEventNode {
	@XmlAttribute( name = "name", required = false )
	private String id;

	@XmlAttribute(name = "unique")
	private boolean unique;

	@XmlAttribute(name = "load")
	private String load;

	@XmlElement(name = "text")
	private FTLText text;

	@XmlElement(name = "ship")
	private ShipEvent ship;

	@XmlElement(name = "item_modify", required = false)
	private ItemModify itemModify;

	@XmlElement(name = "modifyPursuit")
	private ModifyPursuit modifyPursuit;

	@XmlElement(name = "reveal_map")
	private RevealMap revealMap;

	@XmlElement(name = "distressBeacon")
	private DistressBeacon distressBeacon;

	@XmlElement(name = "autoReward")
	private AutoReward autoReward;

	@XmlElement(name = "environment")
	private Environment environment;

	@XmlElement(name = "damage")
	private List<Damage> damages;

	@XmlElement(name = "choice", required = false)
	private List<Choice> choices = new ArrayList<>();

	@XmlElement(name = "crewMember" )
	private List<CrewMember> crewMembers;

	@XmlElement(name = "removeCrew")
	private RemoveCrew removeCrew;

	@XmlElement(name = "boarders")
	private Boarders boarders;

	@XmlElement(name = "remove")
	private Remove remove;

	@XmlElement(name = "secretSector")
	private SecretSector secretSector;

	@XmlElement(name = "store")
	private Store store;

	@XmlElement(name = "weapon")
	private Weapon weapon;

	@XmlElement(name = "drone")
	private Drone drone;

	@XmlElement(name = "augment")
	private Augment augment;

	@XmlElement(name = "upgrade")
	private Upgrade upgrade;

	@XmlElement(name = "status")
	private List<Status> statusList;

	@XmlElement(name = "fleet")
	private Fleet fleet;

	@XmlElement(name = "img")
	private Img img;

	@XmlElement(name = "repair")
	private Repair repair;

	@XmlElement(name = "unlockShip")
	private UnlockShip unlockShip;

	public UnlockShip getUnlockShip() {
		return unlockShip;
	}

	public void setUnlockShip(UnlockShip unlockShip) {
		this.unlockShip = unlockShip;
	}

	public Repair getRepair() {
		return repair;
	}

	public void setRepair(Repair repair) {
		this.repair = repair;
	}

	public Img getImg() {
		return img;
	}

	public void setImg(Img img) {
		this.img = img;
	}

	public Fleet getFleet() {
		return fleet;
	}

	public void setFleet(Fleet fleet) {
		this.fleet = fleet;
	}

	public List<Status> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<Status> statusList) {
		this.statusList = statusList;
	}

	public Boarders getBoarders() {
		return boarders;
	}

	public void setBoarders(Boarders boarders) {
		this.boarders = boarders;
	}

	public Remove getRemove() {
		return remove;
	}

	public void setRemove(Remove remove) {
		this.remove = remove;
	}

	public SecretSector getSecretSector() {
		return secretSector;
	}

	public void setSecretSector(SecretSector secretSector) {
		this.secretSector = secretSector;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	public Drone getDrone() {
		return drone;
	}

	public void setDrone(Drone drone) {
		this.drone = drone;
	}

	public Augment getAugment() {
		return augment;
	}

	public void setAugment(Augment augment) {
		this.augment = augment;
	}

	public Upgrade getUpgrade() {
		return upgrade;
	}

	public void setUpgrade(Upgrade upgrade) {
		this.upgrade = upgrade;
	}

	public RemoveCrew getRemoveCrew() {
		return removeCrew;
	}

	public void setRemoveCrew(RemoveCrew removeCrew) {
		this.removeCrew = removeCrew;
	}

	public List<CrewMember> getCrewMembers() {
		return crewMembers;
	}

	public void setCrewMembers(List<CrewMember> crewMembers) {
		this.crewMembers = crewMembers;
	}

	public DistressBeacon getDistressBeacon() {
		return distressBeacon;
	}

	public void setDistressBeacon(DistressBeacon distressBeacon) {
		this.distressBeacon = distressBeacon;
	}

	public List<Damage> getDamages() {
		return damages;
	}

	public void setDamages(List<Damage> damages) {
		this.damages = damages;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public AutoReward getAutoReward() {
		return autoReward;
	}

	public void setAutoReward(AutoReward autoReward) {
		this.autoReward = autoReward;
	}

	public String getLoad() {
		return load;
	}

	public void setLoad(String load) {
		this.load = load;
	}

	public String getId() {
		if (id == null){
			return "";
		}
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public FTLText getText() {
		if (text == null){
			FTLText text = new FTLText();
			text.setId("Empty Event");
			return text;
		}
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

	public List<Choice> getChoices() {
		return choices;
	}

	public void setChoices(List<Choice> choices) {
		this.choices = choices;
	}

	public ItemModify getItemModify() {
		return itemModify;
	}

	public void setItemModify(ItemModify itemModify) {
		this.itemModify = itemModify;
	}

	public ModifyPursuit getModifyPursuit() {
		return modifyPursuit;
	}

	public void setModifyPursuit(ModifyPursuit modifyPursuit) {
		this.modifyPursuit = modifyPursuit;
	}

	public RevealMap getRevealMap() {
		return revealMap;
	}

	public void setRevealMap(RevealMap revealMap) {
		this.revealMap = revealMap;
	}


	// There some proxy-events that only have the load attribute and just reference an actual event
	// So we resolve to the actual event, note this is not recoursive, because we don't need it
	@Override
	public FTLEventNode resolve(Map<String, FTLEventNode> allEvents){
		if (load != null && !load.isEmpty()){
			FTLEventNode e = allEvents.getOrDefault(load, this);
			return e;
		}
		return this;
	}

	@Override
	public String toString() {
		return ""+id;
	}

	@Override
	public String getDisplayText(DataManager dataManager,  BuildContext context){
		StringBuilder sb = new StringBuilder("<html><b>")
				.append("Event ")
				.append(id != null ? id : "")
				.append("</b> ")
				.append(text != null ? context.getTextForId(dataManager, text.getId()) : "")
				.append(" File: ")
				.append(getSourceFile())
				.append("</html>");
		return sb.toString();
	}

	@Override
	public DefaultMutableTreeNode build(DataManager dataManager,  BuildContext context) {
		FTLEventNode resolved = this.resolve(context.getAllEvents());

		if (this.equals(resolved)){
			return super.build(dataManager, context);
		}
		return resolved.build(dataManager, context);
	}

}
