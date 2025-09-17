package net.gausman.ftl.model.change;

import com.fasterxml.jackson.annotation.*;
import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.change.crew.*;
import net.gausman.ftl.model.change.item.AugmentEvent;
import net.gausman.ftl.model.change.item.DroneEvent;
import net.gausman.ftl.model.change.item.WeaponEvent;
import net.gausman.ftl.model.record.Jump;

import java.time.Instant;


@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Event.class)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AugmentEvent.class, name = "AugmentEvent"),
        @JsonSubTypes.Type(value = CrewEvent.class, name = "CrewEvent"),
        @JsonSubTypes.Type(value = DroneEvent.class, name = "DroneEvent"),
        @JsonSubTypes.Type(value = GeneralEvent.class, name = "GeneralEvent"),
        @JsonSubTypes.Type(value = MasteryEvent.class, name = "MasteryEvent"),
        @JsonSubTypes.Type(value = CrewRenameEvent.class, name = "NameEvent"),
        @JsonSubTypes.Type(value = NewCrewEvent.class, name = "NewCrewEvent"),
        @JsonSubTypes.Type(value = ReactorEvent.class, name = "ReactorEvent"),
        @JsonSubTypes.Type(value = ResourceEvent.class, name = "ResourceEvent"),
        @JsonSubTypes.Type(value = SkillEvent.class, name = "SkillEvent"),
        @JsonSubTypes.Type(value = StatEvent.class, name = "StatEvent"),
        @JsonSubTypes.Type(value = SystemEvent.class, name = "SystemEvent"),
        @JsonSubTypes.Type(value = WeaponEvent.class, name = "WeaponEvent"),
})
public class Event {
    private int id;
    private final Instant ts;
    private SavedGameParser.StoreItemType itemType; // Category
    private Constants.EventType eventType; // Type
    private int amount;
    private int scrap;
    private String text;
    private String displayText;
    private Jump jump;

    public Event(){
        ts = Instant.now();
    }

    public Event(SavedGameParser.StoreItemType itemType, Constants.EventType eventType, int amount, int scrap, String text, Jump jump){
        this.ts = Instant.now();
        this.itemType = itemType;
        this.eventType = eventType;
        this.amount = amount;
        this.scrap = scrap;
        this.text = text;
        this.jump = jump;
        this.displayText = text;
    }

    public String getDisplayText(){
//        return GausmanUtil.getTextToId(itemType, text);
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public void assignId(int id) {
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public Instant getTs() {
        return ts;
    }

    public SavedGameParser.StoreItemType getItemType() {
        return itemType;
    }

    public Constants.EventType getEventType() {
        return eventType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getScrap() {
        return scrap;
    }

    @JsonIgnore
    public int getScrapChange(){
        int result = 0;
        switch (eventType){
            case REWARD -> result = 0;
            case START -> {
                if (this.itemType.equals(SavedGameParser.StoreItemType.GENERAL)){
                    result = scrap;
                } else {
                    result = 0;
                }
            }
            case GENERAL -> {
                GeneralEvent ge = (GeneralEvent) this;
                if (ge.getGeneral().equals(Constants.General.SCRAP_COLLECTED)){
                    result = scrap;
                }
                if (ge.getGeneral().equals(Constants.General.SCRAP_DIFF)){
                    result = scrap;
                }
            }
            case BUY, UPGRADE -> result = -scrap;
            case SELL -> result = scrap/2;
            default -> result = 0;
        };
        return result;

    }


    public void setScrap(int scrap) {
        this.scrap = scrap;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Jump getJump(){
        return jump;
    }

}
