package net.gausman.ftl.service;

import net.blerf.ftl.constants.Difficulty;
import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.SavedGameParser.StateVar;
import net.blerf.ftl.xml.*;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Crew;
import net.gausman.ftl.model.change.*;
import net.gausman.ftl.model.record.*;
import net.gausman.ftl.util.GausmanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

public class EventService {
    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private static final String MISSILE_SELL_EVENT = "SELL_MISSILES";
    private static final String DRONE_SELL_EVENT = "SELL_DRONES";

    private DataManager dm = DataManager.get();

    private List<SavedGameParser.EncounterState> encounterStateList = new ArrayList<>();

    public EventBox getFuelUsedEventBox(Jump jump){
        List<Event> events = new ArrayList<>();
        List<Event> lastJumpEvents = new ArrayList<>();
        EventBox eventBox = new EventBox(events, lastJumpEvents);

        events.add(new ResourceEvent(Constants.EventType.USE, 1, 0, Constants.Resource.FUEL.name(), jump));

        return eventBox;
    }

    public EventBox getEventsStartRun(SavedGameParser.SavedGameState newGameState, Jump jump){
        // For testing
        encounterStateList.add(newGameState.getEncounter());

        List<Event> events = new ArrayList<>();
        List<Event> lastJumpEvents = new ArrayList<>();
        EventBox eventBox = new EventBox(events, lastJumpEvents);

        ShipBlueprint shipBlueprint = dm.getShip(newGameState.getPlayerShipBlueprintId());

        SavedGameParser.ShipState shipState = newGameState.getPlayerShip();

        // GeneralInfo
        events.add(new GeneralEvent(Constants.EventType.START, 0, newGameState.getPlayerShipName(), jump, Constants.General.SHIP_NAME));
        events.add(new GeneralEvent(Constants.EventType.START, 0, GausmanUtil.convertBlueprintName(newGameState.getPlayerShipBlueprintId()) , jump, Constants.General.SHIP_BLUEPRINT));
        events.add(new GeneralEvent(Constants.EventType.START, 0, newGameState.getDifficulty().toString(), jump, Constants.General.DIFFICULTY));


        int startingScrap = 0;
        if (newGameState.getDifficulty().equals(Difficulty.NORMAL)){
            startingScrap += 10;
        } else if (newGameState.getDifficulty().equals(Difficulty.EASY)){
            startingScrap += 30;
        }

        // Resources
        events.add(new ResourceEvent(Constants.EventType.START, shipBlueprint.getHealth().amount, 0, Constants.Resource.HULL.name(), jump));
        events.add(new ResourceEvent(Constants.EventType.START, 16, 0, Constants.Resource.FUEL.name(), jump));
        if (shipBlueprint.getDroneList() != null){
            events.add(new ResourceEvent(Constants.EventType.START, shipBlueprint.getDroneList().drones, 0,Constants.Resource.DRONE.name(), jump));
        }
        events.add(new ResourceEvent(Constants.EventType.START, shipBlueprint.getWeaponList().missiles, 0,Constants.Resource.MISSILE.name(), jump));
        events.add(new GeneralEvent(Constants.EventType.START,0, startingScrap, "scrap collected", jump, Constants.General.SCRAP_COLLECTED));

        // Power
        events.add(new ReactorEvent(Constants.EventType.START, shipBlueprint.getMaxPower().amount, 0, Constants.Reactor.POWER_BAR.name(), jump));

        // Crew
        int index = 0;
        for (SavedGameParser.CrewState crewState : newGameState.getPlayerShip().getCrewList()) {
            NewCrewEvent event = new NewCrewEvent(
                    SavedGameParser.StoreItemType.CREW,
                    Constants.EventType.START,
                    1,
                    GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.CREW, crewState.getRace().getId()),
                    GausmanUtil.getCrewTypeName(crewState.getRace().getId()) + " - " + crewState.getName(),
                    jump
            );
            event.setCrewPosition(index);
            event.setCrew(new Crew(crewState, Constants.EventType.START));
            events.add(event);
            index++;
        }


        // Systems
        for (Map.Entry<SavedGameParser.SystemType, List<SavedGameParser.SystemState>> entry: newGameState.getPlayerShip().getSystemsMap().entrySet()){
            for (SavedGameParser.SystemState systemState: entry.getValue()){
                int capacity = systemState.getCapacity();
                if (capacity == 0){
                    continue;
                }
                SystemEvent systemEvent = new SystemEvent(
                        Constants.EventType.START, capacity,
                        0, // todo scrap of starting systems (Buy+uprades)
                        systemState.getSystemType().getId(),
                        jump,
                        systemState.getSystemType(),
                        false);

                events.add(systemEvent);
            }
        }

        // Weapons
        for (SavedGameParser.WeaponState weaponState: newGameState.getPlayerShip().getWeaponList()){
            events.add(new WeaponEvent(Constants.EventType.START, 1, GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.WEAPON, weaponState.getWeaponId()), weaponState.getWeaponId(), jump));
        }

        // Drones
        for (SavedGameParser.DroneState droneState: newGameState.getPlayerShip().getDroneList()){
            events.add(new DroneEvent(Constants.EventType.START, 1,GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.DRONE, droneState.getDroneId()), droneState.getDroneId(), jump));
        }

        // Augments
        for (String augmentId: newGameState.getPlayerShip().getAugmentIdList()){
            events.add(new AugmentEvent(Constants.EventType.START, 1,GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.AUGMENT, augmentId), augmentId, jump));
        }

        // INFO We assume here that the player ship does not start with anything in the cargo, which is true for vanilla FTL
        // TODO if the run was started late the beacons explored stat is wrong...

        eventBox.setEncounterState(newGameState.getEncounter());
        return eventBox;
    }

    public EventBox getEventsFromGameStateComparison(SavedGameParser.SavedGameState lastGameState, SavedGameParser.SavedGameState currentGameState, Jump jump) {
        // For testing
        encounterStateList.add(currentGameState.getEncounter());

        List<Event> events = new ArrayList<>();
        List<Event> lastJumpEvents = new ArrayList<>();
        EventBox box = new EventBox(events, lastJumpEvents);

        if (lastGameState == null || currentGameState == null){
            return box;
        }


        CargoConsolidator oldCargo = new CargoConsolidator(lastGameState);
        CargoConsolidator newCargo = new CargoConsolidator(currentGameState);

        // General Info
        int beaconsExploredDiff = currentGameState.getTotalBeaconsExplored() - lastGameState.getTotalBeaconsExplored();
        if (beaconsExploredDiff > 0){
            events.add(new GeneralEvent(Constants.EventType.GENERAL, beaconsExploredDiff, "bacon", jump, Constants.General.BEACONS_EXPLORED));
        }

        int shipsDestroyedDiff = currentGameState.getTotalShipsDefeated() - lastGameState.getTotalShipsDefeated();
        if (shipsDestroyedDiff > 0){
            events.add(new GeneralEvent(Constants.EventType.GENERAL, shipsDestroyedDiff, "ships destroyed", jump, Constants.General.SHIPS_DESTROYED));
        }

        int scrapCollected = currentGameState.getTotalScrapCollected() - lastGameState.getTotalScrapCollected();
        if (scrapCollected > 0){
            events.add(new GeneralEvent(Constants.EventType.GENERAL,0, scrapCollected, "scrap collected", jump, Constants.General.SCRAP_COLLECTED));
        }

        int crewHiredDiff = currentGameState.getTotalCrewHired() - lastGameState.getTotalCrewHired();
        if (crewHiredDiff > 0){
            events.add(new GeneralEvent(Constants.EventType.GENERAL, crewHiredDiff, "crew hired", jump, Constants.General.CREW_HIRED));
        }


        boolean jumped = true;
        SavedGameParser.StoreState newStore = currentGameState.getBeaconList().get(currentGameState.getCurrentBeaconId()).getStore();
        SavedGameParser.StoreState oldStore = lastGameState.getBeaconList().get(lastGameState.getCurrentBeaconId()).getStore();

        List<String> boughtItems = new ArrayList<>();
        List<String> boughtCrew = new ArrayList<>();

        if (lastGameState.getCurrentBeaconId() == currentGameState.getCurrentBeaconId()){
            jumped = false;
        } else {
            events.add(new ResourceEvent(Constants.EventType.USE, 1, 0, Constants.Resource.FUEL.name(), jump));
        }

        int repairCountDiff = 0;
        int fuelBought = 0;
        int missilesBought = 0;
        int dronesBought = 0;

        if (!jumped && newStore != null){


            for (int i = 0; i < newStore.getShelfList().size(); i++){
                for (int j = 0; j < newStore.getShelfList().get(i).getItems().size(); j++){
                    if (!newStore.getShelfList().get(i).getItems().get(j).isAvailable()){
                        if (oldStore.getShelfList().get(i).getItems().get(j).isAvailable()){


                            // Drone control comes with a free drone
                            // extra data: 0 -> DEFENSE_1, 1 -> REPAIR, 2 -> COMBAT_1
                            // the drone systems itself costs 60 scrap, and half of the price of the drone that comes with it is added
                            // so the cost of the systems depends on the drones that comes with it, and the drone then is free
                            int localCost = 0;
                            if (newStore.getShelfList().get(i).getItems().get(j).getItemId().equals("drones")){
                                if (newStore.getShelfList().get(i).getItems().get(j).getExtraData() == 0){
                                    events.add(new DroneEvent(Constants.EventType.REWARD, 1, GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.DRONE, "DEFENSE_1"),"DEFENSE_1", jump));
                                    boughtItems.add("DEFENSE_1");
                                    localCost += 25;
                                } else if (newStore.getShelfList().get(i).getItems().get(j).getExtraData() == 1) {
                                    events.add(new DroneEvent(Constants.EventType.REWARD, 1, GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.DRONE, "REPAIR"),"REPAIR", jump));
                                    boughtItems.add("REPAIR");
                                    localCost += 15;
                                } else if (newStore.getShelfList().get(i).getItems().get(j).getExtraData() == 2){
                                    events.add(new DroneEvent(Constants.EventType.REWARD, 1, GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.DRONE, "COMBAT_1"),"COMBAT_1", jump));
                                    boughtItems.add("COMBAT_1");
                                    localCost += 25;
                                }
                            }
                            localCost += GausmanUtil.getCostStoreItemType(newStore.getShelfList().get(i).getItemType(), newStore.getShelfList().get(i).getItems().get(j));
                            // Crew is handled later because it's more complex (by comparing the crewlist)
                            // We just keep track of what crew we bought so we can use it then
                            if (newStore.getShelfList().get(i).getItemType().equals(SavedGameParser.StoreItemType.CREW)){
                                boughtCrew.add(newStore.getShelfList().get(i).getItems().get(j).getItemId());
                            } else {
                                Event boughtItemEvent;
                                switch (newStore.getShelfList().get(i).getItemType()){
                                    case WEAPON -> boughtItemEvent = new WeaponEvent(Constants.EventType.BUY, 1, localCost, newStore.getShelfList().get(i).getItems().get(j).getItemId(), jump);
                                    case DRONE -> boughtItemEvent = new DroneEvent(Constants.EventType.BUY, 1, localCost, newStore.getShelfList().get(i).getItems().get(j).getItemId(), jump);
                                    case AUGMENT -> boughtItemEvent = new AugmentEvent(Constants.EventType.BUY, 1, localCost, newStore.getShelfList().get(i).getItems().get(j).getItemId(), jump);
                                    case SYSTEM -> boughtItemEvent = new SystemEvent(Constants.EventType.BUY, 1, localCost, newStore.getShelfList().get(i).getItems().get(j).getItemId(), jump, SavedGameParser.SystemType.findById(newStore.getShelfList().get(i).getItems().get(j).getItemId()), false);

                                    default -> throw new IllegalArgumentException("Unsupported ItemType: " + newStore.getShelfList().get(i).getItemType());
                                }
                                events.add(boughtItemEvent);

                                boughtItems.add(newStore.getShelfList().get(i).getItems().get(j).getItemId());
                            }

                        }
                    }
                }
            }

            fuelBought = lastGameState.getBeaconList().get(lastGameState.getCurrentBeaconId()).getStore().getFuel() -
                    currentGameState.getBeaconList().get(currentGameState.getCurrentBeaconId()).getStore().getFuel();
            if (fuelBought > 0){
                events.add(new ResourceEvent(Constants.EventType.BUY, fuelBought, GausmanUtil.getCostResource(Constants.Resource.FUEL.name(), fuelBought, currentGameState.getSectorNumber()), Constants.Resource.FUEL.name(), jump));
            }
            missilesBought = lastGameState.getBeaconList().get(lastGameState.getCurrentBeaconId()).getStore().getMissiles() -
                    currentGameState.getBeaconList().get(currentGameState.getCurrentBeaconId()).getStore().getMissiles();
            if (missilesBought > 0){
                events.add(new ResourceEvent(Constants.EventType.BUY, missilesBought, GausmanUtil.getCostResource(Constants.Resource.MISSILE.name(), missilesBought, currentGameState.getSectorNumber()), Constants.Resource.MISSILE.name(), jump));
            }
            dronesBought = lastGameState.getBeaconList().get(lastGameState.getCurrentBeaconId()).getStore().getDroneParts() -
                    currentGameState.getBeaconList().get(currentGameState.getCurrentBeaconId()).getStore().getDroneParts();
            if (dronesBought > 0) {
                events.add(new ResourceEvent(Constants.EventType.BUY, dronesBought, GausmanUtil.getCostResource(Constants.Resource.DRONE.name(), dronesBought, currentGameState.getSectorNumber()), Constants.Resource.DRONE.name(), jump));
            }


            // repair
            repairCountDiff = currentGameState.getStateVar(StateVar.STORE_REPAIR.getId()) - lastGameState.getStateVar(StateVar.STORE_REPAIR.getId());
            if (repairCountDiff > 0){ // TODO does this work with "REPAIR"?
                events.add(new ResourceEvent(Constants.EventType.BUY, repairCountDiff, GausmanUtil.getCostResource(Constants.Resource.HULL.name(), repairCountDiff, currentGameState.getSectorNumber()), Constants.Resource.HULL.name(), jump));
            }


        }

        // drones/missiles used
        int missileUsedDiff = currentGameState.getStateVar(StateVar.USED_MISSILE.getId()) - lastGameState.getStateVar(StateVar.USED_MISSILE.getId());
        if (missileUsedDiff > 0){
            events.add(new ResourceEvent(Constants.EventType.USE, missileUsedDiff, 0, Constants.Resource.MISSILE.name(), jump));
        }
        int droneUsedDiff = currentGameState.getStateVar(StateVar.USED_DRONE.getId()) - lastGameState.getStateVar(StateVar.USED_DRONE.getId());
        if (droneUsedDiff > 0){
            events.add(new ResourceEvent(Constants.EventType.USE, droneUsedDiff, 0, Constants.Resource.DRONE.name(), jump));
        }

        // Scrap -> completely new topic
        int oldScrap = lastGameState.getPlayerShip().getScrapAmt();
        int newScrap = currentGameState.getPlayerShip().getScrapAmt();


        // Hull
        int oldHull = lastGameState.getPlayerShip().getHullAmt();
        int newHull = currentGameState.getPlayerShip().getHullAmt();

        int hullDamage = oldHull + repairCountDiff - newHull;
        if (hullDamage > 0){
            events.add(new ResourceEvent(Constants.EventType.DAMAGE, hullDamage, 0, Constants.Resource.HULL.name(), jump));
        }

        // Todo repair events?


        // Resources
        int oldFuelCount = lastGameState.getPlayerShip().getFuelAmt();
        int newFuelCount = currentGameState.getPlayerShip().getFuelAmt();

        int fuelRewardCount = newFuelCount - oldFuelCount - fuelBought;
        if (fuelRewardCount > 0){
            events.add(new ResourceEvent(Constants.EventType.REWARD, fuelRewardCount, 0, Constants.Resource.FUEL.name(), jump));
        }

        // Todo fuel trade events?

        String eventText = currentGameState.getEncounter().getText();
        boolean isMissilesSellEvent = currentGameState.getEncounter().getText().contains(MISSILE_SELL_EVENT);

        int oldMissilesCount = lastGameState.getPlayerShip().getMissilesAmt();
        int newMissilesCount = currentGameState.getPlayerShip().getMissilesAmt();

        int missilesRewardCount = newMissilesCount - oldMissilesCount + missileUsedDiff - missilesBought;
        if (missilesRewardCount > 0){
            events.add(new ResourceEvent(Constants.EventType.REWARD, missilesRewardCount, 0, Constants.Resource.MISSILE.name(), jump));
        } else if (missilesRewardCount < 0) {
            if (isMissilesSellEvent) {
                events.add(new ResourceEvent(
                        Constants.EventType.SELL,
                        -missilesRewardCount,
                        -missilesRewardCount * 6,
                        Constants.Resource.MISSILE.name(),
                        jump
                ));
            }
        }

        boolean isDronesSellEvent = currentGameState.getEncounter().getText().contains(DRONE_SELL_EVENT);

        int oldDronesCount = lastGameState.getPlayerShip().getDronePartsAmt();
        int newDronesCount = currentGameState.getPlayerShip().getDronePartsAmt();

        int dronesRewardCount = newDronesCount - oldDronesCount + droneUsedDiff - dronesBought;
        if (dronesRewardCount > 0){
            events.add(new ResourceEvent(Constants.EventType.REWARD, dronesRewardCount, 0, Constants.Resource.DRONE.name(), jump));
        } else if (dronesRewardCount < 0){
            if (isDronesSellEvent){
                events.add(new ResourceEvent(
                        Constants.EventType.SELL,
                        -dronesRewardCount,
                        -dronesRewardCount * 8,
                        Constants.Resource.DRONE.name(),
                        jump
                ));
            }
        }

        // TODO missile/drone sale event?
        // i think for that event we have to assume that we can't get missiles for free and trade them on the same jump, which is probably true
        // because otherwise I don't think we could differentiate between selling 15 missiles and getting 5 for free (-10) and selling 10 missiles and getting none (-10)

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
            events.add(new WeaponEvent(Constants.EventType.REWARD, 1, GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.WEAPON, weapon), weapon, jump));
        }

        for (String drone: newDrones){
            events.add(new DroneEvent(Constants.EventType.REWARD, 1, GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.DRONE, drone), drone, jump));
        }

        for (String augment: newAugments){
            events.add(new AugmentEvent(Constants.EventType.REWARD, 1, GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.AUGMENT, augment), augment, jump));
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
                sellCost = GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.WEAPON, weapon);
            } else {
                sellCost = 0;
            }
            tempSellEvents.add(new WeaponEvent(typeNow, 1, sellCost, weapon, jump));
        }

        for (String drone: removedDrones){
            if (typeNow == Constants.EventType.SELL){
                sellCost = GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.DRONE, drone);
            } else {
                sellCost = 0;
            }
            tempSellEvents.add(new DroneEvent(typeNow, 1, sellCost, drone, jump));
        }

        for (String augment: removedAugments){
            if (typeNow == Constants.EventType.SELL){
                sellCost = GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.AUGMENT, augment);
            } else {
                sellCost = 0;
            }
            tempSellEvents.add(new AugmentEvent(typeNow, 1, sellCost, augment, jump));
        }

        if (addToLastJump){
            lastJumpEvents.addAll(tempSellEvents);
        } else {
            events.addAll(tempSellEvents);
        }

        // ship power
        int newReactorCapacity = currentGameState.getPlayerShip().getReservePowerCapacity();
        int oldReactorCapacity = lastGameState.getPlayerShip().getReservePowerCapacity();

        int newCapacitySV = currentGameState.getStateVar(StateVar.REACTOR_UPGRADE.getId());
        int oldCapacitySV = lastGameState.getStateVar(StateVar.REACTOR_UPGRADE.getId());

        if (newCapacitySV > oldCapacitySV){
            while (newCapacitySV > oldCapacitySV){
                oldReactorCapacity++;
                oldCapacitySV++;
                events.add(new ReactorEvent(Constants.EventType.UPGRADE, 1,
                        GausmanUtil.getReactorUpgradeCost(oldReactorCapacity), Constants.Reactor.POWER_BAR.name(), jump));
            }
        }

        // reactor upgrades from events are not included in the state vars
        int reactorDiff = newReactorCapacity - oldReactorCapacity;
        if (reactorDiff > 0){
            events.add(new ReactorEvent(Constants.EventType.REWARD, reactorDiff,
                    GausmanUtil.getReactorUpgradeCost(oldReactorCapacity), Constants.Reactor.POWER_BAR.name(), jump));
        }


        // ship upgrades
        int shipUpgradeCount = currentGameState.getStateVar(StateVar.SYSTEM_UPGRADE.getId()) - lastGameState.getStateVar(StateVar.SYSTEM_UPGRADE.getId());
        int scrapDiff = newScrap - oldScrap;

        SavedGameParser.ShipState newShipState = currentGameState.getPlayerShip();
        SavedGameParser.ShipState oldShipState = lastGameState.getPlayerShip();

        Map<SavedGameParser.SystemType, List<SavedGameParser.SystemState>> systemsMap = currentGameState.getPlayerShip().getSystemsMap();

        SavedGameParser.SystemState oldSystemState;
        int systemDiff;
        List<SystemEvent> tempSystemUpgrades = new ArrayList<>();
        for (Map.Entry<SavedGameParser.SystemType, List<SavedGameParser.SystemState>> entry: systemsMap.entrySet()){
            for (SavedGameParser.SystemState newSystemState: entry.getValue()){
                oldSystemState = oldShipState.getSystem(newSystemState.getSystemType());
                systemDiff = newSystemState.getCapacity() - oldSystemState.getCapacity();
                if (systemDiff > 0 && oldSystemState.getCapacity() > 0){
                    tempSystemUpgrades.add(new SystemEvent(
                            Constants.EventType.UPGRADE,
                            systemDiff,
                            GausmanUtil.getUpgradeCostSystem(newSystemState.getSystemType().getId(), oldSystemState.getCapacity(), newSystemState.getCapacity()),
                            newSystemState.getSystemType().getId(),
                            jump,
                            newSystemState.getSystemType(),
                            true
                    ));

                }
            }
        }

        // Here is some logic trying to find System Upgrades from event
        // The SYSTEM_UPGRADE state_var is increased by 1 for every unique system the player upgraded
        List<SavedGameParser.SystemType> possibleSystemUpgradeEvent = new ArrayList<>();
        possibleSystemUpgradeEvent.add(SavedGameParser.SystemType.PILOT);
        possibleSystemUpgradeEvent.add(SavedGameParser.SystemType.DOORS);
        possibleSystemUpgradeEvent.add(SavedGameParser.SystemType.SENSORS);
        possibleSystemUpgradeEvent.add(SavedGameParser.SystemType.ENGINES);
        possibleSystemUpgradeEvent.add(SavedGameParser.SystemType.OXYGEN);
        possibleSystemUpgradeEvent.add(SavedGameParser.SystemType.MEDBAY); // ?
        possibleSystemUpgradeEvent.add(SavedGameParser.SystemType.CLONEBAY); // ?

        // In this case all upgrades were made by the player
        if (shipUpgradeCount == tempSystemUpgrades.size()){
            events.addAll(tempSystemUpgrades);
        } else if (shipUpgradeCount > tempSystemUpgrades.size()){
            log.error("State var SYSTEM_UPGRADE higher than actual Upgrades");
        } else if (shipUpgradeCount == tempSystemUpgrades.size() - 1){
            for (SystemEvent systemEvent : tempSystemUpgrades){
                if (possibleSystemUpgradeEvent.contains(systemEvent.getType())){
                    systemEvent.setPlayerUpgrade(false);
                    systemEvent.setScrap(-scrapDiff); // Todo calculate
                    break;
                }
            }
            events.addAll(tempSystemUpgrades);
        } else {
            log.error("2 or more System-Upgrade Events on the same beacon are impossible");
        }


        // TODO free upgrade system events

        // Crew
        events.addAll(getCrewEvents(lastGameState.getPlayerShip().getCrewList(), currentGameState.getPlayerShip().getCrewList(), boughtCrew, jump));


        // TESTING
        SavedGameParser.EncounterState lastEncounter = lastGameState.getEncounter();
        SavedGameParser.EncounterState currentEncounter = currentGameState.getEncounter();

//        Map<String, Encounters> encounters = dm.getEncounters();
//        Map<String, ShipEvent> shipEvents = dm.getShipEvents();
//        FTLEventList ftlEventList = dm.getEventListById("SAVE_CIVILIAN_LIST");
//        FTLEvent ftlEvent = dm.getEventById("PIRATE_STATION_CROPS");

        box.setEncounterState(currentGameState.getEncounter());

        return box;

    }

    private List<Event> getCrewEvents(List<SavedGameParser.CrewState> lastCrewState, List<SavedGameParser.CrewState> newCrewState, List<String> boughtCrew, Jump jump){
        List<Event> events = new ArrayList<>();

        lastCrewState.removeIf(cs -> !cs.isPlayerControlled());
        newCrewState.removeIf(cs -> !cs.isPlayerControlled());

        Map<Integer, List<Integer>> possibleMatchesOldToNew = new HashMap<>();
        Map<Integer, Integer> mapOldToNew = new HashMap<>();
        List<Integer> newCrewMatched = new ArrayList<>();

        // Make a list of all possible matches for the old crew
        for (int i = 0; i < lastCrewState.size(); i++){
            SavedGameParser.CrewState lastState = lastCrewState.get(i);
            possibleMatchesOldToNew.put(i, new ArrayList<>());
            for (int j = 0; j < Math.min(i + 1, newCrewState.size()); j++){
                SavedGameParser.CrewState newState = newCrewState.get(j);
                if (lastState.isMale() == newState.isMale()
                        && lastState.getRace().equals(newState.getRace())
                        && lastState.getSpriteTintIndeces().equals(newState.getSpriteTintIndeces())
                        && lastState.getRepairs() <= newState.getRepairs()
                        && lastState.getCombatKills() <= newState.getCombatKills()
                        && lastState.getPilotedEvasions() <= newState.getPilotedEvasions()
                        && lastState.getJumpsSurvived() <= newState.getJumpsSurvived()
                        && lastState.getSkillMasteriesEarned() <= newState.getSkillMasteriesEarned()

                ){
                    possibleMatchesOldToNew.get(i).add(j);

                }

            }
        }

        // Assign the best match for every old crew
        possibleMatchesOldToNew.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getValue().size())) // Todo maybe we need to sort after every assignment
                .forEach(entry -> {
                    Integer key = entry.getKey();
                    List<Integer> possibleValues = entry.getValue();
                    possibleValues.removeAll(newCrewMatched);
                    if (!possibleValues.isEmpty()) {
                        Integer chosen = possibleValues.getFirst(); // Todo Maybe prefer crew with the same name if possible
                        newCrewMatched.add(chosen);
                        mapOldToNew.put(key, chosen);
                    } else {
                        mapOldToNew.put(key, null);
                    }
                });

        // Create events for every Crew-match
        // If the old crew does not match with one in the new list the crew is dead/discarded
        for (Map.Entry<Integer, Integer> entry : mapOldToNew.entrySet()) {
            events.addAll(compareCrewState(lastCrewState.get(entry.getKey()), entry.getValue() != null ? newCrewState.get(entry.getValue()) : null, jump, entry.getKey()));
        }

        // Last we have to check if all new crew were matched with an old crew
        // if not the crew is new
        for (int i = 0; i < newCrewState.size(); i++){
            if (newCrewMatched.contains(i)){
                continue;
            }
            SavedGameParser.CrewState cs = newCrewState.get(i);
            boolean crewBought = removeStringFromList(boughtCrew, cs.getRace().getId());
            Constants.EventType eventType = crewBought ? Constants.EventType.BUY : Constants.EventType.REWARD;
            NewCrewEvent event = new NewCrewEvent(
                    SavedGameParser.StoreItemType.CREW,
                    eventType,
                    1,
                    GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.CREW, cs.getRace().getId()),
                    GausmanUtil.getCrewTypeName(cs.getRace().getId()) + " - " + cs.getName(),
                    jump
            );
            event.setCrewPosition(i);
            event.setCrew(new Crew(cs, eventType));
            events.add(event);

        }

        return events;
    }

    private List<Event> compareCrewState(SavedGameParser.CrewState lastCrewState, SavedGameParser.CrewState newCrewState, Jump jump, Integer crewPosition){
        List<Event> events = new ArrayList<>();
        // Crew dead
        if (newCrewState == null){
            CrewEvent event = new CrewEvent(
                    SavedGameParser.StoreItemType.CREW,
                    Constants.EventType.DISCARD,
                    1,
                    0,
                    GausmanUtil.getCrewTypeName(lastCrewState.getRace().getId()) + " - " + lastCrewState.getName(),
                    jump
            );
            event.setCrewPosition(crewPosition);
            events.add(event);
            return events;
        }

        assert lastCrewState != null;

        // Name Change
        if (!newCrewState.getName().equals(lastCrewState.getName())){
            NameEvent event = new NameEvent(
                    newCrewState.getName(),
                    lastCrewState.getName(),
                    jump
            );
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        // Stat Change
        int repairsChange = newCrewState.getRepairs() - lastCrewState.getRepairs();
        if (repairsChange != 0){
            StatEvent event = new StatEvent(
                    Constants.Stats.REPAIRS,
                    repairsChange,
                    jump
            );
            event.setDisplayText(String.format("%s - %s: %s", newCrewState.getName(), Constants.Stats.REPAIRS, newCrewState.getRepairs()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        int combatKillsChange = newCrewState.getCombatKills() - lastCrewState.getCombatKills();
        if (combatKillsChange != 0){
            StatEvent event = new StatEvent(
                    Constants.Stats.COMBAT_KILLS,
                    combatKillsChange,
                    jump
            );
            event.setDisplayText(String.format("%s - %s: %s", newCrewState.getName(), Constants.Stats.COMBAT_KILLS, newCrewState.getCombatKills()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        int pilotedEvasionsChange = newCrewState.getPilotedEvasions() - lastCrewState.getPilotedEvasions();
        if (pilotedEvasionsChange != 0){
            StatEvent event = new StatEvent(
                    Constants.Stats.PILOTED_EVASIONS,
                    pilotedEvasionsChange,
                    jump
            );
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        int jumpsSurvivedChange = newCrewState.getJumpsSurvived() - lastCrewState.getJumpsSurvived();
        if (jumpsSurvivedChange != 0){
            StatEvent event = new StatEvent(
                    Constants.Stats.JUMPS_SURVIVED,
                    jumpsSurvivedChange,
                    jump
            );
            event.setDisplayText(String.format("%s - %s: %s", newCrewState.getName(), Constants.Stats.JUMPS_SURVIVED, newCrewState.getJumpsSurvived()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        int skillMasteriesEarnedChange = newCrewState.getSkillMasteriesEarned() - lastCrewState.getSkillMasteriesEarned();
        if (skillMasteriesEarnedChange != 0){
            StatEvent event = new StatEvent(
                    Constants.Stats.SKILL_MASTERIES_EARNED,
                    skillMasteriesEarnedChange,
                    jump
            );
            event.setDisplayText(String.format("%s - %s: %s", newCrewState.getName(), Constants.Stats.SKILL_MASTERIES_EARNED, newCrewState.getSkillMasteriesEarned()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }


        // Skill Value Change
        int pilotSkillChange = newCrewState.getPilotSkill() - lastCrewState.getPilotSkill();
        if (pilotSkillChange != 0){
            SkillEvent event = new SkillEvent(
                    Constants.Skill.PILOT,
                    pilotSkillChange,
                    jump
            );
            event.setDisplayText(String.format("%s - %s Skill: %s", newCrewState.getName(), Constants.Skill.PILOT, newCrewState.getPilotSkill()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        int engineSkillChange = newCrewState.getEngineSkill() - lastCrewState.getEngineSkill();
        if (engineSkillChange != 0){
            SkillEvent event = new SkillEvent(
                    Constants.Skill.ENGINE,
                    engineSkillChange,
                    jump
            );
            event.setDisplayText(String.format("%s - %s Skill: %s", newCrewState.getName(), Constants.Skill.ENGINE, newCrewState.getPilotSkill()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        int shieldSkillChange = newCrewState.getShieldSkill() - lastCrewState.getShieldSkill();
        if (shieldSkillChange != 0){
            SkillEvent event = new SkillEvent(
                    Constants.Skill.SHIELD,
                    shieldSkillChange,
                    jump
            );
            event.setDisplayText(String.format("%s - %s Skill: %s", newCrewState.getName(), Constants.Skill.SHIELD, newCrewState.getShieldSkill()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        int weaponSkillChange = newCrewState.getWeaponSkill() - lastCrewState.getWeaponSkill();
        if (weaponSkillChange != 0){
            SkillEvent event = new SkillEvent(
                    Constants.Skill.WEAPON,
                    weaponSkillChange,
                    jump
            );
            event.setDisplayText(String.format("%s - %s Skill: %s", newCrewState.getName(), Constants.Skill.WEAPON, newCrewState.getWeaponSkill()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        int repairSkillChange = newCrewState.getRepairSkill() - lastCrewState.getRepairSkill();
        if (repairSkillChange != 0){
            SkillEvent event = new SkillEvent(
                    Constants.Skill.REPAIR,
                    repairSkillChange,
                    jump
            );
            event.setDisplayText(String.format("%s - %s Skill: %s", newCrewState.getName(), Constants.Skill.REPAIR, newCrewState.getRepairSkill()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        int combatSkillChange = newCrewState.getCombatSkill() - lastCrewState.getCombatSkill();
        if (combatSkillChange != 0){
            SkillEvent event = new SkillEvent(
                    Constants.Skill.COMBAT,
                    combatSkillChange,
                    jump
            );
            event.setDisplayText(String.format("%s - %s Skill: %s", newCrewState.getName(), Constants.Skill.COMBAT, newCrewState.getCombatSkill()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }


        // Skill Mastery Change
        if (lastCrewState.getPilotMasteryOne() != newCrewState.getPilotMasteryOne()){
            MasteryEvent event = new MasteryEvent(
                    Constants.Skill.PILOT,
                    1,
                    newCrewState.getPilotMasteryOne(),
                    jump
            );
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getPilotMasteryTwo() != newCrewState.getPilotMasteryTwo()){
            MasteryEvent event = new MasteryEvent(
                    Constants.Skill.PILOT,
                    2,
                    newCrewState.getPilotMasteryTwo(),
                    jump
            );
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getEngineMasteryOne() != newCrewState.getEngineMasteryOne()){
            MasteryEvent event = new MasteryEvent(
                    Constants.Skill.ENGINE,
                    1,
                    newCrewState.getEngineMasteryOne(),
                    jump
            );
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getEngineMasteryTwo() != newCrewState.getEngineMasteryTwo()){
            MasteryEvent event = new MasteryEvent(
                    Constants.Skill.ENGINE,
                    2,
                    newCrewState.getEngineMasteryTwo(),
                    jump
            );
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getShieldMasteryOne() != newCrewState.getShieldMasteryOne()){
            MasteryEvent event = new MasteryEvent(
                    Constants.Skill.SHIELD,
                    1,
                    newCrewState.getShieldMasteryOne(),
                    jump
            );
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getShieldMasteryTwo() != newCrewState.getShieldMasteryTwo()){
            MasteryEvent event = new MasteryEvent(
                    Constants.Skill.SHIELD,
                    2,
                    newCrewState.getShieldMasteryTwo(),
                    jump
            );
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getWeaponMasteryOne() != newCrewState.getWeaponMasteryOne()){
            MasteryEvent event = new MasteryEvent(
                    Constants.Skill.WEAPON,
                    1,
                    newCrewState.getWeaponMasteryOne(),
                    jump
            );
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getWeaponMasteryTwo() != newCrewState.getWeaponMasteryTwo()){
            MasteryEvent event = new MasteryEvent(
                    Constants.Skill.WEAPON,
                    2,
                    newCrewState.getWeaponMasteryTwo(),
                    jump
            );
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getRepairMasteryOne() != newCrewState.getRepairMasteryOne()){
            MasteryEvent event = new MasteryEvent(
                    Constants.Skill.REPAIR,
                    1,
                    newCrewState.getRepairMasteryOne(),
                    jump
            );
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getRepairMasteryTwo() != newCrewState.getRepairMasteryTwo()){
            MasteryEvent event = new MasteryEvent(
                    Constants.Skill.REPAIR,
                    2,
                    newCrewState.getRepairMasteryTwo(),
                    jump
            );
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getCombatMasteryOne() != newCrewState.getCombatMasteryOne()){
            MasteryEvent event = new MasteryEvent(
                    Constants.Skill.COMBAT,
                    1,
                    newCrewState.getCombatMasteryOne(),
                    jump
            );
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getCombatMasteryTwo() != newCrewState.getCombatMasteryTwo()){
            MasteryEvent event = new MasteryEvent(
                    Constants.Skill.COMBAT,
                    2,
                    newCrewState.getCombatMasteryTwo(),
                    jump
            );
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        return events;
    }

    private void applyEventToCrew(Crew crew, Event event){
        switch (event.getEventType()){
            // we ignore Name Events because those are always first
            case STAT -> {
                StatEvent se = (StatEvent) event;
                String statString = GausmanUtil.convertStatToAttributename(se.getStat());
                int oldValue = (int) getValueInCrewByAttributename(crew, statString);
                setValueInCrewByAttributename(crew, statString,oldValue + event.getAmount());
            }
            case SKILL -> {
                SkillEvent se = (SkillEvent) event;
                String skillString = GausmanUtil.convertSkillToAttributename(se.getSkill());
                int oldValue = (int) getValueInCrewByAttributename(crew, skillString);
                setValueInCrewByAttributename(crew, skillString,oldValue + event.getAmount());
            }
            case MASTERY -> {
                MasteryEvent me = (MasteryEvent) event;
                String masteryString = GausmanUtil.convertMasteryToAttributename(me.getMastery(), ((MasteryEvent) event).getLevel());
                setValueInCrewByAttributename(crew, masteryString, me.getNewValue());
            }
            default -> System.out.println("ApplyEventsToCrew was called with a wrong event Type");
        }


    }

    private void applyEventsToCrew(Crew crew, List<Event> events){
        for (Event event : events){
            switch (event.getEventType()){
                // we ignore Name Events because those are always first
                case STAT -> {
                    StatEvent se = (StatEvent) event;
                    String statString = GausmanUtil.convertStatToAttributename(se.getStat());
                    int oldValue = (int) getValueInCrewByAttributename(crew, statString);
                    setValueInCrewByAttributename(crew, statString,oldValue + event.getAmount());
                }
                case SKILL -> {
                    SkillEvent se = (SkillEvent) event;
                    String skillString = GausmanUtil.convertSkillToAttributename(se.getSkill());
                    int oldValue = (int) getValueInCrewByAttributename(crew, skillString);
                    setValueInCrewByAttributename(crew, skillString,oldValue + event.getAmount());
                }
                case MASTERY -> {
                    MasteryEvent me = (MasteryEvent) event;
                    String masteryString = GausmanUtil.convertMasteryToAttributename(me.getMastery(), ((MasteryEvent) event).getLevel());
                    setValueInCrewByAttributename(crew, masteryString, me.getNewValue());
                }
                default -> System.out.println("ApplyEventsToCrew was called with a wrong event Type");
            }
        }

    }

    private Object getValueInCrewByAttributename(Crew crew, String attributename){
        try {
            Field field = crew.getClass().getDeclaredField(attributename);
            field.setAccessible(true);
            return field.get(crew);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Could not set field '" + attributename + "'", e);
        }
    }

    private void setValueInCrewByAttributename(Crew crew, String attributename, Object newValue){
        try {
            Field field = crew.getClass().getDeclaredField(attributename);
            field.setAccessible(true);
            field.set(crew, newValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Could not set field '" + attributename + "'", e);
        }
    }

    private boolean removeStringFromList(List<String> list, String target) {
        return list.remove(target);
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
