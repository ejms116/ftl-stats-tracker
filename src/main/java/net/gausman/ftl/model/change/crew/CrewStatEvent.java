package net.gausman.ftl.model.change.crew;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Crew;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

public class CrewStatEvent extends CrewEvent {
    private Constants.Stats stat;
    private int amount;

    public CrewStatEvent(){};

    public CrewStatEvent(Jump jump, Constants.Stats stat, int amount){
        super(Constants.EventDetailType.CREW_STAT, jump);
        addTag(Constants.EventTag.STAT);
        this.stat = stat;
        this.amount = amount;
    }

    public Constants.Stats getStat() {
        return stat;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);

        String statString = GausmanUtil.convertStatToAttributename(getStat());
        if (getCrewPosition() == null){
            log.info("Stat event: crew position null");
            return;
        }

        if (getCrewPosition() >= model.getCrewList().size() || getCrewPosition() < 0){
            log.error(String.format("Stat event: crew position %s out of bounds %s", getCrewPosition(), model.getCrewList().size()));
            return;
        }

        Crew crewToChange = model.getCrewList().get(getCrewPosition());

        if (crewToChange == null){
            log.error("Crew for crew stat event not found. Position: " + getCrewPosition());
            return;
        }

        int attributeValueBefore = (int) model.getValueInCrewByAttributename(crewToChange, statString);

        if (apply){
            model.setValueInCrewByAttributename(crewToChange, statString, attributeValueBefore + getAmount());
        } else {
            model.setValueInCrewByAttributename(crewToChange, statString, attributeValueBefore);
        }
    }
}
