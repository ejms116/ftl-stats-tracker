package net.gausman.ftl.model.change;

import com.fasterxml.jackson.annotation.*;
import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.crew.*;
import net.gausman.ftl.model.change.effects.IntegerStatEffect;
import net.gausman.ftl.model.change.effects.StringStatEffect;
import net.gausman.ftl.model.change.item.AugmentEvent;
import net.gausman.ftl.model.change.item.DroneEvent;
import net.gausman.ftl.model.change.item.WeaponEvent;
import net.gausman.ftl.model.change.system.ReactorEvent;
import net.gausman.ftl.model.change.system.SystemEvent;
import net.gausman.ftl.model.record.Jump;

import java.time.Instant;
import java.util.*;


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
        @JsonSubTypes.Type(value = MasteryEvent.class, name = "MasteryEvent"),
        @JsonSubTypes.Type(value = CrewRenameEvent.class, name = "NameEvent"),
        @JsonSubTypes.Type(value = CrewNewEvent.class, name = "NewCrewEvent"),
        @JsonSubTypes.Type(value = ReactorEvent.class, name = "ReactorEvent"),
        @JsonSubTypes.Type(value = CrewSkillEvent.class, name = "SkillEvent"),
        @JsonSubTypes.Type(value = CrewStatEvent.class, name = "StatEvent"),
        @JsonSubTypes.Type(value = SystemEvent.class, name = "SystemEvent"),
        @JsonSubTypes.Type(value = WeaponEvent.class, name = "WeaponEvent"),
})
public class Event {
    private int id;
    private final Instant ts;
    private SavedGameParser.StoreItemType itemType; // Category
    private Constants.EventType eventType; // Type
    private Constants.EventDetailType eventDetailType;
//    private int amount;
    private int scrap;
    private String text;
    private String displayText;
    private Jump jump;
    private EnumMap<Constants.Resource, Integer> resourceEffects = new EnumMap<>(Constants.Resource.class);
    private List<StringStatEffect> stringStatEffects = new ArrayList<>();
    private List<IntegerStatEffect> integerStatEffects = new ArrayList<>();
    private EnumSet<Constants.EventTag> tags = EnumSet.noneOf(Constants.EventTag.class);


    public Event(){
        ts = Instant.now();
    }

//    public Event(SavedGameParser.StoreItemType itemType, Constants.EventType eventType, int amount, int scrap, String text, Jump jump){
    public Event(Jump jump){
        this.ts = Instant.now();
        this.jump = jump;
        this.displayText = text;
        this.stringStatEffects = new ArrayList<>();
        this.integerStatEffects = new ArrayList<>();
        this.resourceEffects = new EnumMap<>(Constants.Resource.class);
        this.tags = EnumSet.noneOf(Constants.EventTag.class);
        this.eventDetailType = Constants.EventDetailType.DEFAULT;
    }

    public Event(Constants.EventDetailType eventDetailType, Jump jump){
        this.ts = Instant.now();
        this.jump = jump;
        this.displayText = text;
        this.stringStatEffects = new ArrayList<>();
        this.integerStatEffects = new ArrayList<>();
        this.resourceEffects = new EnumMap<>(Constants.Resource.class);
        this.tags = EnumSet.noneOf(Constants.EventTag.class);
        this.eventDetailType = eventDetailType;
    }

    public String getDisplayTextWithEffects(){
        String withEffects = getDisplayText() + "special effects";
        return withEffects;
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

    public Constants.EventDetailType getEventFilterType() {
        return eventDetailType;
    }

    public int getScrap() {
        return scrap;
    }

    @JsonIgnore
    public int getScrapChange(){
        return 0;
//        int result = 0;
//        switch (eventType){
//            case REWARD -> result = 0;
//            case START -> {
//                if (this.itemType.equals(SavedGameParser.StoreItemType.GENERAL)){
//                    result = scrap;
//                } else {
//                    result = 0;
//                }
//            }
//            case GENERAL -> {
//                GeneralEvent ge = (GeneralEvent) this;
//                if (ge.getGeneral().equals(Constants.General.SCRAP_COLLECTED)){
//                    result = scrap;
//                }
//                if (ge.getGeneral().equals(Constants.General.SCRAP_DIFF)){
//                    result = scrap;
//                }
//            }
//            case BUY, UPGRADE -> result = -scrap;
//            case SELL -> result = scrap/2;
//            default -> result = 0;
//        };
//        return result;

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

    public EnumMap<Constants.Resource, Integer> getResourceEffects() {
        return resourceEffects;
    }

    public void setResourceEffect(Constants.Resource resource, Integer value) {
        this.resourceEffects.put(resource, value);
    }

    public List<StringStatEffect> getStringStatEffects() {
        return stringStatEffects;
    }

    public void addStringStatEffects(StringStatEffect stringStatEffect) {
        this.stringStatEffects.add(stringStatEffect);
    }

    public List<IntegerStatEffect> getIntegerStatEffects() {
        return integerStatEffects;
    }

    public void addIntegerStatEffects(IntegerStatEffect integerStatEffect) {
        this.integerStatEffects.add(integerStatEffect);
    }

    public EnumSet<Constants.EventTag> getTags() {
        return tags;
    }

    public void addTag(Constants.EventTag tag) {
        tags.add(tag);
    }

    public void applyEventToShipStatusModel(ShipStatusModel model, boolean apply){
        int mult = apply ? 1 : -1;
        applyValueEffects(model.getResources(), mult);
        applyIntegerStatEffects(model.getGeneralInfoInteger(), mult);
        applyStringStatEffects(model.getGeneralInfoString());

        // if tag contains BUY


    }

    private void applyValueEffects(Map<Constants.Resource, Integer> resources, int mult){
        for (Map.Entry<Constants.Resource, Integer> effect : this.resourceEffects.entrySet()){
            resources.put(effect.getKey(), resources.getOrDefault(effect.getKey(), 0) + effect.getValue()*mult);
        }
    }

    private void applyIntegerStatEffects(Map<Constants.General, Integer> generalInfoInteger, int mult){
        for (IntegerStatEffect effect : this.getIntegerStatEffects()){
            generalInfoInteger.put(effect.getGeneral(), generalInfoInteger.getOrDefault(effect.getGeneral(), 0) + effect.getValue()*mult);
        }
    }

    private void applyStringStatEffects(Map<Constants.General, String> generalInfoString){
        for (StringStatEffect effect : this.getStringStatEffects()){
            generalInfoString.put(effect.getGeneral(), effect.getValue());
        }
    }

}
