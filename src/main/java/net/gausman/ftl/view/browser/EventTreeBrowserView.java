package net.gausman.ftl.view.browser;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.xml.event.Choice;
import net.blerf.ftl.xml.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class EventTreeBrowserView extends JFrame {
    private static final Logger log = LoggerFactory.getLogger(EventTreeBrowserView.class);

    private DataManager dm = DataManager.get();
    private Map<String, FTLEventNode> allEvents;
    private Map<String, TextList> textListMap;
    private Map<String, ShipEvent> shipEventMap;

    private EventNodeTableModel model;
    private BuildContext context;
    private JTree tree;

    private JTable eventTable;
    private JScrollPane treeScrollPane;

    private List<EventBrowserListItem> originalItems;


    public EventTreeBrowserView(Map<String, FTLEventNode> allEvents, Map<String, TextList> textListMap, Map<String, ShipEvent> shipEventMap) {
        this.allEvents = allEvents;
        this.textListMap = textListMap;
        this.shipEventMap = shipEventMap;
        initUI();
    }

    private void initUI() {
        context = new BuildContext();
        context.setAllEvents(allEvents);
        context.setShipEventMap(shipEventMap);
        context.setTextListMap(textListMap);

        setTitle("Event Browser");
        setSize(1800, 1000);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // === TOP PANEL ===
        JPanel topPanel = new JPanel(new BorderLayout());

        // Search bar on the left
        JTextField searchField = new JTextField();
        topPanel.add(searchField, BorderLayout.CENTER);

        // Dropdown and checkbox panel on the right
        String[] options = { "Show All", "Only Events", "Only EventLists" };
        JComboBox<String> filterComboBox = new JComboBox<>(options);
        JCheckBox showTextCheckbox = new JCheckBox("Show Text", true);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.add(filterComboBox);
        controlPanel.add(showTextCheckbox);
        topPanel.add(controlPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // === Event table setup ===
        model = new EventNodeTableModel();
        List<FTLEventNode> nodeList = new ArrayList<>(allEvents.values());
        nodeList.sort(Comparator.comparing(FTLEventNode::getId, String.CASE_INSENSITIVE_ORDER));

        originalItems = new ArrayList<>();
        for (FTLEventNode node : nodeList){
            EventBrowserListItem.Type type = EventBrowserListItem.Type.EVENT;
            if (node instanceof FTLEventList){
                type = EventBrowserListItem.Type.EVENT_LIST;
            }
            originalItems.add(new EventBrowserListItem(node.getId(), type));
        }
        model.setItems(new ArrayList<>(originalItems));

        eventTable = new JTable(model);
        eventTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        eventTable.getColumnModel().getColumn(1).setPreferredWidth(30);
        JScrollPane listScroll = new JScrollPane(eventTable);
        add(listScroll, BorderLayout.WEST);

        // === FILTER COMBOBOX LISTENER ===
        filterComboBox.addActionListener(e -> {
            String selected = (String) filterComboBox.getSelectedItem();
            List<EventBrowserListItem> filtered = new ArrayList<>();

            switch (selected) {
                case "Only Events":
                    for (EventBrowserListItem item : originalItems) {
                        if (item.getType() == EventBrowserListItem.Type.EVENT) {
                            filtered.add(item);
                        }
                    }
                    break;
                case "Only EventLists":
                    for (EventBrowserListItem item : originalItems) {
                        if (item.getType() == EventBrowserListItem.Type.EVENT_LIST) {
                            filtered.add(item);
                        }
                    }
                    break;
                default: // "Show All"
                    filtered = new ArrayList<>(originalItems);
                    break;
            }

            model.setItems(filtered);
            model.fireTableDataChanged();
        });

        // === CHECKBOX LISTENER TO TOGGLE TEXT/ID DISPLAY ===
        showTextCheckbox.addActionListener(e -> {
            // Store current state of the tree
            TreePath rootPath = new TreePath(tree.getModel().getRoot());
            List<TreePath> expandedPaths = new ArrayList<>();
            saveExpandedPaths(tree, rootPath, expandedPaths);

            context.setShowText(showTextCheckbox.isSelected());
            ((DefaultTreeModel) tree.getModel()).nodeStructureChanged((TreeNode) tree.getModel().getRoot());

            // restore state of the tree
            for (TreePath path : expandedPaths) {
                tree.expandPath(path);
            }
        });




        // === Filter logic for search field ===
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void updateFilter() {
                String query = searchField.getText().trim().toLowerCase();
                List<EventBrowserListItem> filtered = new ArrayList<>();
                for (EventBrowserListItem item : originalItems) {
                    // Search for Id
                    if (item.getId().toLowerCase().contains(query)) {
                        filtered.add(item);
                    }

                    // Search for text
                    if (item.getType().equals(EventBrowserListItem.Type.EVENT_LIST)){
                        continue;
                    }
                    FTLEventNode node = allEvents.get(item.getId());
                    if (node instanceof FTLEvent nodeEvent){
                        String eventText = dm.getTextForId((nodeEvent).getText().getId());
                        if (eventText != null && !eventText.isEmpty()){
                            if (eventText.toLowerCase().contains(query)) {
                                filtered.add(item);
                            }
                        } else if ((nodeEvent).getText().getLoad() != null) {
                            // Events that have multiple texts via TextList
                            TextList textList = textListMap.get((nodeEvent).getText().getLoad());
                            if (textList != null){
                                for (FTLText text : textList.getText()){
                                    if (dm.getTextForId(text.getId()).toLowerCase().contains(query)){
                                        filtered.add(item);
                                        break;
                                    }
                                }
                            }
                        }

                    }
                }
                model.setItems(filtered);
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateFilter(); }
        });

        // === Selection logic ===
        eventTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()){
                int selected = eventTable.getSelectedRow();
                if (selected >= 0 && selected < model.getRowCount()) {
                    String selectedName = model.getRowItem(selected).getId();
                    loadEventById(selectedName);
                }
            }
        });

        setVisible(true);
    }

    public void selectEventById(String eventId){
        for (int i = 0; i < model.getRowCount(); i++){
            EventBrowserListItem item = model.getRowItem(i);
            if (item.getId().equals(eventId)){
                eventTable.setRowSelectionInterval(i, i);
                eventTable.scrollRectToVisible(eventTable.getCellRect(i, 0, true));
                return;
            }
        }
        log.info("Event not found: " + eventId);
    }

    private void saveExpandedPaths(JTree tree, TreePath parent, List<TreePath> paths) {
        if (tree.isExpanded(parent)) {
            paths.add(parent);
            Object node = parent.getLastPathComponent();
            int count = tree.getModel().getChildCount(node);
            for (int i = 0; i < count; i++) {
                Object child = tree.getModel().getChild(node, i);
                TreePath path = parent.pathByAddingChild(child);
                saveExpandedPaths(tree, path, paths);
            }
        }
    }

    private DefaultMutableTreeNode buildEventTreeRecoursive(FTLEventNode node){
        FTLEventNode resolved = node.resolve(allEvents);
        if (resolved instanceof FTLEvent){
            return buildEventTreeNodeForEvent((FTLEvent) resolved);
        } else if (resolved instanceof FTLEventList){
            return buildEventTreeNodeForEventList((FTLEventList) resolved);
        }
        return null;
    }

    private DefaultMutableTreeNode buildEventTreeNodeForShip(ShipEvent shipEvent){
        EventTreeNodeData data = new EventTreeNodeData(
                shipEvent.getId(),
                "Ship",
                shipEvent.getAutoBlueprintId(),
                shipEvent.getAutoBlueprintId()
        );
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(data);

        DefaultMutableTreeNode destroyed = new DefaultMutableTreeNode("Destroyed");
        root.add(destroyed);

        DefaultMutableTreeNode deadCrew = new DefaultMutableTreeNode("Dead Crew");
        root.add(deadCrew);

        if (shipEvent.getEscape() != null){
            DefaultMutableTreeNode escape = new DefaultMutableTreeNode("Escape");
            root.add(escape);
        }

        if (shipEvent.getSurrender() != null){
            DefaultMutableTreeNode surrender = new DefaultMutableTreeNode("Surrender");
            root.add(surrender);
        }

        return root;
    }

    private DefaultMutableTreeNode buildEventTreeNodeForEvent(FTLEvent event){
        String eventText = dm.getTextForId(event.getText().getId());
        EventTreeNodeData rootData = new EventTreeNodeData(
                event.getId(),
                "Event " + event.getId(),
                event.getText().getId(),
                eventText
        );
        if (eventText.isEmpty() && event.getText() != null && event.getText().getLoad() != null && !event.getText().getLoad().isEmpty()){
            rootData.setTextList(textListMap.get(event.getText().getLoad()));
        }

        rootData.setEvent(event);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootData);

        if (event.getShip() != null){
            ShipEvent shipEvent = event.getShip().resolve(shipEventMap);
            DefaultMutableTreeNode shipNode = buildEventTreeNodeForShip(shipEvent);
            root.add(shipNode);
        }

        for (Choice choice : event.getChoices()){
            DefaultMutableTreeNode treeNode = buildEventTreeRecoursive(choice.getEvent());
            EventTreeNodeData data = new EventTreeNodeData(
                    "",
                    "Choice",
                    choice.getText().getId(),
                    dm.getTextForId(choice.getText().getId())
            );
            data.setChoice(choice);
            // this is not recoursive because a Choice always contains one event
            // (that might contain more choices or is an eventList
            DefaultMutableTreeNode choiceNode = new DefaultMutableTreeNode(data);
            choiceNode.add(treeNode);
            root.add(choiceNode);
        }

        if (event.getChoices().isEmpty()){
            // TODO "terminal" events
        }

        return root;
    }

    private DefaultMutableTreeNode buildEventTreeNodeForEventList(FTLEventList list){
        EventTreeNodeData data = new EventTreeNodeData(
                list.getId(),
                "List " + list.getId(),
                "",
                "This is an EventList, the game normally chooses one at random"
        );
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(data);

        for (FTLEvent event : list.getEventList()){
            DefaultMutableTreeNode treeNode = buildEventTreeRecoursive(event);
            root.add(treeNode);
        }


        return root;
    }

    private DefaultMutableTreeNode buildTreeNew(FTLEventNode node){
        DefaultMutableTreeNode root = node.build(dm, context);
        return root;
    }

    private void loadEventById(String eventId){

        if (!allEvents.containsKey(eventId)){
            // Todo error
            return;
        }

        // Build new tree root
        DefaultMutableTreeNode root = buildTreeNew(allEvents.get(eventId));
//        DefaultMutableTreeNode root = buildEventTreeRecoursive(allEvents.get(eventId));

        tree = new JTree(root);

        tree.setCellRenderer(new EventTreeCellRenderer(dm, context));
        JScrollPane newScrollPane = new JScrollPane(tree);

        // Remove old scroll pane if it exists
        if (treeScrollPane != null) {
            remove(treeScrollPane);
        }

        // Store and add the new scroll pane
        treeScrollPane = newScrollPane;
        add(treeScrollPane, BorderLayout.CENTER);

        // Refresh the UI
        revalidate();
        repaint();
    }

}

