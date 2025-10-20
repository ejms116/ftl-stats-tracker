package net.gausman.ftl.model.change;

import com.fasterxml.jackson.annotation.*;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.crew.*;
import net.gausman.ftl.model.change.effects.IntegerStatEffect;
import net.gausman.ftl.model.change.effects.StringStatEffect;
import net.gausman.ftl.model.change.general.*;
import net.gausman.ftl.model.change.item.AugmentEvent;
import net.gausman.ftl.model.change.item.DroneEvent;
import net.gausman.ftl.model.change.item.WeaponEvent;
import net.gausman.ftl.model.change.other.*;
import net.gausman.ftl.model.change.resources.*;
import net.gausman.ftl.model.change.system.ReactorEvent;
import net.gausman.ftl.model.change.system.SystemEvent;
import net.gausman.ftl.model.record.Jump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;


@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Event.class)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        // Crew
        @JsonSubTypes.Type(value = CrewEvent.class, name = "CrewEvent"),
        @JsonSubTypes.Type(value = CrewLostEvent.class, name = "CrewLostEvent"),
        @JsonSubTypes.Type(value = CrewMasteryEvent.class, name = "CrewMasteryEvent"),
        @JsonSubTypes.Type(value = CrewNewEvent.class, name = "CrewNewEvent"),
        @JsonSubTypes.Type(value = CrewRenameEvent.class, name = "CrewRenameEvent"),
        @JsonSubTypes.Type(value = CrewSkillEvent.class, name = "CrewSkillEvent"),
        @JsonSubTypes.Type(value = CrewStatEvent.class, name = "CrewStatEvent"),

        // General
        @JsonSubTypes.Type(value = BeaconsExploredEvent.class, name = "BeaconsExploredEvent"),
        @JsonSubTypes.Type(value = CrewHiredEvent.class, name = "CrewHiredEvent"),
        @JsonSubTypes.Type(value = ScrapCollectedEvent.class, name = "ScrapCollectedEvent"),
        @JsonSubTypes.Type(value = ShipsDestroyedEvent.class, name = "ShipsDestroyedEvent"),
        @JsonSubTypes.Type(value = SRAExtraScrapEvent.class, name = "SRAExtraScrapEvent"),

        // Other
        @JsonSubTypes.Type(value = DamageEvent.class, name = "DamageEvent"),
        @JsonSubTypes.Type(value = RepairEvent.class, name = "RepairEvent"),
        @JsonSubTypes.Type(value = ResourceDiffErrorEvent.class, name = "ResourceDiffErrorEvent"),
        @JsonSubTypes.Type(value = ShipSetupEvent.class, name = "ShipSetupEvent"),
        @JsonSubTypes.Type(value = StoreFoundEvent.class, name = "StoreFoundEvent"),
        @JsonSubTypes.Type(value = StoreVisitedEvent.class, name = "StoreVisitedEvent"),

        // Item
//        @JsonSubTypes.Type(value = ItemEvent.class, name = "ItemEvent"),
        @JsonSubTypes.Type(value = AugmentEvent.class, name = "AugmentEvent"),
        @JsonSubTypes.Type(value = DroneEvent.class, name = "DroneEvent"),
        @JsonSubTypes.Type(value = WeaponEvent.class, name = "WeaponEvent"),

        // Resources
        @JsonSubTypes.Type(value = DronesBoughtEvent.class, name = "DronesBoughtEvent"),
        @JsonSubTypes.Type(value = DronesUsedEvent.class, name = "DronesUsedEvent"),
        @JsonSubTypes.Type(value = FuelBoughtEvent.class, name = "FuelBoughtEvent"),
        @JsonSubTypes.Type(value = FuelUsedEvent.class, name = "FuelUsedEvent"),
        @JsonSubTypes.Type(value = HackingUsedEvent.class, name = "HackingUsedEvent"),
        @JsonSubTypes.Type(value = MissilesBoughtEvent.class, name = "MissilesBoughtEvent"),
        @JsonSubTypes.Type(value = MissilesUsedEvent.class, name = "MissilesUsedEvent"),
        @JsonSubTypes.Type(value = EventRewardEvent.class, name = "ResourcesReceivedEvent"),

        // System
        @JsonSubTypes.Type(value = SystemEvent.class, name = "SystemEvent"),
        @JsonSubTypes.Type(value = ReactorEvent.class, name = "ReactorEvent"),

})
public class Event {
    @JsonIgnore
    protected static final Logger log = LoggerFactory.getLogger(Event.class);
    private int id;
    private final Instant ts;
    private Constants.EventDetailType eventDetailType;
    private String displayText;
    private Jump jump;
    private EnumMap<Constants.Resource, Integer> resourceEffects = new EnumMap<>(Constants.Resource.class);
    private List<StringStatEffect> stringStatEffects = new ArrayList<>();
    private List<IntegerStatEffect> integerStatEffects = new ArrayList<>();
    private EnumSet<Constants.EventTag> tags = EnumSet.noneOf(Constants.EventTag.class);

    public Event(){
        ts = Instant.now();
    }

    public Event(Jump jump){
        this.ts = Instant.now();
        this.jump = jump;
        this.displayText = "";
        this.stringStatEffects = new ArrayList<>();
        this.integerStatEffects = new ArrayList<>();
        this.resourceEffects = new EnumMap<>(Constants.Resource.class);
        this.tags = EnumSet.noneOf(Constants.EventTag.class);
        this.eventDetailType = Constants.EventDetailType.SHIP_SETUP;
    }

    public Event(Constants.EventDetailType eventDetailType, Jump jump){
        this.ts = Instant.now();
        this.jump = jump;
        this.displayText = "";
        this.stringStatEffects = new ArrayList<>();
        this.integerStatEffects = new ArrayList<>();
        this.resourceEffects = new EnumMap<>(Constants.Resource.class);
        this.tags = EnumSet.noneOf(Constants.EventTag.class);
        this.eventDetailType = eventDetailType;
    }

    public String getDisplayText(){
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


    public Constants.EventDetailType getEventDetailType() {
        return eventDetailType;
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
