package net.gausman.ftl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.random.FTL_1_6_Random;
import net.blerf.ftl.parser.random.NativeRandom;
import net.blerf.ftl.parser.sectortree.RandomSectorTreeGenerator;
import net.gausman.ftl.model.RunUpdateResponse;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.factory.EventFactory;
import net.gausman.ftl.model.record.EventBox;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.model.record.Run;
import net.gausman.ftl.model.record.Sector;
import net.gausman.ftl.util.GausmanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class RunService {
    public static final String CURRENT_RUN_FILENAME = "current_run.json";
    private static final Logger log = LoggerFactory.getLogger(RunService.class);
    private final EventService eventService = new EventService();

    private final Path runsDir;
    private final Path savesDir;

    private Run currentRun = null;
    private String currentRunFolderName;
    private NavigableMap<Integer, Event> eventMapFlat = new TreeMap<>();

    private final NavigableMap<Integer, ShipStatusModel> statusCache = new TreeMap<>();

    private SavedGameParser.SavedGameState lastGameState = null;
    private RandomSectorTreeGenerator generator = new RandomSectorTreeGenerator(new FTL_1_6_Random());
    private ObjectMapper mapper = new ObjectMapper();

    public RunService(Path runsDir, Path savesDir){
        mapper.registerModule(new JavaTimeModule());
        this.runsDir = runsDir;
        this.savesDir = savesDir;
        readRunFromJSON();
        buildEventMapFlat();
    }

    private void buildEventMapFlat(){
        if (currentRun == null){
            return;
        }
        for (Sector sector : currentRun.getSectors().values()){
            for (Jump jump : sector.getJumps().values()){
                for (Event event : jump.getEvents().values()){
                    EventFactory.assignEventId(event);
                    eventMapFlat.put(event.getId(), event);
                }
            }
        }
    }

    public RunUpdateResponse update(SavedGameParser.SavedGameState currentGameState, File file){
        boolean newRun = false;

        // New Run, creates Sector+Jump automatically
        if (currentRun == null || currentRun.getSectorTreeSeed() != currentGameState.getSectorTreeSeed() ||
                (lastGameState != null && lastGameState.getTotalBeaconsExplored() > currentGameState.getTotalBeaconsExplored()) ||
                currentRun.getLastJump().getTotalBeaconsExplored() > currentGameState.getTotalBeaconsExplored()
        ){
            // move old stats json file to runs folder
            if (currentRun != null){
                String currentRunName = String.format("%s-%s.json", GausmanUtil.formatInstant(currentRun.getStartTime()), currentRun.getPlayerShipName());
                Path filePathJson = runsDir.resolve(currentRunName);
                saveRunToJson(filePathJson.toFile());
            }

            lastGameState = null;
            currentRun = new Run(currentGameState, generator);

            // create new sub folder for the runs' save files and copy the first file
            currentRunFolderName = String.format("%s-%s", GausmanUtil.formatInstant(currentRun.getStartTime()), currentRun.getPlayerShipName());
            Path folderPath = savesDir.resolve(currentRunFolderName);
            try {
                Files.createDirectories(folderPath);
                log.info("Created folder: " + folderPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            copySaveFile(file);


            // setup/clear everything for a new run

            newRun = true;
            eventMapFlat = new TreeMap<>();
            statusCache.clear();
            eventService.initEventService();

            EventBox box = eventService.getEventsStartRun(currentGameState, currentRun.getCurrentJump());
            addEventsFromEventBox(box);

        }

        // New Sector, automatically creates Jump
        if (currentRun.getCurrentSector().getId() != currentGameState.getSectorNumber() + 1){
            currentRun.addSector(new Sector(currentGameState, currentRun));
            copySaveFile(file);
        }

        // We always update the beaconlist Todo: can we optimize this?
        currentRun.getCurrentSector().setBeaconList(currentGameState.getBeaconList());


        // New Jump
        if (currentRun.getCurrentJump().getCurrentBeaconId() != currentGameState.getCurrentBeaconId()){
            // When backtracking the game does not necessarily save the game
            // so we need to compare the beacons explored and for create "empty jumps" (just a fuel-used event)
            // the amount is the difference between the new and last beacons explored stats minus 1 (for the new jump)
            int beaconsExploredDiff = currentGameState.getTotalBeaconsExplored() - currentRun.getCurrentJump().getTotalBeaconsExplored();
            if (beaconsExploredDiff > 1){
                int beaconsExploredTemp = currentRun.getCurrentJump().getTotalBeaconsExplored();
                for (int i = 0; i < beaconsExploredDiff-1; i++){
                    beaconsExploredTemp++;

//                    jumpNumber++;
//                    currentJump = new FTLJump(beaconsExploredTemp, -1, jumpNumber);
//                    currentRun.addJump(currentJump);
                    // We set the beaconId to -1, because we don't know where the player actually was
                    currentRun.getCurrentSector().addJump(new Jump(beaconsExploredTemp, -1, currentRun.getCurrentSector()));

                    addEventsFromEventBox(eventService.getFuelUsedEventBox(currentRun.getCurrentJump()));
                }
            }

            currentRun.getCurrentSector().addJump(new Jump(currentGameState, currentRun.getCurrentSector()));

            // Copy save file
            copySaveFile(file);
        }


        // Adding Events
        EventBox box = eventService.getEventsFromGameStateComparison(lastGameState, currentGameState, currentRun.getCurrentJump());
        addEventsFromEventBox(box); // assigns the EventIds

        // save to json file
        saveRunToJson(new File(CURRENT_RUN_FILENAME));

        lastGameState = currentGameState;

        log.info( "Ship Name: " + currentGameState.getPlayerShipName());
        log.info( "Total beacons explored: " + currentGameState.getTotalBeaconsExplored());
        log.info( "Currently at beacon Id: " + currentGameState.getCurrentBeaconId());
        log.info( "Jump number: " + currentRun.getCurrentJump().getId());
//        log.info( "Event number: " + currentRun.getCurrentJump().getEvents().lastEntry().getValue().getId());
        int sectorNumberDebug = currentGameState.getSectorNumber() + 1;
        log.info( "Currently in sector : " +  sectorNumberDebug);
        log.info( "----------------------------------------------------------------");

        return new RunUpdateResponse(newRun);

    }

    private void copySaveFile(File file){
        try {
            String filenameToCopy = String.format("%d.sav", currentRun.getCurrentJump().getId());
            Path target = savesDir.resolve(currentRunFolderName).resolve(filenameToCopy);
            Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied " + file + " → " + target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveRunToJson(File file){
        try {
            mapper.writeValue(file, currentRun);
        } catch (IOException e){
            log.error("Error writing json file");
        }
    }

    private void readRunFromJSON(){
        File file = new File(CURRENT_RUN_FILENAME);
        if (!file.exists()){
            return;
        }
        try {
            currentRun = mapper.readValue(file, Run.class);
        } catch (IOException e){
            log.error("Current run could not be read");
        }
    }


    private void addEventsFromEventBox(EventBox box){
        if (!box.getLastJumpEvents().isEmpty()){
            if (currentRun.getLastJump() != null){
                currentRun.getLastJump().addEvents(box.getLastJumpEvents());
            }
            // todo error handling (should not occur)
        }

        currentRun.getCurrentJump().addEvents(box.getNewJumpEvents());

        // event Ids are already assigned
        for (Event e: box.getLastJumpEvents()){
            eventMapFlat.put(e.getId(), e);
        }

        for (Event e: box.getNewJumpEvents()){
            eventMapFlat.put(e.getId(), e);
        }

        if (box.getEncounterState() == null){
            return;
        }

        List<SavedGameParser.EncounterState> encounterStates = currentRun.getCurrentJump().getEncounterStates();

        if (!encounterStates.isEmpty() && box.getEncounterState().equals(encounterStates.getLast())){
            encounterStates.getLast().setChoiceList(box.getEncounterState().getChoiceList());
        } else {
            currentRun.getCurrentJump().getEncounterStates().add(box.getEncounterState());
        }


    }

    public NavigableMap<Integer, Event> getEventMapFlat() {
        return eventMapFlat;
    }

    public Run getCurrentRun() {
        return currentRun;
    }

    public ShipStatusModel getNewestStatus(){
        int index = eventMapFlat.size() - 1;
        return getStatusAtId(index);
    }

    public ShipStatusModel getStatusAtId(int targetId){
        var entry = getClosestEntry(targetId);

        if (entry == null){
            return recomputeFromZero(targetId);
        }

        int cachedId = entry.getKey();

        int distanceToCache = Math.abs(cachedId - targetId);

        if (distanceToCache > targetId){
            return recomputeFromZero(targetId);
        }

        ShipStatusModel cached = new ShipStatusModel(entry.getValue());

        if (cachedId == targetId){
            return cached;
        }

        return targetId > cachedId
                ? applyForward(cached, cachedId, targetId)
                : applyBackwards(cached, cachedId, targetId);
    }

    private ShipStatusModel recomputeFromZero(int targetId){
        ShipStatusModel status = new ShipStatusModel();
        Collection<Event> headMap = eventMapFlat.headMap(targetId, true).values();

        for (Event e : headMap){
            status.apply(e, true);
        }
        statusCache.put(targetId, status);
        return status;
    }

    private ShipStatusModel applyForward(ShipStatusModel cache, int fromId, int targetId){
        Collection<Event> subMap = eventMapFlat.subMap(fromId + 1, true, targetId, true).values();
        ShipStatusModel status = new ShipStatusModel(cache);
        for (Event e : subMap){
            status.apply(e, true);
        }

        statusCache.put(targetId, status);
        return status;

    }

    private ShipStatusModel applyBackwards(ShipStatusModel cache, int fromId, int targetId){
        Collection<Event> subMap = eventMapFlat.subMap(targetId + 1, true, fromId, true).descendingMap().values();
        ShipStatusModel status = new ShipStatusModel(cache);
        for (Event e : subMap){
            status.apply(e, false);
        }

        statusCache.put(targetId, status);
        return status;
    }

    private Map.Entry<Integer, ShipStatusModel> getClosestEntry(int target){
        Map.Entry<Integer, ShipStatusModel> floor = statusCache.floorEntry(target);
        Map.Entry<Integer, ShipStatusModel> ceiling = statusCache.ceilingEntry(target);

        if (floor == null) return ceiling;
        if (ceiling == null) return floor;

        int diffFloor = Math.abs(target - floor.getKey());
        int diffCeil = Math.abs(target - ceiling.getKey());

        return (diffFloor <= diffCeil) ? floor : ceiling;

    }

}
