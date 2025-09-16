package net.gausman.ftl.model;

import java.util.EnumMap;

public class ResourceOriginIntegerMap {
    private final EnumMap<Constants.ResourceOrigin, Integer> data;

    public ResourceOriginIntegerMap(){
        data = new EnumMap<>(Constants.ResourceOrigin.class);
        for (Constants.ResourceOrigin origin : Constants.ResourceOrigin.values()){
            data.put(origin, 0);
        }
    }
    public void add(Constants.ResourceOrigin origin, int delta){
        data.put(origin, data.get(origin)+delta);
    }

    public EnumMap<Constants.ResourceOrigin, Integer> getData() {
        return data;
    }

    @Override
    public String toString(){
        return String.format("%s - %s - %s",
                data.get(Constants.ResourceOrigin.NORMAL),
                data.get(Constants.ResourceOrigin.EVENT),
                data.get(Constants.ResourceOrigin.STORE)
        );
    }
}
