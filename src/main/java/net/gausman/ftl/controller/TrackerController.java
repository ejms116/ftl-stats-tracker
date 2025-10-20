package net.gausman.ftl.controller;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.MysteryBytes;
import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.RunUpdateResponse;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.change.Event;
import net.gausman.ftl.model.table.EventTableModel;
import net.gausman.ftl.service.RunService;
import net.gausman.ftl.util.FileWatcher;
import net.gausman.ftl.util.GausmanUtil;
import net.gausman.ftl.view.TrackerView;
import net.gausman.ftl.view.browser.EventTreeBrowserView;
import net.gausman.ftl.view.eventtable.EventTablePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.*;

import static net.gausman.ftl.service.RunService.CURRENT_RUN_FILENAME;

public class TrackerController {
    private static final Logger log = LoggerFactory.getLogger(TrackerController.class);
    public final File continueSaveFile;

    private FileWatcher task;
    private Timer timer;

    private SavedGameParser parser = new SavedGameParser();


    private RunService runService;
    private boolean toggleTracking = false;

    private TrackerView view;
    private EventTreeBrowserView eventTreeBrowserView;

    private List<Event> events;
    private EventTableModel eventTableModel;
    private EventTablePanel eventTablePanel;

    private static int saveFileId = 0;

    private DataManager dm = DataManager.get();

    public TrackerController(File saveFile, Path runsDir, Path savesDir, RunService.SaveFileCopySetting saveFileCopySetting) {
        continueSaveFile = saveFile;
        view = new TrackerView();
        runService = new RunService(runsDir, savesDir, saveFileCopySetting);


        eventTableModel = new EventTableModel();

        eventTablePanel = new EventTablePanel(eventTableModel);
        view.setEventTablePanel(eventTablePanel);

        openProgramOnSecondMonitorForTesting();

        view.setVisible(true);

        setupListeners();
        setupFileWatcher();

        initUI();

    }

    private void openProgramOnSecondMonitorForTesting(){
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        if (screens.length > 1) {
            // Get bounds of second screen
            Rectangle bounds = screens[0].getDefaultConfiguration().getBounds();
            // Position the window near the top-left of the second screen
            view.setLocation(bounds.x + 10, bounds.y + 10);
        } else {
            // Center on primary screen
            view.setLocationRelativeTo(null);
        }
    }

    private void setupListeners(){
        view.getToolbarPanel().setTrackingToggleListener(e -> {
            toggleTracking = !toggleTracking;
            log.info("Stats/File tracking: " + toggleTracking);
            view.getToolbarPanel().setTrackingToggleState(toggleTracking);
            if (toggleTracking){
                task.onChange(continueSaveFile);
            }
        });

        view.getToolbarPanel().setTestButtonListener(e -> {
            testSaveFileReading();
        });

        view.getToolbarPanel().setEventTreeBrowserButtonListener(e -> {
            showEventTreeBrowserView();
        });

        view.getEventTablePanel().setOpenEventInBrowserButton(e -> {
            int selected = eventTablePanel.getTable().getSelectedRow();
            if (selected != -1) {
                int modelRow = eventTablePanel.getTable().convertRowIndexToModel(selected);
                Event event = eventTableModel.getRowEvent(modelRow);
                showEventTreeBrowserView();
                if (event != null){
                    List<SavedGameParser.EncounterState> encounterStates = event.getJump().getEncounterStates();
                    if (encounterStates != null && !encounterStates.isEmpty()){
                        String encounterText = encounterStates.getFirst().getText();
                        eventTreeBrowserView.selectEventById(GausmanUtil.extractId(encounterText), encounterStates);
                        eventTreeBrowserView.toFront();
                        eventTreeBrowserView.requestFocus();
                    }
                }
            }
        });

        view.getEventTablePanel().setJumpToNewestEventButton(e -> {
            selectNewestRow();
        });


        JTable table = eventTablePanel.getTable();
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()){
                int selected = table.getSelectedRow();
                if (selected != -1){
                    int modelRow = table.convertRowIndexToModel(selected);
                    int eventId = eventTableModel.getRowEvent(modelRow).getId();
                    eventTablePanel.showOrHideJumpToNewestEventButton(!eventTableModel.getNewestEventId().equals(eventId));
                    updateUI(eventId);
                }
            }
        });
    }

    private void selectNewestRow(){
        JTable table = eventTablePanel.getTable();
//        if (table.getRowCount() == 0){
//            return;
//        }
        int newestModelRow = 0; // eventTableModel.getNewestEventId();
        int viewRow = -1;

        try {
            viewRow = table.convertRowIndexToView(newestModelRow);
        } catch (IndexOutOfBoundsException ignored){

        }

        eventTablePanel.showOrHideJumpToNewestEventButton(false);
        updateUI(eventTableModel.getNewestEventId());

        if (viewRow > -1){
            table.setRowSelectionInterval(viewRow, viewRow);
        } else {
            table.clearSelection();
        }

        if (table.getRowCount() > 0){
            table.scrollRectToVisible(table.getCellRect(0,0,true));
        }
    }

    private void initUI(){
        if (runService.getCurrentRun() == null){
            return;
        }
        ShipStatusModel model = runService.getNewestStatus();
        eventTableModel.setStartTime(runService.getCurrentRun().getStartTime());
        eventTableModel.setEvents(runService.getEventMapFlat());
        view.getShipStatusPanel().update(model);
    }

    private void updateUI(int eventId){
        ShipStatusModel model = runService.getStatusAtId(eventId);
        view.getShipStatusPanel().update(model);
        view.getEventTablePanel().updateJumpInfoPanel(eventTableModel.getEventById(eventId).getJump());
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
        task = new FileWatcher(continueSaveFile) {
            public void onChange( File file ) {
                if (toggleTracking) {
                    if (continueSaveFile.exists()){
                        log.info( "FILE "+ file.getName() +" HAS CHANGED" );
                         loadGameStateFile(continueSaveFile, true);
                    } else {
                        log.info( "FILE "+ file.getName() +" NOT FOUND" ); // TODO prompt when starting a new run
                    }

                }
            }
        };
        timer = new Timer();
        timer.schedule( task , new Date(), 100 );
        log.info("File Watcher setup complete...");
    }

    private void loadGameStateFile(File file, boolean updateUI) {

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
            RunUpdateResponse runUpdateResponse = runService.update(gs, file);
            if (runUpdateResponse.newRun()){
                eventTableModel.setStartTime(runService.getCurrentRun().getStartTime());
                eventTableModel.setEvents(runService.getEventMapFlat());
            }
            if (updateUI){
                updateUI();
            }

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
            log.error( String.format("Error reading saved game (\"%s\").", continueSaveFile.getName()), f );
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

    private void updateUI(){
        eventTableModel.fireTableDataChanged(); // optimize
        SwingUtilities.invokeLater(() -> {
            if (eventTablePanel.getTable().getRowCount() > 0) {
//                    eventTablePanel.getTable().changeSelection(0, 0, false, false);
                selectNewestRow();
            }
        });
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

    }



    private void testSaveFileReading(){
        File folder = new File("saves\\test-gausman-mantis-a");

        if (!folder.isDirectory()) {
            System.out.println("Not a directory.");
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.matches("^\\d+.*")); // only files starting with numbers

        if (files == null) {
            System.out.println("Could not read folder contents.");
            return;
        }

//        Arrays.sort(files, Comparator.comparingInt(file -> GausmanUtil.extractLeadingNumber(file.getName())));
        Arrays.sort(files, Comparator.comparingInt(file -> GausmanUtil.extractNumberAfterHyphen(file.getName())));

        for (File file : files) {
            long lastModified = file.lastModified();
            Date date = new Date(lastModified);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            log.info(file.getName() + " - Last modified: " + sdf.format(date));
            loadGameStateFile(file, false);
        }
        updateUI();
        runService.saveRunToJson(new File(CURRENT_RUN_FILENAME));
        log.info("Testing done");
    }



}
