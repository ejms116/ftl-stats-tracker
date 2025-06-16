package net.blerf.ftl.xml.event;

import jakarta.xml.bind.annotation.*;
import net.blerf.ftl.parser.DataManager;

import java.util.List;

@XmlRootElement(name = "gotaway")
@XmlAccessorType(XmlAccessType.FIELD)
public class Gotaway extends AbstractBuildableTreeNode {
    @XmlElement(name = "text")
    private FTLText text;

    @XmlElement(name = "choice")
    List<Choice> choices;

    @XmlElement(name = "modifyPursuit")
    private ModifyPursuit modifyPursuit;

    @XmlElement(name = "autoReward")
    private AutoReward autoReward;

    @XmlElement(name = "quest")
    private Quest quest;

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    public AutoReward getAutoReward() {
        return autoReward;
    }

    public void setAutoReward(AutoReward autoReward) {
        this.autoReward = autoReward;
    }

    public ModifyPursuit getModifyPursuit() {
        return modifyPursuit;
    }

    public void setModifyPursuit(ModifyPursuit modifyPursuit) {
        this.modifyPursuit = modifyPursuit;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public FTLText getText() {
        return text;
    }

    public void setText(FTLText text) {
        this.text = text;
    }

    @Override
    public String getDisplayText(DataManager dataManager, BuildContext context){
        StringBuilder sb = new StringBuilder("<html><b>")
                .append("Gotaway")
                .append("</b> ")
                .append(text != null ? context.getTextForId(dataManager, text.getId()) : "")
                .append("</html>");
        return sb.toString();
    }
}
