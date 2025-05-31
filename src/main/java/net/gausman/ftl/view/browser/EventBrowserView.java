package net.gausman.ftl.view.browser;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.xml.event.*;
import net.blerf.ftl.xml.event.Choice;
import net.blerf.ftl.xml.event.FTLEvent;
import net.blerf.ftl.xml.event.FTLEventNode;
import net.blerf.ftl.xml.event.FTLText;
import net.blerf.ftl.xml.event.TextList;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class EventBrowserView extends JFrame {

    private DataManager dm = DataManager.get();
    private Map<String, FTLEventNode> allEvents;
    private Map<String, TextList> textListMap;

//    private JList<String> eventListSelector;
    private JTable eventTable;
    private JTextArea eventDetails;
    private JPanel choicesPanel;
    private JPanel mainPanel;

    public EventBrowserView(Map<String, FTLEventNode> allEvents, Map<String, TextList> textListMap) {
        this.allEvents = allEvents;
        this.textListMap = textListMap;
        initUI();
    }

    private void initUI() {
        setTitle("Event Browser");
        setSize(1800, 1000);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // LEFT: List of all names
        EventNodeTableModel model = new EventNodeTableModel();
        List<FTLEventNode> nodeList = new ArrayList<>(allEvents.values());
        nodeList.sort(Comparator.comparing(FTLEventNode::getId, String.CASE_INSENSITIVE_ORDER));

        List<EventBrowserListItem> items = new ArrayList<>();
        for (FTLEventNode node : nodeList){
            EventBrowserListItem.Type type = EventBrowserListItem.Type.EVENT;
            if (node instanceof FTLEventList){
                type = EventBrowserListItem.Type.EVENT_LIST;
            }
            items.add(new EventBrowserListItem(node.getId(), type));
        }
        model.setItems(items);



        eventTable = new JTable(model);
        eventTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        eventTable.getColumnModel().getColumn(1).setPreferredWidth(30);

        JScrollPane listScroll = new JScrollPane(eventTable);
        add(listScroll, BorderLayout.WEST);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // === Event Details Label ===
        JLabel eventDetailsLabel = new JLabel("Event Details");
        eventDetailsLabel.setFont(eventDetailsLabel.getFont().deriveFont(Font.BOLD));
        eventDetailsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(eventDetailsLabel);

        // === Event Details TextArea with Scroll ===
        eventDetails = new JTextArea();
        eventDetails.setEditable(false);
        eventDetails.setLineWrap(true);
        eventDetails.setWrapStyleWord(true);

        JScrollPane eventScroll = new JScrollPane(eventDetails);
        eventScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        eventScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));  // Optional max height

        mainPanel.add(eventScroll);

        // === Choices Label ===
        JLabel choicesLabel = new JLabel("Choices");
        choicesLabel.setFont(choicesLabel.getFont().deriveFont(Font.BOLD));
        choicesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(choicesLabel);

        // === Choices Panel ===
        choicesPanel = new JPanel();
        choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));
        choicesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane choiceScroll = new JScrollPane(choicesPanel);
        choiceScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        choiceScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        mainPanel.add(choiceScroll);


        add(mainPanel, BorderLayout.CENTER);

        eventTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()){
                int selected = eventTable.getSelectedRow();
                String selectedName = model.getRowItem(selected).getId();
                loadElementByName(selectedName);
            }
        });

        setVisible(true);
    }

    private void loadElementByName(String name) {
        if (allEvents.containsKey(name)) {
            loadEventNode(allEvents.get(name));
        } else {
            eventDetails.setText("Unknown element: " + name);
            choicesPanel.removeAll();
            choicesPanel.revalidate();
            choicesPanel.repaint();
        }

    }

    private void loadEventNode(FTLEventNode node){
        FTLEventNode resolved = node.resolve(allEvents);
        if (resolved instanceof FTLEvent){
            loadEvent((FTLEvent) resolved);
        } else if (resolved instanceof FTLEventList){
            loadEventList((FTLEventList) resolved);
        }
    }

    private void loadEventList(FTLEventList list) {
        eventDetails.setText("Event List: " + list.getId() + "\nSelect one of the events below:");
        choicesPanel.removeAll();
        for (FTLEvent e : list.getEventList()) {
            String buttonText = dm.getTextForId(e.getText().getId());
            if (buttonText.isEmpty()){
                buttonText = e.getLoad();
            }
            JButton button = new JButton(buttonText);
            button.addActionListener(ev -> loadEventNode(e));
            choicesPanel.add(button);
        }
        choicesPanel.revalidate();
        choicesPanel.repaint();
    }

    private void loadEvent(FTLEvent e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getId()).append("\n");
        String eventText = dm.getTextForId(e.getText().getId());
        // text obj has load property
        if (eventText.isEmpty() && e.getText() != null && e.getText().getLoad() != null && !e.getText().getLoad().isEmpty()){
            TextList textList = textListMap.get(e.getText().getLoad());

            sb.append("Textlist (game selects a random text):").append("\n");
            int i = 1;
            for (FTLText text : textList.getText()){
                sb.append(i).append(": ").append(dm.getTextForId(text.getId())).append("\n");
                i++;
            }
        }

        if (eventText.isEmpty()){
            sb.append("No information found.");
        } else {
            sb.append(eventText);
        }
        eventDetails.setText(sb.toString());

        choicesPanel.removeAll();
        if (e.getChoices() != null && !e.getChoices().isEmpty()) {
            for (Choice choice : e.getChoices()) {
                String buttonText = dm.getTextForId(choice.getText().getId());
                JButton button = new JButton(buttonText);
                button.addActionListener(ev -> {
                    FTLEventNode next = choice.getEvent();
                    if (next != null) {
                        loadEventNode(next);
                    }
                });
                choicesPanel.add(button);
            }
        }
        choicesPanel.revalidate();
        choicesPanel.repaint();
    }
}

