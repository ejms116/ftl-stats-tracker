package net.gausman.ftl.model.change.crew;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Crew;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

import java.util.Objects;

public class CrewEvent extends Event {
    // this refers to the crew position in our ship status NOT the position in the save file
    private String crewId;

    public CrewEvent(){}

    public CrewEvent(Constants.EventDetailType eventDetailType, Jump jump){
        super(eventDetailType, jump);
    }

    public String getCrewId() {
        return crewId;
    }

    public void setCrewId(String crewId) {
        this.crewId = crewId;
    }

    // When getting new crew the mastery values are not correct
    // because of that we set them manually based on the skill values which are correct
    protected void fixMasteryBooleans(Crew crew){
        // Piloting
        int pilotMastery = crew.getPilotSkill() / GausmanUtil.getFtlConstants().getMasteryIntervalPilot(crew.getCrewType());
        if (pilotMastery >= 2){
            crew.setPilotMasteryTwo(true);
        }
        if (pilotMastery >= 1){
            crew.setPilotMasteryOne(true);
        }

        // Engines
        int engineMastery = crew.getEngineSkill() / GausmanUtil.getFtlConstants().getMasteryIntervalEngine(crew.getCrewType());
        if (engineMastery >= 2){
            crew.setEngineMasteryOne(true);
        }
        if (engineMastery >= 1){
            crew.setEngineMasteryOne(true);
        }

        // Shield
        int shieldMastery = crew.getShieldSkill() / GausmanUtil.getFtlConstants().getMasteryIntervalShield(crew.getCrewType());
        if (shieldMastery >= 2){
            crew.setShieldMasteryOne(true);
        }
        if (shieldMastery >= 1){
            crew.setShieldMasteryOne(true);
        }

        // Weapon
        int weaponMastery = crew.getWeaponSkill() / GausmanUtil.getFtlConstants().getMasteryIntervalWeapon(crew.getCrewType());
        if (weaponMastery >= 2){
            crew.setWeaponMasteryOne(true);
        }
        if (weaponMastery >= 1){
            crew.setWeaponMasteryOne(true);
        }

        // Repairs
        int repairMastery = crew.getRepairSkill() / GausmanUtil.getFtlConstants().getMasteryIntervalRepair(crew.getCrewType());
        if (repairMastery >= 2){
            crew.setRepairMasteryOne(true);
        }
        if (repairMastery >= 1){
            crew.setRepairMasteryOne(true);
        }

        // Combats
        int combatMastery = crew.getCombatSkill() / GausmanUtil.getFtlConstants().getMasteryIntervalCombat(crew.getCrewType());
        if (combatMastery >= 2){
            crew.setCombatMasteryOne(true);
        }
        if (combatMastery >= 1){
            crew.setCombatMasteryOne(true);
        }
    }
}
