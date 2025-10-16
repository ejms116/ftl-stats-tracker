package net.gausman.ftl.model.change.crew;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Crew;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

import java.util.Optional;

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
        Optional<Crew> crewToChange = model.getCrewList().stream()
                .filter(c -> c.getReferenceId().equals(getCrewId()))
                .findFirst();

        crewToChange.ifPresent(c -> {
            int attributeValueBefore = (int) model.getValueInCrewByAttributename(c, statString);

            if (apply){
                model.setValueInCrewByAttributename(c, statString, attributeValueBefore + getAmount());
            } else {
                model.setValueInCrewByAttributename(c, statString, attributeValueBefore);
            }
        });


    }
}
