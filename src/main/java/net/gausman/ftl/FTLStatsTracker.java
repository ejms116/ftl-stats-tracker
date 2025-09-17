package net.gausman.ftl;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import net.blerf.ftl.core.EditorConfig;
import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.DefaultDataManager;
import net.gausman.ftl.controller.TrackerController;
import net.gausman.ftl.service.RunService;
import net.vhati.modmanager.core.FTLUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class FTLStatsTracker {
    private static final Logger log = LoggerFactory.getLogger(FTLStatsTracker.class);

    public static void main(String[] args) {
        initGUI();

    }

    private static void initGUI(){
        boolean writeConfig = false;
        Properties props = new Properties();
        File configFile = new File("ftl-editor.cfg");

        props.setProperty(EditorConfig.FTL_SAVE_PATH, "");
        props.setProperty(EditorConfig.FTL_DATS_PATH, "");  // Prompt.
        props.setProperty(EditorConfig.UPDATE_APP, "");     // Prompt.
        props.setProperty(EditorConfig.USE_DEFAULT_UI, "false");

        // Read the config file.
        InputStream in = null;
        try {
            if (configFile.exists()) {
                log.trace("Loading properties from config file.");
                in = new FileInputStream(configFile);
                props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            } else {
                writeConfig = true; // Create a new cfg, but only if necessary.
            }
        } catch (IOException e) {
            log.error("Error loading config", e);
//            showErrorDialog( "Error loading config from "+ configFile.getPath() );
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
            }
        }



        net.blerf.ftl.core.EditorConfig appConfig = new net.blerf.ftl.core.EditorConfig(props, configFile);
        // FTL Savegame path (continue.sav)
        File saveFile;
        String savePath = appConfig.getProperty(net.blerf.ftl.core.EditorConfig.FTL_SAVE_PATH, "");

        if (!savePath.isEmpty()){
            log.info("Using Save file location from config: " + savePath);
            saveFile = new File(savePath);
            // TODO: check valid savegame path
        } else {
            saveFile = null;
            props.setProperty(net.blerf.ftl.core.EditorConfig.FTL_SAVE_PATH, "C:\\Users\\erikj\\Documents\\My Games\\FasterThanLight\\continue.sav");
            writeConfig = true;
        }

        // FTL Resources Path.
        File datsDir = null;
        String datsPath = appConfig.getProperty(net.blerf.ftl.core.EditorConfig.FTL_DATS_PATH, "");

        if (!datsPath.isEmpty()) {
            log.info("Using FTL dats path from config: " + datsPath);
            datsDir = new File(datsPath);
            if (!FTLUtilities.isDatsDirValid(datsDir)) {
                log.error("The config's " + net.blerf.ftl.core.EditorConfig.FTL_DATS_PATH + " does not exist, or it is invalid");
                datsDir = null;
            }
        } else {
            log.debug("No " + net.blerf.ftl.core.EditorConfig.FTL_DATS_PATH + " previously set");
        }

//        if (datsDir == null) {
//            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You will be promted to select the FTL.dat!", ButtonType.OK);
//            alert.showAndWait();
//
//            FileChooser fc = new FileChooser();
//            fc.setTitle("Locate FTL resources");
////            File temp = fc.showOpenDialog(stage);
//            File temp = fc.showOpenDialog(null);
//            if (temp == null){
//                throw new FTLStatsTrackerApplication.ExitException();
//            }
//            datsDir = temp.getParentFile(); // TODO: add error Handling
//            if (FTLUtilities.isDatsDirValid(datsDir)) {
//                props.setProperty(EditorConfig.FTL_DATS_PATH, datsDir.toString());
//                writeConfig = true;
//            } else {
//                datsDir = null;
//            }
//        }
//
//        if (datsDir == null) {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION, "FTL resources were not found. The editor will now exit.", ButtonType.OK);
//            alert.showAndWait();
//            log.debug("No FTL dats path found, exiting.");
//
//            throw new FTLStatsTrackerApplication.ExitException();
//        }

        // Savefile copy setting
        String setting = appConfig.getProperty(EditorConfig.SAVE_FILE_COPY_SETTING, "ONCE_PER_JUMP");
        RunService.SaveFileCopySetting saveFileCopySetting;
        try {
            saveFileCopySetting = RunService.SaveFileCopySetting.valueOf(setting);
        } catch (IllegalArgumentException | NullPointerException e) {
            // fallback (e.g., default value)
            saveFileCopySetting = RunService.SaveFileCopySetting.ONCE_PER_JUMP;
        }



        Path runsDir = null;
        Path savesDir = null;
        try {
            // Get the directory where the JAR is located
            Path jarDir = new File(FTLStatsTracker.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI())
                    .getParentFile()
                    .toPath();

            // Define required directories
            runsDir = jarDir.resolve("runs");
            savesDir = jarDir.resolve("saves");

            // Ensure they exist
            createDirectoryIfMissing(runsDir);
            createDirectoryIfMissing(savesDir);

            log.info("Directories ensured at: " + jarDir);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (writeConfig) {
            try {
                appConfig.writeConfig();
            } catch (IOException e) {
                String errorMsg = String.format("Error writing config to \"%s\"", configFile.getPath());
                log.error(errorMsg, e);
//                showErrorDialog( errorMsg );
            }
        }

        // Parse the data
        try {
            log.info("parsing data");
            DefaultDataManager dataManager = new DefaultDataManager( datsDir );
            DataManager.setInstance( dataManager );
            dataManager.setDLCEnabledByDefault( true );
        }
        catch ( Exception e ) {
            log.error( "Error parsing FTL resources", e );
//            showErrorDialog( "Error parsing FTL resources" );
        }

        Path finalRunsDir = runsDir;
        Path finalSavesDir = savesDir;
        RunService.SaveFileCopySetting finalSaveFileCopySetting = saveFileCopySetting;
        javax.swing.SwingUtilities.invokeLater(() -> {
            FlatLaf.setup(new FlatDarkLaf());
            new TrackerController(saveFile, finalRunsDir, finalSavesDir, finalSaveFileCopySetting);
        });
    }

    private static void createDirectoryIfMissing(Path dir) {
        if (Files.notExists(dir)) {
            try {
                Files.createDirectories(dir);
                log.info("Created directory: " + dir);
            } catch (IOException e) {
                log.error("Could not create directory: " + dir);
                e.printStackTrace();
            }
        }
    }
}
