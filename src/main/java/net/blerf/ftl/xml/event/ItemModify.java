package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.*;

import java.util.List;

@XmlRootElement(name = "item_modify")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemModify extends AbstractBuildableTreeNode {
    @XmlElement(name = "item")
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
