package net.gausman.ftl.model.record;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import net.blerf.ftl.model.sectortree.SectorDot;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.random.NativeRandom;
import net.blerf.ftl.parser.random.RandRNG;
import net.blerf.ftl.parser.sectormap.GeneratedSectorMap;
import net.blerf.ftl.parser.sectormap.RandomSectorMapGenerator;

import javax.swing.*;
import java.util.*;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Sector.class)
public class Sector {
    private static int nextId = 1;

    private int id;
    private int sectorLayoutSeed;
    private List<SavedGameParser.BeaconState> beaconList = new ArrayList<>();
    private final NavigableMap<Integer,Jump> jumps = new TreeMap<>();
    private SectorDot sectorDot;
    private Run run;
    private final Map<Integer, StoreInfo> stores = new HashMap<>();

    public Sector(){
        nextId++;
    }

    public Sector(SavedGameParser.SavedGameState gameState, Run run){
        this.id = gameState.getSectorNumber() + 1;
        this.sectorDot = run.getSectorDotForId(this.id, gameState.getSectorVisitation());
        this.sectorLayoutSeed = gameState.getSectorLayoutSeed();
        Jump jump1 = new Jump(gameState, this);
        this.jumps.put(jump1.getId(), jump1);
        this.run = run;
        nextId++;

//        RandomSectorMapGenerator generator = new RandomSectorMapGenerator();
//        GeneratedSectorMap sectorMap = generator.generateSectorMap( new NativeRandom(), gameState.getFileFormat());

        GeneratedSectorMap newGenMap = null;

        @SuppressWarnings( "unchecked" )
        RandRNG selectedRNG = new NativeRandom(); //(RandRNG)selectedRNGObj;

        synchronized ( selectedRNG ) {
            selectedRNG.srand(sectorLayoutSeed);

            RandomSectorMapGenerator randomMapGen = new RandomSectorMapGenerator();
            try {
                newGenMap = randomMapGen.generateSectorMap(selectedRNG, gameState.getFileFormat());
            }
            catch ( IllegalStateException e ) {
//                log.error( "Map generation failed", e );
            }
        }

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

    public Map<Integer, StoreInfo> getStores() {
        return stores;
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
