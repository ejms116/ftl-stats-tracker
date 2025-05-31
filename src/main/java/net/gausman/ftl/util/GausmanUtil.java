package net.gausman.ftl.util;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.xml.ShipBlueprint;
import net.blerf.ftl.xml.SystemBlueprint;
import net.blerf.ftl.xml.event.AbstractBuildableTreeNode;
import net.blerf.ftl.xml.event.BuildableTreeNode;
import net.gausman.ftl.controller.StatsManager;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.run.FTLJump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import javax.swing.tree.DefaultMutableTreeNode;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GausmanUtil {
    private static final Logger log = LoggerFactory.getLogger(StatsManager.class);

    public static void logXmlWarningsRecoursive(AbstractBuildableTreeNode node, List<String> occurence){
        if (node.getUnknownElements() != null){
            for (Element element : node.getUnknownElements()){

                String unknownTagMessage =
                        String.format("Unknown Element: <%s>, Occurences: %s",
                                element.getTagName(),
                                String.join(" > ", occurence));
                log.info(unknownTagMessage);

            }
        }
        for (Field field : node.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(node);
                if (value instanceof AbstractBuildableTreeNode){
                    List<String> occurenceNew = new ArrayList<>(occurence);
                    occurenceNew.add(value.toString());
                    logXmlWarningsRecoursive((AbstractBuildableTreeNode) value, occurenceNew);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static String callMethodIfExists(Object obj, String methodName) {
        if (obj == null || methodName == null) return "";

        try {
            // Get the method with no parameters
            Method method = obj.getClass().getMethod(methodName);

            // Call the method and return the result as a string
            Object result = method.invoke(obj);
            return (result != null) ? result.toString() : "";
        } catch (NoSuchMethodException e) {
            // Method does not exist
            return "";
        } catch (Exception e) {
            // Method exists but something went wrong during invocation
            e.printStackTrace();
            return "";
        }
    }

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

    public static String convertBlueprintName(String blueprint){
        DataManager dm = DataManager.get();
        ShipBlueprint ship = dm.getShip(blueprint);
        return ship.getName().getTextValue();
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
            case GENERAL -> 0;
        };
    }

    public static int getCostStoreItemId(SavedGameParser.StoreItemType type, String id){
        DataManager dm = DataManager.get();
        return switch (type){
            case WEAPON -> dm.getWeapon(id).getCost();
            case DRONE -> dm.getDrone(id).getCost();
            case AUGMENT -> dm.getAugment(id).getCost();
            case CREW -> dm.getCrew(id).getCost();
            case SYSTEM -> dm.getSystem(id).getCost();
            case RESOURCE -> 0; // should not get called
            case REACTOR -> 0;
            case GENERAL -> 0;
        };
    }

    @Deprecated
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
            case GENERAL -> 0;
        };
    }

    public static int getCostResource(String id, int amount, int sector){
        Constants.Resource res = Constants.Resource.valueOf(id);
        int result = switch (res){
            case Constants.Resource.FUEL -> 3;
            case Constants.Resource.MISSILE -> 6;
            case Constants.Resource.DRONE -> 8;
            case Constants.Resource.HULL -> 2;
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };

//        if (id.equals(Constants.Resource.REPAIR.name())){
        if (res.equals(Constants.Resource.HULL)){
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

        if (itemType == null){
            log.error("itemtype cant be null");
        }

        return switch (itemType) {
            case SavedGameParser.StoreItemType.RESOURCE -> getResourceText(id);
            case SavedGameParser.StoreItemType.AUGMENT -> dm.getAugment(id).getTitle().getTextValue();
            case SavedGameParser.StoreItemType.CREW -> id; // dm.getCrew(id).getTitle().getTextValue();
            case SavedGameParser.StoreItemType.WEAPON -> dm.getWeapon(id).getTitle().getTextValue();
            case SavedGameParser.StoreItemType.DRONE -> dm.getDrone(id).getTitle().getTextValue();
            case SavedGameParser.StoreItemType.SYSTEM -> dm.getSystem(id).getTitle().getTextValue();
            case SavedGameParser.StoreItemType.REACTOR -> Constants.Reactor.valueOf(id).toString();
            case GENERAL -> id;
        };
    }

    private static String getResourceText(String id){
        try {
            return Constants.Resource.valueOf(id).toString();
        } catch (Exception e){
            return id;
        }
    }

    public static String getCrewTypeName(String id){
        DataManager dm = DataManager.get();
        return dm.getCrew(id).getTitle().getTextValue();
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

    public static String convertStatToAttributename(Constants.Stats stat){
        return switch (stat){
            case REPAIRS -> "repairs";
            case COMBAT_KILLS -> "combatKills";
            case PILOTED_EVASIONS -> "pilotedEvasions";
            case JUMPS_SURVIVED -> "jumpsSurvived";
            case SKILL_MASTERIES_EARNED -> "skillMasteriesEarned";
        };
    }

    public static String convertSkillToAttributename(Constants.Skill skill){
        return switch (skill){
            case PILOT -> "pilotSkill";
            case ENGINE -> "engineSkill";
            case SHIELD -> "shieldSkill";
            case WEAPON -> "weaponSkill";
            case REPAIR -> "repairSkill";
            case COMBAT -> "combatSkill";
        };
    }

    public static String convertMasteryToAttributename(Constants.Skill mastery, int level){
        return switch (mastery){
            case PILOT -> level == 1 ? "pilotMasteryOne" : "pilotMasteryTwo";
            case SHIELD -> level == 1 ? "engineMasteryOne" : "engineMasteryTwo";
            case WEAPON -> level == 1 ? "shieldMasteryOne" : "shieldMasteryTwo";
            case ENGINE -> level == 1 ? "weaponMasteryOne" : "weaponMasteryTwo";
            case REPAIR -> level == 1 ? "repairMasteryOne" : "repairMasteryTwo";
            case COMBAT -> level == 1 ? "combatMasteryOne" : "combatMasteryTwo";
        };
    }

    public static String extractId(String nameAttr) {
        // Extract only the content of the name attribute (everything after "name="event_" and before the next "_")
        String prefix = "event_";
        if (!nameAttr.startsWith("event_")) return null;

        String content = nameAttr.substring(prefix.length());

        // Remove trailing known suffixes like "_text", "_choice", "_clone", etc.
        content = content.replaceAll("(_c\\d+(_text|_choice)?)|(_text)|(_clone)$", "");

        // Split into parts to analyze underscore-separated tokens
        String[] parts = content.split("_");
        StringBuilder idBuilder = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            // Check if the part is uppercase or contains digits directly appended to uppercase letters
            if (part.matches("[A-Z0-9]+") || part.matches("[A-Z]+\\d+")) {
                idBuilder.append(part);
                if (i < parts.length - 1) idBuilder.append("_");
            } else {
                // Stop at the first lowercase or invalid segment
                break;
            }
        }

        return idBuilder.toString().replaceAll("_+$", ""); // remove trailing underscores
    }
}
