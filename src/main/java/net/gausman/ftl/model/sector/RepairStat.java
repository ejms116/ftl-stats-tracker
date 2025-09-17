package net.gausman.ftl.model.sector;

import net.gausman.ftl.model.Constants;

import java.util.EnumMap;

public class RepairStat implements SectorStat<Constants.RepairSub>{

    private final EnumMap<Constants.RepairSub, Integer> values = new EnumMap<>(Constants.RepairSub.class);

    public RepairStat() {
        for (Constants.RepairSub sub : Constants.RepairSub.values()) {
            values.put(sub, 0);
        }
    }


    @Override
    public int getValue(Constants.RepairSub subCategory) {
        return values.get(subCategory);
    }

    @Override
    public void setValue(Constants.RepairSub subCategory, int value) {
        values.put(subCategory, value);
    }

    @Override
    public EnumMap<Constants.RepairSub, Integer> getAll() {
        return values;
    }


}
