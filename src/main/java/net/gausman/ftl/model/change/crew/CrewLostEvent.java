package net.gausman.ftl.model.change.crew;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Crew;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.record.Jump;

import java.util.Optional;

public class CrewLostEvent extends CrewEvent {
     public CrewLostEvent(Jump jump){
         super(Constants.EventDetailType.CREW_LOST, jump);
     }

    // the crew in the Event is always the Crew after the event is applied
    // (except for EventType DISCARD, there we use the state before because the Crew is DEAD
    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);

        if (getCrewPosition() == null){
            log.info("Crew lost event: crew position null");
            return;
        }

        if (apply){
            Optional<Crew> removedCrew = model.removeCrewIfPresent(getCrewPosition(), model.getCrewList());
            removedCrew.ifPresentOrElse(
                    crew -> {
                        crew.setState(Constants.CrewAliveOrDead.DEAD);
                        model.getDeadCrewList().add(crew);
                    },
                    () -> log.error("Can't find crew to DISCARD.")
            );

        } else {
            Optional<Crew> removedDeadCrew = model.removeCrewIfPresent(model.getDeadCrewList().size() - 1, model.getDeadCrewList());
            removedDeadCrew.ifPresentOrElse(
                    crew -> {
                        crew.setState(Constants.CrewAliveOrDead.ALIVE);
                        model.getCrewList().add(getCrewPosition(), crew);
                    },
                    () -> log.error("Can't find DISCARDED Crew to revert.")
            );

        }

    }
}
