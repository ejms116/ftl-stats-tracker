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
        USE("Use");

        private final String displayName;

        EventType(String displayName) {
            this.displayName = displayName;
        }

        public String toString(){
            return displayName;
        }
    };

    public enum Resource{
        REPAIR("Repair"),
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
    public enum Result{WIN, LOSS, ONGOING}
}
