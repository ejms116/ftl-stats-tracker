package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.*;
import net.blerf.ftl.parser.DataManager;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Map;


@XmlRootElement( name = "ship" )
@XmlAccessorType( XmlAccessType.FIELD )
public class ShipEvent extends AbstractBuildableTreeNode {

	@XmlAttribute( name = "name" )
	private String id;

	@XmlAttribute( name = "auto_blueprint" )
	private String autoBlueprintId;

	@XmlAttribute( name = "load")
	private String load;

	@XmlAttribute( name = "hostile")
	private boolean hostile;

	@XmlElement(name = "destroyed")
	private Destroyed destroyed;

	@XmlElement(name = "deadCrew")
	private DeadCrew deadCrew;

	@XmlElement(name = "gotaway")
	private Gotaway gotaway;

	@XmlElement(name = "surrender")
	private Surrender surrender;

	@XmlElement(name = "escape")
	private Escape escape;

	@XmlElement(name = "crew")
	private Crew crew;

	@XmlElement(name = "unlockShip")
	private UnlockShip unlockShip;

	@XmlElement(name = "weaponOverride")
	private WeaponOverride weaponOverride;

	public WeaponOverride getWeaponOverride() {
		return weaponOverride;
	}

	public void setWeaponOverride(WeaponOverride weaponOverride) {
		this.weaponOverride = weaponOverride;
	}

	public UnlockShip getUnlockShip() {
		return unlockShip;
	}

	public void setUnlockShip(UnlockShip unlockShip) {
		this.unlockShip = unlockShip;
	}

	public Crew getCrew() {
		return crew;
	}

	public void setCrew(Crew crew) {
		this.crew = crew;
	}

	public Destroyed getDestroyed() {
		return destroyed;
	}

	public void setDestroyed(Destroyed destroyed) {
		this.destroyed = destroyed;
	}

	public DeadCrew getDeadCrew() {
		return deadCrew;
	}

	public void setDeadCrew(DeadCrew deadCrew) {
		this.deadCrew = deadCrew;
	}

	public Gotaway getGotaway() {
		return gotaway;
	}

	public void setGotaway(Gotaway gotaway) {
		this.gotaway = gotaway;
	}

	public Surrender getSurrender() {
		return surrender;
	}

	public void setSurrender(Surrender surrender) {
		this.surrender = surrender;
	}

	public Escape getEscape() {
		return escape;
	}

	public void setEscape(Escape escape) {
		this.escape = escape;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAutoBlueprintId() {
		return autoBlueprintId;
	}

	public void setAutoBlueprintId( String autoBlueprintId ) {
		this.autoBlueprintId = autoBlueprintId;
	}

	public String getLoad() {
		return load;
	}

	public void setLoad(String load) {
		this.load = load;
	}

	public boolean isHostile() {
		return hostile;
	}

	public void setHostile(boolean hostile) {
		this.hostile = hostile;
	}

	public ShipEvent resolve(Map<String, ShipEvent> allShipEvents){
		if (load != null && !load.isEmpty()){
			ShipEvent se = allShipEvents.getOrDefault(load, this);
			return se;
		}
		return this;
	}

	@Override
	public String toString() {
		return ""+id;
	}

	@Override
	public String getDisplayText(DataManager dataManager, BuildContext context){
		StringBuilder sb = new StringBuilder("<html><b>")
				.append("Ship ")
				.append(id != null ? id : "")
				.append("</b>")
//				.append(" - Hostile: ")
//				.append(hostile)
				.append(" File: ")
				.append(getSourceFile())
				.append("</html>");
		return sb.toString();
	}

	@Override
	public DefaultMutableTreeNode build(DataManager dataManager, BuildContext context) {
		ShipEvent resolved = this.resolve(context.getShipEventMap());
		resolved.setHostile(this.isHostile());



		if (this.equals(resolved)){
			DefaultMutableTreeNode node =  super.build(dataManager, context);
			String treeChildText = String.format("Hostile: %s", hostile);
			DefaultMutableTreeNode treeChild = new DefaultMutableTreeNode(treeChildText);
			node.insert(treeChild, 0);
			return node;
		}
		return resolved.build(dataManager, context);
	}
}
