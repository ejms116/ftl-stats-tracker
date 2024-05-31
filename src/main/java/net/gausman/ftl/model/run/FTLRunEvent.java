package net.gausman.ftl.model.run;

import net.gausman.ftl.model.Constants;

import java.time.Instant;

public class FTLRunEvent {
    private Instant ts;
    private Constants.EventCategory category;
    private Constants.EventType type;
    private int amount;
    private String id;

    public Instant getTs() {
        return ts;
    }

    public Constants.EventCategory getCategory() {
        return category;
    }

    public void setCategory(Constants.EventCategory category) {
        this.category = category;
    }

    public Constants.EventType getType() {
        return type;
    }

    public void setType(Constants.EventType type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FTLRunEvent(){
        ts = Instant.now();
    }


}
