package net.gausman.ftl.model.sector;

import net.gausman.ftl.model.Constants;

import java.util.EnumMap;

public class DamageStat implements SectorStat<Constants.DamageSub> {
    private final EnumMap<Constants.DamageSub, Integer> values = new EnumMap<>(Constants.DamageSub.class);

    public DamageStat() {
        for (Constants.DamageSub sub : Constants.DamageSub.values()) {
            values.put(sub, 0);
        }
    }

    @Override
    public int getValue(Constants.DamageSub subCategory) {
        return values.get(subCategory);
    }

    @Override
    public void setValue(Constants.DamageSub subCategory, int value) {
        values.put(subCategory, value);
    }

    @Override
    public EnumMap<Constants.DamageSub, Integer> getAll() {
        return values;
    }
}

