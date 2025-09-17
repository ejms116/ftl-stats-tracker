package net.gausman.ftl.view.shipstatus;

import net.gausman.ftl.model.Crew;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CrewTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Name", "Type", "Origin", "Masteries", "State"};
    private List<Crew> crewList = new ArrayList<>();

    public CrewTableModel(){
    }

    public void setCrewList(List<Crew> crewList) {
        this.crewList = crewList;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return crewList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Crew crew = crewList.get(rowIndex);

        return switch (columnIndex){
            case 0 -> crew.getName();
            case 1 -> crew.getCrewType().toString();
            case 2 -> crew.getOrigin();
            case 3 -> getMasteriesText(crew);
            case 4 -> crew.getState().toString().charAt(0);

            default -> null;
        };
    }

    public Crew getRowObject(int rowIndex) {
        return crewList.get(rowIndex);
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public String getStringForTesting(Crew crew){
        List<Integer> res = new ArrayList<>();
        res.add(crew.getRepairs());
        res.add(crew.getCombatKills());
        res.add(crew.getPilotedEvasions());
        res.add(crew.getJumpsSurvived());
        res.add(crew.getSkillMasteriesEarned());

        String result = res.stream()
                .map(String::valueOf)  // Convert each Integer to String
                .collect(Collectors.joining("-"));

        return result;
    }

    private String getMasteriesText(Crew crew){
        List<String> masteries = new ArrayList<>();

        if (crew.isPilotMasteryTwo()){
            masteries.add("P2");
        } else if (crew.isPilotMasteryOne()){
            masteries.add("P1");
        }

        if (crew.isEngineMasteryTwo()){
            masteries.add("E2");
        } else if (crew.isEngineMasteryOne()){
            masteries.add("E1");
        }

        if (crew.isShieldMasteryTwo()){
            masteries.add("S2");
        } else if (crew.isShieldMasteryOne()){
            masteries.add("S1");
        }

        if (crew.isWeaponMasteryTwo()){
            masteries.add("W2");
        } else if (crew.isWeaponMasteryOne()){
            masteries.add("W1");
        }

        if (crew.isRepairMasteryTwo()){
            masteries.add("R2");
        } else if (crew.isRepairMasteryOne()){
            masteries.add("R1");
        }

        if (crew.isCombatMasteryTwo()){
            masteries.add("C2");
        } else if (crew.isCombatMasteryOne()){
            masteries.add("C1");
        }

        return String.join("-", masteries);
    }
}
