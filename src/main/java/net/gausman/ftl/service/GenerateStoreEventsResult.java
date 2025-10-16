package net.gausman.ftl.service;

import net.gausman.ftl.model.change.Event;

import java.util.ArrayList;
import java.util.List;

public class GenerateStoreEventsResult {
    private final List<Event> events = new ArrayList<>();
    List<String> boughtItems = new ArrayList<>();
    List<String> boughtCrew = new ArrayList<>();

    int repairCountDiff = 0;
    int fuelBought = 0;
    int missilesBought = 0;
    int dronesBought = 0;

    private boolean storePresent = false;
    private boolean addSellEventsToLastJump = false;

    public void add(Event event){
        events.add(event);
    }

    public List<Event> getEvents() {
        return events;
    }

    public List<String> getBoughtItems() {
        return boughtItems;
    }

    public List<String> getBoughtCrew() {
        return boughtCrew;
    }

    public int getRepairCountDiff() {
        return repairCountDiff;
    }

    public int getFuelBought() {
        return fuelBought;
    }

    public int getMissilesBought() {
        return missilesBought;
    }

    public int getDronesBought() {
        return dronesBought;
    }

    public boolean isStorePresent() {
        return storePresent;
    }

    public boolean isAddSellEventsToLastJump() {
        return addSellEventsToLastJump;
    }

    public void setStorePresent(boolean storePresent) {
        this.storePresent = storePresent;
    }

    public void setAddSellEventsToLastJump(boolean addSellEventsToLastJump) {
        this.addSellEventsToLastJump = addSellEventsToLastJump;
    }

    public void merge(GenerateStoreEventsResult result){
        this.events.addAll(result.events);
        this.boughtItems.addAll(result.boughtItems);
        this.boughtCrew.addAll(result.boughtCrew);
        this.repairCountDiff += result.repairCountDiff;
        this.fuelBought += result.fuelBought;
        this.missilesBought += result.missilesBought;
        this.dronesBought += result.dronesBought;

    }
}
