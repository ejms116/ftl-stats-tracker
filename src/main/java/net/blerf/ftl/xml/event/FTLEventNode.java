package net.blerf.ftl.xml.event;

import java.util.Map;

public interface FTLEventNode extends BuildableTreeNode {
    String getId();

    default FTLEventNode resolve(Map<String, FTLEventNode> allEventNodes) {
        return this;
    }
}
