package de.emilschlampp.probecounterserver.util.lang;

import de.emilschlampp.probecounterserver.Launcher;

import java.io.InputStream;

public class LangManager {
    private static Language current = null;

    static {
        loadDefault();
    }

    public static Language getCurrentLanguage() {
        return current;
    }

    public static String format(Translation k, Object... tof) {
        String t = k.toString();
        for(Object s : tof) {
            t = t.replaceFirst("%", String.valueOf(s).replace("\\", "\\\\"));
        }
        return t;
    }

    public static void loadDefault() {
        if(current != null) {
            return;
        }
        String lang = "DE";
        Launcher.getConfig().setDefault("lang", "DE", Launcher.getConfig().getFile().isFile());
        if(Launcher.getConfig().isSet("language")) {
            lang = Launcher.getConfig().getString("language");
        }
        InputStream stream = LangManager.class.getResourceAsStream("/lang/"+lang+".lang");

        if(stream == null) {
            lang = "DE";
            stream = LangManager.class.getResourceAsStream("/lang/"+lang+".lang");
        }
        current = new StreamLanguage(stream);
    }
}
