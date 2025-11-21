package net.gausman.ftl.model;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.record.Sector;
import net.gausman.ftl.model.record.StoreInfo;
import net.gausman.ftl.util.GausmanUtil;
import org.jfree.data.flow.DefaultFlowDataset;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class SectorMetrics {
    private final Map<Sector, SectorInfo> data = new LinkedHashMap<>();
    private DefaultFlowDataset<String> flowDataset = new DefaultFlowDataset<>();

    public SectorMetrics(){
        flowDataset.setFlow(0,  Constants.Sankey.ITEMS_START.toString(), Constants.Sankey.INVENTORY.toString(), 0.0);
        flowDataset.setFlow(0,  Constants.Sankey.ITEMS_FREE.toString(), Constants.Sankey.INVENTORY.toString(), 0.0);
        flowDataset.setFlow(1,  Constants.Sankey.INVENTORY.toString(), Constants.Sankey.INVENTORY.toString(), 0.0);
        flowDataset.setFlow(2,  Constants.Sankey.INVENTORY.toString(), Constants.Sankey.INVENTORY.toString(), 0.0);
        flowDataset.setFlow(3,  Constants.Sankey.INVENTORY.toString(), Constants.Sankey.SHIP_VALUE.toString(), 0.0);
        flowDataset.setFlow(0,  Constants.Sankey.ITEMS_START.toString(), Constants.Sankey.WASTED.toString(), 0.0);
        flowDataset.setFlow(0,  Constants.Sankey.ITEMS_START.toString(), Constants.Sankey.ITEMS_SOLD.toString(), 0.0);
        flowDataset.setFlow(1, Constants.Sankey.ITEMS_SOLD.toString(), Constants.Sankey.SCRAP_AVAILABLE.toString(), 0.0);
//        flowDataset.setFlow(1, Constants.Sankey.ITEMS_SOLD.toString(), Constants.Sankey.INVENTORY.toString(), 0.0);
        flowDataset.setFlow(1, Constants.Sankey.SCRAP_COLLECTED.toString(), Constants.Sankey.SCRAP_AVAILABLE.toString(), 0.0);
        flowDataset.setFlow(2, Constants.Sankey.SCRAP_AVAILABLE.toString(), Constants.ScrapUsedCategory.REACTOR.toString(), 0.0);

    }

    public SectorMetrics(SectorMetrics other){
        for (Map.Entry<Sector, SectorInfo> entry : other.data.entrySet()) {
            this.data.put(entry.getKey(), new SectorInfo(entry.getValue()));
        }
        this.flowDataset = GausmanUtil.copyFlowDataset(other.getFlowDataset());
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

    public DefaultFlowDataset<String> getFlowDataset() {
        return flowDataset;
    }

    public void updateFlows(int stage, String source, String destination, double flow){
        double currentFlow = Optional.ofNullable(flowDataset.getFlow(stage, source, destination))
                .map(Number::doubleValue)
                .orElse(0.0);
        flowDataset.setFlow(stage, source, destination, currentFlow + flow);
    }

    public void addFlowMultipleStages(int sourceStage, int targetStage, String source, String temporary, String destination, double flow){
        String sourceTemp;
        String destinationTemp;
        for (int stage = sourceStage; stage <= targetStage; stage++){
            if (stage != sourceStage){
                sourceTemp = temporary;
            } else {
                sourceTemp = source;
            }
            if (stage == targetStage){
                destinationTemp = destination;
            } else {
                destinationTemp = temporary;
            }
            updateFlows(
                    stage,
                    sourceTemp,
                    destinationTemp,
                    flow
            );
        }
    }
}


