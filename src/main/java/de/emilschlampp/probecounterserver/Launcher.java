package de.emilschlampp.probecounterserver;

import de.emilschlampp.probecounterserver.setup.SetupWindow;
import de.emilschlampp.probecounterserver.util.SConfig;

import javax.swing.*;

public class Launcher {
    public static void main(String[] args) throws Throwable {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SConfig config = SConfig.getSConfig("config.econf");
        if(!config.getFile().isFile()) {
            SetupWindow.startSetup();
            return;
        }
        config.setDefault("mode", "SERVER", true);
    }
}
