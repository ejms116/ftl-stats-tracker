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
import net.gausman.ftl.model.Constants;
import net.gausman.ftl.model.FTLEventBox;
import net.gausman.ftl.model.ShipStatus;
import net.gausman.ftl.model.run.FTLJump;
import net.gausman.ftl.model.run.FTLRun;
import net.gausman.ftl.model.run.FTLRunEvent;
import net.gausman.ftl.model.run.FTLSector;
import net.gausman.ftl.util.FileWatcher;
import net.gausman.ftl.util.GausmanUtil;
import net.gausman.ftl.view.EventListItem;
import net.gausman.ftl.view.OverviewListItem;
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

    private FileWatcher task;
    private Timer timer;

    private boolean toggleTracking = false;

    private SavedGameParser.SavedGameState lastGameState = null;
    private SavedGameParser parser = new SavedGameParser();

    private FTLStatsTrackerController controller;
    private FTLRun currentRun;
    private List<FTLRun> runList = new ArrayList<>();
    private FTLJump currentJump;
    private FTLJump lastJump;
    private int jumpNumber = 0;
    ObjectMapper mapper = new ObjectMapper();

    private ShipStatus shipStatus = new ShipStatus();

    public void setToggleTracking(){
        toggleTracking = !toggleTracking;
        log.info("Stats/File tracking: " + toggleTracking);
        if (toggleTracking){
            task.onChange(chosenFile);
        }
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
            for (FTLSector sector: currentRun.getSectorList()) {
                for (FTLJump jump : sector.getJumpList()) {
                    for (FTLRunEvent event : jump.getEvents()) {
                        controller.addEvent(new EventListItem(currentRun, sector, jump, event));
                    }
                }
            }
            currentJump = currentRun.getSectorList().getLast().getJumpList().getLast();
            log.info("Found ongoing run to continue"); // TODO error handling when there is a diffrence between json/sav files
        }

        System.out.println(FTLStatsTrackerApplication.appConfig.getProperty(EditorConfig.FTL_DATS_PATH));
        //EditorConfig config = new EditorConfig();
    }

    public void setupFileWatcher(){
        task = new FileWatcher( chosenFile ) {
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
//        System.out.println(SavedGameParser.StoreItemType.AUGMENT); // Augment
//        System.out.println(SavedGameParser.StoreItemType.AUGMENT.name()); // AUGMENT
//        System.out.println(SavedGameParser.StoreItemType.AUGMENT.toString()); // Augment

        // check if new run was started
        if (currentRun == null || currentRun.getSectorTreeSeed() != currentGameState.getSectorTreeSeed() ||
                (lastGameState != null && lastGameState.getTotalBeaconsExplored() > currentGameState.getTotalBeaconsExplored())){
            lastGameState = null;
            controller.startNewRun();
            shipStatus = new ShipStatus();
            moveCurrentJSON();
            currentRun = new FTLRun(currentGameState);
            jumpNumber = 0;
            FTLRunEvent.nextEventNumber = 0;
            currentJump = new FTLJump(currentGameState, jumpNumber);
            currentRun.addJump(currentJump);

            File newSaveDir = new File(currentRun.generateFolderNameForSave());
            newSaveDir.mkdirs();

            copySaveFile(currentGameState);
            addEventsAll(eventGenerator.getEventsStartRun(currentGameState));
        }

        // if we are in a new sector we want to add stores to the new sector and also initialize the sector object
        if (currentRun.getSectorList().getLast().getSectorNumber() != currentGameState.getSectorNumber() + 1){
            currentRun.addSector(currentGameState);
        }

        currentRun.getSectorList().getLast().setBeaconList(currentGameState.getBeaconList());

        // at this point currentJump is never null, so we can safely add events and check it's state
        // check if new jump
        if (currentJump.getCurrentBeaconId() != currentGameState.getCurrentBeaconId()){

            // When backtracking the game does not necessarily save the game
            // so we need to compare the beacons explored and for create "empty jumps" (just a fuel-used event)
            // the amount is the difference between the new and last beacons explored stats minus 1 (for the new jump)
            int beaconsExploredDiff = currentGameState.getTotalBeaconsExplored() - currentJump.getTotalBeaconsExplored();
            if (beaconsExploredDiff > 1){
                int beaconsExploredTemp = currentJump.getTotalBeaconsExplored();
                for (int i = 0; i < beaconsExploredDiff-1; i++){
                    beaconsExploredTemp++;
                    jumpNumber++;
                    currentJump = new FTLJump(beaconsExploredTemp, -1, jumpNumber);
                    currentRun.addJump(currentJump);
                    addEventsAll(eventGenerator.getFuelUsedEventBox());
                }
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
            log.error("error wring json file");
            System.out.println(e);
            //throw new RuntimeException();
        }

        // update overview
        controller.replaceOverviewList(eventGenerator.getOverviewList(currentGameState, jumpNumber));

        lastJump = currentJump;
        lastGameState = currentGameState;

        log.info( "Ship Name: " + currentGameState.getPlayerShipName());
        log.info( "Total beacons explored: " + currentGameState.getTotalBeaconsExplored());
        log.info( "Currently at beacon Id: " + currentGameState.getCurrentBeaconId());
        log.info( "Jump number: " + jumpNumber);
        int sectorNumberDebug = currentGameState.getSectorNumber() + 1;
        log.info( "Currently in sector : " +  sectorNumberDebug);
        log.info( "----------------------------------------------------------------");

    }

    private void moveCurrentJSON(){
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
    }

    private void addEventsAll(FTLEventBox eventBox){
        currentJump.addEvents(eventBox.getNewJumpEvents());
        if (!eventBox.getLastJumpEvents().isEmpty()){
            if (lastJump != null){
                lastJump.addEvents(eventBox.getLastJumpEvents());
            }
        }
        for (FTLRunEvent event: eventBox.getLastJumpEvents()){
            controller.addEvent(new EventListItem(currentRun, currentRun.getSectorList().getLast(), lastJump, event), true);
        }

        for (FTLRunEvent event: eventBox.getNewJumpEvents()){
            controller.addEvent(new EventListItem(currentRun, currentRun.getSectorList().getLast(), currentJump, event), true);
        }
    }

    private void copySaveFile(SavedGameParser.SavedGameState currentGameState){
        try {
            String fName = currentRun.generateFileNameForSave(jumpNumber, currentRun.getSectorList().getLast().getSectorNumber());
            FileOutputStream out = new FileOutputStream(fName);
            parser.writeSavedGame(out, currentGameState);

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

    public ShipStatus getNewShipStatus(int currentSectorNumber, int targetSectorNumber, int currentJumpNumber, int targetJumpNumber, int currentEventNumber, int targetEventNumber){
        List<FTLJump> jumpList;
        FTLJump jump;
        List<FTLRunEvent> eventList;
        FTLRunEvent event;
        if (currentEventNumber < targetEventNumber){ // FORWARD
            for (int i = currentSectorNumber; i <= targetSectorNumber; i++){
                jumpList = currentRun.getSectorList().get(i-1).getJumpList();
                for (int j = 0; j < jumpList.size(); j++){
                    jump = jumpList.get(j);
                    if (jump.getJumpNumber() > targetJumpNumber || jump.getJumpNumber() < currentJumpNumber){
                        continue;
                    }
                    eventList = jump.getEvents();
                    for (int k = 0; k < eventList.size(); k++){
                    event = eventList.get(k);
                        if (event.getEventNumber() > targetEventNumber || event.getEventNumber() <= currentEventNumber){
                            continue;
                        }
                        shipStatus.applyEvent(event, true);

                    }
                }

            }

        } else if (currentEventNumber > targetEventNumber){ //BACKWARDS
            for (int i = currentSectorNumber; i >= targetSectorNumber; i--){
                jumpList = currentRun.getSectorList().get(i-1).getJumpList();
                for (int j = jumpList.size()-1; j >= 0; j--){
                    jump = jumpList.get(j);
                    if (jump.getJumpNumber() < targetJumpNumber || jump.getJumpNumber() > currentJumpNumber){
                        continue;
                    }
                    eventList = jump.getEvents();
                    for (int k = eventList.size()-1; k >= 0; k--){
                        event = eventList.get(k);
                        if (event.getEventNumber() <= targetEventNumber || event.getEventNumber() > currentEventNumber){
                            continue;
                        }
                        shipStatus.applyEvent(event, false);

                    }
                }
            }
        } else {
            log.info("Ship Status not changed");
        }

        return shipStatus;
    }

    private void applyEvent(){
        System.out.println("apply");
    }

    private void unApplyEvent(){
        System.out.println("undo");
    }

    // deprecated
    public void loadGameState (SavedGameParser.SavedGameState currentGameState) {
        log.info( "------" );
        log.info( "Ship Name : " + currentGameState.getPlayerShipName() );
        log.info( "Currently at beacon number : " + currentGameState.getTotalBeaconsExplored() );
        int sectorNumberDebug = currentGameState.getSectorNumber() + 1;
        log.info( "Currently in sector : " +  sectorNumberDebug);

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
