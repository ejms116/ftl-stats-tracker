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
import net.gausman.ftl.model.Constants;

import net.gausman.ftl.controller.StatsManager;
import net.gausman.ftl.view.EventListItem;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class FTLStatsTrackerController implements Initializable {
    private StatsManager statsManager;
    @FXML private BarChart<?,?> barChart;
    @FXML private ToggleButton toggleTrackingButton;


    @FXML private TableColumn<EventListItem, Instant> ts;
    @FXML private TableColumn<EventListItem, Integer> sectorNumber;
    @FXML private TableColumn<EventListItem, Integer> totalBeaconsExplored;
    @FXML private TableColumn<EventListItem, Integer> currentBeaconId;
    @FXML private TableColumn<EventListItem, Integer> jumpNumber;
    @FXML private TableColumn<EventListItem, Constants.EventCategory> category;
    @FXML private TableColumn<EventListItem, Constants.EventType> type;
    @FXML private TableColumn<EventListItem, ObjectProperty<Integer>> amount;
    @FXML private TableColumn<EventListItem, String> id;

    @FXML private TableView<EventListItem> eventTableView;

    @FXML private TextField filterField;

    @FXML private CheckBox resourceCB;
    @FXML private CheckBox crewCB;
    @FXML private CheckBox systemCB;

    //private HashMap<Constants.EventCategory, Boolean> showCategories = new HashMap<Constants.EventCategory, Boolean>();
    private List<String> showCategories = new ArrayList<>();

    private ObservableList<EventListItem> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statsManager = new StatsManager(this);

        ts.setCellValueFactory(new PropertyValueFactory<>("ts"));
        sectorNumber.setCellValueFactory(new PropertyValueFactory<>("sectorNumber"));
        totalBeaconsExplored.setCellValueFactory(new PropertyValueFactory<>("totalBeaconsExplored"));
        currentBeaconId.setCellValueFactory(new PropertyValueFactory<>("currentBeaconId"));
        jumpNumber.setCellValueFactory(new PropertyValueFactory<>("jumpNumber"));
        category.setCellValueFactory(new PropertyValueFactory<>("category"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        id.setCellValueFactory(new PropertyValueFactory<>("id"));

        FilteredList<EventListItem> filteredData = new FilteredList<>(masterData, p -> false);

        resourceCB.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(eventListItem -> {
                if (newValue == true){
                    if (!showCategories.contains("RESOURCE")){
                        showCategories.add("RESOURCE");
                    }
                } else {
                    showCategories.remove("RESOURCE");
                }

                if (showCategories.contains(eventListItem.getCategory().toString())){
                    return true;
                }
                return false;
            });
        });

        systemCB.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(eventListItem -> {
                if (newValue == true){
                    if (!showCategories.contains("SYSTEM")){
                        showCategories.add("SYSTEM");
                    }
                } else {
                    showCategories.remove("SYSTEM");
                }

                if (showCategories.contains(eventListItem.getCategory().toString())){
                    return true;
                }
                return false;
            });
        });

        crewCB.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(eventListItem -> {
                if (newValue == true){
                    if (!showCategories.contains("CREW")){
                        showCategories.add("CREW");
                    }
                } else {
                    showCategories.remove("CREW");
                }

                if (showCategories.contains(eventListItem.getCategory().toString())){
                    return true;
                }
                return false;
            });
        });

        // TODO text filer when filtering category
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(eventListItem -> {
                if (newValue == null || newValue.isEmpty()){
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (showCategories.contains(eventListItem.getCategory().toString()) && eventListItem.getId().toLowerCase().contains(lowerCaseFilter)){
                    return true;
                }
                return false;
            });
        });

        eventTableView.setItems(filteredData);

//        showCategories.put(Constants.EventCategory.RESOURCE, false);
//        showCategories.put(Constants.EventCategory.SYSTEM, false);
//        showCategories.put(Constants.EventCategory.CREW, false);

        resourceCB.selectedProperty().set(true);
//        systemCB.selectedProperty().set(false);
//        crewCB.selectedProperty().set(false);

        setTestDataBarChart();
    }

    public void addEvent(EventListItem event){
        masterData.add(event);
        //eventTableView.getItems().add(event);
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
}