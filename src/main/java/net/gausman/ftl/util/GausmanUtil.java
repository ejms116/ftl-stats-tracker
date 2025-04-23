package net.gausman.ftl.util;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.xml.SystemBlueprint;
import net.gausman.ftl.controller.StatsManager;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.run.FTLJump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class GausmanUtil {
    private static final Logger log = LoggerFactory.getLogger(StatsManager.class);
    public static String formatInstant(Instant instant){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }
    public static String formatDuration(Duration duration){
        return String.format("%d:%02d:%02d",
                duration.toHours(),
                duration.toMinutesPart(),
                duration.toSecondsPart());
    }

    public static int getReactorUpgradeCost(int level){
        if (level < 6){
            return 30;
        } else if (level < 11){
            return 20;
        } else if (level < 16){
            return 25;
        } else if (level < 21){
            return 30;
        } else if (level < 26){
            return 35;
        } else {
            return 0;
        }

    }

    public static int getUpgradeCostSystem(String systemId, int levelBefore, int levelAfter){
        DataManager dm = DataManager.get();
        SystemBlueprint systemBlueprint = dm.getSystem(systemId);
        int upgradeCost = 0;

        while (levelAfter > levelBefore){
            upgradeCost += systemBlueprint.getUpgradeCosts().get(levelBefore-1);
            levelBefore++;
        }

        return upgradeCost;
    }

    public static int getCostStoreItemType(SavedGameParser.StoreItemType type, SavedGameParser.StoreItem item){
        DataManager dm = DataManager.get();
        return switch (type){
            case WEAPON -> dm.getWeapon(item.getItemId()).getCost();
            case DRONE -> dm.getDrone(item.getItemId()).getCost();
            case AUGMENT -> dm.getAugment(item.getItemId()).getCost();
            case CREW -> dm.getCrew(item.getItemId()).getCost();
            case SYSTEM -> dm.getSystem(item.getItemId()).getCost();
            case RESOURCE -> 0; // should not get called
            case REACTOR -> 0;
        };
    }

    public static int getCostSellStoreItemType(SavedGameParser.StoreItemType type, String item){
        DataManager dm = DataManager.get();
        return switch (type){
            case WEAPON -> dm.getWeapon(item).getCost()/2;
            case DRONE -> dm.getDrone(item).getCost()/2;
            case AUGMENT -> dm.getAugment(item).getCost()/2;
            case CREW -> dm.getCrew(item).getCost()/2;
            case SYSTEM -> dm.getSystem(item).getCost()/2;
            case RESOURCE -> 0; // should not get called
            case REACTOR -> 0;
        };
    }

    public static int getCostResource(String id, int amount, int sector){
        Constants.Resource res = Constants.Resource.valueOf(id);
        int result = switch (res){
            case Constants.Resource.FUEL -> 3;
            case Constants.Resource.MISSILE -> 6;
            case Constants.Resource.DRONE -> 8;
            case Constants.Resource.REPAIR -> 2;
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };

//        if (id.equals(Constants.Resource.REPAIR.name())){
        if (res.equals(Constants.Resource.REPAIR)){
            if (sector > 5){
                result += 2;
            } else if (sector > 2) {
                result += 1;
            }
        }

        return result*amount;
    }

    public static String getTextSystemId(SavedGameParser.SystemType systemType){
        DataManager dm = DataManager.get();
        return dm.getSystem(systemType.getId()).getTitle().getTextValue();
    }

    public static String getTextToId(SavedGameParser.StoreItemType itemType, String id){
        DataManager dm = DataManager.get();

        return switch (itemType) {
            case SavedGameParser.StoreItemType.RESOURCE -> getResourceText(id);
            case SavedGameParser.StoreItemType.AUGMENT -> dm.getAugment(id).getTitle().getTextValue();
            case SavedGameParser.StoreItemType.CREW -> dm.getCrew(id).getTitle().getTextValue();
            case SavedGameParser.StoreItemType.WEAPON -> dm.getWeapon(id).getTitle().getTextValue();
            case SavedGameParser.StoreItemType.DRONE -> dm.getDrone(id).getTitle().getTextValue();
            case SavedGameParser.StoreItemType.SYSTEM -> dm.getSystem(id).getTitle().getTextValue();
            case SavedGameParser.StoreItemType.REACTOR -> SavedGameParser.StoreItemType.valueOf(id).toString();
        };
    }

    private static String getResourceText(String id){
        try {
            return Constants.Resource.valueOf(id).toString();
        } catch (Exception e){
            return id;
        }
    }


    public static String generateFileNameForRun(Instant startTime, String playerShipBlueprintId){
        DataManager dm = DataManager.get();
        String runFileName = String.format("runs\\%s-%s.json", GausmanUtil.formatInstant(startTime), dm.getShip(playerShipBlueprintId).getName().getTextValue());
        runFileName = runFileName.replaceAll("\\:", "-");
        return runFileName;
    }

    public static String generateFolderNameForSave(Instant startTime, String playerShipBlueprintId){
        DataManager dm = DataManager.get();
        String runFileName = String.format("saves\\%s-%s", GausmanUtil.formatInstant(startTime), dm.getShip(playerShipBlueprintId).getName().getTextValue());
        runFileName = runFileName.replaceAll("\\:", "-");
        return runFileName;
    }
    public static String generateFileNameForSave(Instant startTime, String playerShipBlueprintId, int jumpNumber, int sectorNumber){
        DataManager dm = DataManager.get();
//        String runFileName = String.format("saves\\%s-%s\\%d(%d).sav", GausmanUtil.formatInstant(startTime), dm.getShip(playerShipBlueprintId).getName().getTextValue(), jumpNumber, sectorNumber);
        String runFileName = String.format("saves\\test\\%d(%d).sav", jumpNumber, sectorNumber);
        runFileName = runFileName.replaceAll("\\:", "-");
        return runFileName;
    }


    public static void consolidateEventList(FTLJump jump){

        // 1. filter all relevant items into new list
        // 2. if new list contains 2 or more elements, we need to consolidate, otherwise not
        // 3. Use the first event with the lowest timestamp
        // 4. add usages of the other events to the first event
        // 5. remove the other everts
        log.info("Consolidate Events");

//        for (FTLRunEvent event: jump.getEvents()){
//            System.out.println(event);
//        }
//
//        System.out.println("Jump");
    }

    public static int extractLeadingNumber(String filename) {
        try {
            String numberPart = filename.split("[^0-9]")[0]; // get leading digits
            return Integer.parseInt(numberPart);
        } catch (Exception e) {
            return Integer.MAX_VALUE; // put files without numbers at the end
        }
    }

}
