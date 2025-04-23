package net.gausman.ftl.view.table;

import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.table.EventTableModel;

import net.gausman.ftl.model.table.EventFilter;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Map;

public class EventTablePanel extends JPanel {

    public static final int PREFERRED_WIDTH_3_DIGITS = 10;

    TableRowSorter<EventTableModel> sorter;
    JTable table;

    public EventTablePanel(EventTableModel model) {
        setLayout(new BorderLayout());


        table = new GroupRowColorJTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getColumnModel().getColumn(1).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
        table.getColumnModel().getColumn(2).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
        table.getColumnModel().getColumn(3).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
        table.getColumnModel().getColumn(4).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);

        table.getColumnModel().getColumn(7).setPreferredWidth(400);

        table.getColumnModel().getColumn(8).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
        table.getColumnModel().getColumn(9).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
        table.getColumnModel().getColumn(10).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Disable clicking on header rows and sorting columns like that
        for (int i = 0; i < model.getColumnCount(); i++) {
            sorter.setSortable(i, false);
        }

    }

    // This method will update the row filter based on the filter states.
    public void updateRowFilter(Map<EventFilter, Boolean> filterStates) {
        RowFilter<EventTableModel, Integer> filter = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends EventTableModel, ? extends Integer> entry) {
                Constants.EventType type = (Constants.EventType) entry.getValue(5); // Event Type
                String id = (String) entry.getValue(7);  // Assuming 'id' is in column 9

                // Custom filtering logic based on filterStates
                boolean showRow = true;

                if (filterStates.get(EventFilter.HIDE_FUEL_USED_EVENTS)) {
                    if (type.equals(Constants.EventType.USE) && id.equals(Constants.Resource.FUEL.toString())) {
                        showRow = false;
                    }
                }

                if (filterStates.get(EventFilter.HIDE_START_EVENTS)){
                    if (type.equals(Constants.EventType.START)) {
                        showRow = false;
                    }
                }

                return showRow;
            }
        };

        sorter.setRowFilter(filter);

    }

    public JTable getTable(){
        return table;
    }

}
