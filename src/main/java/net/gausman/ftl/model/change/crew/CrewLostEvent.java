package net.gausman.ftl.model.change.crew;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Crew;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.record.Jump;

import java.util.Optional;

public class CrewLostEvent extends CrewEvent {
    public CrewLostEvent(){}

     public CrewLostEvent(Jump jump){
         super(Constants.EventDetailType.CREW_LOST, jump);
     }

    // the crew in the Event is always the Crew after the event is applied
    // (except for EventType DISCARD, there we use the state before because the Crew is DEAD
    @Override
    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply) {
        super.applyEventToShipStatusModel(model, apply);

        Optional<Crew> crewToChange = model.getCrewList().stream()
                .filter(c -> c.getReferenceId().equals(getCrewId()))
                .findFirst();

        crewToChange.ifPresent(c -> {
            if (apply){
                model.getCrewList().remove(c);
                model.getDeadCrewList().add(c);
                c.setState(Constants.CrewAliveOrDead.DEAD);

            } else {
                model.getCrewList().add(c);
                model.getDeadCrewList().remove(c);
                c.setState(Constants.CrewAliveOrDead.ALIVE);
            }
        });
    }
}
