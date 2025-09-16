package net.gausman.ftl.model;

import net.gausman.ftl.model.sector.DamageStat;
import net.gausman.ftl.model.sector.RepairStat;
import net.gausman.ftl.model.sector.SectorStat;

import java.util.EnumMap;

public class SectorInfo {
    private final EnumMap<Constants.ScrapOrigin, Integer> scrapGained;
    private final EnumMap<Constants.ScrapUsedCategory, Integer> scrapUsed;
    private final EnumMap<Constants.SectorStatsData, SectorStat<?>> sectorStats;
    private final EnumMap<Constants.SectorStatsData, ResourceOriginIntegerMap> sectorStatsData;

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
//        for (Constants.SectorStatsData sectorStat : Constants.SectorStatsData.values()){
//            sectorStats.put(sectorStat, new ResourceOriginIntegerMap());
//        }
        sectorStatsData = new EnumMap<>(Constants.SectorStatsData.class);
        for (Constants.SectorStatsData sectorStat : Constants.SectorStatsData.values()){
            sectorStatsData.put(sectorStat, new ResourceOriginIntegerMap());
        }
    }

    public SectorInfo(SectorInfo other){
        this.scrapGained = new EnumMap<>(other.scrapGained);
        this.scrapUsed = new EnumMap<>(other.scrapUsed);
        this.sectorStats = new EnumMap<>(other.sectorStats);
        this.sectorStatsData = new EnumMap<>(other.sectorStatsData);
    }

    public void add(Constants.ScrapOrigin origin, int delta){
        scrapGained.put(origin, scrapGained.get(origin)+delta);
    }

    public void add(Constants.ScrapUsedCategory category, int delta){
        scrapUsed.put(category, scrapUsed.get(category)+delta);
    }

//    public void add()

//    public void add(Constants.SectorStatsData sectorStats, Constants.ResourceOrigin resourceOrigin, int delta){
//        sectorStatsData.get(sectorStats).add(resourceOrigin, delta);
//    }

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
