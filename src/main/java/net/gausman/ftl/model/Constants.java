package net.gausman.ftl.model;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    //public enum EventCategory{SYSTEM, AUGMENT, WEAPON, DRONE, CREW, RESOURCE};

    public enum EventTag {
        BUY(Color.GREEN),          // positive action
        SELL(Color.RED),           // negative/action out
        START(Color.CYAN),         // beginning/start
        REWARD(Color.YELLOW),      // reward/gain
        DISCARD(Color.GRAY),       // remove/dispose
        EVENT(Color.MAGENTA),      // neutral/general event
        STORE(Color.LIGHT_GRAY),
        DAMAGE(Color.ORANGE),      // damage/warning
        REPAIR(new Color(139, 69, 19)), // brownish for repair/fix
        USE(Color.PINK),           // usage/action
        STAT(Color.DARK_GRAY),
        ERROR(Color.WHITE),
        ;

        private final Color color;

        EventTag(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

    public enum EventCategory {
            OTHER("Other"),
            RESOURCES("Resources"),
            GENERAL("General"),
            ITEM("Items"),
            SYSTEM("System"), // includes Reactor
            CREW("Crew"),
        ;

        private final String displayName;

        EventCategory(String displayName) {
            this.displayName = displayName;
        }

        public String toString(){
            return displayName;
        }

        private static final Map<String, EventCategory> LOOKUP = new HashMap<>();

        static {
            for (EventCategory type : values()) {
                LOOKUP.put(type.displayName, type);
            }
        }

        public static EventCategory fromText(String displayName) {
            return LOOKUP.get(displayName); // returns null if not found
        }
    }

    public enum EventDetailType {
        REPAIR("Repair", EventCategory.OTHER),
        DAMAGE("Damage", EventCategory.OTHER),
        SHIP_SETUP("Ship setup", EventCategory.OTHER),
        RESOURCE_DIFF_ERROR("Resource Diff Error", EventCategory.OTHER),

        RESOURCES_RECEIVED("Resources received", EventCategory.RESOURCES),
        BUY_FUEL("Fuel buy", EventCategory.RESOURCES),
        BUY_MISSILE("Missiles buy", EventCategory.RESOURCES),
        BUY_DRONE("Drones buy", EventCategory.RESOURCES),
        USE_FUEL("Fuel used", EventCategory.RESOURCES),
        USE_MISSILE("Missiles used", EventCategory.RESOURCES),
        USE_DRONE("Drones used", EventCategory.RESOURCES),
        USE_HACKING("Hacking used", EventCategory.RESOURCES),

        SCRAP_COLLECTED("Scrap collected", EventCategory.GENERAL),
        SRA_EXTRA_SCRAP("SRA bonus scrap", EventCategory.GENERAL),
        BEACONS_EXPLORED("Beacons explored", EventCategory.GENERAL),
        SHIPS_DESTROYED("Ships destroyed", EventCategory.GENERAL),
        CREW_HIRED("Crew hired", EventCategory.GENERAL),

        WEAPON("Weapon", EventCategory.ITEM),
        DRONE("Drone", EventCategory.ITEM),
        AUGMENT("Augment", EventCategory.ITEM),

        SYSTEM("System", EventCategory.SYSTEM),
        SUBSYSTEM("Subsystem", EventCategory.SYSTEM),
        REACTOR("Reactor", EventCategory.SYSTEM),

        CREW_RENAME("Crew rename", EventCategory.CREW),
        CREW_NEW("New Crew", EventCategory.CREW),
        CREW_LOST("Lost Crew", EventCategory.CREW),
        CREW_MASTERY("Crew Mastery", EventCategory.CREW),
        CREW_SKILL("Crew Skill", EventCategory.CREW),
        CREW_STAT("Crew Stat", EventCategory.CREW),


        ;

        private final String displayName;
        private final EventCategory eventCategory;

        EventDetailType(String displayName, EventCategory category) {
            this.displayName = displayName;
            this.eventCategory = category;
        }

        public String toString(){
            return displayName;
        }

        public EventCategory getEventCategory() {
            return eventCategory;
        }

        private static final Map<String, EventDetailType> LOOKUP = new HashMap<>();

        static {
            for (EventDetailType type : values()) {
                LOOKUP.put(type.displayName, type);
            }
        }

        public static EventDetailType fromText(String displayName) {
            return LOOKUP.get(displayName); // returns null if not found
        }
    }

    public enum EventType{
        UPGRADE("Upgrade"),
        BUY("Buy"),
        SELL("Sell"),
        REWARD("Reward"),
        DISCARD("Discard"),
        START("Start"),
        USE("Use"),
        STAT("Stat"),
        SKILL("Skill"),
        MASTERY("Mastery"),
        NAME("Name"),
        DAMAGE("Damage"),
        GENERAL("General"),
        ;

        private final String displayName;

        EventType(String displayName) {
            this.displayName = displayName;
        }

        public String toString(){
            return displayName;
        }

        private static final Map<String, EventType> LOOKUP = new HashMap<>();

        static {
            for (EventType type : values()) {
                LOOKUP.put(type.displayName, type);
            }
        }

        public static EventType fromText(String displayName) {
            return LOOKUP.get(displayName); // returns null if not found
        }
    }

    public enum Stats{
        REPAIRS("Repairs"),
        COMBAT_KILLS("Combat kills"),
        PILOTED_EVASIONS("Piloted evasions"),
        JUMPS_SURVIVED("Jumps survived"),
        SKILL_MASTERIES_EARNED("Skill masteries earned");

        private final String displayName;

        Stats(String displayName) {
            this.displayName = displayName;
        }

        public String toString(){
            return displayName;
        }

    }


    public enum Resource {
        HULL("Hull", "/icons/icon_hull.png"),
        FUEL("Fuel", "/icons/icon_fuel.png"),
        MISSILE("Missile", "/icons/icon_missiles.png"),
        DRONE("Drone", "/icons/icon_drones.png"),
        SCRAP("Scrap", "/icons/icon_scrap.png");

        private final String displayName;
        private final ImageIcon icon;

        Resource(String displayName, String iconPath) {
            this.displayName = displayName;
            URL url = Resource.class.getResource(iconPath);
            this.icon = (url != null) ? new ImageIcon(url) : null;
        }

        public String getDisplayName() {
            return displayName;
        }

        public ImageIcon getIcon() {
            return icon;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public enum General{
        SHIP_NAME("Name"),
        SHIP_BLUEPRINT("Blueprint"),
        DIFFICULTY("Difficulty"),
        BEACONS_EXPLORED("Beacons explored"),
        SHIPS_DESTROYED("Ships destroyed"),
        SCRAP_COLLECTED("Scrap collected"),
        SCRAP_DIFF("Scrap difference"),
        CREW_HIRED("Crew hired"),
        ;

        private final String displayName;

        General(String displayName){
            this.displayName = displayName;
        }

        public String toString(){
            return displayName;
        }

    }

    public enum Skill{
        PILOT("Pilot"),
        ENGINE("Engine"),
        SHIELD("Shield"),
        WEAPON("Weapon"),
        REPAIR("Repair"),
        COMBAT("Combat"),
        ;

        private final String displayName;

        Skill(String displayName){
            this.displayName = displayName;
        }

        public String toString(){
            return displayName;
        }
    }

    public enum ItemOrigin{
        START("Start"),
        BUY("Buy"),
        BUY_EVENT("Buy Event"),
        REWARD("Reward"),
        ;

        private final String displayName;

        ItemOrigin(String displayName){
            this.displayName = displayName;
        }

        public String toString(){
            return displayName;
        }
    }

    public enum ItemState{
        INVENTORY("Inventory"),
        SOLD("Sold"),
        DISCARDED("Discarded"),
        ;

        private final String displayName;

        ItemState(String displayName){
            this.displayName = displayName;
        }

        public String toString(){
            return displayName;
        }
    }

    public enum CrewAliveOrDead {
        ALIVE("Alive"),
        DEAD("Dead"),
        ;

        private final String displayName;

        CrewAliveOrDead(String displayName){
            this.displayName = displayName;
        }

        public String toString(){
            return displayName;
        }
    }

    public enum ScrapOrigin {
        NORMAL("Normal"),
        FREE("Free stuff"),
        SRA("Scrap Recovery Arm")
        ;

        private final String displayName;

        ScrapOrigin(String displayName){
            this.displayName = displayName;
        }

        public String toString(){
            return displayName;
        }
    }

    public enum ScrapUsedCategory {
        FUEL("Fuel"),
        MISSILES("Missiles"),
        DRONE_PARTS("Drone parts"),
        REPAIR("Repair"),
        SYSTEM_BUY("System buy"),
        REACTOR("Reactor"),
        WEAPONS("Weapons"),
        DRONES("Drones"),
        AUGMENTS("Augments"),
        CREW("Crew"),
        ;

        private final String displayName;

        ScrapUsedCategory(String displayName){
            this.displayName = displayName;
        }

        public String toString(){
            return displayName;
        }
    }

    public enum SectorStatsData {
            DAMAGE("Damage"),
            REPAIR("Repair"),
            FUEL("Fuel"),
            MISSILES("Missiles"),
            DRONE_PARTS("Drone part"),
        ;

        private final String displayName;

        SectorStatsData(String displayName){
            this.displayName = displayName;
        }

        public String toString(){
            return displayName;
        }
    }

    public enum DamageSub {
        NORMAL("Normal"),
        EVENT("Event"),
        ;

        private final String displayName;

        DamageSub(String displayName){
            this.displayName = displayName;
        }

        public String toString(){
            return displayName;
        }
    }

    public enum RepairSub {
        STORE("Store"),
        EVENT("Event"),
        DRONE("Drone"),
        ;

        private final String displayName;

        RepairSub(String displayName){
            this.displayName = displayName;
        }

        public String toString(){
            return displayName;
        }
    }

    public enum ResourceOrigin {
        NORMAL("Normal"),
        EVENT("Event"),
        STORE("Store"),
        ;

        private final String displayName;

        ResourceOrigin(String displayName){
            this.displayName = displayName;
        }

        public String toString(){
            return displayName;
        }
    }


    public enum Result{WIN, LOSS, ONGOING}

    public static Color[] flatLafDarkColors = new Color[] {
            new Color(0x4CAF50), // green
            new Color(0x2196F3), // blue
            new Color(0xFFC107), // amber
            new Color(0xE91E63), // pink/red
            new Color(0x9C27B0), // purple
            new Color(0xFF5722), // deep orange
            new Color(0x00BCD4), // cyan
            new Color(0x8BC34A)  // light green
    };
}
