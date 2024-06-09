package net.gausman.ftl.model.run;

import net.blerf.ftl.constants.Difficulty;
import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.util.GausmanUtil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FTLRun {
    private Instant startTime;
    private Instant endTime;
    private String playerShipName = "";
    private String playerShipBlueprintId = "";
    private Difficulty difficulty;
    private int sectorTreeSeed = 42;
    private Constants.Result result = Constants.Result.ONGOING;
    private List<FTLJump> jumpList = new ArrayList<>();
    private List<FTLSector> sectorList = new ArrayList<>();

    // TODO sectorvisitationlist
    // TODO state-vars
    // TODO beaconList: do we need more than the stores?
    // TODO quests?
    // TODO encounter
    // TODO environment

    public FTLRun(){
        startTime = Instant.now();
    }

    public FTLRun(SavedGameParser.SavedGameState gameState){
        startTime = Instant.now();
        this.difficulty = gameState.getDifficulty();
        this.playerShipBlueprintId = gameState.getPlayerShipBlueprintId();
        this.playerShipName = gameState.getPlayerShipName();
        this.sectorTreeSeed = gameState.getSectorTreeSeed();
        this.sectorList.add(new FTLSector(gameState));
    }

    public String generateFileNameForRun(){
        DataManager dm = DataManager.get();
        String runFileName = String.format("runs\\%s-%s.json", GausmanUtil.formatInstant(startTime), dm.getShip(playerShipBlueprintId).getName().getTextValue());
        runFileName = runFileName.replaceAll("\\:", "-");
        return runFileName;
    }

    public String generateFolderNameForSave(){
        DataManager dm = DataManager.get();
        String runFileName = String.format("saves\\%s-%s", GausmanUtil.formatInstant(startTime), dm.getShip(playerShipBlueprintId).getName().getTextValue());
        runFileName = runFileName.replaceAll("\\:", "-");
        return runFileName;
    }
    public String generateFileNameForSave(int jumpNumber, int sectorNumber){
        DataManager dm = DataManager.get();
        String runFileName = String.format("saves\\%s-%s\\%d(%d).sav", GausmanUtil.formatInstant(startTime), dm.getShip(playerShipBlueprintId).getName().getTextValue(), jumpNumber, sectorNumber);
        runFileName = runFileName.replaceAll("\\:", "-");
        return runFileName;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public String getPlayerShipName() {
        return playerShipName;
    }

    public String getPlayerShipBlueprintId() {
        return playerShipBlueprintId;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Constants.Result getResult() {
        return result;
    }

    public int getSectorTreeSeed() {
        return sectorTreeSeed;
    }

    public void endRun(Constants.Result result){
        this.result = result;
        endTime = Instant.now();
    }

    public void addJump(FTLJump jump){
        jumpList.add(jump);
    }

    public List<FTLJump> getJumpList() {
        return jumpList;
    }

    public void addSector(SavedGameParser.SavedGameState gameState){
        sectorList.add(new FTLSector(gameState));
    }

    public List<FTLSector> getSectorList(){
        return sectorList;
    }
}
