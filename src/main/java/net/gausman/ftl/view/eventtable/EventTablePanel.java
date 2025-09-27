package net.gausman.ftl.view.eventtable;

import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.SimpleTableItem;
import net.gausman.ftl.model.record.Jump;
import net.gausman.ftl.model.table.EventFilter;
import net.gausman.ftl.model.table.EventTableModel;
import net.gausman.ftl.model.table.JumpTableModel;
import net.gausman.ftl.view.renderer.ResourceEffectsRenderer;
import net.gausman.ftl.view.renderer.TagsRenderer;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;

public class EventTablePanel extends JSplitPane {
    private final JButton openEventInBrowserButton = new JButton("Open in Event Browser");
    private final EnumSet<Constants.EventDetailType> selectedEventDetailTypes = EnumSet.allOf(Constants.EventDetailType.class);
    private final EnumSet<Constants.EventTag> selectedTags = EnumSet.allOf(Constants.EventTag.class);

    public static final int PREFERRED_WIDTH_3_DIGITS = 10;

    Jump jump;
    JumpTableModel jumpTableModel;
    JTable encounterInfoTable;

    TableRowSorter<EventTableModel> sorter;
    JTable table;

    public EventTablePanel(EventTableModel model) {
        super(JSplitPane.VERTICAL_SPLIT);
        setResizeWeight(0.15);

        setupDefaultFilters();

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


        JMenuBar menuBar = new JMenuBar();

        // ------- Event Types ----------
        JMenu typeFilterMenu = new JMenu("Filter Types ▼");

        Map<Constants.EventCategory, List<Constants.EventDetailType>> categoryMap = new LinkedHashMap<>();
        for (Constants.EventCategory category : Constants.EventCategory.values()) {
            categoryMap.put(category, new ArrayList<>());
        }
        for (Constants.EventDetailType detail : Constants.EventDetailType.values()) {
            categoryMap.get(detail.getEventCategory()).add(detail);
        }

        for (Constants.EventCategory category : Constants.EventCategory.values()){
            JMenu categoryMenu = new JMenu(category.toString());

            JCheckBoxMenuItem masterItem = new JCheckBoxMenuItem("All " + category.toString(), true);

            categoryMenu.add(masterItem);
            categoryMenu.addSeparator();

            List<JCheckBoxMenuItem> detailItems = new ArrayList<>();
            for (Constants.EventDetailType detail : categoryMap.get(category)) {
                JCheckBoxMenuItem detailItem = new JCheckBoxMenuItem(detail.toString(), selectedEventDetailTypes.contains(detail));
                detailItems.add(detailItem);
                categoryMenu.add(detailItem);
                detailItem.addActionListener(e -> {
                    if (detailItem.isSelected()){
                        selectedEventDetailTypes.add(detail);
                    } else {
                        selectedEventDetailTypes.remove(detail);
                    }
                    updateTableFilter();
                });
            }

            // Sync master checkbox with detail checkboxes
            masterItem.addActionListener(e -> {
                boolean selected = masterItem.isSelected();
                for (JCheckBoxMenuItem detailItem : detailItems) {
                    detailItem.setSelected(selected);
                    for (Constants.EventDetailType detailType : categoryMap.get(category)){
                        if (selected){
                            selectedEventDetailTypes.add(detailType);
                        } else {
                            selectedEventDetailTypes.remove(detailType);
                        }
                    }

                }
                updateTableFilter();
            });
            typeFilterMenu.add(categoryMenu);
        }
        menuBar.add(typeFilterMenu);
        filterPanel.add(menuBar);


        // ------- TAGS ---------
        JMenu tagFilterMenu = new JMenu("Filter Tags ▼");

        // All Tags
        JCheckBoxMenuItem tagMasterItem = new JCheckBoxMenuItem("All Tags", true);
        tagFilterMenu.add(tagMasterItem);
        tagFilterMenu.add(new JSeparator());

        List<JCheckBoxMenuItem> tagItems = new ArrayList<>();
        for (Constants.EventTag tag : Constants.EventTag.values()) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(String.valueOf(tag), selectedTags.contains(tag));
            item.addActionListener(e -> {
                if (item.isSelected()) {
                    selectedTags.add(tag);
                } else {
                    selectedTags.remove(tag);
                }
                updateTableFilter();
            });
            tagItems.add(item);
            tagFilterMenu.add(item);
        }

        tagMasterItem.addActionListener(e -> {
            boolean selected = tagMasterItem.isSelected();
            for (JCheckBoxMenuItem detailItem : tagItems) {
                detailItem.setSelected(selected);

            }
            for (Constants.EventTag tag : Constants.EventTag.values()){
                if (selected){
                    selectedTags.add(tag);
                } else {
                    selectedTags.remove(tag);
                }
            }
            updateTableFilter();
        });

        menuBar.add(tagFilterMenu);

        // Action buttons
        filterPanel.add(openEventInBrowserButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(filterPanel, BorderLayout.NORTH);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        setBottomComponent(bottomPanel);


        table.getColumnModel().getColumn(0).setPreferredWidth(10);
        table.getColumnModel().getColumn(1).setMinWidth(20);
        table.getColumnModel().getColumn(1).setMaxWidth(20);
        table.getColumnModel().getColumn(2).setMinWidth(40);
        table.getColumnModel().getColumn(2).setMaxWidth(40);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);

        table.getColumnModel().getColumn(4).setCellRenderer(new ResourceEffectsRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new TagsRenderer());
        table.getColumnModel().getColumn(6).setPreferredWidth(400);

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

        updateTableFilter();
        SwingUtilities.invokeLater(() -> setDividerLocation(0.15));

    }

    // todo we probably want this to be saved into the settings.cfg file
    private void setupDefaultFilters() {
//        // General
//        selectedEventDetailTypes.remove(Constants.EventDetailType.BEACONS_EXPLORED);
//        selectedEventDetailTypes.remove(Constants.EventDetailType.SHIPS_DESTROYED);
//        selectedEventDetailTypes.remove(Constants.EventDetailType.CREW_HIRED);

//        // Use
//        selectedEventDetailTypes.remove(Constants.EventDetailType.USE_FUEL);
//
//        // Crew
//        selectedEventDetailTypes.remove(Constants.EventDetailType.CREW_SKILL);
//        selectedEventDetailTypes.remove(Constants.EventDetailType.CREW_STAT);
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

                Constants.EventDetailType type = (Constants.EventDetailType) entry.getValue(3);

                @SuppressWarnings("unchecked")
                EnumMap<Constants.Resource, Integer> resourceEffects = (EnumMap<Constants.Resource, Integer>) entry.getValue(4);

                @SuppressWarnings("unchecked")
                Set<Constants.EventTag> tags = (Set<Constants.EventTag>) entry.getValue(5);
//                String id = (String) entry.getValue(7);

                boolean matchType = selectedEventDetailTypes.contains(type);
                boolean matchTag = tags.isEmpty() || !Collections.disjoint(tags, selectedTags);

                // todo "smart" filters like fuel used events
//                boolean fuelUsed = tags.contains(Constants.EventTag.USE) && resourceEffects.containsKey(Constants.Resource.FUEL);

//                return matchType && matchTag && !fuelUsed;
                return matchType && matchTag;


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
