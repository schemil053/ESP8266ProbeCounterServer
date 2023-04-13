package de.emilschlampp.probecounterserver;

import de.emilschlampp.probecounterserver.console.ConsoleThread;
import de.emilschlampp.probecounterserver.setup.SetupWindow;
import de.emilschlampp.probecounterserver.util.Mode;
import de.emilschlampp.probecounterserver.util.SConfig;
import de.emilschlampp.probecounterserver.util.color.SystemUtils;

import javax.swing.*;

public class Launcher {
    private static ConsoleThread consoleThread;
    private static Mode mode = Mode.UNKNOWN;
    public static void main(String[] args) throws Throwable {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SConfig config = getConfig();

        config.setDefault("debug", false, config.getFile().isFile());
        config.setDefault("console-colors", true, config.getFile().isFile());

        SystemUtils.init();
        if((!config.getFile().isFile()) || (!System.getProperty("setup", "?").equals("?"))) {
            SetupWindow.startSetup();
            return;
        }

        config.setDefault("mode", Mode.SERVER.name(), config.getFile().isFile());


        try {
            mode = Mode.valueOf(config.getString("mode"));
        } catch (IllegalArgumentException ignored) {}

        mode.init();

        if(consoleThread != null) {
            consoleThread.interrupt();
        }

        consoleThread = new ConsoleThread();
        consoleThread.start();
    }

    public static SConfig getConfig() {
        return SConfig.getSConfig("config.econf");
    }

    public static boolean isDebug() {
        return getConfig().getBoolean("debug");
    }

    public static Mode getMode() {
        return mode;
    }
}
