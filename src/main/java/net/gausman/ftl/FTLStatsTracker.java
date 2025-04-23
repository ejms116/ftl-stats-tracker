package net.gausman.ftl;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import net.blerf.ftl.core.EditorConfig;
import net.gausman.ftl.controller.TrackerController;
import net.gausman.ftl.util.NewConfigSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTLStatsTracker {
    private static final Logger log = LoggerFactory.getLogger(FTLStatsTracker.class);
    public static EditorConfig appConfig;

    public static void main(String[] args) {
        appConfig = NewConfigSetup.init();
        initGUI();

    }

    private static void initGUI(){
        javax.swing.SwingUtilities.invokeLater(() -> {
            FlatLaf.setup(new FlatLightLaf());
            new TrackerController();
        });
    }
}
