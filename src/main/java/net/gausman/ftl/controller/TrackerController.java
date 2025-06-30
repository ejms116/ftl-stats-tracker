package net.gausman.ftl.controller;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.MysteryBytes;
import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.RunUpdateResponse;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.table.EventFilter;
import net.gausman.ftl.model.table.EventTableModel;
import net.gausman.ftl.service.RunService;
import net.gausman.ftl.util.FileWatcher;
import net.gausman.ftl.util.GausmanUtil;
import net.gausman.ftl.view.browser.EventBrowserView;
import net.gausman.ftl.view.TrackerView;
import net.gausman.ftl.view.browser.EventTreeBrowserView;
import net.gausman.ftl.view.table.EventTablePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.*;

public class TrackerController {
    private static final Logger log = LoggerFactory.getLogger(TrackerController.class);

    public final String savePath = "C:\\Users\\erikj\\Documents\\My Games\\FasterThanLight\\continue.sav";
    public final File chosenFile = new File(savePath);

    private FileWatcher task;
    private Timer timer;

    private SavedGameParser parser = new SavedGameParser();


    private RunService runService;

    private Map<EventFilter, Boolean> eventFilterMap = new EnumMap<>(EventFilter.class);
    private boolean toggleTracking = false;



//    private StatsModel model;
    private TrackerView view;
    private EventBrowserView eventBrowserView;
    private EventTreeBrowserView eventTreeBrowserView;

    private List<Event> events;
    private EventTableModel eventTableModel;
    private EventTablePanel eventTablePanel;

    private static int saveFileId = 0;

    private DataManager dm = DataManager.get(); // todo remove from here

    public TrackerController() {

//        model = new StatsModel();

        view = new TrackerView();
        runService = new RunService();


        eventTableModel = new EventTableModel();

        eventTablePanel = new EventTablePanel(eventTableModel);
        view.setEventTablePanel(eventTablePanel);

        // >>> NEW CODE: Open window on second monitor if available
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        if (screens.length > 1) {
            // Get bounds of second screen
            Rectangle bounds = screens[0].getDefaultConfiguration().getBounds();
            // Position the window near the top-left of the second screen
            view.setLocation(bounds.x + 50, bounds.y + 50);
        } else {
            // Center on primary screen
            view.setLocationRelativeTo(null);
        }
        // <<< END NEW CODE

        view.setVisible(true);

        setupListeners();
        setupFileWatcher();


    }

    private void setupListeners(){
        view.getToolbarPanel().setTrackingToggleListener(e -> {
            toggleTracking = !toggleTracking;
            log.info("Stats/File tracking: " + toggleTracking);
            view.getToolbarPanel().setTrackingToggleState(toggleTracking);
            if (toggleTracking){
                task.onChange(chosenFile);
            }
        });

        view.getToolbarPanel().setTestButtonListener(e -> {
            testSaveFileReading();
        });

        view.getToolbarPanel().setEventBrowserButtonListener(e -> {
            if (eventBrowserView == null || !eventBrowserView.isDisplayable()){
                eventBrowserView = new EventBrowserView(dm.getEventNodeIdMap(), dm.getDlcTextListIdMap());
                eventBrowserView.setVisible(true);
            } else {
                eventBrowserView.toFront();
                eventBrowserView.requestFocus();
            }
        });

        view.getToolbarPanel().setEventTreeBrowserButtonListener(e -> {
            showEventTreeBrowserView();
        });
        
        view.getToolbarPanel().setOpenEventInBrowserButton(e -> {
            showEventTreeBrowserView();
            int selected = eventTablePanel.getTable().getSelectedRow();
            if (selected != -1){
                Event event = eventTableModel.getRowEvent(selected);
                if (event != null){
                    List<SavedGameParser.EncounterState> encounterStates = event.getJump().getEncounterStates();
                    if (encounterStates != null && !encounterStates.isEmpty()){
                        String encounterText = encounterStates.getFirst().getText();
                        eventTreeBrowserView.selectEventById(GausmanUtil.extractId(encounterText));
                        eventTreeBrowserView.toFront();
                        eventTreeBrowserView.requestFocus();
                    }

                }
            }
        });

        for (EventFilter filter : EventFilter.values()){
            eventFilterMap.put(filter, false);
        }

        for (Map.Entry<EventFilter, JCheckBoxMenuItem> entry : view.getToolbarPanel().getFilterJCheckBoxMap().entrySet()){
            EventFilter filter = entry.getKey();
            JCheckBoxMenuItem box = entry.getValue();
            box.addActionListener(e -> {
                eventFilterMap.put(filter, box.isSelected());
                eventTablePanel.updateRowFilter(eventFilterMap);
            });
        }

        eventTablePanel.updateRowFilter(eventFilterMap);

        JTable table = eventTablePanel.getTable();
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()){
                int selected = table.getSelectedRow();
                if (selected != -1){
                    int eventId = eventTableModel.getRowEvent(selected).getId();
                    ShipStatusModel model = runService.getStatusAtId(eventId);
                    view.getShipStatusPanel().update(model);
                    view.getEventTablePanel().updateJumpInfoPanel(eventTableModel.getRowEvent(selected).getJump());
                }
            }
        });

    }

    private void showEventTreeBrowserView() {
        if (eventTreeBrowserView == null || !eventTreeBrowserView.isDisplayable()){
            eventTreeBrowserView = new EventTreeBrowserView(dm.getEventNodeIdMap(), dm.getDlcTextListIdMap(), dm.getShipEvents());
            eventTreeBrowserView.setVisible(true);
        } else {
            eventTreeBrowserView.toFront();
            eventTreeBrowserView.requestFocus();
        }
    }

    private void setupFileWatcher(){
        task = new FileWatcher(chosenFile) {
            public void onChange( File file ) {
                if (toggleTracking == true) {
                    if (chosenFile.exists()){
                        log.info( "FILE "+ file.getName() +" HAS CHANGED" );
                        loadGameStateFile(chosenFile);
                    } else {
                        log.info( "FILE "+ file.getName() +" NOT FOUND" ); // TODO prompt when starting a new run
                        moveCurrentJSON();
                    }

                }
            }
        };
        timer = new Timer();
        timer.schedule( task , new Date(), 100 );
        log.info("File Watcher setup complete...");
    }

    private void loadGameStateFile(File file) {

        FileInputStream in = null;
        StringBuilder hexBuf = new StringBuilder();
        Exception exception = null;

        try {
            log.info( "Opening game state: "+ file.getAbsolutePath() );

            in = new FileInputStream( file );

            // Read the content in advance, in case an error ocurs.
            byte[] buf = new byte[4096];
            int len = 0;
            while ( (len = in.read(buf)) >= 0 ) {
                for (int j=0; j < len; j++) {
                    hexBuf.append( String.format( "%02x", buf[j] ) );
                    if ( (j+1) % 32 == 0 ) {
                        hexBuf.append( "\n" );
                    }
                }
            }
            in.getChannel().position( 0 );

            SavedGameParser parser = new SavedGameParser();
            SavedGameParser.SavedGameState gs = parser.readSavedGame(in);
            RunUpdateResponse runUpdateResponse = runService.update(gs);
            if (runUpdateResponse.newRun()){
                eventTableModel.setStartTime(runService.getCurrentRun().getStartTime());
                eventTableModel.setEvents(runService.getEventMapFlat());
            }
            eventTableModel.fireTableDataChanged(); // optimize
            SwingUtilities.invokeLater(() -> {
                if (eventTablePanel.getTable().getRowCount() > 0) {
                    eventTablePanel.getTable().changeSelection(0, 0, false, false);
                }
            });
            log.info("Game state read successfully.");

            if (!gs.getMysteryList().isEmpty()) {
                StringBuilder musteryBuf = new StringBuilder();
                musteryBuf.append("This saved game file contains mystery bytes the developers hadn't anticipated!\n");
                boolean first = true;
                for (MysteryBytes m : gs.getMysteryList()) {
                    if (first) { first = false; }
                    else { musteryBuf.append( ",\n" ); }
                    musteryBuf.append(m.toString().replaceAll( "(^|\n)(.+)", "$1  $2") );
                }
                log.warn( musteryBuf.toString() );
            }
//		} catch ( FileNotFoundException f ) {
//			showErrorDialog( String.format(
//					"Save file was not found")
//				);
        } catch ( Exception f ) {
            log.error( String.format("Error reading saved game (\"%s\").", chosenFile.getName()), f );
//            showErrorDialog( String.format(
//                    "Error reading saved game (\"%s\"):\n%s: %s\n" +
//                            "This error is probably caused by a game-over or the restarting of a game.\n" +
//                            "If not, please report this on the %s GitHub Issue page.\n" +
//                            "You can still save the graph by pressing the Export button.\n" +
//                            "Restart %s to reset everything.",
//                    chosenFile.getName(), f.getClass().getSimpleName(), f.getMessage(), appName, appName )
//            );

            exception = f;
        }


        finally {
            try { if ( in != null ) in.close(); }
            catch ( IOException f ) {}
        }
    }

    private void copySaveFile(SavedGameParser.SavedGameState currentGameState){
        Instant now = Instant.now();
        try {
            String fName = GausmanUtil.generateFileNameForSave(now, "TestRun", saveFileId, currentGameState.getSectorNumber());
            FileOutputStream out = new FileOutputStream(fName);
            parser.writeSavedGame(out, currentGameState);
            saveFileId++;

        } catch (IOException e){
            log.error("Error copying save file");
        }

//        Path localSavePath = Paths.get(savePath);
//        Path targetSavePath = Paths.get(currentRun.generateFileNameForSave(jumpNumber, currentJump.getSectorNumber()));
//        try {
//            Files.copy(localSavePath, targetSavePath);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }


    private void moveCurrentJSON(){
        log.info("Stats file moved to runs folder");
    }

    private void testSaveFileReading(){
        File folder = new File("saves\\test");

        if (!folder.isDirectory()) {
            System.out.println("Not a directory.");
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.matches("^\\d+.*")); // only files starting with numbers

        if (files == null) {
            System.out.println("Could not read folder contents.");
            return;
        }

        Arrays.sort(files, Comparator.comparingInt(file -> GausmanUtil.extractLeadingNumber(file.getName())));

        for (File file : files) {
            long lastModified = file.lastModified();
            Date date = new Date(lastModified);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            System.out.println(file.getName() + " - Last modified: " + sdf.format(date));
            log.info(file.getName() + " - Last modified: " + sdf.format(date));
            // Here logic
            loadGameStateFile(file);

        }

        runService.saveRunToJson();
        log.info("Testing done");

//        eventTableModel.setEvents(runService.getEventMapFlat());

    }



}
