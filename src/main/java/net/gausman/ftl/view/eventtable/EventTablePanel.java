package net.gausman.ftl.view.eventtable;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.SimpleTableItem;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.model.table.EventTableModel;

import net.gausman.ftl.model.table.EventFilter;
import net.gausman.ftl.model.table.JumpTableModel;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventTablePanel extends JSplitPane {

    public static final int PREFERRED_WIDTH_3_DIGITS = 10;

    Jump jump;
    JumpTableModel jumpTableModel;
    JTable encounterInfoTable;

    TableRowSorter<EventTableModel> sorter;
    JTable table;


    public EventTablePanel(EventTableModel model) {
        super(JSplitPane.VERTICAL_SPLIT);
        setResizeWeight(0.15);
        setEnabled(false);
        setDividerSize(5);

        // top Panel
        JPanel jumpInfoPanel = new JPanel(new BorderLayout());
        jumpTableModel = new JumpTableModel();
        encounterInfoTable = new JTable(jumpTableModel);
        encounterInfoTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPaneJump = new JScrollPane(encounterInfoTable);


        encounterInfoTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        encounterInfoTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        scrollPaneJump.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        jumpInfoPanel.add(scrollPaneJump, BorderLayout.CENTER);
        setTopComponent(jumpInfoPanel);

        // bottom Panel
        table = new GroupRowColorJTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(table);
        setBottomComponent(scrollPane);


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

        SwingUtilities.invokeLater(() -> setDividerLocation(0.15));

    }

    public void updateJumpInfoPanel(Jump jump){
        if (!(jump.equals(this.jump))){
            this.jump = jump;
            List<SimpleTableItem> items = new ArrayList<>();
            String newText = "";
            String lastText = "";
            for (SavedGameParser.EncounterState state : jump.getEncounterStates()){
//                newText = GausmanUtil.extractId(state.getText()) != null ? GausmanUtil.extractId(state.getText()) : state.getText();
                newText = state.getText();
                assert newText != null;
//                if (newText.isEmpty()){
//                    newText = lastText;
//                }
                for (Integer choice : state.getChoiceList()){
                    SimpleTableItem item = new SimpleTableItem(newText, choice.toString());
                    items.add(item);
                }
                if (state.getChoiceList().isEmpty()){
                    SimpleTableItem item = new SimpleTableItem(newText, "");
                    items.add(item);
                }
                lastText = newText;
            }

            this.jumpTableModel.setList(items);
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
