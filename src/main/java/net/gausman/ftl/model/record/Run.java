package net.gausman.ftl.model.record;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import net.blerf.ftl.constants.Difficulty;
import net.blerf.ftl.model.sectortree.SectorDot;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.random.NativeRandom;
import net.blerf.ftl.parser.sectortree.RandomSectorTreeGenerator;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.factory.EventFactory;

import java.time.Instant;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "sectorTreeSeed")
public class Run {
    private final Instant startTime;
    private Instant endTime;
    private String playerShipName = "";
    private String playerShipBlueprintId = "";
    private Difficulty difficulty;
    private int sectorTreeSeed = 42;
    private Constants.Result result = Constants.Result.ONGOING;
    private final NavigableMap<Integer, Sector> sectors = new TreeMap<>();
//    @JsonIgnore
//    private final NavigableMap<Integer, Event> events = new TreeMap<>();
    @JsonIgnore
    private List<List<SectorDot>> tree;
//    @JsonIgnore
//    private List<Boolean> sectorVisitationList;
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


    public Run(SavedGameParser.SavedGameState gameState, RandomSectorTreeGenerator generator) {
        startTime = Instant.now();
        this.difficulty = gameState.getDifficulty();
        this.playerShipBlueprintId = gameState.getPlayerShipBlueprintId();
        this.playerShipName = gameState.getPlayerShipName();
        this.sectorTreeSeed = gameState.getSectorTreeSeed();

        this.tree = generator.generateSectorTree(gameState.getSectorTreeSeed(), true);
        Sector.resetNextId();
        Jump.resetNextId();
        EventFactory.resetNextEventId();
        Sector sector1 = new Sector(gameState, this);
        this.sectors.put(sector1.getId(), sector1);
    }

    public Run(@JsonProperty("seed") int seed){
        startTime = Instant.now();
        RandomSectorTreeGenerator generator = new RandomSectorTreeGenerator(new NativeRandom());
        this.tree = generator.generateSectorTree(seed, true);
    }

    public SectorDot getSectorDotForId(int id, List<Boolean> sectorVisitationList){
        List<SectorDot> flatList = tree.stream()
                .flatMap(List::stream)
                .toList();

        int index = indexOfNthTrue(sectorVisitationList, id);

        return flatList.get(index);

    }

    private int indexOfNthTrue(List<Boolean> list, int n) {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (Boolean.TRUE.equals(list.get(i))) {
                count++;
                if (count == n) {
                    return i;
                }
            }
        }
        return -1;
    }

    @JsonIgnore
    public Sector getCurrentSector(){
        return sectors.lastEntry().getValue();
    }

    @JsonIgnore
    public Jump getCurrentJump(){
        return sectors.lastEntry().getValue().getJumps().lastEntry().getValue();
    }

    @JsonIgnore
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
