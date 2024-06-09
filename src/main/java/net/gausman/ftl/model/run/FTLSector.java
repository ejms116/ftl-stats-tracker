package net.gausman.ftl.model.run;

import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.SavedGameParser.StoreState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FTLSector {
    private int sectorLayoutSeed;
    private List<SavedGameParser.BeaconState> beaconList = new ArrayList<>();


    public FTLSector(SavedGameParser.SavedGameState gameState){
        this.sectorLayoutSeed = gameState.getSectorLayoutSeed();
    }

    public int getSectorLayoutSeed(){
        return sectorLayoutSeed;
    }

    public void setBeaconList(List<SavedGameParser.BeaconState> beaconList){
        this.beaconList = beaconList;
    }

    public List<SavedGameParser.BeaconState> getBeaconList(){
        return beaconList;
    }

}
