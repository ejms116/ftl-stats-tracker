package net.gausman.ftl.service;

import net.blerf.ftl.constants.Difficulty;
import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.SavedGameParser.StateVar;
import net.blerf.ftl.xml.DroneBlueprint;
import net.blerf.ftl.xml.ShipBlueprint;
import net.blerf.ftl.xml.WeaponBlueprint;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.Crew;
import net.gausman.ftl.model.change.*;
import net.gausman.ftl.model.change.crew.*;
import net.gausman.ftl.model.change.effects.IntegerStatEffect;
import net.gausman.ftl.model.change.effects.StringStatEffect;
import net.gausman.ftl.model.change.general.*;
import net.gausman.ftl.model.change.item.AugmentEvent;
import net.gausman.ftl.model.change.item.DroneEvent;
import net.gausman.ftl.model.change.item.WeaponEvent;
import net.gausman.ftl.model.change.other.*;
import net.gausman.ftl.model.change.system.ReactorEvent;
import net.gausman.ftl.model.change.system.SystemEvent;
import net.gausman.ftl.model.change.use.DronesUsedEvent;
import net.gausman.ftl.model.change.use.FuelUsedEvent;
import net.gausman.ftl.model.change.use.MissilesUsedEvent;
import net.gausman.ftl.model.record.EventBox;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.util.GausmanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

public class EventService {
    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private static final String MISSILE_SELL_EVENT = "SELL_MISSILES";
    private static final String DRONE_SELL_EVENT = "SELL_DRONES";
    public static final int STARTING_FUEL = 16;
    public static final int FUEL_PRICE_STORE = 3;
    public static final int MISSILE_PRICE_STORE = 6;
    public static final int DRONE_PRICE_STORE = 8;

    private DataManager dm = DataManager.get();

    private List<SavedGameParser.EncounterState> encounterStateList = new ArrayList<>();

    private Map<Integer, Integer> mapStateToInternal = new HashMap<>();

    public void initEventService(){
        encounterStateList.clear();
    }

    public EventBox getFuelUsedEventBox(Jump jump){
        List<Event> events = new ArrayList<>();
        List<Event> lastJumpEvents = new ArrayList<>();
        EventBox eventBox = new EventBox(events, lastJumpEvents);

        FuelUsedEvent defaultFuelEvent = new FuelUsedEvent(jump);
        events.add(defaultFuelEvent);

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
        ShipSetupEvent generalInfoEvent = new ShipSetupEvent(jump);
        generalInfoEvent.addTag(Constants.EventTag.START);
        generalInfoEvent.addStringStatEffects(new StringStatEffect(Constants.General.SHIP_NAME, newGameState.getPlayerShipName()));
        generalInfoEvent.addStringStatEffects(new StringStatEffect(Constants.General.SHIP_BLUEPRINT, newGameState.getPlayerShipBlueprintId()));
        generalInfoEvent.addStringStatEffects(new StringStatEffect(Constants.General.DIFFICULTY, newGameState.getDifficulty().toString()));

        generalInfoEvent.setDisplayText("Setting name, blueprint and difficulty");
        events.add(generalInfoEvent);

        int startingScrap = 0;
        if (newGameState.getDifficulty().equals(Difficulty.NORMAL)){
            startingScrap += 10;
        } else if (newGameState.getDifficulty().equals(Difficulty.EASY)){
            startingScrap += 30;
        }

        // Resources
        ResourceEvent resourcesStartEvent = new ResourceEvent( jump);
        resourcesStartEvent.addTag(Constants.EventTag.START);
        resourcesStartEvent.setResourceEffect(Constants.Resource.FUEL, STARTING_FUEL);

        if (shipBlueprint.getDroneList() != null){
            resourcesStartEvent.setResourceEffect(Constants.Resource.DRONE, shipBlueprint.getDroneList().drones);
        }
        resourcesStartEvent.setResourceEffect(Constants.Resource.MISSILE, shipBlueprint.getWeaponList().missiles);
        resourcesStartEvent.setDisplayText("Received starting resources");
        events.add(resourcesStartEvent);

        // Hull & scrap
        ShipSetupEvent hullScrapStartEvent = new ShipSetupEvent(jump);
        hullScrapStartEvent.addTag(Constants.EventTag.START);
        hullScrapStartEvent.setResourceEffect(Constants.Resource.HULL, shipBlueprint.getHealth().amount);
        hullScrapStartEvent.setResourceEffect(Constants.Resource.SCRAP, startingScrap);
        hullScrapStartEvent.setDisplayText("Setting starting hull and scrap");
        events.add(hullScrapStartEvent);



        // Crew
        int index = 0;
        for (SavedGameParser.CrewState crewState : newGameState.getPlayerShip().getCrewList()) {
            CrewNewEvent creweventNew = new CrewNewEvent(jump);
            creweventNew.addTag(Constants.EventTag.START);
            creweventNew.setCrewPosition(index);
            creweventNew.setCrew(new Crew(crewState, Constants.EventType.START)); // todo we should change this
            creweventNew.addIntegerStatEffects(new IntegerStatEffect(Constants.General.CREW_HIRED, 1));
            events.add(creweventNew);
            index++;
        }

        // Power
        ReactorEvent reactorEvent = new ReactorEvent(jump, shipBlueprint.getMaxPower().amount, shipBlueprint.getMaxPower().amount);
        reactorEvent.addTag(Constants.EventTag.START);
        reactorEvent.getTags().remove(Constants.EventTag.BUY);
        events.add(reactorEvent);

        // Systems
        for (Map.Entry<SavedGameParser.SystemType, List<SavedGameParser.SystemState>> entry: newGameState.getPlayerShip().getSystemsMap().entrySet()){
            for (SavedGameParser.SystemState systemState: entry.getValue()){
                int capacity = systemState.getCapacity();
                if (capacity == 0){
                    continue;
                }
                SystemEvent systemEvent = new SystemEvent(jump, systemState.getSystemType(), false, capacity, capacity);
                systemEvent.addTag(Constants.EventTag.START);
                events.add(systemEvent);
            }
        }

        // Weapons
        for (SavedGameParser.WeaponState weaponState: newGameState.getPlayerShip().getWeaponList()){
            WeaponEvent weaponEvent = new WeaponEvent(jump, weaponState.getWeaponId());
            weaponEvent.addTag(Constants.EventTag.START);
            events.add(weaponEvent);
        }

        // Drones
        for (SavedGameParser.DroneState droneState: newGameState.getPlayerShip().getDroneList()){
            DroneEvent droneEvent = new DroneEvent(jump, droneState.getDroneId());
            droneEvent.addTag(Constants.EventTag.START);
            events.add(droneEvent);
        }

        // Augments
        for (String augmentId: newGameState.getPlayerShip().getAugmentIdList()){
            AugmentEvent augmentEvent = new AugmentEvent( jump, augmentId);
            augmentEvent.addTag(Constants.EventTag.START);
            events.add(augmentEvent);
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
            BeaconsExploredEvent beaconsExploredEvent = new BeaconsExploredEvent(jump);
            beaconsExploredEvent.addIntegerStatEffects(new IntegerStatEffect(Constants.General.BEACONS_EXPLORED, beaconsExploredDiff));
            events.add(beaconsExploredEvent);
        }

        int shipsDestroyedDiff = currentGameState.getTotalShipsDefeated() - lastGameState.getTotalShipsDefeated();
        if (shipsDestroyedDiff > 0){
            ShipsDestroyedEvent shipsDestroyedEvent = new ShipsDestroyedEvent(jump);
            shipsDestroyedEvent.addIntegerStatEffects(new IntegerStatEffect(Constants.General.SHIPS_DESTROYED, shipsDestroyedDiff));
            events.add(shipsDestroyedEvent);
        }

        int scrapCollected = currentGameState.getTotalScrapCollected() - lastGameState.getTotalScrapCollected();
        if (scrapCollected > 0){
            ScrapCollectedEvent scrapCollectedEvent = new ScrapCollectedEvent(jump);
            scrapCollectedEvent.addIntegerStatEffects(new IntegerStatEffect(Constants.General.SCRAP_COLLECTED, scrapCollected));
            scrapCollectedEvent.setResourceEffect(Constants.Resource.SCRAP, scrapCollected);
            scrapCollectedEvent.addTag(Constants.EventTag.REWARD);
            events.add(scrapCollectedEvent);
        }

        int crewHiredDiff = currentGameState.getTotalCrewHired() - lastGameState.getTotalCrewHired();
        if (crewHiredDiff > 0){
            CrewHiredEvent crewHiredEvent = new CrewHiredEvent(jump);
            crewHiredEvent.addIntegerStatEffects(new IntegerStatEffect(Constants.General.CREW_HIRED, crewHiredDiff));
            events.add(crewHiredEvent);
        }


        boolean jumped = true;
        SavedGameParser.StoreState newStore = currentGameState.getBeaconList().get(currentGameState.getCurrentBeaconId()).getStore();
        SavedGameParser.StoreState oldStore = lastGameState.getBeaconList().get(lastGameState.getCurrentBeaconId()).getStore();

        List<String> boughtItems = new ArrayList<>();
        List<String> boughtCrew = new ArrayList<>();

        if (lastGameState.getCurrentBeaconId() == currentGameState.getCurrentBeaconId()){
            jumped = false;
        } else {
            events.add(new FuelUsedEvent(jump));
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
                                DroneEvent freeDroneWhenBuyingDroneControl;
                                if (newStore.getShelfList().get(i).getItems().get(j).getExtraData() == 0){
                                    freeDroneWhenBuyingDroneControl = new DroneEvent(jump, "DEFENSE_1");
//                                    events.add(new DroneEvent(Constants.EventType.REWARD, 1, GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.DRONE, "DEFENSE_1"),"DEFENSE_1", jump));
                                    boughtItems.add("DEFENSE_1");
                                    localCost += 25;
                                } else if (newStore.getShelfList().get(i).getItems().get(j).getExtraData() == 1) {
                                    freeDroneWhenBuyingDroneControl = new DroneEvent(jump, "REPAIR");
//                                    events.add(new DroneEvent(Constants.EventType.REWARD, 1, GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.DRONE, "REPAIR"),"REPAIR", jump));
                                    boughtItems.add("REPAIR");
                                    localCost += 15;
                                } else if (newStore.getShelfList().get(i).getItems().get(j).getExtraData() == 2){
                                    freeDroneWhenBuyingDroneControl = new DroneEvent(jump, "COMBAT_1");
//                                    events.add(new DroneEvent(Constants.EventType.REWARD, 1, GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.DRONE, "COMBAT_1"),"COMBAT_1", jump));
                                    boughtItems.add("COMBAT_1");
                                    localCost += 25;
                                } else {
                                    throw new IllegalArgumentException("Unsupported extra drone info: " + newStore.getShelfList().get(i).getItems().get(j).getExtraData());
                                }
                                freeDroneWhenBuyingDroneControl.addTag(Constants.EventTag.BUY);
                                freeDroneWhenBuyingDroneControl.addTag(Constants.EventTag.REWARD);
                                events.add(freeDroneWhenBuyingDroneControl);
                            }
                            localCost += GausmanUtil.getCostStoreItemType(newStore.getShelfList().get(i).getItemType(), newStore.getShelfList().get(i).getItems().get(j));
                            // Crew is handled later because it's more complex (by comparing the crewlist)
                            // We just keep track of what crew we bought so we can use it then
                            if (newStore.getShelfList().get(i).getItemType().equals(SavedGameParser.StoreItemType.CREW)){
                                boughtCrew.add(newStore.getShelfList().get(i).getItems().get(j).getItemId());
                            } else {
                                Event boughtItemEvent;
                                switch (newStore.getShelfList().get(i).getItemType()){
                                    case WEAPON -> boughtItemEvent = new WeaponEvent(jump, newStore.getShelfList().get(i).getItems().get(j).getItemId());
                                    case DRONE -> boughtItemEvent = new DroneEvent(jump, newStore.getShelfList().get(i).getItems().get(j).getItemId());
                                    case AUGMENT -> boughtItemEvent = new AugmentEvent(jump, newStore.getShelfList().get(i).getItems().get(j).getItemId());
                                    case SYSTEM -> boughtItemEvent = new SystemEvent(jump, SavedGameParser.SystemType.findById(newStore.getShelfList().get(i).getItems().get(j).getItemId()), true, 1, 1);
                                    // todo newAmount is not 1 if you buy systems, then it's 2
                                    default -> throw new IllegalArgumentException("Unsupported ItemType: " + newStore.getShelfList().get(i).getItemType());
                                }
                                boughtItemEvent.addTag(Constants.EventTag.BUY);
                                boughtItemEvent.addTag(Constants.EventTag.STORE);
                                boughtItemEvent.setResourceEffect(Constants.Resource.SCRAP, -localCost);
                                events.add(boughtItemEvent);

                                boughtItems.add(newStore.getShelfList().get(i).getItems().get(j).getItemId());
                            }

                        }
                    }
                }
            }

            if (lastGameState.getBeaconList().get(lastGameState.getCurrentBeaconId()).getStore() == null){
                log.error("shop error");
            }

            fuelBought = lastGameState.getBeaconList().get(lastGameState.getCurrentBeaconId()).getStore().getFuel() -
                    currentGameState.getBeaconList().get(currentGameState.getCurrentBeaconId()).getStore().getFuel();
            if (fuelBought > 0){
                ResourceEvent fuelBoughtEvent = new ResourceEvent(jump);
                fuelBoughtEvent.setResourceEffect(Constants.Resource.FUEL, fuelBought);
                fuelBoughtEvent.setResourceEffect(Constants.Resource.SCRAP, -FUEL_PRICE_STORE * fuelBought);
                fuelBoughtEvent.addTag(Constants.EventTag.STORE);
                fuelBoughtEvent.addTag(Constants.EventTag.BUY);
                fuelBoughtEvent.setDisplayText("Bought fuel");
                events.add(fuelBoughtEvent);
            }
            missilesBought = lastGameState.getBeaconList().get(lastGameState.getCurrentBeaconId()).getStore().getMissiles() -
                    currentGameState.getBeaconList().get(currentGameState.getCurrentBeaconId()).getStore().getMissiles();
            if (missilesBought > 0){
                ResourceEvent missilesBoughtEvent = new ResourceEvent(jump);
                missilesBoughtEvent.setResourceEffect(Constants.Resource.MISSILE, missilesBought);
                missilesBoughtEvent.setResourceEffect(Constants.Resource.SCRAP, -MISSILE_PRICE_STORE * missilesBought);
                missilesBoughtEvent.addTag(Constants.EventTag.STORE);
                missilesBoughtEvent.addTag(Constants.EventTag.BUY);
                missilesBoughtEvent.setDisplayText("Bought missiles");
                events.add(missilesBoughtEvent);
            }
            dronesBought = lastGameState.getBeaconList().get(lastGameState.getCurrentBeaconId()).getStore().getDroneParts() -
                    currentGameState.getBeaconList().get(currentGameState.getCurrentBeaconId()).getStore().getDroneParts();
            if (dronesBought > 0) {
                ResourceEvent dronesBoughtEvent = new ResourceEvent(jump);
                dronesBoughtEvent.setResourceEffect(Constants.Resource.DRONE, dronesBought);
                dronesBoughtEvent.setResourceEffect(Constants.Resource.SCRAP, -DRONE_PRICE_STORE * dronesBought);
                dronesBoughtEvent.addTag(Constants.EventTag.STORE);
                dronesBoughtEvent.addTag(Constants.EventTag.BUY);
                dronesBoughtEvent.setDisplayText("Bought drone parts");
                events.add(dronesBoughtEvent);
            }


            // repair
            repairCountDiff = currentGameState.getStateVar(StateVar.STORE_REPAIR.getId()) - lastGameState.getStateVar(StateVar.STORE_REPAIR.getId());
            if (repairCountDiff > 0){ // TODO does this work with "REPAIR"?
                RepairEvent repairBoughtEvent = new RepairEvent(jump);
                repairBoughtEvent.setResourceEffect(Constants.Resource.HULL, repairCountDiff);
                repairBoughtEvent.setResourceEffect(Constants.Resource.SCRAP, -GausmanUtil.getCostResource(Constants.Resource.HULL.name(), repairCountDiff, currentGameState.getSectorNumber()));
                repairBoughtEvent.addTag(Constants.EventTag.REPAIR);
                repairBoughtEvent.addTag(Constants.EventTag.STORE);
                repairBoughtEvent.setDisplayText("Bought repairs");
                events.add(repairBoughtEvent);
            }


        }

        // drones/missiles used
        int missileUsedDiff = currentGameState.getStateVar(StateVar.USED_MISSILE.getId()) - lastGameState.getStateVar(StateVar.USED_MISSILE.getId());
        if (missileUsedDiff > 0){
            events.add(new MissilesUsedEvent(jump, missileUsedDiff));
        }
        int droneUsedDiff = currentGameState.getStateVar(StateVar.USED_DRONE.getId()) - lastGameState.getStateVar(StateVar.USED_DRONE.getId());
        if (droneUsedDiff > 0){
            events.add(new DronesUsedEvent(jump, droneUsedDiff));

        }

        // Scrap -> completely new topic
        int oldScrap = lastGameState.getPlayerShip().getScrapAmt();
        int newScrap = currentGameState.getPlayerShip().getScrapAmt();


        // Hull
        int oldHull = lastGameState.getPlayerShip().getHullAmt();
        int newHull = currentGameState.getPlayerShip().getHullAmt();

        int hullDamage = oldHull + repairCountDiff - newHull;
        if (hullDamage > 0){
            DamageEvent hullDamageEvent = new DamageEvent(jump);
            hullDamageEvent.setResourceEffect(Constants.Resource.HULL, -hullDamage);
            events.add(hullDamageEvent);
        }

        // Todo repair events?


        // Resources
        int oldFuelCount = lastGameState.getPlayerShip().getFuelAmt();
        int newFuelCount = currentGameState.getPlayerShip().getFuelAmt();

        ResourceEvent resourcesRewardEvent = new ResourceEvent(jump);
        resourcesRewardEvent.addTag(Constants.EventTag.REWARD);

        int fuelRewardCount = newFuelCount - oldFuelCount - fuelBought;
        if (fuelRewardCount > 0){
            resourcesRewardEvent.setResourceEffect(Constants.Resource.FUEL, fuelRewardCount);
        }

        // Todo fuel trade events?
        // todo missiles/drones sell Events
        String eventText = currentGameState.getEncounter().getText();
        boolean isMissilesSellEvent = currentGameState.getEncounter().getText().contains(MISSILE_SELL_EVENT);

        int oldMissilesCount = lastGameState.getPlayerShip().getMissilesAmt();
        int newMissilesCount = currentGameState.getPlayerShip().getMissilesAmt();

        int missilesRewardCount = newMissilesCount - oldMissilesCount + missileUsedDiff - missilesBought;
        if (missilesRewardCount > 0){
            resourcesRewardEvent.setResourceEffect(Constants.Resource.MISSILE, missilesRewardCount);
        } else if (missilesRewardCount < 0) {
            if (isMissilesSellEvent) {
                Event missilesSellEvent = new Event(jump);
                missilesSellEvent.addTag(Constants.EventTag.SELL);
                missilesSellEvent.addTag(Constants.EventTag.EVENT);
                missilesSellEvent.setResourceEffect(Constants.Resource.SCRAP, -missilesRewardCount * 6);
                missilesSellEvent.setResourceEffect(Constants.Resource.MISSILE, -missilesRewardCount);
                events.add(missilesSellEvent);
            }
        }

        boolean isDronesSellEvent = currentGameState.getEncounter().getText().contains(DRONE_SELL_EVENT);

        int oldDronesCount = lastGameState.getPlayerShip().getDronePartsAmt();
        int newDronesCount = currentGameState.getPlayerShip().getDronePartsAmt();

        int dronesRewardCount = newDronesCount - oldDronesCount + droneUsedDiff - dronesBought;
        if (dronesRewardCount > 0){
            resourcesRewardEvent.setResourceEffect(Constants.Resource.DRONE, dronesRewardCount);
        } else if (dronesRewardCount < 0){
            if (isDronesSellEvent){
                Event dronesSellEvent = new Event(jump);
                dronesSellEvent.addTag(Constants.EventTag.SELL);
                dronesSellEvent.addTag(Constants.EventTag.EVENT);
                dronesSellEvent.setResourceEffect(Constants.Resource.SCRAP, -dronesRewardCount * 8);
                dronesSellEvent.setResourceEffect(Constants.Resource.DRONE, -dronesRewardCount);
                events.add(dronesSellEvent);
            }
        }

        if (!resourcesRewardEvent.getResourceEffects().isEmpty()){
            resourcesRewardEvent.setDisplayText("Received resources");
            events.add(resourcesRewardEvent);
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
            WeaponEvent newWeaponEvent = new WeaponEvent(jump, weapon);
            newWeaponEvent.addTag(Constants.EventTag.REWARD);
            events.add(newWeaponEvent);
        }

        for (String drone: newDrones){
            DroneEvent newdroneEvent = new DroneEvent(jump, drone);
            newdroneEvent.addTag(Constants.EventTag.REWARD);
            events.add(newdroneEvent);
        }

        for (String augment: newAugments){
            AugmentEvent augmentEvent = new AugmentEvent(jump, augment);
            augmentEvent.addTag(Constants.EventTag.REWARD);
            events.add(augmentEvent);
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
                WeaponEvent newWeaponSellEvent = new WeaponEvent(jump, weapon);
                newWeaponSellEvent.addTag(Constants.EventTag.SELL);
                newWeaponSellEvent.addTag(Constants.EventTag.STORE);
                newWeaponSellEvent.setResourceEffect(Constants.Resource.SCRAP, sellCost);
                tempSellEvents.add(newWeaponSellEvent);
            }
        }

        for (String drone: removedDrones){
            if (typeNow == Constants.EventType.SELL){
                sellCost = GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.DRONE, drone);
                DroneEvent newDroneSellEvent = new DroneEvent(jump, drone);
                newDroneSellEvent.addTag(Constants.EventTag.SELL);
                newDroneSellEvent.addTag(Constants.EventTag.STORE);
                newDroneSellEvent.setResourceEffect(Constants.Resource.SCRAP, sellCost);
                tempSellEvents.add(newDroneSellEvent);
            }

        }

        for (String augment: removedAugments){
            if (typeNow == Constants.EventType.SELL){
                sellCost = GausmanUtil.getCostStoreItemId(SavedGameParser.StoreItemType.AUGMENT, augment);
                AugmentEvent newAugmentSellEvent = new AugmentEvent(jump, augment);
                newAugmentSellEvent.addTag(Constants.EventTag.SELL);
                newAugmentSellEvent.addTag(Constants.EventTag.STORE);
                newAugmentSellEvent.setResourceEffect(Constants.Resource.SCRAP, sellCost);
                tempSellEvents.add(newAugmentSellEvent);
            }
        }

        if (addToLastJump){
            lastJumpEvents.addAll(tempSellEvents);
        } else {
            events.addAll(tempSellEvents);
        }

        // ship power
        int newReactorCapacity = currentGameState.getPlayerShip().getReservePowerCapacity();
        int oldReactorCapacity = lastGameState.getPlayerShip().getReservePowerCapacity();

        // this state var describes how often the player upgraded the reactor
        // so if you upgrade your reactor by one the state var increases by one
        // if you upgrade your reactor by two or more the state vor also increases by one
        // the state var does not increase from reactor upgrade events
        int newCapacitySV = currentGameState.getStateVar(StateVar.REACTOR_UPGRADE.getId());
        int oldCapacitySV = lastGameState.getStateVar(StateVar.REACTOR_UPGRADE.getId());

        // todo we need to detect reactor upgrade events by reading event choices
        // one other indicater is: newReactorCapacity > oldReactorCapacity (actullay difference is exactly 1)
        // and newCapacitySV = oldCapacitySv
        if ((newReactorCapacity > oldReactorCapacity) && (newCapacitySV == oldCapacitySV)){
            log.error("undeteced reactor upgrade event");
        }

        int reactorDiff = newReactorCapacity - oldReactorCapacity;
        if (reactorDiff > 0){
            ReactorEvent reactorEvent = new ReactorEvent(jump, reactorDiff, newReactorCapacity);
            int totalUpgradeCost = 0;
            for (int i = oldReactorCapacity; i < newReactorCapacity; i++){
                totalUpgradeCost += GausmanUtil.getReactorUpgradeCost(i+1);
            }
            reactorEvent.setResourceEffect(Constants.Resource.SCRAP, -totalUpgradeCost);
            events.add(reactorEvent);
        }


//        if (newCapacitySV > oldCapacitySV){
//            while (newCapacitySV > oldCapacitySV){
//                oldReactorCapacity++;
//                oldCapacitySV++;
//                // Todo merge into one event
//                ReactorEvent reactorUpgradeEvent = new ReactorEvent(jump, 1, newReactorCapacity);
//                reactorUpgradeEvent.setResourceEffect(Constants.Resource.SCRAP, -GausmanUtil.getReactorUpgradeCost(oldReactorCapacity));
//                events.add(reactorUpgradeEvent);
//            }
//        }
//
//        // reactor upgrades from events are not included in the state vars
//        int reactorDiff = newReactorCapacity - oldReactorCapacity;
//        if (reactorDiff > 0){
//            ReactorEvent freeReactorFromEvent = new ReactorEvent(jump, newReactorCapacity - oldReactorCapacity, newReactorCapacity);
//            freeReactorFromEvent.addTag(Constants.EventTag.REWARD);
//            events.add(freeReactorFromEvent);
//        }


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
                    SystemEvent systemUpgradeEvent = new SystemEvent(jump, newSystemState.getSystemType(), true, systemDiff, newSystemState.getCapacity());
                    systemUpgradeEvent.addTag(Constants.EventTag.BUY);
                    systemUpgradeEvent.setResourceEffect(Constants.Resource.SCRAP, -GausmanUtil.getUpgradeCostSystem(newSystemState.getSystemType().getId(), oldSystemState.getCapacity(), newSystemState.getCapacity()));
                    tempSystemUpgrades.add(systemUpgradeEvent);
                }
            }
        }

        // TODO free upgrade system events
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

        // Crew
        events.addAll(getCrewEvents(lastGameState, currentGameState, boughtCrew, jump));


        // TESTING
        SavedGameParser.EncounterState lastEncounter = lastGameState.getEncounter();
        SavedGameParser.EncounterState currentEncounter = currentGameState.getEncounter();

//        Map<String, Encounters> encounters = dm.getEncounters();
//        Map<String, ShipEvent> shipEvents = dm.getShipEvents();
//        FTLEventList ftlEventList = dm.getEventListById("SAVE_CIVILIAN_LIST");
//        FTLEvent ftlEvent = dm.getEventById("PIRATE_STATION_CROPS");


        // Scrap Diff
        int scrapChangeExpected = 0;
        for (Event e : events){
            scrapChangeExpected += e.getResourceEffects().getOrDefault(Constants.Resource.SCRAP, 0);
        }
        int scrapChangeDiff = currentGameState.getPlayerShip().getScrapAmt() - lastGameState.getPlayerShip().getScrapAmt() - scrapChangeExpected;

        if (scrapChangeDiff != 0){
            ScrapDiffErrorEvent scrapChangeDiffEvent = new ScrapDiffErrorEvent(jump);
            scrapChangeDiffEvent.setResourceEffect(Constants.Resource.SCRAP, scrapChangeDiff);
            scrapChangeDiffEvent.setDisplayText("scrap change diff");
            events.add(scrapChangeDiffEvent);
        }

        // Fuel Diff

        // Missiles Diff

        // Drones Diff

        // Hull diff

        box.setEncounterState(currentGameState.getEncounter());

        return box;

    }

    private List<Event> getCrewEvents(SavedGameParser.SavedGameState lastGameState, SavedGameParser.SavedGameState currentGameState, List<String> boughtCrew, Jump jump){
        List<Event> events = new ArrayList<>();

        List<SavedGameParser.CrewState> lastCrewStatePlayer =
                Optional.ofNullable(lastGameState.getPlayerShip())
                        .map(SavedGameParser.ShipState::getCrewList)
                        .orElse(Collections.emptyList());

        List<SavedGameParser.CrewState> newCrewStatePlayer =
                Optional.ofNullable(currentGameState.getPlayerShip())
                        .map(SavedGameParser.ShipState::getCrewList)
                        .orElse(Collections.emptyList());

        List<SavedGameParser.CrewState> lastCrewStateEnemy =
                Optional.ofNullable(lastGameState.getNearbyShip())
                        .map(SavedGameParser.ShipState::getCrewList)
                        .orElse(Collections.emptyList());

        List<SavedGameParser.CrewState> newCrewStateEnemy =
                Optional.ofNullable(currentGameState.getNearbyShip())
                        .map(SavedGameParser.ShipState::getCrewList)
                        .orElse(Collections.emptyList());

        List<SavedGameParser.CrewState> lastCrewState = Stream.concat(lastCrewStatePlayer.stream(), lastCrewStateEnemy.stream())
                .filter(SavedGameParser.CrewState::isPlayerControlled)
                .toList();

        List<SavedGameParser.CrewState> newCrewState = Stream.concat(newCrewStatePlayer.stream(), newCrewStateEnemy.stream())
                .filter(SavedGameParser.CrewState::isPlayerControlled)
                .toList();

        Map<Integer, List<Integer>> possibleMatchesOldToNew = new HashMap<>();
        Map<Integer, Integer> mapOldToNew = new HashMap<>();
        List<Integer> newCrewMatched = new ArrayList<>();

        // Make a list of all possible matches for the old crew
        for (int i = 0; i < lastCrewState.size(); i++){
            SavedGameParser.CrewState lastState = lastCrewState.get(i);
            possibleMatchesOldToNew.put(i, new ArrayList<>());
            for (int j = 0; j < newCrewState.size(); j++){
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
//        possibleMatchesOldToNew.entrySet().stream()
//                .sorted(Comparator.comparingInt(entry -> entry.getValue().size())) // Todo maybe we need to sort after every assignment
//                .forEach(entry -> {
//                    Integer key = entry.getKey();
//                    List<Integer> possibleValues = entry.getValue();
//                    possibleValues.removeAll(newCrewMatched);
//                    if (!possibleValues.isEmpty()) {
//                        Integer chosen = possibleValues.getFirst(); // Todo Maybe prefer crew with the same name if possible
//                        newCrewMatched.add(chosen);
//                        mapOldToNew.put(key, chosen);
//                    } else {
//                        mapOldToNew.put(key, null);
//                    }
//                });

        // While we still have unprocessed entries
        while (!possibleMatchesOldToNew.isEmpty()) {
            // Sort dynamically by number of possible matches
            List<Map.Entry<Integer, List<Integer>>> sortedEntries =
                    possibleMatchesOldToNew.entrySet().stream()
                            .sorted(Comparator.comparingInt(entry -> entry.getValue().size()))
                            .toList();

            for (Map.Entry<Integer, List<Integer>> entry : sortedEntries) {
                Integer oldId = entry.getKey();
                List<Integer> possibleValues = new ArrayList<>(entry.getValue());

                // Remove already matched crew
                possibleValues.removeAll(newCrewMatched);

                Integer chosen = null;
                if (!possibleValues.isEmpty()) {
                    if (possibleValues.size() == 1) {
                        chosen = possibleValues.getFirst();
                    } else {
                        String oldName = lastCrewState.get(oldId).getName();
                        chosen = possibleValues.stream()
                                .filter(newId -> newCrewState.get(newId).getName().equals(oldName))
                                .findFirst()
                                .orElse(possibleValues.getFirst());
                    }
                    newCrewMatched.add(chosen);
                    mapOldToNew.put(oldId, chosen);
                } else {
                    mapOldToNew.put(oldId, null);
                }
                possibleMatchesOldToNew.remove(oldId);
                break;
            }
        }

        if (mapStateToInternal.isEmpty()){
            mapStateToInternal.putAll(mapOldToNew);
        }

        // Problem here
        // when discarding crew the crew positions need to be updated
        // at the moment this happens at the bottom in the projectValues method
        // so here the crew positions are wrong
        // maybe we can add these events after updating the crew positions
        // or we need pre calculate what the values will be

        // Create events for every Crew-match
        // If the old crew does not match with one in the new list the crew is dead/discarded
        int removedCrewCount = 0;
        Integer adjustedCrewPosition = null;

        for (Map.Entry<Integer, Integer> entry : mapOldToNew.entrySet()) {
            adjustedCrewPosition = getKeyByValue(mapStateToInternal, entry.getKey());
            if (adjustedCrewPosition != null){
                adjustedCrewPosition -= removedCrewCount;
            }


            events.addAll(compareCrewState(lastCrewState.get(entry.getKey()), entry.getValue() != null ? newCrewState.get(entry.getValue()) : null, jump, adjustedCrewPosition));

            if (entry.getValue() == null){
                removedCrewCount++;
            }
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
            Constants.EventTag tag = crewBought ? Constants.EventTag.BUY : Constants.EventTag.REWARD;
            CrewNewEvent event = new CrewNewEvent(jump);
            event.addTag(tag);
            event.setCrewPosition(lastCrewState.size());
            event.setCrew(new Crew(cs, eventType)); // todo why do we need eventType?
            events.addFirst(event);
            mapOldToNew.put(lastCrewState.size(), i);
        }

        if (!validateMap(mapStateToInternal, newCrewState)){
            log.error("Map validation failed");
        }

        mapStateToInternal = projectValues(mapStateToInternal, mapOldToNew);

        if (!validateMap(mapStateToInternal, newCrewState) || mapStateToInternal.size() != newCrewState.size()){
            log.error("Map validation failed after recalculation");
        }

        return events;
    }

    private boolean validateMap(Map<Integer, Integer> map, List<SavedGameParser.CrewState> crewStates){
        List<Integer> ids = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()){
            if (ids.contains(entry.getValue())){
                return false;
            }
            ids.add(entry.getValue());
        }

        for (int i = 0; i < map.size(); i++){
            if (!map.containsKey(i)){
                return false;
            }
        }

        return true;
    }

    private Integer getKeyByValue(Map<Integer, Integer> map, Integer value) {
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (Objects.equals(entry.getValue(), value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Map<Integer, Integer> projectValues(Map<Integer, Integer> map1, Map<Integer, Integer> map2) {
        Map<Integer, Integer> result = new HashMap<>();
        if (map1 == null) return result;

        for (Map.Entry<Integer, Integer> e1 : map1.entrySet()) {
            Integer key = e1.getKey();
            Integer lookupKey = e1.getValue();

            if (map2 != null && map2.containsKey(lookupKey)) {
                Integer newValue = map2.get(lookupKey);
                result.put(key, newValue);
            } else {
                result.put(key, e1.getValue());
            }
        }


        for (Map.Entry<Integer, Integer> e2 : map2.entrySet()){
            if (!result.containsKey(e2.getKey()) && !result.containsValue(e2.getValue()) && e2.getValue() != null){
                result.put(e2.getKey(), e2.getValue());
            }
        }

        result = compactMap(result);

        return result;
    }

    private Map<Integer, Integer> compactMap(Map<Integer, Integer> input) {
        Map<Integer, Integer> result = new LinkedHashMap<>(); // keeps order

        int newKey = 0;
        for (int oldKey = 0; oldKey <= input.size() - 1; oldKey++) {
            Integer value = input.get(oldKey);
            if (value != null) {
                result.put(newKey++, value);
            }
        }
        return result;
    }



    private List<Event> compareCrewState(SavedGameParser.CrewState lastCrewState, SavedGameParser.CrewState newCrewState, Jump jump, Integer crewPosition){
        List<Event> events = new ArrayList<>();
        // Crew dead
        if (newCrewState == null){
            CrewLostEvent event = new CrewLostEvent(jump);
//                    GausmanUtil.getCrewTypeName(lastCrewState.getRace().getId()) + " - " + lastCrewState.getName(),
            event.addTag(Constants.EventTag.DISCARD);
            event.setCrewPosition(crewPosition);
            events.add(event);
            return events;
        }

        assert lastCrewState != null;

        // Name Change
        if (!newCrewState.getName().equals(lastCrewState.getName())){
            CrewRenameEvent event = new CrewRenameEvent(
                    jump,
                    newCrewState.getName(),
                    lastCrewState.getName()
            );
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        // Stat Change
        int repairsChange = newCrewState.getRepairs() - lastCrewState.getRepairs();
        if (repairsChange != 0){
            CrewStatEvent event = new CrewStatEvent(
                    jump,
                    Constants.Stats.REPAIRS,
                    repairsChange
            );
            event.setDisplayText(String.format("%s - %s: %s", newCrewState.getName(), Constants.Stats.REPAIRS, newCrewState.getRepairs()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        int combatKillsChange = newCrewState.getCombatKills() - lastCrewState.getCombatKills();
        if (combatKillsChange != 0){
            CrewStatEvent event = new CrewStatEvent(
                    jump,
                    Constants.Stats.COMBAT_KILLS,
                    combatKillsChange
            );
            event.setDisplayText(String.format("%s - %s: %s", newCrewState.getName(), Constants.Stats.COMBAT_KILLS, newCrewState.getCombatKills()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        int pilotedEvasionsChange = newCrewState.getPilotedEvasions() - lastCrewState.getPilotedEvasions();
        if (pilotedEvasionsChange != 0){
            CrewStatEvent event = new CrewStatEvent(
                    jump,
                    Constants.Stats.PILOTED_EVASIONS,
                    pilotedEvasionsChange
            );
            event.setCrewPosition(crewPosition);
            event.setDisplayText(String.format("%s - %s: %s", newCrewState.getName(), Constants.Stats.PILOTED_EVASIONS, newCrewState.getPilotedEvasions()));
            events.add(event);
        }

        int jumpsSurvivedChange = newCrewState.getJumpsSurvived() - lastCrewState.getJumpsSurvived();
        if (jumpsSurvivedChange != 0){
            CrewStatEvent event = new CrewStatEvent(
                    jump,
                    Constants.Stats.JUMPS_SURVIVED,
                    jumpsSurvivedChange
            );
            event.setDisplayText(String.format("%s - %s: %s", newCrewState.getName(), Constants.Stats.JUMPS_SURVIVED, newCrewState.getJumpsSurvived()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        int skillMasteriesEarnedChange = newCrewState.getSkillMasteriesEarned() - lastCrewState.getSkillMasteriesEarned();
        if (skillMasteriesEarnedChange != 0){
            CrewStatEvent event = new CrewStatEvent(
                    jump,
                    Constants.Stats.SKILL_MASTERIES_EARNED,
                    skillMasteriesEarnedChange
            );
            event.setDisplayText(String.format("%s - %s: %s", newCrewState.getName(), Constants.Stats.SKILL_MASTERIES_EARNED, newCrewState.getSkillMasteriesEarned()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }


        // Skill Value Change
        int pilotSkillChange = newCrewState.getPilotSkill() - lastCrewState.getPilotSkill();
        if (pilotSkillChange != 0){
            CrewSkillEvent event = new CrewSkillEvent(
                    jump,
                    Constants.Skill.PILOT,
                    pilotSkillChange
            );
            event.setDisplayText(String.format("%s - %s Skill: %s", newCrewState.getName(), Constants.Skill.PILOT, newCrewState.getPilotSkill()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        int engineSkillChange = newCrewState.getEngineSkill() - lastCrewState.getEngineSkill();
        if (engineSkillChange != 0){
            CrewSkillEvent event = new CrewSkillEvent(
                    jump,
                    Constants.Skill.ENGINE,
                    engineSkillChange
            );
            event.setDisplayText(String.format("%s - %s Skill: %s", newCrewState.getName(), Constants.Skill.ENGINE, newCrewState.getEngineSkill()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        int shieldSkillChange = newCrewState.getShieldSkill() - lastCrewState.getShieldSkill();
        if (shieldSkillChange != 0){
            CrewSkillEvent event = new CrewSkillEvent(
                    jump,
                    Constants.Skill.SHIELD,
                    shieldSkillChange
            );
            event.setDisplayText(String.format("%s - %s Skill: %s", newCrewState.getName(), Constants.Skill.SHIELD, newCrewState.getShieldSkill()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        int weaponSkillChange = newCrewState.getWeaponSkill() - lastCrewState.getWeaponSkill();
        if (weaponSkillChange != 0){
            CrewSkillEvent event = new CrewSkillEvent(
                    jump,
                    Constants.Skill.WEAPON,
                    weaponSkillChange
            );
            event.setDisplayText(String.format("%s - %s Skill: %s", newCrewState.getName(), Constants.Skill.WEAPON, newCrewState.getWeaponSkill()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        int repairSkillChange = newCrewState.getRepairSkill() - lastCrewState.getRepairSkill();
        if (repairSkillChange != 0){
            CrewSkillEvent event = new CrewSkillEvent(
                    jump,
                    Constants.Skill.REPAIR,
                    repairSkillChange
            );
            event.setDisplayText(String.format("%s - %s Skill: %s", newCrewState.getName(), Constants.Skill.REPAIR, newCrewState.getRepairSkill()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        int combatSkillChange = newCrewState.getCombatSkill() - lastCrewState.getCombatSkill();
        if (combatSkillChange != 0){
            CrewSkillEvent event = new CrewSkillEvent(
                    jump,
                    Constants.Skill.COMBAT,
                    combatSkillChange
            );
            event.setDisplayText(String.format("%s - %s Skill: %s", newCrewState.getName(), Constants.Skill.COMBAT, newCrewState.getCombatSkill()));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }


        // Skill Mastery Change
        if (lastCrewState.getPilotMasteryOne() != newCrewState.getPilotMasteryOne()){
            MasteryEvent event = new MasteryEvent(
                    jump,
                    Constants.Skill.PILOT,
                    1,
                    newCrewState.getPilotMasteryOne()
            );
            event.setDisplayText(String.format("%s - %s Mastery Level %s", newCrewState.getName(), Constants.Skill.PILOT, 1));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getPilotMasteryTwo() != newCrewState.getPilotMasteryTwo()){
            MasteryEvent event = new MasteryEvent(
                    jump,
                    Constants.Skill.PILOT,
                    2,
                    newCrewState.getPilotMasteryTwo()
            );
            event.setDisplayText(String.format("%s - %s Mastery Level %s", newCrewState.getName(), Constants.Skill.PILOT, 2));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getEngineMasteryOne() != newCrewState.getEngineMasteryOne()){
            MasteryEvent event = new MasteryEvent(
                    jump,
                    Constants.Skill.ENGINE,
                    1,
                    newCrewState.getEngineMasteryOne()
            );
            event.setDisplayText(String.format("%s - %s Mastery Level %s", newCrewState.getName(), Constants.Skill.ENGINE, 1));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getEngineMasteryTwo() != newCrewState.getEngineMasteryTwo()){
            MasteryEvent event = new MasteryEvent(
                    jump,
                    Constants.Skill.ENGINE,
                    2,
                    newCrewState.getEngineMasteryTwo()
            );
            event.setDisplayText(String.format("%s - %s Mastery Level %s", newCrewState.getName(), Constants.Skill.ENGINE, 2));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getShieldMasteryOne() != newCrewState.getShieldMasteryOne()){
            MasteryEvent event = new MasteryEvent(
                    jump,
                    Constants.Skill.SHIELD,
                    1,
                    newCrewState.getShieldMasteryOne()
            );
            event.setDisplayText(String.format("%s - %s Mastery Level %s", newCrewState.getName(), Constants.Skill.SHIELD, 1));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getShieldMasteryTwo() != newCrewState.getShieldMasteryTwo()){
            MasteryEvent event = new MasteryEvent(
                    jump,
                    Constants.Skill.SHIELD,
                    2,
                    newCrewState.getShieldMasteryTwo()
            );
            event.setDisplayText(String.format("%s - %s Mastery Level %s", newCrewState.getName(), Constants.Skill.SHIELD, 2));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getWeaponMasteryOne() != newCrewState.getWeaponMasteryOne()){
            MasteryEvent event = new MasteryEvent(
                    jump,
                    Constants.Skill.WEAPON,
                    1,
                    newCrewState.getWeaponMasteryOne()
            );
            event.setDisplayText(String.format("%s - %s Mastery Level %s", newCrewState.getName(), Constants.Skill.WEAPON, 1));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getWeaponMasteryTwo() != newCrewState.getWeaponMasteryTwo()){
            MasteryEvent event = new MasteryEvent(
                    jump,
                    Constants.Skill.WEAPON,
                    2,
                    newCrewState.getWeaponMasteryTwo()
            );
            event.setDisplayText(String.format("%s - %s Mastery Level %s", newCrewState.getName(), Constants.Skill.WEAPON, 2));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getRepairMasteryOne() != newCrewState.getRepairMasteryOne()){
            MasteryEvent event = new MasteryEvent(
                    jump,
                    Constants.Skill.REPAIR,
                    1,
                    newCrewState.getRepairMasteryOne()
            );
            event.setDisplayText(String.format("%s - %s Mastery Level %s", newCrewState.getName(), Constants.Skill.REPAIR, 1));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getRepairMasteryTwo() != newCrewState.getRepairMasteryTwo()){
            MasteryEvent event = new MasteryEvent(
                    jump,
                    Constants.Skill.REPAIR,
                    2,
                    newCrewState.getRepairMasteryTwo()
            );
            event.setDisplayText(String.format("%s - %s Mastery Level %s", newCrewState.getName(), Constants.Skill.REPAIR, 2));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getCombatMasteryOne() != newCrewState.getCombatMasteryOne()){
            MasteryEvent event = new MasteryEvent(
                    jump,
                    Constants.Skill.COMBAT,
                    1,
                    newCrewState.getCombatMasteryOne()
            );
            event.setDisplayText(String.format("%s - %s Mastery Level %s", newCrewState.getName(), Constants.Skill.COMBAT, 1));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        if (lastCrewState.getCombatMasteryTwo() != newCrewState.getCombatMasteryTwo()){
            MasteryEvent event = new MasteryEvent(
                    jump,
                    Constants.Skill.COMBAT,
                    2,
                    newCrewState.getCombatMasteryTwo()
            );
            event.setDisplayText(String.format("%s - %s Mastery Level %s", newCrewState.getName(), Constants.Skill.COMBAT, 2));
            event.setCrewPosition(crewPosition);
            events.add(event);
        }

        return events;
    }


//    private void applyEventToCrew(Crew crew, Event event){
//        switch (event.getEventType()){
//            // we ignore Name Events because those are always first
//            case STAT -> {
//                StatEvent se = (StatEvent) event;
//                String statString = GausmanUtil.convertStatToAttributename(se.getStat());
//                int oldValue = (int) getValueInCrewByAttributename(crew, statString);
//                setValueInCrewByAttributename(crew, statString,oldValue + event.getAmount());
//            }
//            case SKILL -> {
//                SkillEvent se = (SkillEvent) event;
//                String skillString = GausmanUtil.convertSkillToAttributename(se.getSkill());
//                int oldValue = (int) getValueInCrewByAttributename(crew, skillString);
//                setValueInCrewByAttributename(crew, skillString,oldValue + event.getAmount());
//            }
//            case MASTERY -> {
//                MasteryEvent me = (MasteryEvent) event;
//                String masteryString = GausmanUtil.convertMasteryToAttributename(me.getMastery(), ((MasteryEvent) event).getLevel());
//                setValueInCrewByAttributename(crew, masteryString, me.getNewValue());
//            }
//            default -> System.out.println("ApplyEventsToCrew was called with a wrong event Type");
//        }
//
//    }

//    private void applyEventsToCrew(Crew crew, List<Event> events){
//        for (Event event : events){
//            switch (event.getEventType()){
//                // we ignore Name Events because those are always first
//                case STAT -> {
//                    StatEvent se = (StatEvent) event;
//                    String statString = GausmanUtil.convertStatToAttributename(se.getStat());
//                    int oldValue = (int) getValueInCrewByAttributename(crew, statString);
//                    setValueInCrewByAttributename(crew, statString,oldValue + event.getAmount());
//                }
//                case SKILL -> {
//                    SkillEvent se = (SkillEvent) event;
//                    String skillString = GausmanUtil.convertSkillToAttributename(se.getSkill());
//                    int oldValue = (int) getValueInCrewByAttributename(crew, skillString);
//                    setValueInCrewByAttributename(crew, skillString,oldValue + event.getAmount());
//                }
//                case MASTERY -> {
//                    MasteryEvent me = (MasteryEvent) event;
//                    String masteryString = GausmanUtil.convertMasteryToAttributename(me.getMastery(), ((MasteryEvent) event).getLevel());
//                    setValueInCrewByAttributename(crew, masteryString, me.getNewValue());
//                }
//                default -> System.out.println("ApplyEventsToCrew was called with a wrong event Type");
//            }
//        }
//
//    }

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
