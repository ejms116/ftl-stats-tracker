package net.gausman.ftl.view.eventtable;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.SimpleTableItem;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.model.table.EventTableModel;

import net.gausman.ftl.model.table.EventFilter;
import net.gausman.ftl.model.table.JumpTableModel;
import net.gausman.ftl.view.renderer.TagsRenderer;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class EventTablePanel extends JSplitPane {
    private final JButton openEventInBrowserButton = new JButton("Open in Event Browser");
    private final EnumSet<SavedGameParser.StoreItemType> selectedItemType = EnumSet.allOf(SavedGameParser.StoreItemType.class);
    private final EnumSet<Constants.EventType> selectedEventType = EnumSet.allOf(Constants.EventType.class);

    public static final int PREFERRED_WIDTH_3_DIGITS = 10;

    Jump jump;
    JumpTableModel jumpTableModel;
    JTable encounterInfoTable;

    TableRowSorter<EventTableModel> sorter;
    JTable table;


    public EventTablePanel(EventTableModel model) {
        super(JSplitPane.VERTICAL_SPLIT);
        setResizeWeight(0.15);

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

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));



        // Filter EventType Category
        JButton categoryFilterButton = new JButton("Type ▼");
        JPopupMenu categoryFilterMenu = new JPopupMenu();

        // --- Select All / Deselect All ---
        JMenuItem selectAllEventType = new JMenuItem("Select All");
        selectAllEventType.addActionListener(e -> {
            for (Component comp : categoryFilterMenu.getComponents()) {
                if (comp instanceof JCheckBoxMenuItem item) {
                    item.setSelected(true);
                    selectedEventType.add(Constants.EventType.fromText(item.getText()));
                }
            }
            updateTableFilter();
        });
        categoryFilterMenu.add(selectAllEventType);

        JMenuItem deselectAllEventType = new JMenuItem("Deselect All");
        deselectAllEventType.addActionListener(e -> {
            for (Component comp : categoryFilterMenu.getComponents()) {
                if (comp instanceof JCheckBoxMenuItem item) {
                    item.setSelected(false);
                }
            }
            selectedEventType.clear();
            updateTableFilter();
        });
        categoryFilterMenu.add(deselectAllEventType);

        // --- Separator ---
        categoryFilterMenu.add(new JSeparator());

        for (Constants.EventType value : Constants.EventType.values()) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(value.toString(), true);
            item.addActionListener(e -> {
                        if (item.isSelected()) {
                            selectedEventType.add(value);
                        } else {
                            selectedEventType.remove(value);
                        }
                updateTableFilter();
            });
            categoryFilterMenu.add(item);
        }
        categoryFilterButton.addActionListener(e ->
                categoryFilterMenu.show(categoryFilterButton, 0, categoryFilterButton.getHeight()));
        filterPanel.add(categoryFilterButton);

        // Filter Type
        JButton typeFilterButton = new JButton("Category ▼");
        JPopupMenu typeFilterMenu = new JPopupMenu();

        // --- Select All / Deselect All ---
        JMenuItem selectAllStoreItemType = new JMenuItem("Select All");
        selectAllStoreItemType.addActionListener(e -> {
            for (Component comp : typeFilterMenu.getComponents()) {
                if (comp instanceof JCheckBoxMenuItem item) {
                    item.setSelected(true);
                    selectedItemType.add(SavedGameParser.StoreItemType.fromText(item.getText()));
                }
            }
            updateTableFilter();
        });
        typeFilterMenu.add(selectAllStoreItemType);

        JMenuItem deselectAllStoreItemType = new JMenuItem("Deselect All");
        deselectAllStoreItemType.addActionListener(e -> {
            for (Component comp : typeFilterMenu.getComponents()) {
                if (comp instanceof JCheckBoxMenuItem item) {
                    item.setSelected(false);
                }
            }
            selectedItemType.clear();
            updateTableFilter();
        });
        typeFilterMenu.add(deselectAllStoreItemType);

        // --- Separator ---
        typeFilterMenu.add(new JSeparator());

        for (SavedGameParser.StoreItemType value : SavedGameParser.StoreItemType.values()) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(value.toString(), true);
            item.addActionListener(e -> {
                if (item.isSelected()) {
                    selectedItemType.add(value);
                } else {
                    selectedItemType.remove(value);
                }
                updateTableFilter();
            });
            typeFilterMenu.add(item);
        }
        typeFilterButton.addActionListener(e ->
                typeFilterMenu.show(typeFilterButton, 0, typeFilterButton.getHeight()));
        filterPanel.add(typeFilterButton);

        // Action buttons
        filterPanel.add(openEventInBrowserButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(filterPanel, BorderLayout.NORTH);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        setBottomComponent(bottomPanel);


        table.getColumnModel().getColumn(0).setPreferredWidth(10);
        table.getColumnModel().getColumn(1).setPreferredWidth(3);
        table.getColumnModel().getColumn(2).setPreferredWidth(3);
        table.getColumnModel().getColumn(3).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
        table.getColumnModel().getColumn(4).setPreferredWidth(400);

        table.getColumnModel().getColumn(5).setCellRenderer(new TagsRenderer());
//        table.getColumnModel().getColumn(4).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
//
//        table.getColumnModel().getColumn(7).setPreferredWidth(400);
//
//        table.getColumnModel().getColumn(8).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
//        table.getColumnModel().getColumn(9).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);
//        table.getColumnModel().getColumn(10).setPreferredWidth(PREFERRED_WIDTH_3_DIGITS);




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

    private void updateTableFilter() {
        RowFilter<EventTableModel, Integer> rf = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends EventTableModel, ? extends Integer> entry) {
                EventTableModel model = entry.getModel();


                Constants.EventType eventType = (Constants.EventType) entry.getValue(5); // EventType/Category
                SavedGameParser.StoreItemType storeItemType = (SavedGameParser.StoreItemType) entry.getValue(6); // StoreItemType

                String id = (String) entry.getValue(7);

                boolean matchStoreItemType = selectedItemType.isEmpty() || selectedItemType.contains(storeItemType);
                boolean matchEventType = selectedEventType.isEmpty() || selectedEventType.contains(eventType);

                return matchStoreItemType && matchEventType;
            }
        };

        sorter.setRowFilter(rf);
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

    public void setOpenEventInBrowserButton(ActionListener listener){
        openEventInBrowserButton.addActionListener(listener);
    }

    public JTable getTable(){
        return table;
    }

}
