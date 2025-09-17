package net.gausman.ftl.view.browser;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.xml.event.*;

import java.util.ArrayList;
import java.util.List;

public class EventTreeNodeData {
    DataManager dm = DataManager.get();
    private final String id;
    private final String headline;
    private final String textId;
    private final String text;
    private FTLEvent event;
    private TextList textList;
    private Choice choice;

    public EventTreeNodeData(String id, String headline, String textId, String text) {
        this.id = id;
        this.headline = headline;
        this.textId = textId;
        this.text = text;
    }

    public Choice getChoice() {
        return choice;
    }

    public void setChoice(Choice choice) {
        this.choice = choice;
    }

    public TextList getTextList() {
        return textList;
    }

    public void setTextList(TextList textList) {
        this.textList = textList;
    }

    public FTLEvent getEvent() {
        return event;
    }

    public void setEvent(FTLEvent event) {
        this.event = event;
    }

    public String getId() {
        return id;
    }

    public String getHeadline(){
        return headline;
    }

    public String getTextId() {
        return textId;
    }

    public String getText() {
        return text;
    }

    public String toHtml() {
        StringBuilder sb = new StringBuilder("<html><b>")
                .append(headline)
                .append("</b> ")
                .append(text);

        int i = 1;
        if (textList != null){
            sb.append("Textlist (game selects a random text):").append("<br>");
            for (FTLText text : textList.getText()){
                sb.append(i).append(": ").append(dm.getTextForId(text.getId())).append("<br>");
                i++;
            }
        }

        if (choice != null){
            addChoiceInfo(sb);
        }

        if (event != null){
            addEventInfo(sb);
        }

        sb.append("</html>");
        return sb.toString();
    }

    private void addChoiceInfo(StringBuilder sb){
        List<String> lines = new ArrayList<>();

        if (choice.isHidden()){
            lines.add("Hidden");
        }

        if (choice.getReq() != null){
            lines.add(String.format("Requirement: %s, Level: %s", choice.getReq(), choice.getLvl()));
        }

        if (!lines.isEmpty()){
            sb.append("<ul style='margin:0;padding-left:12px;'>");
            for (String line : lines){
                sb.append("<li style='margin:0;padding:0;'>").append(line).append("</li>");
            }
            sb.append("</ul>");
        }
    }

    private String buildCrewString(CrewMember crewMember){
        StringBuilder sb = new StringBuilder();

        sb.append("Free Crew - Amount: ").append(crewMember.getAmount()).append(" ");

        if (crewMember.getId() != null){
            sb.append("Id: ").append(crewMember.getId());
        }

        if (crewMember.getCrewClass() != null){
            sb.append("Class: ").append(crewMember.getCrewClass()).append(" ");
        }

        if (crewMember.getWeapons() > 0){
            sb.append("Weapons: ").append(crewMember.getWeapons()).append(" ");
        }

        if (crewMember.getShields() > 0){
            sb.append("Shields: ").append(crewMember.getShields()).append(" ");
        }

        if (crewMember.getPilot() > 0){
            sb.append("Pilot: ").append(crewMember.getPilot()).append(" ");
        }

        if (crewMember.getEngines() > 0){
            sb.append("Engines: ").append(crewMember.getEngines()).append(" ");
        }

        if (crewMember.getCombat() > 0){
            sb.append("Combat: ").append(crewMember.getCombat()).append(" ");
        }

        if (crewMember.getRepair() > 0){
            sb.append("Repair: ").append(crewMember.getRepair()).append(" ");
        }

        if (crewMember.getAllSkills() > 0){
            sb.append("All Skills: ").append(crewMember.getAllSkills()).append(" ");
        }

        if (crewMember.getProp() > 0){
            sb.append("Probability: ").append(crewMember.getProp());
        }

        return sb.toString();
    }

    private void addEventInfo(StringBuilder sb){
        List<String> lines = new ArrayList<>();


        if (event.isUnique()){
            lines.add("Unique Event - can only happen once");
        }

        if (event.getRevealMap() != null){
            lines.add("Map reveal");
        }

        if (event.getDistressBeacon() != null){
            lines.add("Distress Beacon");
        }

        if (event.getCrewMembers() != null){
            // I don't think this can be more that one crew but just in case
            for (CrewMember crewMember : event.getCrewMembers()){
                lines.add(buildCrewString(crewMember));
            }
        }

        if (event.getRemoveCrew() != null){
            lines.add(String.format("Crew loss, cloneable: %s", event.getRemoveCrew().isClone()));
        }

        if (event.getShip() != null){
            lines.add(String.format("Ship: %s, hostile: %s", event.getShip().getLoad(), event.getShip().isHostile()));
        }

        if (event.getItemModify() != null){
            for (Item item : event.getItemModify().getItems()){
                lines.add(String.format("Item Modify: %s, min: %s, max: %s", item.getType(), item.getMin(), item.getMax()));
            }
        }

        if (event.getDamages() != null){
            for (Damage damage : event.getDamages()){
                lines.add(String.format("Damage: %s, System: %s, Effect: %s", damage.getAmount(), damage.getSystem(), damage.getEffect()));
            }
        }

        if (event.getModifyPursuit() != null){
            lines.add(String.format("Modify Pursuit: %s", event.getModifyPursuit().getAmount()));
        }

        if (event.getEnvironment() != null){
            String lineEnvironment = String.format("Environment: %s", event.getEnvironment().getType());
            if (event.getEnvironment().getType().equals(Environment.Type.PDS) && event.getEnvironment().getTarget() != null){
                lineEnvironment += ", Target: " + event.getEnvironment().getTarget();
            }
            lines.add(lineEnvironment);
        }

        if (event.getAutoReward() != null){
            lines.add(String.format("Auto reward: Level %s, Type %s", event.getAutoReward().getLevel(), event.getAutoReward().getValue()));
        }

        if (event.getBoarders() != null){
            lines.add(String.format("Boarders - amount: %s-%s, class: %s", event.getBoarders().getMin(), event.getBoarders().getMax(), event.getBoarders().getClazz()));
        }

        if (event.getRemove() != null){
            lines.add(String.format("Remove item: %s", event.getRemove().getName()));
        }

        if (event.getWeapon() != null){
            lines.add(String.format("Weapon: %s", dm.getWeapon(event.getWeapon().getName()).getTitle().getTextValue()));
        }

        if (event.getDrone() != null){
            lines.add(String.format("Drone: %s", dm.getDrone(event.getDrone().getName()).getTitle().getTextValue()));
        }

        if (event.getAugment() != null){
            lines.add(String.format("Augment: %s", dm.getAugment(event.getAugment().getName()).getTitle().getTextValue()));
        }

        if (event.getSecretSector() != null){
            lines.add("Secret Sector");
        }

        if (event.getStore() != null){
            lines.add("Store");
        }

        if (event.getUpgrade() != null){
            lines.add(String.format("Upgrade - System: %s, amount: %s", event.getUpgrade().getSystem(), event.getUpgrade().getAmount()));
        }

        if (!lines.isEmpty()){
            sb.append("<ul style='margin:0;padding-left:12px;'>");
            for (String line : lines){
                sb.append("<li style='margin:0;padding:0;'>").append(line).append("</li>");
            }
            sb.append("</ul>");
        }
    }

    @Override
    public String toString() {
        return toHtml();  // Used as fallback
    }
}
