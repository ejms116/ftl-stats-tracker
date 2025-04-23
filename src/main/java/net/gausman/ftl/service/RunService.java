package net.gausman.ftl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.controller.TrackerController;
import net.gausman.ftl.model.FTLEventBox;
import net.gausman.ftl.model.RunUpdateResponse;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.record.*;
import net.gausman.ftl.model.record.Event;
import net.gausman.ftl.model.run.FTLJump;
import net.gausman.ftl.model.run.FTLRunEvent;
import net.gausman.ftl.view.EventListItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.*;

public class RunService {
    private static final Logger log = LoggerFactory.getLogger(RunService.class);

    private EventService eventService = new EventService();

    private Run currentRun = null;
    private NavigableMap<Integer, Event> eventMapFlat = new TreeMap<>();

    private NavigableMap<Integer, ShipStatusModel> statusCache = new TreeMap<>();

    private SavedGameParser.SavedGameState lastGameState = null;

    ObjectMapper mapper = new ObjectMapper();

    public RunUpdateResponse update(SavedGameParser.SavedGameState currentGameState){
        boolean newRun = false;

//        if (lastGameState != null && currentGameState != null){
//            try {
//                String oldSave = mapper.writeValueAsString(lastGameState);
//                String newSave = mapper.writeValueAsString(currentGameState);
//                if (oldSave.equals(newSave)){
//                    log.info("SAME");
//                } else {
//                    log.info("!!!!!!!NEW!!!!!!!!");
//                    copySaveFile(currentGameState);
//                }
//            } catch (Exception e){
//
//            }
//
//        }

        // New Run, creates Sector+Jump automatically
        if (currentRun == null || currentRun.getSectorTreeSeed() != currentGameState.getSectorTreeSeed() ||
                (lastGameState != null && lastGameState.getTotalBeaconsExplored() > currentGameState.getTotalBeaconsExplored())){
            lastGameState = null;
            currentRun = new Run(currentGameState);

            // Todo move stats-json file
            // Todo create new folder for save-files

            // generate starting Events

            newRun = true;
            eventMapFlat = new TreeMap<>();

            EventBox box = eventService.getEventsStartRun(currentGameState, currentRun.getCurrentJump());
            addEventsFromEventBox(box);

        }

        // New Sector, automatically creates Jump
        if (currentRun.getCurrentSector().getId() != currentGameState.getSectorNumber() + 1){
            currentRun.addSector(new Sector(currentGameState, currentRun));
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
            // Todo copy save files


        }



        // Adding Events
        EventBox box = eventService.getEventsFromGameStateComparison(lastGameState, currentGameState, currentRun.getCurrentJump());
        addEventsFromEventBox(box); // assigns the EventIds




        // Todo possible merge events

        // save to json file

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

    }

    public NavigableMap<Integer, Event> getEventMapFlat() {
        return eventMapFlat;
    }

    public Run getCurrentRun() {
        return currentRun;
    }

    //    public List<Event> getAllEventsFlat(){
//        List<Event> events = new ArrayList<>();
//
//        for (Map.Entry<Integer, Sector> sector : currentRun.getSectors().entrySet()){
//            for (Map.Entry<Integer, Jump> jump : sector.getValue().getJumps().entrySet()){
//                for (Map.Entry<Integer, Event> event : jump.getValue().getEvents().entrySet() ){
//                    events.add(event.getValue());
//                }
//            }
//        }
//        return events;
//    }

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
