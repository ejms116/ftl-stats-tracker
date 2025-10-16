package net.gausman.ftl.model;

import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.SavedGameParser.SystemType;
import net.gausman.ftl.model.change.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

public class ShipStatusModel {
    private static final Logger log = LoggerFactory.getLogger(ShipStatusModel.class);

    private final Map<Constants.General, String> generalInfoString = new HashMap<>();
    private final Map<Constants.General, Integer> generalInfoInteger = new HashMap<>();

    private final Map<SystemType, Integer> systems = new EnumMap<>(SystemType.class);

    private int reactor = 0;
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
        reactor = status.reactor;

        itemList = new ArrayList<>(status.itemList.stream().map(Item::new).toList());
        crewList = new ArrayList<>(status.crewList.stream().map(Crew::new).toList());
        deadCrewList = new ArrayList<>(status.deadCrewList.stream().map(Crew::new).toList());

        sectorMetrics = new SectorMetrics(status.getSectorMetrics());
    }


    public void apply(Event event, boolean apply) {
        event.applyEventToShipStatusModel(this, apply);
    }

    public Optional<Crew> removeCrewIfPresent(int index, List<Crew> list) {
        if (index < 0 || index >= list.size()) {
            return Optional.empty();
        }
        return Optional.of(list.remove(index));
    }

    public Object getValueInCrewByAttributename(Crew crew, String attributename){
        try {
            Field field = crew.getClass().getDeclaredField(attributename);
            field.setAccessible(true);
            return field.get(crew);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Could not set field '" + attributename + "'", e);
        }
    }

    public void setValueInCrewByAttributename(Crew crew, String attributename, Object newValue){
        try {
            Field field = crew.getClass().getDeclaredField(attributename);
            field.setAccessible(true);
            field.set(crew, newValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Could not set field '" + attributename + "'", e);
        }
    }

    public Constants.ItemState convertEventTypeToItemState(Constants.EventTag tag){
        return switch (tag){
            case SELL -> Constants.ItemState.SOLD;
            case DISCARD -> Constants.ItemState.DISCARDED;
            default -> null;
        };
    }

    public boolean setStateMatchingItem(String id, SavedGameParser.StoreItemType itemType, Constants.ItemState itemState, Constants.ItemState newState) {
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

    public boolean removeMatchingItem(String id, SavedGameParser.StoreItemType itemType, Constants.ItemOrigin origin, Constants.ItemState itemState) {
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

    public int getReactor() {
        return reactor;
    }

    public void changeReactor(int delta){
        reactor += delta;
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
