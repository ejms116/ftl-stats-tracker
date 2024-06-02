package net.gausman.ftl;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.blerf.ftl.core.EditorConfig;

import java.io.IOException;

import net.gausman.ftl.util.ConfigSetup;
import org.slf4j.*;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

public class FTLStatsTrackerApplication extends Application {
    private static final Logger log = LoggerFactory.getLogger(FTLStatsTrackerApplication.class);
    public static EditorConfig appConfig;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            appConfig = ConfigSetup.init(stage);
            FXMLLoader fxmlLoader = new FXMLLoader(FTLStatsTrackerApplication.class.getResource("main.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1493, 806);
            scene.getStylesheets().add(FTLStatsTrackerApplication.class.getResource("main.css").toExternalForm());
            stage.setTitle("FTL Stats Tracker");
            stage.setScene(scene);
            FTLStatsTrackerController FTLStatsTrackerController = fxmlLoader.getController();
            stage.setOnHidden(e -> FTLStatsTrackerController.shutdown());
            stage.show();
        } catch (ExitException e) {
            System.gc();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class ExitException extends RuntimeException {
        public ExitException() {
        }

        public ExitException( String message ) {
            super( message );
        }

        public ExitException( Throwable cause ) {
            super( cause );
        }

        public ExitException( String message, Throwable cause ) {
            super( message, cause );
        }
    }
}