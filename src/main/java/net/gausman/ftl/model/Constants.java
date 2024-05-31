package net.gausman.ftl.model;

public class Constants {
    public enum EventCategory{SYSTEM, AUGMENT, WEAPON, DRONE, CREW, RESOURCE};
    public enum EventType{UPGRADE, BUY, SELL, REWARD, DISCARD, START};
    public enum Resource{HULL, FUEL, MISSILES, DRONE_PARTS, SCRAP}
    public enum Result{WIN, LOSS, ONGOING}
}
