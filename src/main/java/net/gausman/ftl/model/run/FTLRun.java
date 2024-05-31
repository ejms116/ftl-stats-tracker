package net.gausman.ftl.model.run;

import net.blerf.ftl.constants.Difficulty;
import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;

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

    public FTLRun(SavedGameParser.SavedGameState gameState){
        startTime = Instant.now();
        this.difficulty = gameState.getDifficulty();
        this.playerShipBlueprintId = gameState.getPlayerShipBlueprintId();
        this.playerShipName = gameState.getPlayerShipName();
        this.sectorTreeSeed = gameState.getSectorTreeSeed();
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
}
