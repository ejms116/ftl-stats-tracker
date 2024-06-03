package net.gausman.ftl.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.xml.bind.JAXBException;
import net.blerf.ftl.core.EditorConfig;
import net.blerf.ftl.model.sectortree.SectorDot;
import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.DefaultDataManager;
import net.blerf.ftl.parser.MysteryBytes;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.random.NativeRandom;
import net.blerf.ftl.parser.sectortree.RandomSectorTreeGenerator;
import net.gausman.ftl.FTLStatsTrackerApplication;
import net.gausman.ftl.FTLStatsTrackerController;
import net.gausman.ftl.model.run.FTLJump;
import net.gausman.ftl.model.run.FTLRun;
import net.gausman.ftl.model.run.FTLRunEvent;
import net.gausman.ftl.util.FileWatcher;
import net.gausman.ftl.view.EventListItem;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StatsManager {
    private static final Logger log = LoggerFactory.getLogger(StatsManager.class);
    private static final String currentRunFilename = "current_run.json";
    private static final String runHistoryFoldername = "runs";
    public final String datPath = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\FTL Faster Than Light"; // TODO make user prompt for this
    private DataManager manager;

    private FTLEventGenerator eventGenerator = new FTLEventGenerator();

    public static ArrayList<SavedGameParser.SavedGameState> gameStateArray = new ArrayList<SavedGameParser.SavedGameState>();
    public static ArrayList<SavedGameParser.ShipState> shipStateArray = new ArrayList<SavedGameParser.ShipState>();
    public static ArrayList<SavedGameParser.ShipState> nearbyShipStateArray = new ArrayList<SavedGameParser.ShipState>();
    public static ArrayList<List<SavedGameParser.CrewState>> playerCrewArray = new ArrayList<List<SavedGameParser.CrewState>>();
    public static ArrayList<List<SavedGameParser.CrewState>> enemyCrewArray = new ArrayList<List<SavedGameParser.CrewState>>();
    public static ArrayList<SavedGameParser.EnvironmentState> environmentArray = new ArrayList<SavedGameParser.EnvironmentState>();
    public static ArrayList<SectorDot> sectorArray = new ArrayList<SectorDot>();

    public final String savePath = "C:\\Users\\erikj\\Documents\\My Games\\FasterThanLight\\continue.sav";
    public final File chosenFile = new File(savePath);

    private TimerTask task;
    private Timer timer;

    private boolean toggleTracking = false;

    private SavedGameParser.SavedGameState lastGameState = null;
    private SavedGameParser parser = new SavedGameParser();

    private FTLStatsTrackerController controller;
    private FTLRun currentRun;
    private List<FTLRun> runList = new ArrayList<>();
    private FTLJump currentJump;
    private int jumpNumber = 0;
    ObjectMapper mapper = new ObjectMapper();


    public void setToggleTracking(){
        toggleTracking = !toggleTracking;
        log.info("Stats/File tracking: " + toggleTracking);
    }

    public StatsManager(FTLStatsTrackerController ftlStatsTrackerController){
        mapper.registerModule(new JavaTimeModule());
        log.info("JavaTimeModule registered");
        this.controller = ftlStatsTrackerController;
        setupFileWatcher();
        log.info("File Watcher setup complete...");

        currentRun = readFTLRunFromJSON(new File(currentRunFilename));
        if (currentRun != null){
            // add events to GUI
            for (FTLJump jump: currentRun.getJumpList()){
                for (FTLRunEvent event: jump.getEvents()){
                    controller.addEvent(new EventListItem(currentRun, jump, event));
                }
            }
            currentJump = currentRun.getJumpList().getLast();
            log.info("Found ongoing run to continue"); // TODO error handling when there is a diffrence between json/sav files
        }

        System.out.println(FTLStatsTrackerApplication.appConfig.getProperty(EditorConfig.FTL_DATS_PATH));
        //EditorConfig config = new EditorConfig();
    }

    public void setupFileWatcher(){
        task = new FileWatcher( chosenFile ) {
            protected void onChange( File file ) {
                if (toggleTracking == true) {
                    if (chosenFile.exists()){
                        log.info( "FILE "+ file.getName() +" HAS CHANGED" );
                        loadGameStateFile(chosenFile);
                    } else {
                        log.info( "FILE "+ file.getName() +" WAS DELETED" );
                    }

                }
            }
        };
        timer = new Timer();
        timer.schedule( task , new Date(), 100 );
    }

    public void shutdownFileWatcher(){
        timer.cancel();
    }

    public void loadGameStateFile(File file) {

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
            handleGameState(gs);
            log.trace( "Game state read successfully." );

            if ( lastGameState.getMysteryList().size() > 0 ) {
                StringBuilder musteryBuf = new StringBuilder();
                musteryBuf.append("This saved game file contains mystery bytes the developers hadn't anticipated!\n");
                boolean first = true;
                for (MysteryBytes m : lastGameState.getMysteryList()) {
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

    private FTLRun readFTLRunFromJSON(File file){
        if (!file.exists()){
            return null;
        }
        try {
            return mapper.readValue(new File(file.getAbsolutePath()), FTLRun.class);
        } catch (IOException e){
            log.error("No file to continue found");
        }
        return null;
    }

    private void handleGameState(SavedGameParser.SavedGameState currentGameState){
        // TEST
//        DataManager dm = DataManager.get();
//        WeaponBlueprint w = dm.getWeapon("LASER_BURST_3");
//        WeaponBlueprint w2 = dm.getWeapon("LASER_HEAVY_1");
//        //DroneBlueprint d1 = dm.getDrone("")
//        ShipBlueprint blueprint = dm.getShip("PLAYER_SHIP_HARD");

        // TEST

        // check if new run was started
        if (currentRun == null || currentRun.getSectorTreeSeed() != currentGameState.getSectorTreeSeed()){
            if (currentRun != null){
                Path currentPath = Paths.get(currentRunFilename);
                Path targetPath = Paths.get(currentRun.generateFileNameForRun());
                try {
                    Files.move(currentPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    //throw new RuntimeException(e);
                    System.out.println("cant move file");
                }

                log.info("Stats file moved to runs folder");
            }
            currentRun = new FTLRun(currentGameState);
            jumpNumber = 0;
            currentJump = new FTLJump(currentGameState, jumpNumber);
            currentRun.addJump(currentJump);

            File newSaveDir = new File(currentRun.generateFolderNameForSave());
            newSaveDir.mkdirs();

            copySaveFile(currentGameState);

            //currentJump.addEvents(eventGenerator.getEventsStartRun(currentGameState));
            addEventsAll(eventGenerator.getEventsStartRun(currentGameState));
        }

        // at this point currentJump is never null, so we can safely add events and check it's state
        // check if new jump
        if (currentJump.getCurrentBeaconId() != currentGameState.getCurrentBeaconId()){
            // add a dummy event in case the jump doesn't contain any events
            if (currentJump.getEvents().size() == 0){
                FTLRunEvent testEvent = new FTLRunEvent();
                testEvent.setId("DUMMY EMPTY JUMP");
                currentJump.getEvents().add(testEvent);
            }

            jumpNumber++;
            currentJump = new FTLJump(currentGameState, jumpNumber);
            currentRun.addJump(currentJump);
            copySaveFile(currentGameState);
        }


        // currentJump is a reference to the latest jump that was made, it's already added to the jumpList in the Run
        addEventsAll(eventGenerator.getEventsFromGameStateComparison(lastGameState, currentGameState));

        // save to json file
        try {
            mapper.writeValue(new File(currentRunFilename), currentRun);
        } catch (IOException e){
            throw new RuntimeException();
        }

        lastGameState = currentGameState;

        log.info( "Ship Name: " + currentGameState.getPlayerShipName());
        log.info( "Total beacons explored: " + currentGameState.getTotalBeaconsExplored());
        log.info( "Currently at beacon Id: " + currentGameState.getCurrentBeaconId());
        log.info( "Jump number: " + jumpNumber);
        log.info( "Currently in sector: " + currentGameState.getSectorNumber() + 1);
        log.info( "----------------------------------------------------------------");

    }

    private void addEventsAll(List<FTLRunEvent> events){
        currentJump.addEvents(events);
        for (FTLRunEvent event: events){
            controller.addEvent(new EventListItem(currentRun, currentJump, event));
        }
    }

    private void copySaveFile(SavedGameParser.SavedGameState currentGameState){
        try {
            String fName = currentRun.generateFileNameForSave(jumpNumber, currentJump.getSectorNumber());
            FileOutputStream out = new FileOutputStream(fName);
            parser.writeSavedGame(out, currentGameState);

        } catch (IOException e){

        }

//        Path localSavePath = Paths.get(savePath);
//        Path targetSavePath = Paths.get(currentRun.generateFileNameForSave(jumpNumber, currentJump.getSectorNumber()));
//        try {
//            Files.copy(localSavePath, targetSavePath);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    // deprecated
    public void loadGameState (SavedGameParser.SavedGameState currentGameState) {
        log.info( "------" );
        log.info( "Ship Name : " + currentGameState.getPlayerShipName() );
        log.info( "Currently at beacon number : " + currentGameState.getTotalBeaconsExplored() );
        log.info( "Currently in sector : " + currentGameState.getSectorNumber() + 1 );

        if (gameStateArray.isEmpty() ||
                currentGameState.getTotalBeaconsExplored() > lastGameState.getTotalBeaconsExplored()
        ) {
            gameStateArray.add(currentGameState);
            shipStateArray.add(currentGameState.getPlayerShip());
            nearbyShipStateArray.add(currentGameState.getNearbyShip());
            environmentArray.add(currentGameState.getEnvironment());

            ArrayList<SavedGameParser.CrewState> currentPlayerCrew = new ArrayList<SavedGameParser.CrewState>();
            ArrayList<SavedGameParser.CrewState> currentEnemyCrew = new ArrayList<SavedGameParser.CrewState>();
            for (int i = 0; i < currentGameState.getPlayerShip().getCrewList().size(); i++) {
                if (currentGameState.getPlayerShip().getCrewList().get(i).isPlayerControlled()) {
                    currentPlayerCrew.add(currentGameState.getPlayerShip().getCrewList().get(i));
                } else {
                    currentEnemyCrew.add(currentGameState.getPlayerShip().getCrewList().get(i));
                }
            }
            if (currentGameState.getNearbyShip() != null) {
                for (int i = 0; i < currentGameState.getNearbyShip().getCrewList().size(); i++) {
                    if (currentGameState.getNearbyShip().getCrewList().get(i).isPlayerControlled()) {
                        currentPlayerCrew.add(currentGameState.getNearbyShip().getCrewList().get(i));
                    } else {
                        currentEnemyCrew.add(currentGameState.getNearbyShip().getCrewList().get(i));
                    }
                }
            }
            playerCrewArray.add(currentPlayerCrew);
            enemyCrewArray.add(currentEnemyCrew);

            sectorArray.clear();
            RandomSectorTreeGenerator myGen = new RandomSectorTreeGenerator( new NativeRandom() );
            List<List<SectorDot>> myColumns = myGen.generateSectorTree(
                    currentGameState.getSectorTreeSeed(),
                    currentGameState.isDLCEnabled()
            );
        }

        lastGameState = currentGameState;
    }

}
