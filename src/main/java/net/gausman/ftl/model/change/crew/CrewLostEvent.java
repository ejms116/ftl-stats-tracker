package net.gausman.ftl.model.change.crew;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Jump;

public class CrewLostEvent extends CrewEvent {
     public CrewLostEvent(Jump jump){
         super(Constants.EventDetailType.CREW_LOST, jump);
     }
}
