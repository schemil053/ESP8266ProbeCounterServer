package de.emilschlampp.probecounterserver;

import de.emilschlampp.probecounterserver.setup.SetupWindow;
import de.emilschlampp.probecounterserver.util.Mode;
import de.emilschlampp.probecounterserver.util.SConfig;
import de.emilschlampp.probecounterserver.util.color.SystemUtils;

import javax.swing.*;

public class Launcher {
    public static void main(String[] args) throws Throwable {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SConfig config = SConfig.getSConfig("config.econf");

        config.setDefault("console-colors", true, config.getFile().isFile());

        SystemUtils.init();
        if((!config.getFile().isFile()) || (!System.getProperty("setup", "?").equals("?"))) {
            SetupWindow.startSetup();
            return;
        }

        config.setDefault("mode", Mode.SERVER.name(), true);

        Mode mode = Mode.UNKNOWN;

        try {
            mode = Mode.valueOf(config.getString("mode"));
        } catch (IllegalArgumentException ignored) {}

        mode.init();
    }
}
