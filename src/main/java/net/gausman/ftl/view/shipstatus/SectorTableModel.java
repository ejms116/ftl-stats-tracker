package net.gausman.ftl.view.shipstatus;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.ResourceOriginIntegerMap;
import net.gausman.ftl.model.SectorInfo;
import net.gausman.ftl.model.SectorMetrics;
import net.gausman.ftl.model.record.Sector;

import javax.swing.table.AbstractTableModel;
import java.util.*;

public class SectorTableModel extends AbstractTableModel {
    private final List<String> columnNames;

    private SectorMetrics sectorMetrics;


    public SectorTableModel(){
        columnNames = new ArrayList<>();
        columnNames.add("#");
        columnNames.add("Name");

//        for (Constants.SectorStatsData statsData : Constants.SectorStatsData.values()){
//            columnNames.add(statsData.toString());
//        }
    }

    public SectorMetrics getSectorMetrics() {
        return sectorMetrics;
    }

    public void setSectorMetrics(SectorMetrics sectorMetrics) {
        this.sectorMetrics = sectorMetrics;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return Optional.ofNullable(sectorMetrics)
                .map(SectorMetrics::getData)
                .map(Map::size)
                .orElse(0);
    }


    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // Convert the map to a list view for indexing
        Map.Entry<Sector, SectorInfo> map =
                sectorMetrics.getData().entrySet().stream().toList().get(rowIndex);

        Sector sector = map.getKey();

        EnumMap<Constants.SectorStatsData, ResourceOriginIntegerMap> sectorStatsData = map.getValue().getSectorStatsData();

        return switch (columnIndex) {
            case 0 -> sector.getId();
            case 1 -> sector.getSectorDot().getTitle();
//            case 2 -> sectorStatsData.get(Constants.SectorStatsData.DAMAGE);
//            case 3 -> sectorStatsData.get(Constants.SectorStatsData.REPAIR);
//            case 4 -> sectorStatsData.get(Constants.SectorStatsData.FUEL);
//            case 5 -> sectorStatsData.get(Constants.SectorStatsData.MISSILES);
//            case 6 -> sectorStatsData.get(Constants.SectorStatsData.DRONE_PARTS);
            default -> null;
        };
    }

    @Override
    public String getColumnName(int column) {
        return columnNames.get(column);
    }
}
