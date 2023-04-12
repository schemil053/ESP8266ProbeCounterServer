package de.emilschlampp.probecounterserver.util;

import de.emilschlampp.probecounterserver.util.color.ConsoleColor;
import de.emilschlampp.probecounterserver.util.lang.Translation;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ConsoleUtil {
    private static String hostname = new Translation("hostName.unknown").toString();

    static {
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ignored) {

        }
        try {
            in = new Scanner(System.in);
        } catch (Exception exception) {
            in = new Scanner("");
        }
    }

    private static Scanner in;

    public static String nextLine() {
        System.out.print(ConsoleColor.RED +System.getProperty("user.name")+ConsoleColor.YELLOW+"@"+ConsoleColor.LIGHT_CYAN+hostname+"#ProbeCounter:~"+ConsoleColor.RESET);
        return in.nextLine();
    }

    public static String nextLine(String line) {
        System.out.print(ConsoleColor.RED +System.getProperty("user.name")+ConsoleColor.YELLOW+"@"+ConsoleColor.LIGHT_CYAN+hostname+"#"+line+":~"+ConsoleColor.RESET);
        return in.nextLine();
    }
}
