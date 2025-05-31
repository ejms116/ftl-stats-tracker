package net.gausman.ftl.model;

public class Constants {
    //public enum EventCategory{SYSTEM, AUGMENT, WEAPON, DRONE, CREW, RESOURCE};
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
    };

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

    public enum Resource{
//        REPAIR("Repair"),
        HULL("Hull"),
        FUEL("Fuel"),
        MISSILE("Missile"),
        DRONE("Drone"),
        SCRAP("Scrap");

        private final String displayName;

        Resource(String displayName) {
            this.displayName = displayName;
        }

        public String toString(){
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

    public enum Reactor{
        POWER_BAR("Power Bar"),
        ;

        private final String displayName;

        Reactor(String displayName){
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

    public enum Result{WIN, LOSS, ONGOING}
}
