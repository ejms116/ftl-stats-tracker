package net.gausman.ftl.model;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.record.Sector;
import net.gausman.ftl.model.record.StoreInfo;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SectorMetrics {
    private final Map<Sector, SectorInfo> data = new LinkedHashMap<>();

    public SectorMetrics(){}

    public SectorMetrics(SectorMetrics other){
        for (Map.Entry<Sector, SectorInfo> entry : other.data.entrySet()) {
            this.data.put(entry.getKey(), new SectorInfo(entry.getValue()));
        }
    }

    private SectorInfo ensureSector(Sector sector) {
        return data.computeIfAbsent(sector, s -> new SectorInfo());
    }

    public void update(Sector sector, boolean apply, Integer index, String itemId, SavedGameParser.StoreItemType itemType){
        ensureSector(sector).setItemAvailableInStore(apply, index, itemId, itemType);
    }

    public void update(Sector sector, StoreInfo storeInfo, boolean apply, Integer index){
        ensureSector(sector).applyStoreInfo(apply, index, storeInfo);
    }

    public void update(Sector sector, Constants.ScrapOrigin origin, int delta) {
        ensureSector(sector).add(origin, delta);
    }

    public void update(Sector sector, Constants.ScrapUsedCategory category, int delta){
        ensureSector(sector).add(category, delta);
    }

    public SectorInfo getInfo(Sector sector) {
        return ensureSector(sector);
    }

    public Map<Sector, SectorInfo> getData() {
        return Collections.unmodifiableMap(data);
    }
}


