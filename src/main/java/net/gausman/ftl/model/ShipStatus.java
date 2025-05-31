package net.gausman.ftl.model;

import net.gausman.ftl.controller.StatsManager;
import net.gausman.ftl.model.run.FTLRunEvent;
import net.gausman.ftl.util.GausmanUtil;
import net.gausman.ftl.view.SimpleListItem;
import net.gausman.ftl.view.SystemListItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShipStatus {
    private static final Logger log = LoggerFactory.getLogger(StatsManager.class);
    private List<SystemListItem> systemList = new ArrayList<>();
    private List<SystemListItem> subSystemList = new ArrayList<>();
    private List<SimpleListItem> weaponsList = new ArrayList<>();
    private List<SimpleListItem> dronesList = new ArrayList<>();
    private List<SimpleListItem> augmentsList = new ArrayList<>();
    private List<SimpleListItem> crewList = new ArrayList<>();

    private List<SystemListItem> resourceList = new ArrayList<>();



    public ShipStatus(){
        systemList.add(new SystemListItem("Shields", 0));
        systemList.add(new SystemListItem("Engines", 0));
        systemList.add(new SystemListItem("Medbay", 0));
        systemList.add(new SystemListItem("Clone Bay", 0));
        systemList.add(new SystemListItem("Cloaking", 0));
        systemList.add(new SystemListItem("Oxygen", 0));
        systemList.add(new SystemListItem("Weapon Control", 0));
        systemList.add(new SystemListItem("Drone Control", 0));
        systemList.add(new SystemListItem("Hacking", 0));
        systemList.add(new SystemListItem("Mind Control", 0));
        systemList.add(new SystemListItem("Crew Teleporter", 0));
        systemList.add(new SystemListItem("Artillery Beam", 0));


        subSystemList.add(new SystemListItem("Piloting", 0));
        subSystemList.add(new SystemListItem("Sensors", 0));
        subSystemList.add(new SystemListItem("Door System", 0));
        subSystemList.add(new SystemListItem("Backup Battery", 0));

        resourceList.add(new SystemListItem("HULL", 0));
        resourceList.add(new SystemListItem("FUEL", 0));
        resourceList.add(new SystemListItem("MISSILE", 0));
        resourceList.add(new SystemListItem("DRONE_PART", 0));




    }
    public void applyEvent(FTLRunEvent event, boolean apply){
        int multiplier = apply ? 1:-1;
        String name = GausmanUtil.getTextToId(event.getItemType(), event.getId());
        switch (event.getItemType()){
            case SYSTEM -> {
                Optional<SystemListItem> item = systemList.stream().filter(x -> x.getName().equals(GausmanUtil.getTextToId(event.getItemType(), event.getId()))).findFirst();
                if (item.isPresent()){
                    item.get().changeLevel(multiplier*event.getAmount());
                } else {
                    item = subSystemList.stream().filter(x -> x.getName().equals(GausmanUtil.getTextToId(event.getItemType(), event.getId()))).findFirst();
                    if (item.isPresent()){
                        item.get().changeLevel(multiplier*event.getAmount());
                    } else {
                        System.out.println("no item found");
                    }

                }
            }
            case RESOURCE -> {
                Optional<SystemListItem> item = resourceList.stream().filter(x -> x.getName().equals(GausmanUtil.getTextToId(event.getItemType(), event.getId()))).findFirst();
                if (item.isPresent()){
                    switch (event.getType()){
                        case START, BUY -> item.get().changeLevel(multiplier*event.getAmount());
                        case USE -> item.get().changeLevel(-multiplier*event.getAmount());
                        default -> log.info("Resource Event with Type not allowed: " + event.getType());
                    }

                } else {
                    System.out.println("Resource not found");

                }
            }
            case WEAPON -> {
                switch (event.getType()){
                    case START, BUY -> removeOrAddSimpleList(name, weaponsList, apply);
                    case DISCARD, SELL -> removeOrAddSimpleList(name, weaponsList, !apply);
                    default -> log.info("Weapon Event with Type not allowed: " + event.getType());
                }
            }
            case DRONE -> {
                switch (event.getType()){
                    case START, BUY -> removeOrAddSimpleList(name, dronesList, apply);
                    case DISCARD, SELL -> removeOrAddSimpleList(name, dronesList, !apply);
                    default -> log.info("Drone Event with Type not allowed: " + event.getType());
                }
            }
            case AUGMENT -> {
                switch (event.getType()){
                    case START, BUY -> removeOrAddSimpleList(name, augmentsList, apply);
                    case DISCARD, SELL -> removeOrAddSimpleList(name, augmentsList, !apply);
                    default -> log.info("Augment Event with Type not allowed: " + event.getType());
                }
            }
            case CREW -> {
                switch (event.getType()){
                    case START, BUY -> removeOrAddSimpleList(name, crewList, apply);
                    case DISCARD -> removeOrAddSimpleList(name, crewList, !apply);
                    default -> log.info("Crew Event with Type not allowed: " + event.getType());
                }
            }
            default -> log.info("ItemType not found: " + event.getItemType());
        }
    }

    private void removeOrAddSimpleList(String name, List<SimpleListItem> list, boolean apply){
        if (apply){
            list.add(new SimpleListItem(name));
        } else {
            list.remove(list.stream().filter(x -> x.getName().equals(name)).findFirst().get());
        }
    }


    public List<SystemListItem> getSystemList() {
        return systemList;
    }

    public List<SystemListItem> getSubSystemList() {
        return subSystemList;
    }

    public List<SystemListItem> getResourceList() {
        return resourceList;
    }

    public List<SimpleListItem> getWeaponsList() {
        return weaponsList;
    }

    public List<SimpleListItem> getDronesList() {
        return dronesList;
    }

    public List<SimpleListItem> getAugmentsList() {
        return augmentsList;
    }

    public List<SimpleListItem> getCrewList() {
        return crewList;
    }
}




