package net.gausman.ftl;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.Constants;

import net.gausman.ftl.controller.StatsManager;
import net.gausman.ftl.model.ShipStatus;
import net.gausman.ftl.view.EventListItem;
import net.gausman.ftl.view.OverviewListItem;
import net.gausman.ftl.view.SimpleListItem;
import net.gausman.ftl.view.SystemListItem;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class FTLStatsTrackerController implements Initializable {
    private StatsManager statsManager;
    @FXML private BarChart<?,?> barChart;
    @FXML private ToggleButton toggleTrackingButton;


    @FXML private TableColumn<EventListItem, String> time;
    @FXML private TableColumn<EventListItem, Integer> sectorNumber;
    @FXML private TableColumn<EventListItem, Integer> totalBeaconsExplored;
    @FXML private TableColumn<EventListItem, Integer> currentBeaconId;
    @FXML private TableColumn<EventListItem, Integer> jumpNumber;
    @FXML private TableColumn<EventListItem, SavedGameParser.StoreItemType> itemType;
    @FXML private TableColumn<EventListItem, Constants.EventType> type;
    @FXML private TableColumn<EventListItem, ObjectProperty<Integer>> amount;
    @FXML private TableColumn<EventListItem, ObjectProperty<Integer>> cost;
    @FXML private TableColumn<EventListItem, String> id;
    @FXML private TableColumn<EventListItem, String> text;

    @FXML private TableView<EventListItem> eventTableView;

    @FXML private TextField filterField;

    @FXML private CheckBox systemCB;
    @FXML private CheckBox resourceCB;
    @FXML private CheckBox crewCB;
    @FXML private CheckBox weaponCB;
    @FXML private CheckBox droneCB;
    @FXML private CheckBox augmentCB;
    @FXML private CheckBox reactorCB;

    @FXML private CheckBox startCB;
    @FXML private CheckBox upgradeCB;
    @FXML private CheckBox rewardCB;
    @FXML private CheckBox buyCB;
    @FXML private CheckBox sellCB;
    @FXML private CheckBox discardCB;
    @FXML private CheckBox useCB;

    @FXML private TableColumn<SystemListItem, String> systemCol;
    @FXML private TableColumn<SystemListItem, String> systemLevelCol;
    @FXML private TableView<SystemListItem> systemTab;

    @FXML private TableColumn<SystemListItem, String> subSystemCol;
    @FXML private TableColumn<SystemListItem, String> subSystemLevelCol;
    @FXML private TableView<SystemListItem> subSystemTab;

    @FXML private TableColumn<SystemListItem, String> resourceCol;
    @FXML private TableColumn<SystemListItem, String> resourceAmountCol;
    @FXML private TableView<SystemListItem> resourceTab;

    @FXML private TableColumn<SimpleListItem, String> weaponsCol;
    @FXML private TableView<SimpleListItem> weaponsTab;

    @FXML private TableColumn<SimpleListItem, String> dronesCol;
    @FXML private TableView<SimpleListItem> dronesTab;

    @FXML private TableColumn<SimpleListItem, String> augmentsCol;
    @FXML private TableView<SimpleListItem> augmentsTab;

    @FXML private TableColumn<SimpleListItem, String> crewCol;
    @FXML private TableView<SimpleListItem> crewTab;

    @FXML private TableColumn<OverviewListItem, String> propertyCol;
    @FXML private TableColumn<OverviewListItem, String> valueCol;
    @FXML private TableView<OverviewListItem> overviewTableView;

    private List<String> showItemTypes = new ArrayList<>();
    private List<String> showTypes = new ArrayList<>();
    private String searchString = "";

    private ObservableList<OverviewListItem> overviewMasterData = FXCollections.observableArrayList();
    private ObservableList<EventListItem> masterData = FXCollections.observableArrayList();
    private ObservableList<SystemListItem> systemListItemObservableList = FXCollections.observableArrayList();
    private ObservableList<SystemListItem> subSystemListItemObservableList = FXCollections.observableArrayList();
    private ObservableList<SystemListItem> resourceListItemObservableList = FXCollections.observableArrayList();
    private ObservableList<SimpleListItem> weaponsListItemObservableList = FXCollections.observableArrayList();
    private ObservableList<SimpleListItem> dronesListItemObservableList = FXCollections.observableArrayList();
    private ObservableList<SimpleListItem> augmentsListItemObservableList = FXCollections.observableArrayList();
    private ObservableList<SimpleListItem> crewListItemObservableList = FXCollections.observableArrayList();

    EventListItem lastSelectedItem = new EventListItem();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statsManager = new StatsManager(this);

        propertyCol.setCellValueFactory(new PropertyValueFactory<>("property"));
        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        overviewTableView.setItems(overviewMasterData);

        time.setCellValueFactory(new PropertyValueFactory<>("time"));
        sectorNumber.setCellValueFactory(new PropertyValueFactory<>("sectorNumber"));
        totalBeaconsExplored.setCellValueFactory(new PropertyValueFactory<>("totalBeaconsExplored"));
        currentBeaconId.setCellValueFactory(new PropertyValueFactory<>("currentBeaconId"));
        jumpNumber.setCellValueFactory(new PropertyValueFactory<>("jumpNumber"));
        itemType.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        cost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        text.setCellValueFactory(new PropertyValueFactory<>("text"));

        systemCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        systemLevelCol.setCellValueFactory(new PropertyValueFactory<>("level"));

        subSystemCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        subSystemLevelCol.setCellValueFactory(new PropertyValueFactory<>("level"));

        resourceCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        resourceAmountCol.setCellValueFactory(new PropertyValueFactory<>("level"));

        weaponsCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        dronesCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        augmentsCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        crewCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        FilteredList<EventListItem> filteredData = new FilteredList<>(masterData, p -> true);

        resourceCB.selectedProperty().addListener((observable, oldValue, newValue) -> dynamicListener(filteredData, showItemTypes, SavedGameParser.StoreItemType.RESOURCE.name(), newValue));
        systemCB.selectedProperty().addListener((observable, oldValue, newValue) -> dynamicListener(filteredData, showItemTypes, SavedGameParser.StoreItemType.SYSTEM.name(), newValue));
        crewCB.selectedProperty().addListener((observable, oldValue, newValue) -> dynamicListener(filteredData, showItemTypes, SavedGameParser.StoreItemType.CREW.name(), newValue));
        weaponCB.selectedProperty().addListener((observable, oldValue, newValue) -> dynamicListener(filteredData, showItemTypes, SavedGameParser.StoreItemType.WEAPON.name(), newValue));
        droneCB.selectedProperty().addListener((observable, oldValue, newValue) -> dynamicListener(filteredData, showItemTypes, SavedGameParser.StoreItemType.DRONE.name(), newValue));
        augmentCB.selectedProperty().addListener((observable, oldValue, newValue) -> dynamicListener(filteredData, showItemTypes, SavedGameParser.StoreItemType.AUGMENT.name(), newValue));
        reactorCB.selectedProperty().addListener((observable, oldValue, newValue) -> dynamicListener(filteredData, showItemTypes, SavedGameParser.StoreItemType.REACTOR.name(), newValue));

        startCB.selectedProperty().addListener((observable, oldValue, newValue) -> dynamicListener(filteredData, showTypes, "START", newValue));
        upgradeCB.selectedProperty().addListener((observable, oldValue, newValue) -> dynamicListener(filteredData, showTypes, "UPGRADE", newValue));
        rewardCB.selectedProperty().addListener((observable, oldValue, newValue) -> dynamicListener(filteredData, showTypes, "REWARD", newValue));
        buyCB.selectedProperty().addListener((observable, oldValue, newValue) -> dynamicListener(filteredData, showTypes, "BUY", newValue));
        sellCB.selectedProperty().addListener((observable, oldValue, newValue) -> dynamicListener(filteredData, showTypes, "SELL", newValue));
        discardCB.selectedProperty().addListener((observable, oldValue, newValue) -> dynamicListener(filteredData, showTypes, "DISCARD", newValue));
        useCB.selectedProperty().addListener((observable, oldValue, newValue) -> dynamicListener(filteredData, showTypes, "USE", newValue));

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(eventListItem -> {
                searchString = newValue.toLowerCase();
                return filterEventList(eventListItem);
            });
        });


        FilteredList<SystemListItem> systemListItems = new FilteredList<>(systemListItemObservableList, p -> true);
        systemTab.setItems(systemListItems);

        FilteredList<SystemListItem> subSystemListItems = new FilteredList<>(subSystemListItemObservableList, p -> true);
        subSystemTab.setItems(subSystemListItems);

        FilteredList<SystemListItem> resourceListItems = new FilteredList<>(resourceListItemObservableList, p -> true);
        resourceTab.setItems(resourceListItems);

        FilteredList<SimpleListItem> weaponsListItems = new FilteredList<>(weaponsListItemObservableList, p -> true);
        weaponsTab.setItems(weaponsListItems);

        FilteredList<SimpleListItem> dronesListItems = new FilteredList<>(dronesListItemObservableList, p -> true);
        dronesTab.setItems(dronesListItems);

        FilteredList<SimpleListItem> augmentsListItems = new FilteredList<>(augmentsListItemObservableList, p -> true);
        augmentsTab.setItems(augmentsListItems);

        FilteredList<SimpleListItem> crewListItems = new FilteredList<>(crewListItemObservableList, p -> true);
        crewTab.setItems(crewListItems);



        eventTableView.setItems(filteredData);

        // TODO save these values to the settings.cfg
        resourceCB.selectedProperty().set(true);
        systemCB.selectedProperty().set(true);
        crewCB.selectedProperty().set(true);
        weaponCB.selectedProperty().set(true);
        droneCB.selectedProperty().set(true);
        augmentCB.selectedProperty().set(true);
        reactorCB.selectedProperty().set(true);

        startCB.selectedProperty().set(true);
        upgradeCB.selectedProperty().set(true);
        rewardCB.selectedProperty().set(true);
        buyCB.selectedProperty().set(true);
        sellCB.selectedProperty().set(true);
        discardCB.selectedProperty().set(true);
        useCB.selectedProperty().set(true);

        setTestDataBarChart();
    }

    private void dynamicListener(FilteredList<EventListItem> filteredData, List<String> show, String text, boolean newValue){
        if (newValue == true) {
            if (!show.contains(text)) {
                show.add(text);
            }
        } else {
            show.remove(text);
        }
        filteredData.setPredicate(this::filterEventList);
    }



    private boolean filterEventList(EventListItem item){
        if (!showItemTypes.contains(item.getItemType().name())){
            return false;
        }
        if (!showTypes.contains(item.getType().toString())){
            return false;
        }
        if (!item.getId().toLowerCase().contains(searchString)){
            return false;
        }

        return true;
    }

    public void replaceOverviewList(List<OverviewListItem> overviewList){
        overviewMasterData.clear();
        overviewMasterData.addAll(overviewList);
    }

    public void addEvent(EventListItem event){
        masterData.add(event);
        eventTableView.getSelectionModel().select(event);
    }

    public void addEvent(EventListItem event, boolean update){
        masterData.add(event);
        eventTableView.getSelectionModel().select(event);
        mouseClickEventList();
    }

    public void startNewRun(){
        lastSelectedItem = new EventListItem();
        overviewMasterData.clear();
        masterData.clear();
        systemListItemObservableList.clear();
        subSystemListItemObservableList.clear();
        resourceListItemObservableList.clear();

        weaponsListItemObservableList.clear();
        dronesListItemObservableList.clear();
        augmentsListItemObservableList.clear();
        crewListItemObservableList.clear();
    }

    @FXML
    void mouseClickEventList(){
        EventListItem item = eventTableView.getSelectionModel().getSelectedItem();
        ShipStatus shipStatus = statsManager.getNewShipStatus(lastSelectedItem.getSectorNumber(), item.getSectorNumber(),
                lastSelectedItem.getJumpNumber(), item.getJumpNumber(),
                lastSelectedItem.getEventNumber(), item.getEventNumber());
        lastSelectedItem = item;
        systemListItemObservableList.clear();
        systemListItemObservableList.addAll(shipStatus.getSystemList());

        subSystemListItemObservableList.clear();
        subSystemListItemObservableList.addAll(shipStatus.getSubSystemList());

        resourceListItemObservableList.clear();
        resourceListItemObservableList.addAll(shipStatus.getResourceList());

        weaponsListItemObservableList.clear();
        weaponsListItemObservableList.addAll(shipStatus.getWeaponsList());

        dronesListItemObservableList.clear();
        dronesListItemObservableList.addAll(shipStatus.getDronesList());

        augmentsListItemObservableList.clear();
        augmentsListItemObservableList.addAll(shipStatus.getAugmentsList());

        crewListItemObservableList.clear();
        crewListItemObservableList.addAll(shipStatus.getCrewList());

    }



    @FXML
    void toggleTracking(){
        statsManager.setToggleTracking();
    }

    public void shutdown(){
        statsManager.shutdownFileWatcher();
    }

    private void setTestDataBarChart(){
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Scrap");
        series1.getData().add(new XYChart.Data("Civilian1", 100));
        series1.getData().add(new XYChart.Data("Rock1", 200));
        series1.getData().add(new XYChart.Data("Nebula1", 300));
        series1.getData().add(new XYChart.Data("Nebula2", 400));
        series1.getData().add(new XYChart.Data("Rock2", 500));
        series1.getData().add(new XYChart.Data("Civilian2", 500));
        series1.getData().add(new XYChart.Data("Abandoned", 500));
        series1.getData().add(new XYChart.Data("Last Stand", 500));

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Free stuff");
        series2.getData().add(new XYChart.Data("Civilian1", 120));
        series2.getData().add(new XYChart.Data("Rock1", 234));
        series2.getData().add(new XYChart.Data("Nebula1", 444));
        series2.getData().add(new XYChart.Data("Nebula2", 555));
        series2.getData().add(new XYChart.Data("Rock2", 651));
        series2.getData().add(new XYChart.Data("Civilian2", 500));
        series2.getData().add(new XYChart.Data("Abandoned", 500));
        series2.getData().add(new XYChart.Data("Last Stand", 500));

        barChart.getData().addAll(series1, series2);
    }

//    class EventListChangeListener implements ChangeListener{
//        FilteredList<EventListItem> filteredData = new FilteredList<>(masterData, p -> false);
//
//        @Override
//        public void stateChanged(ChangeEvent e) {
//            (observable, oldValue, newValue) -> {
//                filteredData.setPredicate(eventListItem -> {
//                    e.
//                    if (newValue == true){
//                        if (!showCategories.contains("RESOURCE")){
//                            showCategories.add("RESOURCE");
//                        }
//                    } else {
//                        showCategories.remove("RESOURCE");
//                    }
//
//                    return filterEventList(eventListItem);
//                });
//            }
//        }
//    }
}