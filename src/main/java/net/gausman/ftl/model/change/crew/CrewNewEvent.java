package net.gausman.ftl.model.change.crew;


import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Crew;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

public class CrewNewEvent extends CrewEvent {
    private Crew crew;

    public CrewNewEvent(){};

    public CrewNewEvent(Jump jump) {
        super(Constants.EventDetailType.CREW_NEW, jump);
    }

    public Crew getCrew() {
        return crew;
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
        setDisplayText(String.format("%s - %s", crew.getCrewType().name(), crew.getName()));
    }

    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);
        int mult = apply ? 1 : -1;

        if (getCrew() == null){
            log.error("New crew event without Crew");
            return;
        }

        if (!(getTags().contains(Constants.EventTag.START) ||
                getTags().contains(Constants.EventTag.BUY) ||
                getTags().contains(Constants.EventTag.REWARD))){
            log.error("New crew event without START, BUY, REWARD Tag");
            return;
        }

        if (apply){
            model.getCrewList().add(new Crew(getCrew()));
        } else {
            model.getCrewList().remove(getCrew());
        }

        model.getSectorMetrics().update(
                getJump().getSector(),
                Constants.ScrapUsedCategory.CREW,
                mult*-getResourceEffects().getOrDefault(Constants.Resource.SCRAP,0)
        );
    }
}
