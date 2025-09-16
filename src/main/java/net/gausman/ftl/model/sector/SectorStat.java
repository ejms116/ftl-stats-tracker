package net.gausman.ftl.model.sector;

import java.util.EnumMap;

public interface SectorStat<S extends Enum<S>> {
    int getValue(S subCategory);
    void setValue(S subCategory, int value);
    EnumMap<S, Integer> getAll();
}
