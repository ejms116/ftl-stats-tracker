package net.gausman.ftl.model;

import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.SavedGameParser.SystemType;
import net.gausman.ftl.model.change.*;
import net.gausman.ftl.model.change.crew.*;
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
    private final List<Crew> deadCrewList;
    private final SectorMetrics sectorMetrics;

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
        deadCrewList = new ArrayList<>();
        sectorMetrics = new SectorMetrics();

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
        deadCrewList = new ArrayList<>(status.deadCrewList.stream().map(Crew::new).toList());

        sectorMetrics = new SectorMetrics(status.getSectorMetrics());
    }

//    private void addScrapGained(int key, Constants.ScrapOrigin origin, int amount){
//        Map<Constants.ScrapOrigin, Integer> innerMap = scrapGained.computeIfAbsent(key,
//                k -> createDefaultScrapGainedMap());
//        innerMap.put(origin, innerMap.get(origin) + amount);
//    }

    private void applyValueEffects(Event event){
        for (ValueEffect effect : event.getValueEffects()){
            resources.put(effect.getResource(), resources.getOrDefault(effect.getResource(), 0) + effect.getValue());
        }
    }

    public void apply(Event event, boolean apply) {
        event.applyEventToShipStatusModel(this, apply);
    }

//    public void apply2(Event event, boolean apply){
//        int mult = apply ? 1 : -1;
//
//        // Update current scrap
////        resources.compute(Constants.Resource.SCRAP, (k,v) -> v + mult * event.getScrapChange());
//
//        // updates scrap, hull, fuel, missiles, drone-part values
//        // Does not update scrap collected stat!
//        applyValueEffects(event);
//
//        switch (event.getItemType()){
//            case GENERAL -> {
//                GeneralEvent ge = (GeneralEvent) event;
//
//                switch (ge.getGeneral()){
//                    case SHIP_NAME, SHIP_BLUEPRINT, DIFFICULTY -> {
//                        if (apply){
//                            generalInfoString.put(ge.getGeneral(), ge.getText());
//                        }
//                    }
//                    case BEACONS_EXPLORED, SHIPS_DESTROYED, CREW_HIRED -> {
//                        generalInfoInteger.compute(ge.getGeneral(), (k,v) -> v + mult * event.getAmount());
//                    }
//                    case SCRAP_COLLECTED -> {
//                        if (!event.getEventType().equals(Constants.EventType.START)){
//                            generalInfoInteger.compute(ge.getGeneral(), (k,v) -> v + mult * event.getScrap());
//                            sectorMetrics.update(event.getJump().getSector(), Constants.ScrapOrigin.NORMAL, mult*event.getScrap());
//                        }
//                    }
//                }
//            }
//
//            case SYSTEM -> {
//                SystemType type = SystemType.findById(event.getText());
//                systems.compute(type, (k,v) -> v + mult * event.getAmount());
//                if (!event.getEventType().equals(Constants.EventType.START)){
//                    sectorMetrics.update(
//                            event.getJump().getSector(),
//                            event.getEventType().equals(Constants.EventType.BUY) ? Constants.ScrapUsedCategory.SYSTEM_BUY : Constants.ScrapUsedCategory.SYSTEM_UPGRADE,
//                            mult*event.getScrap());
//                }
//
//            }
//
//            case RESOURCE -> {
//                Constants.Resource resource = Constants.Resource.valueOf(event.getText());
//                if (resources.containsKey(resource)){
//                    switch (event.getEventType()){
//                        case START, BUY, REWARD -> resources.compute(resource, (k,v) -> v + mult * event.getAmount());
//                        case USE, DAMAGE, SELL -> resources.compute(resource, (k,v) -> v - mult * event.getAmount());
//                        default -> log.info("Resource Event with Type not implemented: " + event.getEventType());
//                        // TODO implement Trading for events
//                    }
//                }else {
//                    log.info("Resource not found");
//                }
//                if (event.getEventType().equals(Constants.EventType.BUY)){
//                    Constants.ScrapUsedCategory cat = switch (resource) {
//                        case FUEL    -> Constants.ScrapUsedCategory.FUEL;
//                        case MISSILE -> Constants.ScrapUsedCategory.MISSILES;
//                        case DRONE   -> Constants.ScrapUsedCategory.DRONE_PARTS;
//                        case HULL ->  Constants.ScrapUsedCategory.REPAIR;
//                        default      -> null;
//                    };
//
//                    if (cat == null) {
//                        break;
//                    }
//
//                    sectorMetrics.update(
//                            event.getJump().getSector(),
//                            cat,
//                            mult*event.getScrap()
//                    );
//
//                }
//
//            }
//
//            case REACTOR -> {
//                Constants.Reactor r = Constants.Reactor.valueOf(event.getText());
//                switch (event.getEventType()){
//                    case START, UPGRADE -> reactor.compute(r, (k,v) -> v + mult * event.getAmount());
//                    default -> log.info("Reactor Event with Type not implemented: " + event.getEventType());
//                }
//                sectorMetrics.update(
//                        event.getJump().getSector(),
//                        Constants.ScrapUsedCategory.REACTOR,
//                        mult*event.getScrap()
//                );
//            }
//
//            case WEAPON, DRONE, AUGMENT -> {
//                switch (event.getEventType()){
//                    case BUY, START, REWARD -> {
//                        if (apply){
//                            itemList.add(new Item(event.getText(), event.getItemType(), event.getEventType()));
//                        } else {
//                            boolean removed = removeMatchingItem(itemList, event.getText(), event.getItemType(), event.getEventType(), Constants.ItemState.INVENTORY);
//                            if (!removed){
//                                log.info("Item could not be removed from list.");
//                            }
//                        }
//                        if (event.getEventType().equals(Constants.EventType.REWARD)){
//                            sectorMetrics.update(event.getJump().getSector(), Constants.ScrapOrigin.FREE, mult*event.getScrap()/2);
//                        }
//                        if (!event.getEventType().equals(Constants.EventType.BUY)){
//                            break;
//                        }
//                        Constants.ScrapUsedCategory cat = switch (event.getItemType()) {
//                            case WEAPON    -> Constants.ScrapUsedCategory.WEAPONS;
//                            case DRONE-> Constants.ScrapUsedCategory.DRONES;
//                            case AUGMENT  -> Constants.ScrapUsedCategory.AUGMENTS;
//                            default      -> null;
//                        };
//
//                        if (cat == null){
//                            break;
//                        }
//                        sectorMetrics.update(
//                                event.getJump().getSector(),
//                                cat,
//                                mult*event.getScrap()
//                        );
//                    }
//                    case SELL, DISCARD -> {
//                        boolean stateChanged;
//                        if (apply){
//                            stateChanged = setStateMatchingItem(
//                                    itemList,
//                                    event.getText(),
//                                    event.getItemType(),
//                                    event.getEventType(),
//                                    Constants.ItemState.INVENTORY,
//                                    convertEventTypeToItemState(event.getEventType()));
//
//                        } else {
//                            stateChanged = setStateMatchingItem(
//                                    itemList,
//                                    event.getText(),
//                                    event.getItemType(),
//                                    event.getEventType(),
//                                    convertEventTypeToItemState(event.getEventType()),
//                                    Constants.ItemState.INVENTORY);
//                        }
//                        if (!stateChanged){
//                            log.info("Item state could not be changed.");
//                        }
//                    }
//                    default -> log.info("Item Event with Type not implemented: " + event.getEventType());
//                }
//
//            }
//
//            case CREW -> {
//                // the crew in the Event is always the Crew after the event is applied
//                // (except for EventType DISCARD, there we use the state before because the Crew is DEAD
//                CrewEvent ce = (CrewEvent) event;
//                if (ce.getCrewPosition() == null || ce.getCrewPosition() >= crewList.size() || ce.getCrewPosition() < 0){
//                    log.error("Crew index to change out of list");
//                    break;
//                }
//                switch (event.getEventType()){
//                    case START, BUY, REWARD -> {
//                        if (!(event instanceof NewCrewEvent nce)){
//                            log.info("START, BUY, REWARD Event without Crew");
//                            return;
//                        }
//                        if (apply){
//                            crewList.add(new Crew(nce.getCrew()));
//                        } else {
//                            crewList.remove(nce.getCrew());
//                        }
//
//                        if (!event.getEventType().equals(Constants.EventType.BUY)){
//                            break;
//                        }
//
//                        sectorMetrics.update(
//                                event.getJump().getSector(),
//                                Constants.ScrapUsedCategory.CREW,
//                                mult*event.getScrap()
//                        );
//                    }
//
//                    case DISCARD -> {
//                        if (apply){
//                            Optional<Crew> removedCrew = removeCrewIfPresent(ce.getCrewPosition(), crewList);
//                            removedCrew.ifPresentOrElse(
//                                    crew -> {
//                                        crew.setState(Constants.CrewAliveOrDead.DEAD);
//                                        deadCrewList.add(crew);
//                                    },
//                                    () -> log.info("Can't find crew to DISCARD.")
//                            );
//
//                        } else {
//                            Optional<Crew> removedDeadCrew = removeCrewIfPresent(deadCrewList.size() - 1, deadCrewList);
//                            removedDeadCrew.ifPresentOrElse(
//                                    crew -> {
//                                        crew.setState(Constants.CrewAliveOrDead.ALIVE);
//                                        crewList.add(ce.getCrewPosition(), crew);
//                                    },
//                                    () -> log.info("Can't find DISCARDED Crew to revert.")
//                            );
//
//                        }
//                    }
//
//                    case NAME -> {
//                        if (!(event instanceof CrewRenameEvent crewRenameEvent)){
//                            log.info("NAME Event issue");
//                            return;
//                        }
//                        Crew crewToChange = crewList.get(crewRenameEvent.getCrewPosition());
//                        if (apply){
//                            crewToChange.setName(crewRenameEvent.getNewName());
//                        } else {
//                            crewToChange.setName(crewRenameEvent.getOldName());
//                        }
//                    }
//
//                    case STAT -> {
//                        if (!(event instanceof StatEvent statEvent)){
//                            log.info("STAT Event issue");
//                            return;
//                        }
//
//                        String statString = convertStatToAttributename(statEvent.getStat());
//                        Crew crewToChange = crewList.get(statEvent.getCrewPosition());
//                        int attributeValueBefore = (int) getValueInCrewByAttributename(crewToChange, statString);
//
//                        if (apply){
//                            setValueInCrewByAttributename(crewToChange, statString, attributeValueBefore + statEvent.getAmount());
//                        } else {
//                            setValueInCrewByAttributename(crewToChange, statString, attributeValueBefore);
//                        }
//
//                    }
//
//                    case SKILL -> {
//                        if (!(event instanceof SkillEvent skillEvent)){
//                            log.info("SKILL Event issue");
//                            return;
//                        }
//
//                        String skillString = convertSkillToAttributename(skillEvent.getSkill());
//                        Crew crewToChange = crewList.get(skillEvent.getCrewPosition());
//                        int attributeValueBefore = (int) getValueInCrewByAttributename(crewToChange, skillString);
//
//                        if (apply){
//                            setValueInCrewByAttributename(crewToChange, skillString, attributeValueBefore + skillEvent.getAmount());
//                        } else {
//                            setValueInCrewByAttributename(crewToChange, skillString, attributeValueBefore);
//                        }
//                    }
//
//                    case MASTERY -> {
//                        if (!(event instanceof MasteryEvent masteryEvent)){
//                            log.info("MASTERY Event issue");
//                            return;
//                        }
//
//                        String masteryString = convertMasteryToAttributename(masteryEvent.getMastery(), masteryEvent.getLevel());
//                        Crew crewToChange = crewList.get(masteryEvent.getCrewPosition());
//
//                        if (apply){
//                            setValueInCrewByAttributename(crewToChange, masteryString, masteryEvent.getNewValue());
//                        } else {
//                            setValueInCrewByAttributename(crewToChange, masteryString, !masteryEvent.getNewValue());
//                        }
//                    }
//
//                    default -> log.info("Crew Event with Type not implemented: " + event.getEventType());
//
//                }
//
//            }
//
//            default -> log.info("Apply not implemented for ItemType: "+ event.getItemType());
//        }
//    }

    public Optional<Crew> removeCrewIfPresent(int index, List<Crew> list) {
        if (index < 0 || index >= list.size()) {
            return Optional.empty();
        }
        return Optional.of(list.remove(index));
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

    private Map<Constants.ScrapOrigin, Integer> createDefaultScrapGainedMap(){
        Map<Constants.ScrapOrigin, Integer> map = new EnumMap<>(Constants.ScrapOrigin.class);

        for (Constants.ScrapOrigin origin : Constants.ScrapOrigin.values()){
            map.put(origin, 0);
        }

        return map;
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

    public List<Crew> getDeadCrewList() {
        return deadCrewList;
    }

    public SectorMetrics getSectorMetrics() {
        return sectorMetrics;
    }
}
