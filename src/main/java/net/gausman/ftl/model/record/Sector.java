package net.gausman.ftl.model.record;

import com.fasterxml.jackson.annotation.JsonBackReference;
import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.run.FTLJump;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Sector {
    private static int nextId = 1;

    private final int id;
    private final int sectorLayoutSeed;
    private Jump lastJump = null;
    private List<SavedGameParser.BeaconState> beaconList = new ArrayList<>();
    private final NavigableMap<Integer,Jump> jumps = new TreeMap<>();
    @JsonBackReference
    private Run run;

    public Sector(SavedGameParser.SavedGameState gameState, Run run){
//        this.id = gameState.getSectorNumber() + 1;
        this.id = nextId;
        this.sectorLayoutSeed = gameState.getSectorLayoutSeed();
        Jump jump1 = new Jump(gameState, this);
        this.jumps.put(jump1.getId(), jump1);
        this.run = run;
        nextId++;
    }

    public int getId() {
        return id;
    }


    public int getSectorLayoutSeed() {
        return sectorLayoutSeed;
    }

    public List<SavedGameParser.BeaconState> getBeaconList() {
        return beaconList;
    }

    public void setBeaconList(List<SavedGameParser.BeaconState> beaconList) {
        this.beaconList = beaconList;
    }

    public void addJump(Jump jump){
        lastJump = jumps.lastEntry().getValue();
        jumps.put(jump.getId(), jump);
    }

    public NavigableMap<Integer, Jump> getJumps() {
        return jumps;
    }

    public Jump getLastJump() {
        return lastJump;
    }

    public static void resetNextId(){
        nextId = 1;
    }

    public Run getRun(){
        return run;
    }
}
