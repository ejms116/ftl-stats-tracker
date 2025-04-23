package net.gausman.ftl.model;

import net.blerf.ftl.parser.SavedGameParser.SystemType;
import net.gausman.ftl.model.record.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Map;

public class ShipStatusModel {
    private static final Logger log = LoggerFactory.getLogger(ShipStatusModel.class);
    private final Map<SystemType, Integer> subSystems = new EnumMap<>(SystemType.class);
    private final Map<SystemType, Integer> systems = new EnumMap<>(SystemType.class);

    public ShipStatusModel(){
        for (SystemType type : SystemType.values()){
            if (type.isSubsystem()){
                subSystems.put(type, 0);
            } else {
                systems.put(type, 0);
            }
        }
    }

    public ShipStatusModel(ShipStatusModel status){
        subSystems.putAll(status.subSystems);
        systems.putAll(status.systems);
    }

    public void apply(Event event, boolean apply){
        int mult = apply ? 1 : -1;

        switch (event.getItemType()){
            case SYSTEM -> {
                SystemType type = SystemType.findById(event.getText());
                if (systems.containsKey(type)){
                    systems.compute(type, (k,v) -> v + mult * event.getAmount());
                }
                if (subSystems.containsKey(type)){
                    subSystems.compute(type, (k,v) -> v + mult * event.getAmount());
                }
                log.info("SYSTEM");
            }

            default -> log.info("Apply not implemented for ItemType: "+ event.getItemType());
        }
    }

    public Map<SystemType, Integer> getSubSystems() {
        return subSystems;
    }

    public Map<SystemType, Integer> getSystems() {
        return systems;
    }
}
