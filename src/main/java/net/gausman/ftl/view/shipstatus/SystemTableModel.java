package net.gausman.ftl.view.shipstatus;

import net.gausman.ftl.model.ShipSystem;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SystemTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Name", "Type", "Level"};
    private List<ShipSystem> shipSystems = new ArrayList<>();

    public void setSystems(List<ShipSystem> shipSystems) {
        this.shipSystems = shipSystems;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return shipSystems.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ShipSystem shipSystem = shipSystems.get(rowIndex);

        return switch (columnIndex){
            case 0 -> shipSystem.getDisplayText();
            case 1 -> shipSystem.getDisplayType();
            case 2 -> shipSystem.getLevel();
            default -> null;
        };
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}
