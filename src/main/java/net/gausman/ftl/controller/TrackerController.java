package net.gausman.ftl.controller;

import net.blerf.ftl.parser.MysteryBytes;
import net.blerf.ftl.parser.SavedGameParser;
import net.gausman.ftl.model.RunUpdateResponse;
import net.gausman.ftl.model.ShipStatusModel;
import net.gausman.ftl.model.record.Event;
import net.gausman.ftl.model.table.EventFilter;
import net.gausman.ftl.model.table.EventTableModel;
import net.gausman.ftl.service.RunService;
import net.gausman.ftl.util.FileWatcher;
import net.gausman.ftl.util.GausmanUtil;
import net.gausman.ftl.view.table.EventTablePanel;
import net.gausman.ftl.view.TrackerView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.Timer;

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


    private List<Event> events;
    private EventTableModel eventTableModel;
    private EventTablePanel eventTablePanel;

    private static int saveFileId = 0;

    public TrackerController() {
        runService = new RunService();
//        model = new StatsModel();
        view = new TrackerView();

        eventTableModel = new EventTableModel();

        eventTablePanel = new EventTablePanel(eventTableModel);
        view.setEventTablePanel(eventTablePanel);

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

        for (EventFilter filter : EventFilter.values()){
            eventFilterMap.put(filter, true);
        }

        for (Map.Entry<EventFilter, JCheckBox> entry : view.getEventFilterPanel().getFilterJCheckBoxMap().entrySet()){
            EventFilter filter = entry.getKey();
            JCheckBox box = entry.getValue();
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
                    int eventId = (int) table.getValueAt(selected, 10);
                    ShipStatusModel model = runService.getStatusAtId(eventId);
                    view.getShipStatusPanel().update(model);
                }
            }
        });

//        eventTablePanel.getTable().addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                int row = eventTablePanel.getTable().rowAtPoint(e.getPoint());
//
//                Integer eventId = (Integer) eventTablePanel.getTable().getValueAt(row, 10);
//                System.out.println("click");
//
//            }
//        });

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

//        eventTableModel.setEvents(runService.getEventMapFlat());

    }



}
