package de.emilschlampp.probecounterserver.util.color;

import de.emilschlampp.probecounterserver.Launcher;

public class SystemUtils {
    public enum OS {
        WINDOWS, LINUX, MAC, SOLARIS
    }

    private static OS os = null;

    public static OS getOS() {
        if (os == null) {
            String operSys = System.getProperty("os.name").toLowerCase();
            if (operSys.contains("win")) {
                os = OS.WINDOWS;
            } else if (operSys.contains("nix") || operSys.contains("nux")
                    || operSys.contains("aix")) {
                os = OS.LINUX;
            } else if (operSys.contains("mac")) {
                os = OS.MAC;
            } else if (operSys.contains("sunos")) {
                os = OS.SOLARIS;
            }
        }
        return os;
    }

    private static boolean accept = false;
    private static boolean checked = false;

    public static boolean acceptColors() {
        if(!checked) {
            checked = true;
            if (System.console() != null && System.getenv().get("TERM") != null) {
                accept = true;
            }
            if(getOS().equals(OS.WINDOWS)) {
                accept = true;
            }
        }
        return accept && Launcher.getConfig().getBoolean("console-colors");
    }

    private static boolean i = false;

    public static void init() {
        if(i) { return; }
        i= true;

        if(acceptColors()) {
            ColorInjector.inject();
        }
    }
}
