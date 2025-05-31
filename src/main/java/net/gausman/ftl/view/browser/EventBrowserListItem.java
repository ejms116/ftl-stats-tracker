package net.gausman.ftl.view.browser;

public class EventBrowserListItem {
    public enum Type{
        EVENT("Event"),
        EVENT_LIST("List"),
        ;

        private final String displayName;

        Type(String displayName){
            this.displayName = displayName;
        }

        public String toString(){
            return displayName;
        }
    }

    private String id;
    private Type type;

    public EventBrowserListItem(String id, Type type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }
}
