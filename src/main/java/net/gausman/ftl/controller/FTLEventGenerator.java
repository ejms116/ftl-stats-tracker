package net.gausman.ftl.controller;

import net.blerf.ftl.constants.Difficulty;
import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.xml.DroneBlueprint;
import net.blerf.ftl.xml.ShipBlueprint;
import net.blerf.ftl.xml.WeaponBlueprint;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.FTLEventBox;
import net.gausman.ftl.model.run.FTLRunEvent;
import net.gausman.ftl.util.GausmanUtil;
import net.gausman.ftl.view.OverviewListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Deprecated
public class FTLEventGenerator {

    private DataManager dm = DataManager.get();

    public List<OverviewListItem> getOverviewList(SavedGameParser.SavedGameState newGameState, int jumpNumber){
        List<OverviewListItem> overview = new ArrayList<>();

        OverviewListItem item;

        item = new OverviewListItem("Ship", dm.getShip(newGameState.getPlayerShipBlueprintId()).getName().getTextValue());
        overview.add(item);

        item = new OverviewListItem("Difficulty", newGameState.getDifficulty().name());
        overview.add(item);

        item = new OverviewListItem("Sector", Integer.toString(newGameState.getSectorNumber()+1));
        overview.add(item);

        item = new OverviewListItem("Jump", Integer.toString(jumpNumber));
        overview.add(item);

        item = new OverviewListItem("BeaconId", Integer.toString(newGameState.getCurrentBeaconId()));
        overview.add(item);

        item = new OverviewListItem("Beacons explored", Integer.toString(newGameState.getTotalBeaconsExplored()));
        overview.add(item);

        item = new OverviewListItem("Ships destroyed", Integer.toString(newGameState.getTotalShipsDefeated()));
        overview.add(item);

        item = new OverviewListItem("Scrap collected", Integer.toString(newGameState.getTotalScrapCollected()));
        overview.add(item);

        item = new OverviewListItem("Sector seed", Integer.toString(newGameState.getSectorTreeSeed()));
        overview.add(item);

        return overview;
    }

    public FTLEventBox getFuelUsedEventBox(){
        List<FTLRunEvent> events = new ArrayList<>();
        List<FTLRunEvent> lastJumpEvents = new ArrayList<>();
        FTLEventBox eventBox = new FTLEventBox(events, lastJumpEvents);

        events.add(new FTLRunEvent(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.USE, 1, 0, "FUEL"));

        return eventBox;
    }

    public FTLEventBox getEventsStartRun(SavedGameParser.SavedGameState newGameState){
        List<FTLRunEvent> events = new ArrayList<>();
        List<FTLRunEvent> lastJumpEvents = new ArrayList<>();
        FTLEventBox eventBox = new FTLEventBox(events, lastJumpEvents);

        ShipBlueprint shipBlueprint = dm.getShip(newGameState.getPlayerShipBlueprintId());

        SavedGameParser.ShipState shipState = newGameState.getPlayerShip();

        int startingScrap = 0;
        if (newGameState.getDifficulty().equals(Difficulty.NORMAL)){
            startingScrap += 10;
        } else if (newGameState.getDifficulty().equals(Difficulty.EASY)){
            startingScrap += 30;
        }

        // Resources
        events.add(new FTLRunEvent(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.START, shipBlueprint.getHealth().amount, 0, "HULL"));
        events.add(new FTLRunEvent(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.START, 16, 0, "FUEL"));
        events.add(new FTLRunEvent(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.START, shipBlueprint.getDroneList().drones, 0,"DRONE_PART"));
        events.add(new FTLRunEvent(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.START, shipBlueprint.getWeaponList().missiles, 0,"MISSILE"));
        events.add(new FTLRunEvent(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.START, startingScrap, 0,"SCRAP"));

        // Power
        events.add(new FTLRunEvent(SavedGameParser.StoreItemType.REACTOR, Constants.EventType.START, shipBlueprint.getMaxPower().amount, 0, "REACTOR"));

        // Crew
        for (SavedGameParser.CrewState crewState: newGameState.getPlayerShip().getCrewList()){
            events.add(new FTLRunEvent(SavedGameParser.StoreItemType.CREW, Constants.EventType.START, 1, 0,crewState.getRace().getId()));
        }

        // Systems
        for (Map.Entry<SavedGameParser.SystemType, List<SavedGameParser.SystemState>> entry: newGameState.getPlayerShip().getSystemsMap().entrySet()){
            for (SavedGameParser.SystemState systemState: entry.getValue()){
                int capacity = systemState.getCapacity();
                if (capacity == 0){
                    continue;
                }
                events.add(new FTLRunEvent(SavedGameParser.StoreItemType.SYSTEM, Constants.EventType.START, capacity, 0, systemState.getSystemType().getId()));
            }
        }

        // Weapons
        for (SavedGameParser.WeaponState weaponState: newGameState.getPlayerShip().getWeaponList()){
            events.add(new FTLRunEvent(SavedGameParser.StoreItemType.WEAPON, Constants.EventType.START, 1,0, weaponState.getWeaponId()));
        }

        // Drones
        for (SavedGameParser.DroneState droneState: newGameState.getPlayerShip().getDroneList()){
            events.add(new FTLRunEvent(SavedGameParser.StoreItemType.DRONE, Constants.EventType.START, 1,0, droneState.getDroneId()));
        }

        // Augments
        for (String augmentId: newGameState.getPlayerShip().getAugmentIdList()){
            events.add(new FTLRunEvent(SavedGameParser.StoreItemType.AUGMENT, Constants.EventType.START, 1,0, augmentId));
        }

        // INFO We assume here that the player ship does not start with anything in the cargo, which is true for vanilla FTL
        // TODO if the run was started late the beacons explored stat is wrong...

        return eventBox;
    }

    public FTLEventBox getEventsFromGameStateComparison(SavedGameParser.SavedGameState oldGameState, SavedGameParser.SavedGameState newGameState){

        List<FTLRunEvent> events = new ArrayList<>();
        List<FTLRunEvent> lastJumpEvents = new ArrayList<>();
        FTLEventBox box = new FTLEventBox(events, lastJumpEvents);

        if (oldGameState == null || newGameState == null){
            return box;
        }

        CargoConsolidator oldCargo = new CargoConsolidator(oldGameState);
        CargoConsolidator newCargo = new CargoConsolidator(newGameState);

        // TODO delete?

        FTLRunEvent event;

        boolean jumped = true;
        SavedGameParser.StoreState newStore = newGameState.getBeaconList().get(newGameState.getCurrentBeaconId()).getStore();
        SavedGameParser.StoreState oldStore = oldGameState.getBeaconList().get(oldGameState.getCurrentBeaconId()).getStore();
        boolean store_present = false;

        List<String> boughtItems = new ArrayList<>();


        if (oldGameState.getCurrentBeaconId() == newGameState.getCurrentBeaconId()){
            jumped = false;
        } else {
            events.add(new FTLRunEvent(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.USE, 1, 0, "FUEL"));
        }

        if (!jumped && newStore != null){
            int fuel_diff = oldGameState.getBeaconList().get(oldGameState.getCurrentBeaconId()).getStore().getFuel() -
                    newGameState.getBeaconList().get(newGameState.getCurrentBeaconId()).getStore().getFuel();
            if (fuel_diff > 0){
                events.add(new FTLRunEvent(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.BUY, fuel_diff, GausmanUtil.getCostResource("FUEL", fuel_diff, newGameState.getSectorNumber()), "FUEL"));
            }
            int missiles_diff = oldGameState.getBeaconList().get(oldGameState.getCurrentBeaconId()).getStore().getMissiles() -
                    newGameState.getBeaconList().get(newGameState.getCurrentBeaconId()).getStore().getMissiles();
            if (missiles_diff > 0){
                events.add(new FTLRunEvent(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.BUY, missiles_diff,GausmanUtil.getCostResource("MISSILE", missiles_diff, newGameState.getSectorNumber()), "MISSILE"));
            }
            int drones_diff = oldGameState.getBeaconList().get(oldGameState.getCurrentBeaconId()).getStore().getDroneParts() -
                    newGameState.getBeaconList().get(newGameState.getCurrentBeaconId()).getStore().getDroneParts();
            if (drones_diff > 0) {
                events.add(new FTLRunEvent(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.BUY, fuel_diff,GausmanUtil.getCostResource("DRONE_PART", drones_diff, newGameState.getSectorNumber()), "DRONE_PART"));
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
                                    events.add(new FTLRunEvent(SavedGameParser.StoreItemType.DRONE, Constants.EventType.BUY, 1, 0,"DEFENSE_1"));
                                    boughtItems.add("DEFENSE_1");
                                    localCost += 25;
                                } else if (newStore.getShelfList().get(i).getItems().get(j).getExtraData() == 1) {
                                    events.add(new FTLRunEvent(SavedGameParser.StoreItemType.DRONE, Constants.EventType.BUY, 1, 0,"REPAIR"));
                                    boughtItems.add("REPAIR");
                                    localCost += 15;
                                } else if (newStore.getShelfList().get(i).getItems().get(j).getExtraData() == 2){
                                    events.add(new FTLRunEvent(SavedGameParser.StoreItemType.DRONE, Constants.EventType.BUY, 1, 0,"COMBAT_1"));
                                    boughtItems.add("COMBAT_1");
                                    localCost += 25;
                                }
                            }
                            localCost += GausmanUtil.getCostStoreItemType(newStore.getShelfList().get(i).getItemType(), newStore.getShelfList().get(i).getItems().get(j));
                            events.add(new FTLRunEvent(newStore.getShelfList().get(i).getItemType(), Constants.EventType.BUY, 1,
                                    localCost,
                                    newStore.getShelfList().get(i).getItems().get(j).getItemId()));
                            boughtItems.add(newStore.getShelfList().get(i).getItems().get(j).getItemId());



                        }
                    }
                }
            }

            // repair
            int repairCountDiff = newGameState.getStateVar("store_repair") - oldGameState.getStateVar("store_repair");
            if (repairCountDiff > 0){
                events.add(new FTLRunEvent(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.BUY, repairCountDiff, GausmanUtil.getCostResource("REPAIR", repairCountDiff, newGameState.getSectorNumber()), "REPAIR"));
            }
        }

        // drones/missiles used
        int missileUsedDiff = newGameState.getStateVar("used_missile") - oldGameState.getStateVar("used_missile");
        if (missileUsedDiff > 0){
            events.add(new FTLRunEvent(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.USE, missileUsedDiff, 0, "MISSILE"));
        }
        int droneUsedDiff = newGameState.getStateVar("used_drone") - oldGameState.getStateVar("used_drone");
        if (droneUsedDiff > 0){
            events.add(new FTLRunEvent(SavedGameParser.StoreItemType.RESOURCE, Constants.EventType.USE, droneUsedDiff, 0, "DRONE"));
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
            events.add(new FTLRunEvent(SavedGameParser.StoreItemType.WEAPON, Constants.EventType.REWARD, 1,GausmanUtil.getCostSellStoreItemType(SavedGameParser.StoreItemType.WEAPON, weapon), weapon));
        }

        for (String drone: newDrones){
            events.add(new FTLRunEvent(SavedGameParser.StoreItemType.DRONE, Constants.EventType.REWARD, 1,GausmanUtil.getCostSellStoreItemType(SavedGameParser.StoreItemType.DRONE, drone), drone));
        }

        for (String augment: newAugments){
            events.add(new FTLRunEvent(SavedGameParser.StoreItemType.AUGMENT, Constants.EventType.REWARD, 1,GausmanUtil.getCostSellStoreItemType(SavedGameParser.StoreItemType.AUGMENT, augment), augment));
        }

        // Removed Items
        Constants.EventType typeNow;
        boolean addToLastJump = false;
        List<FTLRunEvent> tempSellEvents = new ArrayList<>();

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
            tempSellEvents.add(new FTLRunEvent(SavedGameParser.StoreItemType.WEAPON, typeNow, 1,sellCost, weapon));
        }

        for (String drone: removedDrones){
            if (typeNow == Constants.EventType.SELL){
                sellCost = GausmanUtil.getCostSellStoreItemType(SavedGameParser.StoreItemType.DRONE, drone);
            } else {
                sellCost = 0;
            }
            tempSellEvents.add(new FTLRunEvent(SavedGameParser.StoreItemType.DRONE, typeNow, 1,sellCost, drone));
        }

        for (String augment: removedAugments){
            if (typeNow == Constants.EventType.SELL){
                sellCost = GausmanUtil.getCostSellStoreItemType(SavedGameParser.StoreItemType.AUGMENT, augment);
            } else {
                sellCost = 0;
            }
            tempSellEvents.add(new FTLRunEvent(SavedGameParser.StoreItemType.AUGMENT, typeNow, 1,sellCost, augment));
        }

        if (addToLastJump){
            lastJumpEvents.addAll(tempSellEvents);
        } else {
            events.addAll(tempSellEvents);
        }

        // ship power
        int newReactorCapacity = newGameState.getPlayerShip().getReservePowerCapacity();
        int oldReactorCapacity = oldGameState.getPlayerShip().getReservePowerCapacity();

        int newCapacitySV = newGameState.getStateVar("reactor_upgrade");
        int oldCapacitySV = oldGameState.getStateVar("reactor_upgrade");

        if (newCapacitySV > oldCapacitySV){
            while (newCapacitySV > oldCapacitySV){
                oldReactorCapacity++;
                oldCapacitySV++;
                events.add(new FTLRunEvent(SavedGameParser.StoreItemType.REACTOR, Constants.EventType.UPGRADE, 1,
                       GausmanUtil.getReactorUpgradeCost(oldReactorCapacity) ,"REACTOR"));
            }
        }

        // reactor upgrades from events are not included in the state vars
        int reactorDiff = newReactorCapacity - oldReactorCapacity;
        if (reactorDiff > 0){
            events.add(new FTLRunEvent(SavedGameParser.StoreItemType.REACTOR, Constants.EventType.REWARD, reactorDiff,
                    GausmanUtil.getReactorUpgradeCost(oldCapacitySV) ,"REACTOR"));
        }


        // ship upgrades
        SavedGameParser.ShipState newShipState = newGameState.getPlayerShip();
        SavedGameParser.ShipState oldShipState = oldGameState.getPlayerShip();

        Map<SavedGameParser.SystemType, List<SavedGameParser.SystemState>> systemsMap = newGameState.getPlayerShip().getSystemsMap();

        SavedGameParser.SystemState oldSystemState;
        int systemDiff = 0;
        for (Map.Entry<SavedGameParser.SystemType, List<SavedGameParser.SystemState>> entry: systemsMap.entrySet()){
            for (SavedGameParser.SystemState newSystemState: entry.getValue()){
                oldSystemState = oldShipState.getSystem(newSystemState.getSystemType());
                systemDiff = newSystemState.getCapacity() - oldSystemState.getCapacity();
                if (systemDiff > 0 && oldSystemState.getCapacity() > 0){
                    events.add(new FTLRunEvent(SavedGameParser.StoreItemType.SYSTEM, Constants.EventType.UPGRADE, systemDiff,
                            GausmanUtil.getUpgradeCostSystem(newSystemState.getSystemType().getId(), oldSystemState.getCapacity(), newSystemState.getCapacity()) ,newSystemState.getSystemType().getId()));

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
