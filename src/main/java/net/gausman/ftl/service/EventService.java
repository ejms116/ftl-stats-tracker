package net.gausman.ftl.service;

import net.blerf.ftl.constants.Difficulty;
import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.xml.DroneBlueprint;
import net.blerf.ftl.xml.ShipBlueprint;
import net.blerf.ftl.xml.WeaponBlueprint;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.record.Event;
import net.gausman.ftl.model.record.EventBox;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventService {
    private DataManager dm = DataManager.get();

    public EventBox getFuelUsedEventBox(Jump jump){
        List<Event> events = new ArrayList<>();
        List<Event> lastJumpEvents = new ArrayList<>();
        EventBox eventBox = new EventBox(events, lastJumpEvents);

        events.add(new Event(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.USE, 1, 0, Constants.Resource.FUEL.name(), jump));

        return eventBox;
    }

    public EventBox getEventsStartRun(SavedGameParser.SavedGameState newGameState, Jump jump){
        List<Event> events = new ArrayList<>();
        List<Event> lastJumpEvents = new ArrayList<>();
        EventBox eventBox = new EventBox(events, lastJumpEvents);

        ShipBlueprint shipBlueprint = dm.getShip(newGameState.getPlayerShipBlueprintId());

        SavedGameParser.ShipState shipState = newGameState.getPlayerShip();

        int startingScrap = 0;
        if (newGameState.getDifficulty().equals(Difficulty.NORMAL)){
            startingScrap += 10;
        } else if (newGameState.getDifficulty().equals(Difficulty.EASY)){
            startingScrap += 30;
        }

        // Resources
        events.add(new Event(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.START, shipBlueprint.getHealth().amount, 0, Constants.Resource.HULL.name(), jump));
        events.add(new Event(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.START, 16, 0, Constants.Resource.FUEL.name(), jump));
        events.add(new Event(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.START, shipBlueprint.getDroneList().drones, 0,Constants.Resource.DRONE.name(), jump));
        events.add(new Event(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.START, shipBlueprint.getWeaponList().missiles, 0,Constants.Resource.MISSILE.name(), jump));
        events.add(new Event(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.START, startingScrap, 0,Constants.Resource.SCRAP.name(), jump));

        // Power
        events.add(new Event(SavedGameParser.StoreItemType.REACTOR, Constants.EventType.START, shipBlueprint.getMaxPower().amount, 0, SavedGameParser.StoreItemType.REACTOR.name(), jump));

        // Crew
        for (SavedGameParser.CrewState crewState: newGameState.getPlayerShip().getCrewList()){
            events.add(new Event(SavedGameParser.StoreItemType.CREW, Constants.EventType.START, 1, 0,crewState.getRace().getId(), jump));
        }

        // Systems
        for (Map.Entry<SavedGameParser.SystemType, List<SavedGameParser.SystemState>> entry: newGameState.getPlayerShip().getSystemsMap().entrySet()){
            for (SavedGameParser.SystemState systemState: entry.getValue()){
                int capacity = systemState.getCapacity();
                if (capacity == 0){
                    continue;
                }
                events.add(new Event(SavedGameParser.StoreItemType.SYSTEM, Constants.EventType.START, capacity, 0, systemState.getSystemType().getId(), jump));
            }
        }

        // Weapons
        for (SavedGameParser.WeaponState weaponState: newGameState.getPlayerShip().getWeaponList()){
            events.add(new Event(SavedGameParser.StoreItemType.WEAPON, Constants.EventType.START, 1,0, weaponState.getWeaponId(), jump));
        }

        // Drones
        for (SavedGameParser.DroneState droneState: newGameState.getPlayerShip().getDroneList()){
            events.add(new Event(SavedGameParser.StoreItemType.DRONE, Constants.EventType.START, 1,0, droneState.getDroneId(), jump));
        }

        // Augments
        for (String augmentId: newGameState.getPlayerShip().getAugmentIdList()){
            events.add(new Event(SavedGameParser.StoreItemType.AUGMENT, Constants.EventType.START, 1,0, augmentId, jump));
        }

        // INFO We assume here that the player ship does not start with anything in the cargo, which is true for vanilla FTL
        // TODO if the run was started late the beacons explored stat is wrong...

        return eventBox;
    }

    public EventBox getEventsFromGameStateComparison(SavedGameParser.SavedGameState lastGameState, SavedGameParser.SavedGameState currentGameState, Jump jump) {

        List<Event> events = new ArrayList<>();
        List<Event> lastJumpEvents = new ArrayList<>();
        EventBox box = new EventBox(events, lastJumpEvents);

        if (lastGameState == null || currentGameState == null){
            return box;
        }

        CargoConsolidator oldCargo = new CargoConsolidator(lastGameState);
        CargoConsolidator newCargo = new CargoConsolidator(currentGameState);

        // TODO delete?

        Event event;

        boolean jumped = true;
        SavedGameParser.StoreState newStore = currentGameState.getBeaconList().get(currentGameState.getCurrentBeaconId()).getStore();
        SavedGameParser.StoreState oldStore = lastGameState.getBeaconList().get(lastGameState.getCurrentBeaconId()).getStore();
        boolean store_present = false;

        List<String> boughtItems = new ArrayList<>();


        if (lastGameState.getCurrentBeaconId() == currentGameState.getCurrentBeaconId()){
            jumped = false;
        } else {
            events.add(new Event(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.USE, 1, 0, Constants.Resource.FUEL.name(), jump));
        }

        if (!jumped && newStore != null){
            int fuel_diff = lastGameState.getBeaconList().get(lastGameState.getCurrentBeaconId()).getStore().getFuel() -
                    currentGameState.getBeaconList().get(currentGameState.getCurrentBeaconId()).getStore().getFuel();
            if (fuel_diff > 0){
                events.add(new Event(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.BUY, fuel_diff, GausmanUtil.getCostResource(Constants.Resource.FUEL.name(), fuel_diff, currentGameState.getSectorNumber()), "FUEL", jump));
            }
            int missiles_diff = lastGameState.getBeaconList().get(lastGameState.getCurrentBeaconId()).getStore().getMissiles() -
                    currentGameState.getBeaconList().get(currentGameState.getCurrentBeaconId()).getStore().getMissiles();
            if (missiles_diff > 0){
                events.add(new Event(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.BUY, missiles_diff,GausmanUtil.getCostResource(Constants.Resource.MISSILE.name(), missiles_diff, currentGameState.getSectorNumber()), "MISSILE", jump));
            }
            int drones_diff = lastGameState.getBeaconList().get(lastGameState.getCurrentBeaconId()).getStore().getDroneParts() -
                    currentGameState.getBeaconList().get(currentGameState.getCurrentBeaconId()).getStore().getDroneParts();
            if (drones_diff > 0) {
                events.add(new Event(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.BUY, fuel_diff,GausmanUtil.getCostResource(Constants.Resource.DRONE.name(), drones_diff, currentGameState.getSectorNumber()), "DRONE_PART", jump));
            }

            for (int i = 0; i < newStore.getShelfList().size(); i++){
                for (int j = 0; j < newStore.getShelfList().get(i).getItems().size(); j++){
                    if (!newStore.getShelfList().get(i).getItems().get(j).isAvailable()){
                        if (oldStore.getShelfList().get(i).getItems().get(j).isAvailable()){


                            // Drone control comes with a free drone
                            // extra data: 0 -> DEFENSE_1, 1 -> REPAIR, 2 -> COMBAT_1
                            // the drone systems itself costs 60 scrap, and half of the price of the drone that comes with it is added
                            int localCost = 0;
                            if (newStore.getShelfList().get(i).getItems().get(j).getItemId().equals("drones")){
                                if (newStore.getShelfList().get(i).getItems().get(j).getExtraData() == 0){
                                    events.add(new Event(SavedGameParser.StoreItemType.DRONE, Constants.EventType.BUY, 1, 0,"DEFENSE_1", jump));
                                    boughtItems.add("DEFENSE_1");
                                    localCost += 25;
                                } else if (newStore.getShelfList().get(i).getItems().get(j).getExtraData() == 1) {
                                    events.add(new Event(SavedGameParser.StoreItemType.DRONE, Constants.EventType.BUY, 1, 0,"REPAIR", jump));
                                    boughtItems.add("REPAIR");
                                    localCost += 15;
                                } else if (newStore.getShelfList().get(i).getItems().get(j).getExtraData() == 2){
                                    events.add(new Event(SavedGameParser.StoreItemType.DRONE, Constants.EventType.BUY, 1, 0,"COMBAT_1", jump));
                                    boughtItems.add("COMBAT_1");
                                    localCost += 25;
                                }
                            }
                            localCost += GausmanUtil.getCostStoreItemType(newStore.getShelfList().get(i).getItemType(), newStore.getShelfList().get(i).getItems().get(j));
                            events.add(new Event(newStore.getShelfList().get(i).getItemType(), Constants.EventType.BUY, 1,
                                    localCost,
                                    newStore.getShelfList().get(i).getItems().get(j).getItemId(), jump));
                            boughtItems.add(newStore.getShelfList().get(i).getItems().get(j).getItemId());



                        }
                    }
                }
            }

            // repair
            int repairCountDiff = currentGameState.getStateVar("store_repair") - lastGameState.getStateVar("store_repair");
            if (repairCountDiff > 0){
                events.add(new Event(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.BUY, repairCountDiff, GausmanUtil.getCostResource(Constants.Resource.REPAIR.name(), repairCountDiff, currentGameState.getSectorNumber()), "REPAIR", jump));
            }
        }

        // drones/missiles used
        int missileUsedDiff = currentGameState.getStateVar("used_missile") - lastGameState.getStateVar("used_missile");
        if (missileUsedDiff > 0){
            events.add(new Event(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.USE, missileUsedDiff, 0, Constants.Resource.MISSILE.toString(), jump));
        }
        int droneUsedDiff = currentGameState.getStateVar("used_drone") - lastGameState.getStateVar("used_drone");
        if (droneUsedDiff > 0){
            events.add(new Event(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.USE, droneUsedDiff, 0, Constants.Resource.DRONE.name(), jump));
        }


        ArrayList<String> newWeapons = new ArrayList<>(newCargo.weaponList);
        ArrayList<String> newDrones = new ArrayList<>(newCargo.droneList);
        ArrayList<String> newAugments = new ArrayList<>(newCargo.augmentList);
        for (String weapon: oldCargo.weaponList){
            newWeapons.remove(weapon);
        }

        for (String drone: oldCargo.droneList){
            newDrones.remove(drone);
        }

        for (String augment: oldCargo.augmentList){
            newAugments.remove(augment);
        }

        for (String boughtItem: boughtItems){
            newWeapons.remove(boughtItem);
            newDrones.remove(boughtItem);
            newAugments.remove(boughtItem);
        }

        for (String weapon: newWeapons){
            events.add(new Event(SavedGameParser.StoreItemType.WEAPON, Constants.EventType.REWARD, 1,GausmanUtil.getCostSellStoreItemType(SavedGameParser.StoreItemType.WEAPON, weapon), weapon, jump));
        }

        for (String drone: newDrones){
            events.add(new Event(SavedGameParser.StoreItemType.DRONE, Constants.EventType.REWARD, 1,GausmanUtil.getCostSellStoreItemType(SavedGameParser.StoreItemType.DRONE, drone), drone, jump));
        }

        for (String augment: newAugments){
            events.add(new Event(SavedGameParser.StoreItemType.AUGMENT, Constants.EventType.REWARD, 1,GausmanUtil.getCostSellStoreItemType(SavedGameParser.StoreItemType.AUGMENT, augment), augment, jump));
        }

        // Removed Items
        Constants.EventType typeNow;
        boolean addToLastJump = false;
        List<Event> tempSellEvents = new ArrayList<>();

        if (newStore != null || oldStore != null){
            typeNow = Constants.EventType.SELL;
            if (newStore == null){
                addToLastJump = true;
            }
        } else {
            typeNow = Constants.EventType.DISCARD;
        }

        ArrayList<String> removedWeapons = new ArrayList<>(oldCargo.weaponList);
        for (String weapon: newCargo.weaponList){
            removedWeapons.remove(weapon);
        }

        ArrayList<String> removedDrones = new ArrayList<>(oldCargo.droneList);
        for (String drone: newCargo.droneList){
            removedDrones.remove(drone);
        }

        ArrayList<String> removedAugments = new ArrayList<>(oldCargo.augmentList);
        for (String augment: newCargo.augmentList){
            removedAugments.remove(augment);
        }

        int sellCost = 0;
        for (String weapon: removedWeapons){
            if (typeNow == Constants.EventType.SELL){
                sellCost = GausmanUtil.getCostSellStoreItemType(SavedGameParser.StoreItemType.WEAPON, weapon);
            } else {
                sellCost = 0;
            }
            tempSellEvents.add(new Event(SavedGameParser.StoreItemType.WEAPON, typeNow, 1,sellCost, weapon, jump));
        }

        for (String drone: removedDrones){
            if (typeNow == Constants.EventType.SELL){
                sellCost = GausmanUtil.getCostSellStoreItemType(SavedGameParser.StoreItemType.DRONE, drone);
            } else {
                sellCost = 0;
            }
            tempSellEvents.add(new Event(SavedGameParser.StoreItemType.DRONE, typeNow, 1,sellCost, drone, jump));
        }

        for (String augment: removedAugments){
            if (typeNow == Constants.EventType.SELL){
                sellCost = GausmanUtil.getCostSellStoreItemType(SavedGameParser.StoreItemType.AUGMENT, augment);
            } else {
                sellCost = 0;
            }
            tempSellEvents.add(new Event(SavedGameParser.StoreItemType.AUGMENT, typeNow, 1,sellCost, augment, jump));
        }

        if (addToLastJump){
            lastJumpEvents.addAll(tempSellEvents);
        } else {
            events.addAll(tempSellEvents);
        }

        // ship power
        int newReactorCapacity = currentGameState.getPlayerShip().getReservePowerCapacity();
        int oldReactorCapacity = lastGameState.getPlayerShip().getReservePowerCapacity();

        int newCapacitySV = currentGameState.getStateVar("reactor_upgrade");
        int oldCapacitySV = lastGameState.getStateVar("reactor_upgrade");

        if (newCapacitySV > oldCapacitySV){
            while (newCapacitySV > oldCapacitySV){
                oldReactorCapacity++;
                oldCapacitySV++;
                events.add(new Event(SavedGameParser.StoreItemType.REACTOR, Constants.EventType.UPGRADE, 1,
                        GausmanUtil.getReactorUpgradeCost(oldReactorCapacity), SavedGameParser.StoreItemType.REACTOR.name(), jump));
            }
        }

        // reactor upgrades from events are not included in the state vars
        int reactorDiff = newReactorCapacity - oldReactorCapacity;
        if (reactorDiff > 0){
            events.add(new Event(SavedGameParser.StoreItemType.REACTOR, Constants.EventType.REWARD, reactorDiff,
                    GausmanUtil.getReactorUpgradeCost(oldCapacitySV), SavedGameParser.StoreItemType.REACTOR.name(), jump));
        }


        // ship upgrades
        SavedGameParser.ShipState newShipState = currentGameState.getPlayerShip();
        SavedGameParser.ShipState oldShipState = lastGameState.getPlayerShip();

        Map<SavedGameParser.SystemType, List<SavedGameParser.SystemState>> systemsMap = currentGameState.getPlayerShip().getSystemsMap();

        SavedGameParser.SystemState oldSystemState;
        int systemDiff = 0;
        for (Map.Entry<SavedGameParser.SystemType, List<SavedGameParser.SystemState>> entry: systemsMap.entrySet()){
            for (SavedGameParser.SystemState newSystemState: entry.getValue()){
                oldSystemState = oldShipState.getSystem(newSystemState.getSystemType());
                systemDiff = newSystemState.getCapacity() - oldSystemState.getCapacity();
                if (systemDiff > 0 && oldSystemState.getCapacity() > 0){
                    events.add(new Event(SavedGameParser.StoreItemType.SYSTEM, Constants.EventType.UPGRADE, systemDiff,
                            GausmanUtil.getUpgradeCostSystem(newSystemState.getSystemType().getId(), oldSystemState.getCapacity(), newSystemState.getCapacity()), newSystemState.getSystemType().getId(), jump));

                }
            }
        }

        // TODO free upgrade system events

        // Crew

        return box;

    }

    class CargoConsolidator {
        List<String> weaponList = new ArrayList<>();
        List<String> droneList = new ArrayList<>();
        List<String> augmentList = new ArrayList<>();

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

            for (String augment: gameState.getPlayerShip().getAugmentIdList()){
                augmentList.add(augment);
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
