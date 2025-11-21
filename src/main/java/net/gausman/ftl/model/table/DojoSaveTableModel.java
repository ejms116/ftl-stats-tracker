package net.gausman.ftl.model.table;

import net.gausman.ftl.model.SaveMetadata;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class DojoSaveTableModel extends AbstractTableModel {

    private List<SaveMetadata> data;

    private final String[] columns = {
            "Filename",
            "Ship",
            "Sector",
            "Difficulty",
            "Description",
    };

    public DojoSaveTableModel(List<SaveMetadata> data) {
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public boolean isCellEditable(int row, int col){
        if (col == 3 || col == 4){
            return true;
        }
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 2, 3 -> Integer.class; // your integer columns
            default -> String.class;
        };
    }


    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        SaveMetadata save = data.get(rowIndex);

        switch (columnIndex) {
            case 0: save.setFilename((String)aValue); break;
            case 1: save.setShip((String)aValue); break;
            case 2: save.setSector((Integer)aValue); break;
            case 3: save.setDifficulty(Integer.valueOf(aValue.toString())); break;
            case 4: save.setDescription((String)aValue); break;
            default: break;
        }

        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public Object getValueAt(int row, int col) {
        SaveMetadata s = data.get(row);
        return switch (col) {
            case 0 -> s.getFilename();
            case 1 -> s.getShip() == null ? "" : s.getShip();
            case 2 -> s.getSector() == null ? "" : s.getSector();
            case 3 -> s.getDifficulty() == null ? "" : s.getDifficulty();
            case 4 -> s.getDescription() == null ? "" : s.getDescription();
            default -> "";
        };
    }

    /**
     * Call this after updating metadata (e.g., after saving edits)
     */
    public void updateData(List<SaveMetadata> newData) {
        this.data = newData;
        fireTableDataChanged();
    }

    /**
     * Utility: retrieve SaveMetadata for selected row
     */
    public SaveMetadata getSaveAt(int row) {
        return data.get(row);
    }
}
