package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "textList")
@XmlAccessorType(XmlAccessType.FIELD)
public class TextList extends AbstractBuildableTreeNode {
    @XmlAttribute(name = "name")
    private String name;

    private List<FTLText> text;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FTLText> getText() {
        return text;
    }

    public void setText(List<FTLText> text) {
        this.text = text;
    }
}
