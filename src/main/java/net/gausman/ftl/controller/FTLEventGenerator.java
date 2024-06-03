package net.gausman.ftl.controller;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.xml.DroneBlueprint;
import net.blerf.ftl.xml.ShipBlueprint;
import net.blerf.ftl.xml.WeaponBlueprint;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.run.FTLRunEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FTLEventGenerator {

    private DataManager dm = DataManager.get();

    public List<FTLRunEvent> getEventsStartRun(SavedGameParser.SavedGameState newGameState){
        List<FTLRunEvent> events = new ArrayList<>();

        ShipBlueprint shipBlueprint = dm.getShip(newGameState.getPlayerShipBlueprintId());

        SavedGameParser.ShipState shipState = newGameState.getPlayerShip();
        FTLRunEvent event;

        event = new FTLRunEvent();
        event.setCategory(Constants.EventCategory.RESOURCE);
        event.setType(Constants.EventType.START);
        event.setId("HULL");
        event.setAmount(shipState.getHullAmt());
        events.add(event);

        event = new FTLRunEvent();
        event.setCategory(Constants.EventCategory.RESOURCE);
        event.setType(Constants.EventType.START);
        event.setId("FUEL");
        event.setAmount(shipState.getFuelAmt());
        events.add(event);

        event = new FTLRunEvent();
        event.setCategory(Constants.EventCategory.RESOURCE);
        event.setType(Constants.EventType.START);
        event.setId("DRONE_PARTS");
        event.setAmount(shipState.getDronePartsAmt());
        events.add(event);

        event = new FTLRunEvent();
        event.setCategory(Constants.EventCategory.RESOURCE);
        event.setType(Constants.EventType.START);
        event.setId("MISSILES");
        event.setAmount(shipState.getMissilesAmt());
        events.add(event);

        event = new FTLRunEvent();
        event.setCategory(Constants.EventCategory.RESOURCE);
        event.setType(Constants.EventType.START);
        event.setId("SCRAP");
        event.setAmount(shipState.getScrapAmt());
        events.add(event);

        // Crew
        for (SavedGameParser.CrewState crewState: newGameState.getPlayerShip().getCrewList()){
            event = new FTLRunEvent();
            event.setCategory(Constants.EventCategory.CREW);
            event.setType(Constants.EventType.START);
            event.setId(crewState.getRace().getId());
            events.add(event);
        }

        // Systems
        for (Map.Entry<SavedGameParser.SystemType, List<SavedGameParser.SystemState>> entry: newGameState.getPlayerShip().getSystemsMap().entrySet()){
            for (SavedGameParser.SystemState systemState: entry.getValue()){
                int capacity = systemState.getCapacity();
                if (capacity == 0){
                    continue;
                }
                event = new FTLRunEvent();
                event.setCategory(Constants.EventCategory.SYSTEM);
                event.setType(Constants.EventType.START);
                event.setId(systemState.getSystemType().getId());
                event.setAmount(capacity);
                events.add(event);
            }
        }

        // Weapons
        for (SavedGameParser.WeaponState weaponState: newGameState.getPlayerShip().getWeaponList()){
            event = new FTLRunEvent();
            event.setCategory(Constants.EventCategory.WEAPON);
            event.setType(Constants.EventType.START);
            event.setId(weaponState.getWeaponId());
            events.add(event);
        }

        // Drones
        for (SavedGameParser.DroneState droneState: newGameState.getPlayerShip().getDroneList()){
            event = new FTLRunEvent();
            event.setCategory(Constants.EventCategory.DRONE);
            event.setType(Constants.EventType.START);
            event.setId(droneState.getDroneId());
            events.add(event);
        }

        // Augments
        for (String augmentId: newGameState.getPlayerShip().getAugmentIdList()){
            event = new FTLRunEvent();
            event.setCategory(Constants.EventCategory.AUGMENT);
            event.setType(Constants.EventType.START);
            event.setId(augmentId);
            events.add(event);
        }

        // INFO We assume here that the player ship does not start with anything in the cargo, which is true for vanilla FTL
        // TODO if the run was started late the beacons explored stat is wrong...

        return events;
    }

    public List<FTLRunEvent> getEventsFromGameStateComparison(SavedGameParser.SavedGameState oldGameState, SavedGameParser.SavedGameState newGameState){
        List<FTLRunEvent> events = new ArrayList<>();

        if (oldGameState == null || newGameState == null){
            return events;
        }

        CargoConsolidator oldCargo = new CargoConsolidator(oldGameState);
        CargoConsolidator newCargo = new CargoConsolidator(newGameState);

        // TODO delete?

        FTLRunEvent event;

        boolean jumped = true;
        boolean store_present = false;

         if (oldGameState.getCurrentBeaconId() == newGameState.getCurrentBeaconId()){
            jumped = false;
        }

        if (jumped == false && newGameState.getBeaconList().get(newGameState.getCurrentBeaconId()) != null &&
                newGameState.getBeaconList().get(newGameState.getCurrentBeaconId()).getStore() != null &&
                oldGameState.getBeaconList().get(oldGameState.getCurrentBeaconId()) != null &&
                newGameState.getBeaconList().get(newGameState.getCurrentBeaconId()).getStore() != null){
            int fuel_diff = oldGameState.getBeaconList().get(oldGameState.getCurrentBeaconId()).getStore().getFuel() -
                    newGameState.getBeaconList().get(newGameState.getCurrentBeaconId()).getStore().getFuel();
            if (fuel_diff > 0){
                event = new FTLRunEvent();
                event.setCategory(Constants.EventCategory.RESOURCE);
                event.setType(Constants.EventType.BUY);
                event.setId("FUEL");
                event.setAmount(fuel_diff);
                events.add(event);
            }
            int missiles_diff = oldGameState.getBeaconList().get(oldGameState.getCurrentBeaconId()).getStore().getMissiles() -
                    newGameState.getBeaconList().get(newGameState.getCurrentBeaconId()).getStore().getMissiles();
            if (missiles_diff > 0){
                event = new FTLRunEvent();
                event.setCategory(Constants.EventCategory.RESOURCE);
                event.setType(Constants.EventType.BUY);
                event.setId("MISSILES");
                event.setAmount(missiles_diff);
                events.add(event);
            }
            int drones_diff = oldGameState.getBeaconList().get(oldGameState.getCurrentBeaconId()).getStore().getDroneParts() -
                    newGameState.getBeaconList().get(newGameState.getCurrentBeaconId()).getStore().getDroneParts();
            if (drones_diff > 0){
                event = new FTLRunEvent();
                event.setCategory(Constants.EventCategory.RESOURCE);
                event.setType(Constants.EventType.BUY);
                event.setId("DRONE_PARTS");
                event.setAmount(drones_diff);
                events.add(event);
            }
        }

        ArrayList<String> newWeapons = new ArrayList<>(newCargo.weaponList);
        for (String weapon: oldCargo.weaponList){
            newWeapons.remove(weapon);
        }

        for (String weapon: newWeapons){
            event = new FTLRunEvent();
            event.setCategory(Constants.EventCategory.WEAPON);
            event.setType(Constants.EventType.BUY); // TODO free/buy
            event.setId(weapon);
            events.add(event);
        }


        ArrayList<String> removedWeapons = new ArrayList<>(newCargo.weaponList);
        for (String weapon: oldCargo.weaponList){
            removedWeapons.remove(weapon);
        }

        for (String weapon: removedWeapons){
            event = new FTLRunEvent();
            event.setCategory(Constants.EventCategory.WEAPON);
            event.setType(Constants.EventType.SELL); // TODO sell/discarded
            event.setId(weapon);
            events.add(event);
        }



        return events;
    }


   class CargoConsolidator {
       List<String> weaponList = new ArrayList<>();
       List<String> droneList = new ArrayList<>();

       public CargoConsolidator(SavedGameParser.SavedGameState gameState){
           if (gameState == null){
               return;
           }
           for (SavedGameParser.WeaponState weaponState: gameState.getPlayerShip().getWeaponList()){
               weaponList.add(weaponState.getWeaponId());
           }

           for (SavedGameParser.DroneState droneState: gameState.getPlayerShip().getDroneList()){
               droneList.add(droneState.getDroneId());
           }

           for (String cargoId: gameState.getCargoIdList()){
               WeaponBlueprint weaponBlueprint = dm.getWeapon(cargoId);
               if (weaponBlueprint != null){
                   weaponList.add(cargoId);
               }
               DroneBlueprint droneBlueprint = dm.getDrone(cargoId);
               if (droneBlueprint != null){
                   droneList.add(cargoId);
               }
           }
       }
   }

}
