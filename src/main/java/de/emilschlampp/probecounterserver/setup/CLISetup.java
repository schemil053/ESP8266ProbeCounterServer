package de.emilschlampp.probecounterserver.setup;

import de.emilschlampp.probecounterserver.Launcher;
import de.emilschlampp.probecounterserver.util.ConsoleUtil;
import de.emilschlampp.probecounterserver.util.Mode;
import de.emilschlampp.probecounterserver.util.SConfig;
import de.emilschlampp.probecounterserver.util.Values;
import de.emilschlampp.probecounterserver.util.color.ConsoleColor;

import java.util.Scanner;

public class CLISetup {
    public static void startCLISetup() {
        SConfig config = SConfig.getSConfig("config.econf");
        try {
            Scanner scanner = new Scanner(SetupWindow.class.getResourceAsStream("/icon.cli"));
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        } catch (Throwable throwable) {

        }
        System.out.println();
        System.out.println(ConsoleColor.MAGENTA +"Willkommen bei ESP8266ProbeCounterServer");
        System.out.println(ConsoleColor.MAGENTA +"Du befindest dich im Einrichtungsmodus.");
        System.out.println(ConsoleColor.MAGENTA +"Weil du scheinbar keine grafische Nutzeroberfläche hast, kannst du nur den Server einrichten.");
        System.out.println(ConsoleColor.MAGENTA +"Bitte gebe den Port des Servers ein:");
        int port = 29000;
        try {
            port = Integer.parseInt(ConsoleUtil.nextLine());
            if(port < 1 || port > Values.maxPort) {
                throw new IllegalArgumentException("");
            }
        } catch (Exception exception) {
            System.err.println("Setup abgebrochen. Bitte gebe einen validen Port an.");
            System.exit(1);
        }
        System.out.println(ConsoleColor.LIGHT_GREEN+"Port wurde gesetzt. Der Server wird gestartet.");
        config.set("mode", Mode.SERVER.name());
        config.set("port", port);
        config.save();
        try {
            Launcher.main(new String[0]);
        } catch (Throwable e) {
            System.err.println("Beim starten des Server ist ein Problem aufgetreten:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
