package net.gausman.ftl.model.record;

import net.blerf.ftl.parser.SavedGameParser;

import java.util.ArrayList;
import java.util.List;

public class StoreInfo {
    private final int sector;
    private final int beaconId;
    private SavedGameParser.StoreState store;
    private final int initialFuel;
    private final int initialMissiles;
    private final int initialDroneParts;
    private int repairCount = 0;
    private final List<Integer> visitedOnJumps = new ArrayList<>();
    private final int scrapAvailable;

    public StoreInfo(int sector, int beaconId, SavedGameParser.StoreState store, int scrapAvailable){
        this.sector = sector;
        this.beaconId = beaconId;
        this.store = store;
        this.initialFuel = store.getFuel();
        this.initialMissiles = store.getMissiles();
        this.initialDroneParts = store.getDroneParts();
        this.scrapAvailable = scrapAvailable;
    }

    // deep copy
    public StoreInfo(StoreInfo other){
        this.sector = other.sector;
        this.beaconId = other.beaconId;
        this.store = new SavedGameParser.StoreState(other.getStore());
        this.initialFuel = other.getInitialFuel();
        this.initialMissiles = other.getInitialMissiles();
        this.initialDroneParts = other.getInitialDroneParts();
        this.repairCount = other.getRepairCount();
        this.visitedOnJumps.addAll(other.visitedOnJumps);
        this.scrapAvailable = other.scrapAvailable;
    }

    public int getScrapAvailable() {
        return scrapAvailable;
    }

    public int getSector() {
        return sector;
    }

    public int getBeaconId() {
        return beaconId;
    }

    public List<Integer> getVisitedOnJumps() {
        return visitedOnJumps;
    }

    public void addVisitedJump(Integer jump) {
        this.visitedOnJumps.add(jump);
    }

    public SavedGameParser.StoreState getStore() {
        return store;
    }

    public void setStore(SavedGameParser.StoreState store) {
        this.store = store;
    }



    public int getInitialFuel() {
        return initialFuel;
    }

    public int getInitialMissiles() {
        return initialMissiles;
    }

    public int getInitialDroneParts() {
        return initialDroneParts;
    }

    public int getRepairCount() {
        return repairCount;
    }

    public void setRepairCount(int repairCount) {
        this.repairCount = repairCount;
    }
}
