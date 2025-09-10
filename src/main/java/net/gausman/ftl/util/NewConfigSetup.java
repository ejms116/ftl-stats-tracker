package net.gausman.ftl.util;


import net.blerf.ftl.core.EditorConfig;
import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.DefaultDataManager;
import net.vhati.modmanager.core.FTLUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class NewConfigSetup {
    private static final Logger log = LoggerFactory.getLogger(NewConfigSetup.class);
    public static EditorConfig init(){

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
                props.load(new InputStreamReader(in, "UTF-8"));
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



        EditorConfig appConfig = new EditorConfig(props, configFile);

        String savePath = appConfig.getProperty(EditorConfig.FTL_SAVE_PATH);
        if (!savePath.isEmpty()){
            log.info("Using Save file location from config: " + savePath);
            // TODO: check valid savegame path
        } else {
            props.setProperty(EditorConfig.FTL_SAVE_PATH, "C:\\Users\\Erik\\Documents\\My Games\\FasterThanLight\\continue.sav");
            writeConfig = true;
        }

        // FTL Resources Path.
        File datsDir = null;
        String datsPath = appConfig.getProperty(EditorConfig.FTL_DATS_PATH, "");

        if (datsPath.length() > 0) {
            log.info("Using FTL dats path from config: " + datsPath);
            datsDir = new File(datsPath);
            if (FTLUtilities.isDatsDirValid(datsDir) == false) {
                log.error("The config's " + EditorConfig.FTL_DATS_PATH + " does not exist, or it is invalid");
                datsDir = null;
            }
        } else {
            log.debug("No " + EditorConfig.FTL_DATS_PATH + " previously set");
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

        File runsDir = new File("runs");
        if (!runsDir.exists()){
            runsDir.mkdirs();
            log.info("runs directory created");
        }

        File savesDir = new File("saves");
        if (!savesDir.exists()){
            savesDir.mkdirs();
            log.info("saves directory created");
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
//
//            throw new FTLStatsTrackerApplication.ExitException();
        }
        return appConfig;
    }

//    private static void showErrorDialog( String message ) {
//        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.CLOSE);
//        alert.showAndWait();
//    }
}
