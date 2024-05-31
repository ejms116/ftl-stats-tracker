package net.gausman.ftl.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.xml.bind.JAXBException;
import net.blerf.ftl.model.sectortree.SectorDot;
import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.DefaultDataManager;
import net.blerf.ftl.parser.MysteryBytes;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.random.NativeRandom;
import net.blerf.ftl.parser.sectortree.RandomSectorTreeGenerator;
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
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StatsManager {
    private static final Logger log = LoggerFactory.getLogger(StatsManager.class);
    public final String datPath = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\FTL Faster Than Light";
    public final File datFile = new File(datPath);
    public DataManager manager;

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

    private FTLStatsTrackerController controller;
    private FTLRun currentRun;
    private List<FTLRun> runList = new ArrayList<>();
    private FTLJump currentJump;
    private int jumpNumber = 0;
    ObjectMapper mapper = new ObjectMapper();
    public void setToggleTracking(){
        toggleTracking = !toggleTracking;
        System.out.println(toggleTracking);
    }

    private void setupDataManager(){
        try {
            manager = new DefaultDataManager(datFile);
            DataManager.setInstance(manager);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        } catch (JDOMException e) {
            throw new RuntimeException(e);
        }
    }

    public void setupFileWatcher(){
        task = new FileWatcher( chosenFile ) {
            protected void onChange( File file ) {
                if (toggleTracking == true) {
                    log.info( "\nFILE "+ file.getName() +" HAS CHANGED !" );
                    if (chosenFile.exists()){
                        loadGameStateFile (chosenFile);
                    }

                }
            }
        };
        timer = new Timer();
        timer.schedule( task , new Date(), 100 );
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
            loadGameState(gs);
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



    public void loadGameState (SavedGameParser.SavedGameState currentGameState) {
        // TEST
//        DataManager dm = DataManager.get();
//        WeaponBlueprint w = dm.getWeapon("LASER_BURST_3");
//        WeaponBlueprint w2 = dm.getWeapon("LASER_HEAVY_1");
//        //DroneBlueprint d1 = dm.getDrone("")
//        ShipBlueprint blueprint = dm.getShip("PLAYER_SHIP_HARD");

        // TEST

        log.info( "------" );
        log.info( "Ship Name : " + currentGameState.getPlayerShipName() );
        log.info( "Currently at beacon number : " + currentGameState.getTotalBeaconsExplored() );
        log.info( "Currently in sector : " + currentGameState.getSectorNumber() + 1 );

        // check if new run was started
        if (currentRun == null || currentRun.getSectorTreeSeed() != currentGameState.getSectorTreeSeed()){
            currentRun = new FTLRun(currentGameState);
            jumpNumber = 0;
            currentJump = new FTLJump(currentGameState, jumpNumber);
            currentJump.addEvents(eventGenerator.getEventsStartRun(currentGameState));
        }

        // check if new jump
        if (currentJump.getCurrentBeaconId() != currentGameState.getCurrentBeaconId()){
            if (currentJump.getEvents().size() == 0){
                FTLRunEvent testEvent = new FTLRunEvent();
                testEvent.setId("TEST");
                currentJump.getEvents().add(testEvent);
            }
            // add events to GUI
            for (FTLRunEvent event: currentJump.getEvents()){
                controller.addEvent(new EventListItem(currentRun, currentJump, event));
            }
            currentRun.addJump(currentJump);
            jumpNumber++;
            currentJump = new FTLJump(currentGameState, jumpNumber);
        }

        currentJump.addEvents(eventGenerator.getEventsFromGameStateComparison(lastGameState, currentGameState));

        // save to json file
        try {
            Path dir = Paths.get("runs");
            String pathImport = "runs/" + "current_run" + ".json";

            mapper.writeValue(new File(pathImport), currentRun);
        } catch (IOException e){
            throw new RuntimeException();
        }


        // old logic
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


    public void init(FTLStatsTrackerController ftlStatsTrackerController) {
        mapper.registerModule(new JavaTimeModule());
        this.controller = ftlStatsTrackerController;
        setupFileWatcher();
        log.info("File Watcher setup complete...");
    }

    public void shutdownFileWatcher(){
        timer.cancel();
    }
}
