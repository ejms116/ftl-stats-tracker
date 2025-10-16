package net.gausman.ftl.model;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.record.StoreInfo;
import net.gausman.ftl.model.sector.DamageStat;
import net.gausman.ftl.model.sector.RepairStat;
import net.gausman.ftl.model.sector.SectorStat;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class SectorInfo {
    private final EnumMap<Constants.ScrapOrigin, Integer> scrapGained;
    private final EnumMap<Constants.ScrapUsedCategory, Integer> scrapUsed;
    private final EnumMap<Constants.SectorStatsData, SectorStat<?>> sectorStats;
    private final EnumMap<Constants.SectorStatsData, ResourceOriginIntegerMap> sectorStatsData;
    private final Map<Integer, StoreInfo> storeInfoMap;

    public SectorInfo(){
        scrapGained = new EnumMap<>(Constants.ScrapOrigin.class);
        for (Constants.ScrapOrigin origin : Constants.ScrapOrigin.values()){
            scrapGained.put(origin, 0);
        }
        scrapUsed = new EnumMap<>(Constants.ScrapUsedCategory.class);
        for (Constants.ScrapUsedCategory category : Constants.ScrapUsedCategory.values()){
            scrapUsed.put(category, 0);
        }
        sectorStats = new EnumMap<>(Constants.SectorStatsData.class);
        sectorStats.put(Constants.SectorStatsData.DAMAGE, new DamageStat());
        sectorStats.put(Constants.SectorStatsData.REPAIR, new RepairStat());

        sectorStatsData = new EnumMap<>(Constants.SectorStatsData.class);
        for (Constants.SectorStatsData sectorStat : Constants.SectorStatsData.values()){
            sectorStatsData.put(sectorStat, new ResourceOriginIntegerMap());
        }
        storeInfoMap = new HashMap<>();
    }

    public SectorInfo(SectorInfo other){
        this.scrapGained = new EnumMap<>(other.scrapGained);
        this.scrapUsed = new EnumMap<>(other.scrapUsed);
        this.sectorStats = new EnumMap<>(other.sectorStats);
        this.sectorStatsData = new EnumMap<>(other.sectorStatsData);
        this.storeInfoMap = new HashMap<>();
        for (Map.Entry<Integer, StoreInfo> entry : other.getStoreInfoMap().entrySet()){
            this.storeInfoMap.put(entry.getKey(), new StoreInfo(entry.getValue()));
        }
    }

    public void setItemAvailableInStore(boolean apply, Integer index, String itemId, SavedGameParser.StoreItemType itemType){
        StoreInfo storeInfo = storeInfoMap.get(index);
        if (storeInfo == null){
            return;
        }

        SavedGameParser.StoreState store = storeInfo.getStore();

        if (store == null){
            return;
        }

        for (SavedGameParser.StoreShelf shelf : store.getShelfList()){
            if (shelf.getItemType().equals(itemType)){
                for (SavedGameParser.StoreItem item : shelf.getItems()){
                    if (item.getItemId().equals(itemId)){
                        if (item.isAvailable() == apply){
                            item.setAvailable(!apply);
                            return;
                        }
                    }
                }
                return;
            }
        }
    }


    public void applyStoreInfo(boolean apply, Integer index, StoreInfo storeInfo){
        if (apply){
            storeInfoMap.put(index, storeInfo);
        } else {
            storeInfoMap.remove(index);
        }
    }

    public void add(Constants.ScrapOrigin origin, int delta){
        scrapGained.put(origin, scrapGained.get(origin)+delta);
    }

    public void add(Constants.ScrapUsedCategory category, int delta){
        scrapUsed.put(category, scrapUsed.get(category)+delta);
    }

    public Map<Integer, StoreInfo> getStoreInfoMap() {
        return storeInfoMap;
    }

    public EnumMap<Constants.ScrapOrigin, Integer> getScrapGained() {
        return scrapGained;
    }

    public EnumMap<Constants.ScrapUsedCategory, Integer> getScrapUsed() {
        return scrapUsed;
    }

    public EnumMap<Constants.SectorStatsData, SectorStat<?>> getSectorStats() {
        return sectorStats;
    }

    public EnumMap<Constants.SectorStatsData, ResourceOriginIntegerMap> getSectorStatsData() {
        return sectorStatsData;
    }
}
