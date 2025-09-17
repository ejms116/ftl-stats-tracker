package net.gausman.ftl.model;

import net.blerf.ftl.parser.SavedGameParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Crew {
    private String name;
    // Todo Maybe list of previous names?
    private SavedGameParser.CrewType crewType;
    private Constants.EventType origin;
    private Constants.CrewAliveOrDead state;

    private boolean male;
    private final List<Integer> spriteTintIndeces = new ArrayList<Integer>();

    private int repairs = 0;
    private int combatKills = 0;
    private int pilotedEvasions = 0;
    private int jumpsSurvived = 0;
    private int skillMasteriesEarned = 0;

    private int pilotSkill = 0, engineSkill = 0, shieldSkill = 0;
    private int weaponSkill = 0, repairSkill = 0, combatSkill = 0;
    private boolean pilotMasteryOne = false, pilotMasteryTwo = false;
    private boolean engineMasteryOne = false, engineMasteryTwo = false;
    private boolean shieldMasteryOne = false, shieldMasteryTwo = false;
    private boolean weaponMasteryOne = false, weaponMasteryTwo = false;
    private boolean repairMasteryOne = false, repairMasteryTwo = false;
    private boolean combatMasteryOne = false, combatMasteryTwo = false;

    public Crew(){};

    public Crew(String name, SavedGameParser.CrewType crewType, Constants.EventType origin, boolean male) {
        this.name = name;
        this.crewType = crewType;
        this.origin = origin;
        this.state = Constants.CrewAliveOrDead.ALIVE;
        this.male = male;
    }

    // Deep copy
    public Crew(Crew other){
        this.name = other.name;
        this.crewType = other.crewType;
        this.origin = other.origin;
        this.state = other.state;
        this.male = other.male;
        this.spriteTintIndeces.addAll(other.spriteTintIndeces);
        this.repairs = other.repairs;
        this.combatKills = other.combatKills;
        this.pilotedEvasions = other.pilotedEvasions;
        this.jumpsSurvived = other.jumpsSurvived;
        this.skillMasteriesEarned = other.skillMasteriesEarned;
        this.pilotSkill = other.pilotSkill;
        this.engineSkill = other.engineSkill;
        this.shieldSkill = other.shieldSkill;
        this.weaponSkill = other.weaponSkill;
        this.repairSkill = other.repairSkill;
        this.combatSkill = other.combatSkill;
        this.pilotMasteryOne = other.pilotMasteryOne;
        this.pilotMasteryTwo = other.pilotMasteryTwo;
        this.engineMasteryOne = other.engineMasteryOne;
        this.engineMasteryTwo = other.engineMasteryTwo;
        this.shieldMasteryOne = other.shieldMasteryOne;
        this.shieldMasteryTwo = other.shieldMasteryTwo;
        this.weaponMasteryOne = other.weaponMasteryOne;
        this.weaponMasteryTwo = other.weaponMasteryTwo;
        this.repairMasteryOne = other.repairMasteryOne;
        this.repairMasteryTwo = other.repairMasteryTwo;
        this.combatMasteryOne = other.combatMasteryOne;
        this.combatMasteryTwo = other.combatMasteryTwo;
    }

    public Crew(SavedGameParser.CrewState crewState, Constants.EventType origin){
        this.name = crewState.getName();
        this.crewType = crewState.getRace();
        this.origin = origin;
        this.state = Constants.CrewAliveOrDead.ALIVE;
        this.male = crewState.isMale();
        this.spriteTintIndeces.addAll(crewState.getSpriteTintIndeces());
        this.repairs = crewState.getRepairs();
        this.combatKills = crewState.getCombatKills();
        this.pilotedEvasions = crewState.getPilotedEvasions();
        this.jumpsSurvived = crewState.getJumpsSurvived();
        this.skillMasteriesEarned = crewState.getSkillMasteriesEarned();
        this.pilotSkill = crewState.getPilotSkill();
        this.engineSkill = crewState.getEngineSkill();
        this.shieldSkill = crewState.getShieldSkill();
        this.weaponSkill = crewState.getWeaponSkill();
        this.repairSkill = crewState.getRepairSkill();
        this.combatSkill = crewState.getCombatSkill();
        this.pilotMasteryOne = crewState.getPilotMasteryOne();
        this.pilotMasteryTwo = crewState.getPilotMasteryTwo();
        this.engineMasteryOne = crewState.getEngineMasteryOne();
        this.engineMasteryTwo = crewState.getEngineMasteryTwo();
        this.shieldMasteryOne = crewState.getShieldMasteryOne();
        this.shieldMasteryTwo = crewState.getShieldMasteryTwo();
        this.weaponMasteryOne = crewState.getWeaponMasteryOne();
        this.weaponMasteryTwo = crewState.getWeaponMasteryTwo();
        this.repairMasteryOne = crewState.getRepairMasteryOne();
        this.repairMasteryTwo = crewState.getRepairMasteryTwo();
        this.combatMasteryOne = crewState.getCombatMasteryOne();
        this.combatMasteryTwo = crewState.getCombatMasteryTwo();
    }

    public boolean equalsWithoutOrigin(Object o, Constants.CrewAliveOrDead cs) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crew crew = (Crew) o;
        return male == crew.male && repairs == crew.repairs && combatKills == crew.combatKills && pilotedEvasions == crew.pilotedEvasions && jumpsSurvived == crew.jumpsSurvived && skillMasteriesEarned == crew.skillMasteriesEarned && pilotSkill == crew.pilotSkill && engineSkill == crew.engineSkill && shieldSkill == crew.shieldSkill && weaponSkill == crew.weaponSkill && repairSkill == crew.repairSkill && combatSkill == crew.combatSkill && pilotMasteryOne == crew.pilotMasteryOne && pilotMasteryTwo == crew.pilotMasteryTwo && engineMasteryOne == crew.engineMasteryOne && engineMasteryTwo == crew.engineMasteryTwo && shieldMasteryOne == crew.shieldMasteryOne && shieldMasteryTwo == crew.shieldMasteryTwo && weaponMasteryOne == crew.weaponMasteryOne && weaponMasteryTwo == crew.weaponMasteryTwo && repairMasteryOne == crew.repairMasteryOne && repairMasteryTwo == crew.repairMasteryTwo && combatMasteryOne == crew.combatMasteryOne && combatMasteryTwo == crew.combatMasteryTwo && Objects.equals(name, crew.name) && crewType == crew.crewType && state == cs && Objects.equals(spriteTintIndeces, crew.spriteTintIndeces);
//        return male == crew.male && crewType == crew.crewType && state == cs && Objects.equals(spriteTintIndeces, crew.spriteTintIndeces);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crew crew = (Crew) o;
        return male == crew.male && repairs == crew.repairs && combatKills == crew.combatKills && pilotedEvasions == crew.pilotedEvasions && jumpsSurvived == crew.jumpsSurvived && skillMasteriesEarned == crew.skillMasteriesEarned && pilotSkill == crew.pilotSkill && engineSkill == crew.engineSkill && shieldSkill == crew.shieldSkill && weaponSkill == crew.weaponSkill && repairSkill == crew.repairSkill && combatSkill == crew.combatSkill && pilotMasteryOne == crew.pilotMasteryOne && pilotMasteryTwo == crew.pilotMasteryTwo && engineMasteryOne == crew.engineMasteryOne && engineMasteryTwo == crew.engineMasteryTwo && shieldMasteryOne == crew.shieldMasteryOne && shieldMasteryTwo == crew.shieldMasteryTwo && weaponMasteryOne == crew.weaponMasteryOne && weaponMasteryTwo == crew.weaponMasteryTwo && repairMasteryOne == crew.repairMasteryOne && repairMasteryTwo == crew.repairMasteryTwo && combatMasteryOne == crew.combatMasteryOne && combatMasteryTwo == crew.combatMasteryTwo && Objects.equals(name, crew.name) && crewType == crew.crewType && origin == crew.origin && state == crew.state && Objects.equals(spriteTintIndeces, crew.spriteTintIndeces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, crewType, origin, state, male, spriteTintIndeces, repairs, combatKills, pilotedEvasions, jumpsSurvived, skillMasteriesEarned, pilotSkill, engineSkill, shieldSkill, weaponSkill, repairSkill, combatSkill, pilotMasteryOne, pilotMasteryTwo, engineMasteryOne, engineMasteryTwo, shieldMasteryOne, shieldMasteryTwo, weaponMasteryOne, weaponMasteryTwo, repairMasteryOne, repairMasteryTwo, combatMasteryOne, combatMasteryTwo);
    }

    public void setState(Constants.CrewAliveOrDead state) {
        this.state = state;
    }

    public Constants.EventType getOrigin() {
        return origin;
    }

    public Constants.CrewAliveOrDead getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SavedGameParser.CrewType getCrewType() {
        return crewType;
    }

    public boolean isMale() {
        return male;
    }

    public List<Integer> getSpriteTintIndeces() {
        return spriteTintIndeces;
    }

    public int getRepairs() {
        return repairs;
    }

    public void setRepairs(int repairs) {
        this.repairs = repairs;
    }

    public int getCombatKills() {
        return combatKills;
    }

    public void setCombatKills(int combatKills) {
        this.combatKills = combatKills;
    }

    public int getPilotedEvasions() {
        return pilotedEvasions;
    }

    public void setPilotedEvasions(int pilotedEvasions) {
        this.pilotedEvasions = pilotedEvasions;
    }

    public int getJumpsSurvived() {
        return jumpsSurvived;
    }

    public void setJumpsSurvived(int jumpsSurvived) {
        this.jumpsSurvived = jumpsSurvived;
    }

    public int getSkillMasteriesEarned() {
        return skillMasteriesEarned;
    }

    public void setSkillMasteriesEarned(int skillMasteriesEarned) {
        this.skillMasteriesEarned = skillMasteriesEarned;
    }

    public int getPilotSkill() {
        return pilotSkill;
    }

    public void setPilotSkill(int pilotSkill) {
        this.pilotSkill = pilotSkill;
    }

    public int getEngineSkill() {
        return engineSkill;
    }

    public void setEngineSkill(int engineSkill) {
        this.engineSkill = engineSkill;
    }

    public int getShieldSkill() {
        return shieldSkill;
    }

    public void setShieldSkill(int shieldSkill) {
        this.shieldSkill = shieldSkill;
    }

    public int getWeaponSkill() {
        return weaponSkill;
    }

    public void setWeaponSkill(int weaponSkill) {
        this.weaponSkill = weaponSkill;
    }

    public int getRepairSkill() {
        return repairSkill;
    }

    public void setRepairSkill(int repairSkill) {
        this.repairSkill = repairSkill;
    }

    public int getCombatSkill() {
        return combatSkill;
    }

    public void setCombatSkill(int combatSkill) {
        this.combatSkill = combatSkill;
    }

    public boolean isPilotMasteryOne() {
        return pilotMasteryOne;
    }

    public void setPilotMasteryOne(boolean pilotMasteryOne) {
        this.pilotMasteryOne = pilotMasteryOne;
    }

    public boolean isPilotMasteryTwo() {
        return pilotMasteryTwo;
    }

    public void setPilotMasteryTwo(boolean pilotMasteryTwo) {
        this.pilotMasteryTwo = pilotMasteryTwo;
    }

    public boolean isEngineMasteryOne() {
        return engineMasteryOne;
    }

    public void setEngineMasteryOne(boolean engineMasteryOne) {
        this.engineMasteryOne = engineMasteryOne;
    }

    public boolean isEngineMasteryTwo() {
        return engineMasteryTwo;
    }

    public void setEngineMasteryTwo(boolean engineMasteryTwo) {
        this.engineMasteryTwo = engineMasteryTwo;
    }

    public boolean isShieldMasteryOne() {
        return shieldMasteryOne;
    }

    public void setShieldMasteryOne(boolean shieldMasteryOne) {
        this.shieldMasteryOne = shieldMasteryOne;
    }

    public boolean isShieldMasteryTwo() {
        return shieldMasteryTwo;
    }

    public void setShieldMasteryTwo(boolean shieldMasteryTwo) {
        this.shieldMasteryTwo = shieldMasteryTwo;
    }

    public boolean isWeaponMasteryOne() {
        return weaponMasteryOne;
    }

    public void setWeaponMasteryOne(boolean weaponMasteryOne) {
        this.weaponMasteryOne = weaponMasteryOne;
    }

    public boolean isWeaponMasteryTwo() {
        return weaponMasteryTwo;
    }

    public void setWeaponMasteryTwo(boolean weaponMasteryTwo) {
        this.weaponMasteryTwo = weaponMasteryTwo;
    }

    public boolean isRepairMasteryOne() {
        return repairMasteryOne;
    }

    public void setRepairMasteryOne(boolean repairMasteryOne) {
        this.repairMasteryOne = repairMasteryOne;
    }

    public boolean isRepairMasteryTwo() {
        return repairMasteryTwo;
    }

    public void setRepairMasteryTwo(boolean repairMasteryTwo) {
        this.repairMasteryTwo = repairMasteryTwo;
    }

    public boolean isCombatMasteryOne() {
        return combatMasteryOne;
    }

    public void setCombatMasteryOne(boolean combatMasteryOne) {
        this.combatMasteryOne = combatMasteryOne;
    }

    public boolean isCombatMasteryTwo() {
        return combatMasteryTwo;
    }

    public void setCombatMasteryTwo(boolean combatMasteryTwo) {
        this.combatMasteryTwo = combatMasteryTwo;
    }

    @Override
    public String toString() {
        return "Crew {" +
                "\n  name='" + name + '\'' +
                ",\n  crewType=" + crewType +
                ", origin=" + origin +
                ", state=" + state +
                ", male=" + male +
                ", spriteTintIndices=" + spriteTintIndeces +
                "\n\n  === Stats ===" +
                "\n  repairs=" + repairs +
                ", combatKills=" + combatKills +
                ", pilotedEvasions=" + pilotedEvasions +
                ", jumpsSurvived=" + jumpsSurvived +
                ", skillMasteriesEarned=" + skillMasteriesEarned +
                "\n\n  === Skills ===" +
                "\n  pilotSkill=" + pilotSkill +
                ", engineSkill=" + engineSkill +
                ", shieldSkill=" + shieldSkill +
                ", weaponSkill=" + weaponSkill +
                ", repairSkill=" + repairSkill +
                ", combatSkill=" + combatSkill +
                "\n\n  === Masteries ===" +
                "\n  pilotMasteryOne=" + pilotMasteryOne +
                ", pilotMasteryTwo=" + pilotMasteryTwo +
                ", engineMasteryOne=" + engineMasteryOne +
                ", engineMasteryTwo=" + engineMasteryTwo +
                ", shieldMasteryOne=" + shieldMasteryOne +
                ", shieldMasteryTwo=" + shieldMasteryTwo +
                ", weaponMasteryOne=" + weaponMasteryOne +
                ", weaponMasteryTwo=" + weaponMasteryTwo +
                ", repairMasteryOne=" + repairMasteryOne +
                ", repairMasteryTwo=" + repairMasteryTwo +
                ", combatMasteryOne=" + combatMasteryOne +
                ", combatMasteryTwo=" + combatMasteryTwo +
                "\n}";
    }


}
