package net.gausman.ftl.view.shipstatus;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.record.StoreInfo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoreTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Sector", "Visited", "Fuel", "Missiles", "Drones", "Repair", "Shelf1", "Shelf2", "Shelf3", "Shelf4", "Scrap"};

    private List<StoreInfo> storeInfoList = new ArrayList<>();

    public void setStoreInfoList(List<StoreInfo> storeInfoList){
        this.storeInfoList = storeInfoList;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return storeInfoList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        StoreInfo storeInfo = storeInfoList.get(rowIndex);

        return switch (columnIndex){
            case 0 -> String.format("%s", storeInfo.getSector());
            case 1 -> Arrays.toString(storeInfo.getVisitedOnJumps().toArray());
            case 2 -> paintResources(storeInfo.getInitialFuel()-storeInfo.getStore().getFuel(), storeInfo.getInitialFuel());
            case 3 -> paintResources(storeInfo.getInitialMissiles()-storeInfo.getStore().getMissiles(), storeInfo.getInitialMissiles());
            case 4 -> paintResources(storeInfo.getInitialDroneParts()-storeInfo.getStore().getDroneParts(), storeInfo.getInitialDroneParts());
            case 5 -> paintRepairCount(storeInfo);
            case 6 -> getItemsAsString(storeInfo.getStore().getShelfList(), 0);
            case 7 -> getItemsAsString(storeInfo.getStore().getShelfList(), 1);
            case 8 -> getItemsAsString(storeInfo.getStore().getShelfList(), 2);
            case 9 -> getItemsAsString(storeInfo.getStore().getShelfList(), 3);
            case 10 -> String.format("%s", storeInfo.getScrapAvailable());
            default -> null;
        };
    }

    private String paintResources(int bought, int total){
        if (bought == 0){
            return String.format("%s/%s", bought, total);
        }
        return String.format("<html><span style=\"color: rgb(255, 215, 0);\">%s</span>/%s</html>", bought, total);
    }

    private String paintRepairCount(StoreInfo storeInfo){
        if (storeInfo.getRepairCount() == 0){
            return "";
        }
        return String.format("<html><span style=\"color: rgb(255, 215, 0);\">%s</span></html>", storeInfo.getRepairCount());
    }

    private SavedGameParser.StoreShelf getItemsAsString(List<SavedGameParser.StoreShelf> shelfList, int index){
        if (shelfList.size() <= index){
            return null;
        }
        return shelfList.get(index);
    }


    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}
