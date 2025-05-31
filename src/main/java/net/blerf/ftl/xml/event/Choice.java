package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.*;
import net.blerf.ftl.parser.DataManager;

@XmlRootElement(name = "choice")
@XmlAccessorType(XmlAccessType.FIELD)
public class Choice extends AbstractBuildableTreeNode {
    @XmlAttribute(name = "hidden")
    private boolean hidden;

    @XmlAttribute(name = "req")
    private String req;

    @XmlAttribute(name = "lvl")
    private int lvl;

    @XmlAttribute(name = "max_group") // seems to be unused?
    private int maxGroup;

    @XmlElement(name = "text")
    private FTLText text;

    @XmlElement(name = "event")
    private FTLEvent event;

    public int getMaxGroup() {
        return maxGroup;
    }

    public void setMaxGroup(int maxGroup) {
        this.maxGroup = maxGroup;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getReq() {
        return req;
    }

    public void setReq(String req) {
        this.req = req;
    }

    public FTLText getText() {
        return text;
    }

    public void setText(FTLText text) {
        this.text = text;
    }

    public FTLEvent getEvent() {
        return event;
    }

    public void setEvent(FTLEvent event) {
        this.event = event;
    }

    @Override
    public String getDisplayText(DataManager dataManager, BuildContext context){
        StringBuilder sb = new StringBuilder("<html><b>")
                .append("Choice ")
                .append("</b> ")
                .append(text != null ? context.getTextForId(dataManager, text.getId()) : "")
                .append("</html>");
        return sb.toString();
    }

    // Todo display requirements!
}
