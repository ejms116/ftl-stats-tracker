package net.gausman.ftl.model.record;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import net.blerf.ftl.model.sectortree.SectorDot;
import net.blerf.ftl.parser.SavedGameParser;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Sector.class)
public class Sector {
    private static int nextId = 1;

    private int id;
    private int sectorLayoutSeed;
    private List<SavedGameParser.BeaconState> beaconList = new ArrayList<>();
    private final NavigableMap<Integer,Jump> jumps = new TreeMap<>();
    private SectorDot sectorDot;
    private Run run;

    public Sector(){
        nextId++;
    }

    public Sector(SavedGameParser.SavedGameState gameState, Run run){
//        this.id = gameState.getSectorNumber() + 1;
//        this.id = nextId;
        this.id = gameState.getSectorNumber() + 1;
        this.sectorDot = run.getSectorDotForId(this.id, gameState.getSectorVisitation());
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
        jumps.put(jump.getId(), jump);
    }

    public NavigableMap<Integer, Jump> getJumps() {
        return jumps;
    }

    @JsonIgnore
    public Jump getLastJump() {
        return jumps.lastEntry().getValue();
    }

    public static void resetNextId(){
        nextId = 1;
    }

    public Run getRun(){
        return run;
    }

    public SectorDot getSectorDot() {
        return sectorDot;
    }
}
