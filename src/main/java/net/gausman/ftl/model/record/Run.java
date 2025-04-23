package net.gausman.ftl.model.record;

import com.fasterxml.jackson.databind.deser.BasicDeserializerFactory;
import net.blerf.ftl.constants.Difficulty;
import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.factory.EventFactory;

import java.time.Instant;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Run {
    private final Instant startTime;
    private Instant endTime;
    private String playerShipName = "";
    private String playerShipBlueprintId = "";
    private Difficulty difficulty;
    private int sectorTreeSeed = 42;
    private Constants.Result result = Constants.Result.ONGOING;
    private final NavigableMap<Integer, Sector> sectors = new TreeMap<>();
    private final NavigableMap<Integer, Event> events = new TreeMap<>();

    /*
    Generally speaking we have this hierarchy: Run>Sector>Jump>Event
    The Event-Map here is used to easily display it on the GUI
    We have two versions of the Event-Map here:
    -full => just all events, this is the full data-structure that we use in the backend
    -merged => we merge similar events within a Jump, this is probably what most users want to see in the GUI

    The user will be able to toggle between the two modes, when writing to a file we always use the full version of the events.

    >> Loading
    When loading a run we will iterate over Sectors and Jumps and add events.
    If merging is active we merge events within a Jump before adding the event.

    >> Runtime
    At runtime, when an event is added and merging is active we need to merge it with the events of the run.
    And of course always add it to the normal events within the Jump.

    If the user toggles between full/merged during the run we just build the events here from scratch.

    */

    // TODO add Links from Event, Jump, Sector back to the layer above



    // TODO sectorvisitationlist
    // TODO state-vars
    // TODO beaconList: do we need more than the stores?
    // TODO quests?
    // TODO encounter
    // TODO environment




    public Run(){
        startTime = Instant.now();
    }

    public Run(SavedGameParser.SavedGameState gameState) {
        startTime = Instant.now();
        this.difficulty = gameState.getDifficulty();
        this.playerShipBlueprintId = gameState.getPlayerShipBlueprintId();
        this.playerShipName = gameState.getPlayerShipName();
        this.sectorTreeSeed = gameState.getSectorTreeSeed();
        Sector.resetNextId();
        Jump.resetNextId();
        EventFactory.resetNextEventId();
        Sector sector1 = new Sector(gameState, this);
        this.sectors.put(sector1.getId(), sector1);
    }

    public Sector getCurrentSector(){
        return sectors.lastEntry().getValue();
    }

    public Jump getCurrentJump(){
        return sectors.lastEntry().getValue().getJumps().lastEntry().getValue();
    }

    public Jump getLastJump(){
        return sectors.lastEntry().getValue().getLastJump();
    }

    public void endRun(Constants.Result result){
        endTime = Instant.now();
        this.result = result;
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


    public int getSectorTreeSeed() {
        return sectorTreeSeed;
    }

    public Constants.Result getResult() {
        return result;
    }


    public NavigableMap<Integer, Sector> getSectors() {
        return sectors;
    }

    public void addSector(Sector sector){
        sectors.put(sector.getId(), sector);
    }

}
