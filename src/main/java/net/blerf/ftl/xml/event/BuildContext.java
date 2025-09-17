package net.blerf.ftl.xml.event;

import net.blerf.ftl.parser.DataManager;

import java.util.Map;

public class BuildContext {
    private boolean showText = true;
    private Map<String, FTLEventNode> allEvents;
    private Map<String, TextList> textListMap;
    private Map<String, ShipEvent> shipEventMap;

    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    public Map<String, TextList> getTextListMap() {
        return textListMap;
    }

    public void setTextListMap(Map<String, TextList> textListMap) {
        this.textListMap = textListMap;
    }

    public Map<String, ShipEvent> getShipEventMap() {
        return shipEventMap;
    }

    public void setShipEventMap(Map<String, ShipEvent> shipEventMap) {
        this.shipEventMap = shipEventMap;
    }

    public Map<String, FTLEventNode> getAllEvents() {
        return allEvents;
    }

    public void setAllEvents(Map<String, FTLEventNode> allEvents) {
        this.allEvents = allEvents;
    }

    public String getTextForId(DataManager dataManager, String id){
        if (showText){
            return dataManager.getTextForId(id);
        }
        return id;
    }
}
