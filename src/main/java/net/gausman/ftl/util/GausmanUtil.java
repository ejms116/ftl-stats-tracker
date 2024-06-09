package net.gausman.ftl.util;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.xml.FTLEvent;
import net.gausman.ftl.controller.StatsManager;
import net.gausman.ftl.model.run.FTLJump;
import net.gausman.ftl.model.run.FTLRunEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    public static int getCostStoreItemType(SavedGameParser.StoreItemType type, SavedGameParser.StoreItem item){
        DataManager dm = DataManager.get();
        int cost = 0;
        return switch (type){
            case WEAPON -> dm.getWeapon(item.getItemId()).getCost();
            case DRONE -> dm.getDrone(item.getItemId()).getCost();
            case AUGMENT -> dm.getAugment(item.getItemId()).getCost();
            case CREW -> dm.getCrew(item.getItemId()).getCost();
            case SYSTEM -> dm.getSystem(item.getItemId()).getCost();
            case RESOURCE -> 0; // should not get called
            case REACTOR -> 0;
        };

        //return cost;
    }

    public static int getCostResource(String id, int amount, int sector){
        int result = switch (id){
            case "FUEL" -> 3;
            case "MISSILE" -> 6;
            case "DRONE_PART" -> 8;
            case "REPAIR" -> 2;
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };

        if (id.equals("REPAIR")){
            if (sector > 5){
                result += 2;
            } else if (sector > 2) {
                result += 1;
            }
        }

        return result*amount;
    }

    public static String getTextToId(SavedGameParser.StoreItemType itemType, String id){
        DataManager dm = DataManager.get();

        return switch (itemType) {
            case SavedGameParser.StoreItemType.RESOURCE -> id;
            case SavedGameParser.StoreItemType.AUGMENT -> dm.getAugment(id).getTitle().getTextValue();
            case SavedGameParser.StoreItemType.CREW -> dm.getCrew(id).getTitle().getTextValue();
            case SavedGameParser.StoreItemType.WEAPON -> dm.getWeapon(id).getTitle().getTextValue();
            case SavedGameParser.StoreItemType.DRONE -> dm.getDrone(id).getTitle().getTextValue();
            case SavedGameParser.StoreItemType.SYSTEM -> dm.getSystem(id).getTitle().getTextValue();
            case SavedGameParser.StoreItemType.REACTOR -> id;
        };
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

}
