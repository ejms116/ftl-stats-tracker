package net.gausman.ftl.model;

import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.SavedGameParser.SystemType;
import net.gausman.ftl.model.record.*;
import net.gausman.ftl.util.CrewMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

import static net.gausman.ftl.util.GausmanUtil.*;

public class ShipStatusModel {
    private static final Logger log = LoggerFactory.getLogger(ShipStatusModel.class);

    private final Map<Constants.General, String> generalInfoString = new HashMap<>();
    private final Map<Constants.General, Integer> generalInfoInteger = new HashMap<>();

    private final Map<SystemType, Integer> systems = new EnumMap<>(SystemType.class);

    private final Map<Constants.Reactor, Integer> reactor = new EnumMap<>(Constants.Reactor.class);
    private final Map<Constants.Resource, Integer> resources = new EnumMap<>(Constants.Resource.class);

    private final List<Item> itemList;
    private final List<Crew> crewList;

    // New ShipStatus
    public ShipStatusModel(){
        // General Run Info
        generalInfoString.put(Constants.General.SHIP_NAME, "");
        generalInfoString.put(Constants.General.SHIP_BLUEPRINT, "");
        generalInfoString.put(Constants.General.DIFFICULTY, "");

        generalInfoInteger.put(Constants.General.BEACONS_EXPLORED, 0);
        generalInfoInteger.put(Constants.General.SHIPS_DESTROYED, 0);
        generalInfoInteger.put(Constants.General.SCRAP_COLLECTED, 0);
        generalInfoInteger.put(Constants.General.CREW_HIRED, 0);

        // Init Resources
        for (Constants.Resource resource : Constants.Resource.values()){
            resources.put(resource, 0);
        }

        // Init Systems and subsystems
        for (SystemType type : SystemType.values()){
            systems.put(type, 0);
        }

        for (Constants.Reactor r : Constants.Reactor.values()){
            reactor.put(r, 0);
        }

        itemList = new ArrayList<>();
        crewList = new ArrayList<>();

    }

    // Duplicate Shipstatus (to modify)
    public ShipStatusModel(ShipStatusModel status){
        generalInfoString.putAll(status.generalInfoString);
        generalInfoInteger.putAll(status.generalInfoInteger);

        resources.putAll(status.resources);
        systems.putAll(status.systems);
        reactor.putAll(status.reactor);


        itemList = new ArrayList<>(status.itemList.stream().map(Item::new).toList());
        crewList = new ArrayList<>(status.crewList.stream().map(Crew::new).toList());

    }

    public void apply(Event event, boolean apply){
        int mult = apply ? 1 : -1;

        // Update current scrap
        resources.compute(Constants.Resource.SCRAP, (k,v) -> v + mult * event.getScrapChange());

        switch (event.getItemType()){
            case GENERAL -> {
                GeneralEvent ge = (GeneralEvent) event;

                switch (ge.getGeneral()){
                    case SHIP_NAME, SHIP_BLUEPRINT, DIFFICULTY -> {
                        if (apply){
                            generalInfoString.put(ge.getGeneral(), ge.getText());
                        }
                    }
                    case BEACONS_EXPLORED, SHIPS_DESTROYED, CREW_HIRED -> {
                        generalInfoInteger.compute(ge.getGeneral(), (k,v) -> v + mult * event.getAmount());
                    }
                    case SCRAP_COLLECTED -> {
                        if (!event.getEventType().equals(Constants.EventType.START)){
                            generalInfoInteger.compute(ge.getGeneral(), (k,v) -> v + mult * event.getScrap());
                        }
                    }
                }
            }

            case SYSTEM -> {
                SystemType type = SystemType.findById(event.getText());
                systems.compute(type, (k,v) -> v + mult * event.getAmount());
            }

            case RESOURCE -> {
                Constants.Resource resource = Constants.Resource.valueOf(event.getText());
                if (resources.containsKey(resource)){
                    switch (event.getEventType()){
                        case START, BUY, REWARD -> resources.compute(resource, (k,v) -> v + mult * event.getAmount());
                        case USE, DAMAGE, SELL -> resources.compute(resource, (k,v) -> v - mult * event.getAmount());
                        default -> log.info("Resource Event with Type not implemented: " + event.getEventType());
                        // TODO implement Trading for events
                    }
                }else {
                    log.info("Resource not found");
                }
            }

            case REACTOR -> {
                Constants.Reactor r = Constants.Reactor.valueOf(event.getText());
                switch (event.getEventType()){
                    case START, UPGRADE -> reactor.compute(r, (k,v) -> v + mult * event.getAmount());
                    default -> log.info("Reactor Event with Type not implemented: " + event.getEventType());
                }
            }

            case WEAPON, DRONE, AUGMENT -> {
                switch (event.getEventType()){
                    case BUY, START, REWARD -> {
                        if (apply){
                            itemList.add(new Item(event.getText(), event.getItemType(), event.getEventType()));
                        } else {
                            boolean removed = removeMatchingItem(itemList, event.getText(), event.getItemType(), event.getEventType(), Constants.ItemState.INVENTORY);
                            if (!removed){
                                log.info("Item could not be removed from list.");
                            }
                        }
                    }
                    case SELL, DISCARD -> {
                        boolean stateChanged;
                        if (apply){
                            stateChanged = setStateMatchingItem(
                                    itemList,
                                    event.getText(),
                                    event.getItemType(),
                                    event.getEventType(),
                                    Constants.ItemState.INVENTORY,
                                    convertEventTypeToItemState(event.getEventType()));

                        } else {
                            stateChanged = setStateMatchingItem(
                                    itemList,
                                    event.getText(),
                                    event.getItemType(),
                                    event.getEventType(),
                                    convertEventTypeToItemState(event.getEventType()),
                                    Constants.ItemState.INVENTORY);
                        }
                        if (!stateChanged){
                            log.info("Item state could not be changed.");
                        }
                    }
                    default -> log.info("Item Event with Type not implemented: " + event.getEventType());
                }

            }

            case CREW -> {
                // the crew in the Event is always the Crew after the event is applied
                // (except for EventType DISCARD, there we use the state before because the Crew is DEAD
                CrewEvent ce = (CrewEvent) event;
                switch (event.getEventType()){
                    case START, BUY, REWARD -> {
                        if (apply){
                            crewList.add(new Crew(ce.getCrew()));
                        } else {
                            crewList.remove(ce.getCrew());
                        }
                    }
                    case DISCARD -> {
                        if (apply){
                            Crew c = findCrew(crewList, ce.getCrew(), Constants.CrewAliveOrDead.ALIVE);
                            if (c != null){
                                c.setState(Constants.CrewAliveOrDead.DEAD);
                            } else {
                                log.info("Can't find crew to DISCARD.");
                            }

                        } else {
                            Crew c = findCrew(crewList, ce.getCrew(), Constants.CrewAliveOrDead.DEAD);
                            if (c != null){
                                c.setState(Constants.CrewAliveOrDead.ALIVE);
                            } else {
                                log.info("Can't find DISCARDED Crew to revert.");
                            }
                        }
                    }

                    case NAME -> {
                        // Find crew with the same values AND the old name and change the name to the new one
                        List<String> fieldsToCompare = new ArrayList<>();
                        Map<String, Object> fieldOverrides = new HashMap<>();
                        initFieldToCompareList(fieldsToCompare);
                        fieldsToCompare.remove("name");

                        NameEvent ne = (NameEvent) event;

                        if (apply){
                            fieldOverrides.put("name", ne.getOldName());
                        } else {
                            fieldOverrides.put("name", ne.getNewName());
                        }

                        Crew match = CrewMatcher.findMatchingCrew(
                                crewList,
                                ne.getCrew(),
                                fieldsToCompare,
                                fieldOverrides
                        );

                        if (match != null){
                            if (apply){
                                match.setName(ne.getNewName());
                            } else {
                                match.setName(ne.getOldName());
                            }

                        } else {
                            String errorRename = String.format("Crew rename failed. Old: %s, New: %s", ne.getOldName(), ne.getNewName());
                            log.error(errorRename);
                        }


                    }

                    case STAT -> {
                        List<String> fieldsToCompare = new ArrayList<>();
                        Map<String, Object> fieldOverrides = new HashMap<>();
                        initFieldToCompareList(fieldsToCompare);

                        StatEvent ne = (StatEvent) event;

                        String statString = convertStatToAttributename(ne.getStat());
                        fieldsToCompare.remove(statString);

                        int attributeValueBefore = (int) getValueInCrewByAttributename(ne.getCrew(), statString);

                        // the Crew inside the event has the event already applied to
                        // so when going forward (apply = true) we need to subtract the amount in the event first
                        // when going backwards the value in the Crew should be right
                        if (apply){
                            fieldOverrides.put(statString, attributeValueBefore);
                        } else {
                            fieldOverrides.put(statString, attributeValueBefore + ne.getAmount());
                        }

                        Crew match = CrewMatcher.findMatchingCrew(
                                crewList,
                                ne.getCrew(),
                                fieldsToCompare,
                                fieldOverrides
                        );

                        if (match != null){
                            if (apply){
                                setValueInCrewByAttributename(match, statString, attributeValueBefore + ne.getAmount());
                            } else {
                                setValueInCrewByAttributename(match, statString, attributeValueBefore);
                            }

                        } else {
                            String errorStatChange = String.format("Stat change failed. %s", ne.getStat());
                            log.error(errorStatChange);
                        }
                    }

                    case SKILL -> {
                        List<String> fieldsToCompare = new ArrayList<>();
                        Map<String, Object> fieldOverrides = new HashMap<>();
                        initFieldToCompareList(fieldsToCompare);

                        SkillEvent se = (SkillEvent) event;

                        String skillString = convertSkillToAttributename(se.getSkill());
                        fieldsToCompare.remove(skillString);

                        int attributeValueBefore = (int) getValueInCrewByAttributename(se.getCrew(), skillString);

                        // the Crew inside the event has the event already applied to
                        // so when going forward (apply = true) we need to subtract the amount in the event first
                        // when going backwards the value in the Crew should be right
                        if (apply){
                            fieldOverrides.put(skillString, attributeValueBefore);
                        } else {
                            fieldOverrides.put(skillString, attributeValueBefore + se.getAmount());
                        }

                        Crew match = CrewMatcher.findMatchingCrew(
                                crewList,
                                se.getCrew(),
                                fieldsToCompare,
                                fieldOverrides
                        );

                        if (match != null){
                            if (apply){
                                setValueInCrewByAttributename(match, skillString, attributeValueBefore + se.getAmount());
                            } else {
                                setValueInCrewByAttributename(match, skillString, attributeValueBefore);
                            }

                        } else {
                            String errorStatChange = String.format("Skill change failed. %s", se.getSkill());
                            log.error(errorStatChange);
                        }
                    }

                    case MASTERY -> {
                        List<String> fieldsToCompare = new ArrayList<>();
                        Map<String, Object> fieldOverrides = new HashMap<>();
                        initFieldToCompareList(fieldsToCompare);

                        MasteryEvent se = (MasteryEvent) event;

                        String masteryString = convertMasteryToAttributename(se.getMastery(), se.getLevel());
                        fieldsToCompare.remove(masteryString);

                        boolean attributeValueBefore = (boolean) getValueInCrewByAttributename(se.getCrew(), masteryString);

                        // the Crew inside the event has the event already applied to
                        // so when going forward (apply = true) we need to subtract the amount in the event first
                        // when going backwards the value in the Crew should be right
                        if (apply){
                            fieldOverrides.put(masteryString, attributeValueBefore);
                        } else {
                            fieldOverrides.put(masteryString, !attributeValueBefore);
                        }

                        Crew match = CrewMatcher.findMatchingCrew(
                                crewList,
                                se.getCrew(),
                                fieldsToCompare,
                                fieldOverrides
                        );

                        if (match != null){
                            if (apply){
                                setValueInCrewByAttributename(match, masteryString, se.getNewValue());
                            } else {
                                setValueInCrewByAttributename(match, masteryString, !se.getNewValue());
                            }

                        } else {
                            String errorStatChange = String.format("Skill change failed. %s", se.getMastery());
                            log.error(errorStatChange);
                        }
                    }

                    default -> log.info("Crew Event with Type not implemented: " + event.getEventType());
                }

            }

            default -> log.info("Apply not implemented for ItemType: "+ event.getItemType());
        }
    }

    private Object getValueInCrewByAttributename(Crew crew, String attributename){
        try {
            Field field = crew.getClass().getDeclaredField(attributename);
            field.setAccessible(true);
            return field.get(crew);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Could not set field '" + attributename + "'", e);
        }
    }

    private void setValueInCrewByAttributename(Crew crew, String attributename, Object newValue){
        try {
            Field field = crew.getClass().getDeclaredField(attributename);
            field.setAccessible(true);
            field.set(crew, newValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Could not set field '" + attributename + "'", e);
        }
    }

    private Crew findCrew(List<Crew> list, Crew toFind, Constants.CrewAliveOrDead cs){
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).equalsWithoutOrigin(toFind, cs)){
                return list.get(i);
            }
        }
        return null;
    }

    private Constants.ItemState convertEventTypeToItemState(Constants.EventType eventType){
        return switch (eventType){
            case SELL -> Constants.ItemState.SOLD;
            case DISCARD -> Constants.ItemState.DISCARDED;
            case REWARD, UPGRADE, BUY, START, USE, MASTERY, SKILL, STAT, NAME, DAMAGE, GENERAL -> null;
        };
    }

    private boolean setStateMatchingItem(List<Item> itemList, String id, SavedGameParser.StoreItemType itemType, Constants.EventType origin, Constants.ItemState itemState, Constants.ItemState newState) {
        for (int i = itemList.size() - 1; i >= 0; i--) {
            Item item = itemList.get(i);
            if (item.getId().equals(id) &&
                    item.getItemType() == itemType &&
                    item.getState() == itemState) {
                itemList.get(i).setState(newState);
                return true;
            }
        }
        return false;
    }

    private boolean removeMatchingItem(List<Item> itemList, String id, SavedGameParser.StoreItemType itemType, Constants.EventType origin, Constants.ItemState itemState) {
        for (int i = itemList.size() - 1; i >= 0; i--) {
            Item item = itemList.get(i);
            if (item.getId().equals(id) &&
                    item.getItemType() == itemType &&
                    item.getOrigin() == origin &&
                    item.getState() == itemState) {
                itemList.remove(i);
                return true; // Remove only the first match from the back
            }
        }
        return false;
    }

    public Map<Constants.General, String> getGeneralInfoString() {
        return generalInfoString;
    }

    public Map<Constants.General, Integer> getGeneralInfoInteger() {
        return generalInfoInteger;
    }

    public Map<Constants.Resource, Integer> getResources() {
        return resources;
    }

    public Map<SystemType, Integer> getSystems() {
        return systems;
    }

    public Map<Constants.Reactor, Integer> getReactor() {
        return reactor;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public List<Crew> getCrewList() {
        return crewList;
    }



    private void initFieldToCompareList(List<String> fieldsToCompare){
        fieldsToCompare.add("crewType");
//        fieldsToCompare.add("origin");
        fieldsToCompare.add("name");
        fieldsToCompare.add("state");
        fieldsToCompare.add("male");
        fieldsToCompare.add("spriteTintIndeces");
        fieldsToCompare.add("repairs");
        fieldsToCompare.add("combatKills");
        fieldsToCompare.add("pilotedEvasions");
        fieldsToCompare.add("jumpsSurvived");
        fieldsToCompare.add("skillMasteriesEarned");
        fieldsToCompare.add("pilotSkill");
        fieldsToCompare.add("engineSkill");
        fieldsToCompare.add("shieldSkill");
        fieldsToCompare.add("weaponSkill");
        fieldsToCompare.add("repairSkill");
        fieldsToCompare.add("combatSkill");
        fieldsToCompare.add("pilotMasteryOne");
        fieldsToCompare.add("pilotMasteryTwo");
        fieldsToCompare.add("engineMasteryOne");
        fieldsToCompare.add("engineMasteryTwo");
        fieldsToCompare.add("shieldMasteryOne");
        fieldsToCompare.add("shieldMasteryTwo");
        fieldsToCompare.add("weaponMasteryOne");
        fieldsToCompare.add("weaponMasteryTwo");
        fieldsToCompare.add("repairMasteryOne");
        fieldsToCompare.add("repairMasteryTwo");
        fieldsToCompare.add("combatMasteryOne");
        fieldsToCompare.add("combatMasteryTwo");
    }
}
