package de.emilschlampp.probecounterserver.setup;

import de.emilschlampp.probecounterserver.Launcher;
import de.emilschlampp.probecounterserver.util.ConsoleUtil;
import de.emilschlampp.probecounterserver.util.Mode;
import de.emilschlampp.probecounterserver.util.SConfig;
import de.emilschlampp.probecounterserver.util.Values;
import de.emilschlampp.probecounterserver.util.color.ConsoleColor;
import de.emilschlampp.probecounterserver.util.lang.Translation;

import java.util.Objects;
import java.util.Scanner;

public class CLISetup {
    public static void startCLISetup() {
        SConfig config = Launcher.getConfig();
        try {
            Scanner scanner = new Scanner(Objects.requireNonNull(SetupWindow.class.getResourceAsStream("/icon.cli")));
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        } catch (Throwable throwable) {

        }
        String trans = new Translation("cli.setup.welcome").toString();
        for(String s : trans.split("\n")) {
            System.out.println(ConsoleColor.MAGENTA +s);
        }
        int port = 29000;
        try {
            port = Integer.parseInt(ConsoleUtil.nextLine());
            if(port < 1 || port > Values.maxPort) {
                throw new IllegalArgumentException("");
            }
        } catch (Exception exception) {
            System.err.println(new Translation("cli.invalidPort"));
            System.exit(1);
        }
        System.out.println(ConsoleColor.LIGHT_GREEN+new Translation("cli.portSetAndServerStarts").toString());
        config.set("mode", Mode.SERVER.name());
        config.set("port", port);
        config.save();
        try {
            Launcher.main(new String[0]);
        } catch (Throwable e) {
            System.err.println(new Translation("cli.startError"));
            e.printStackTrace();
            System.exit(1);
        }
    }
}
