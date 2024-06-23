package net.gausman.ftl.model.run;

import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.SavedGameParser.StoreState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FTLSector {
    private int sectorNumber;
    private int sectorLayoutSeed;
    private List<FTLJump> jumpList = new ArrayList<>();
    private List<SavedGameParser.BeaconState> beaconList = new ArrayList<>();

    public FTLSector(){

    }

    public FTLSector(SavedGameParser.SavedGameState gameState){
        this.sectorNumber = gameState.getSectorNumber() + 1;
        this.sectorLayoutSeed = gameState.getSectorLayoutSeed();
    }

    public int getSectorNumber() {
        return sectorNumber;
    }

    public int getSectorLayoutSeed(){
        return sectorLayoutSeed;
    }

    public void setBeaconList(List<SavedGameParser.BeaconState> beaconList){
        this.beaconList = beaconList;
    }

    public void addJump(FTLJump jump){
        jumpList.add(jump);
    }

    public List<FTLJump> getJumpList() {
        return jumpList;
    }

    public List<SavedGameParser.BeaconState> getBeaconList(){
        return beaconList;
    }

}
