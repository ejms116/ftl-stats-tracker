package net.gausman.ftl;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import net.gausman.ftl.model.Constants;

import net.gausman.ftl.controller.StatsManager;
import net.gausman.ftl.view.EventListItem;

import java.net.URL;
import java.time.Instant;
import java.util.ResourceBundle;

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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //statsManager.init(this);
        statsManager = new StatsManager(this);

        ts.setCellValueFactory(new PropertyValueFactory<EventListItem, Instant>("ts"));
        sectorNumber.setCellValueFactory(new PropertyValueFactory<EventListItem, Integer>("sectorNumber"));
        totalBeaconsExplored.setCellValueFactory(new PropertyValueFactory<EventListItem, Integer>("totalBeaconsExplored"));
        currentBeaconId.setCellValueFactory(new PropertyValueFactory<EventListItem, Integer>("currentBeaconId"));
        jumpNumber.setCellValueFactory(new PropertyValueFactory<EventListItem, Integer>("jumpNumber"));
        category.setCellValueFactory(new PropertyValueFactory<EventListItem, Constants.EventCategory>("category"));
        type.setCellValueFactory(new PropertyValueFactory<EventListItem, Constants.EventType>("type"));
        amount.setCellValueFactory(new PropertyValueFactory<EventListItem, ObjectProperty<Integer>>("amount"));
        id.setCellValueFactory(new PropertyValueFactory<EventListItem, String>("id"));
        ObservableList<EventListItem> data = FXCollections.observableArrayList();

//        EventListItem event = new EventListItem();
//        event.setId("TEST");
//        data.add(event);
//
//        event = new EventListItem();
//        event.setId("BURST_1");
//        data.add(event);
//
//        eventTableView.getItems().setAll(data);

        setTestDataBarChart();
    }

    public void addEvent(EventListItem event){
        eventTableView.getItems().add(event);
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