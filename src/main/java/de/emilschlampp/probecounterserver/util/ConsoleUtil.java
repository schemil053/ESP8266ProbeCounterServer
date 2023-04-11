package de.emilschlampp.probecounterserver.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ConsoleUtil {
    private static String hostname = "unknown";

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
        System.out.print(System.getProperty("user.name")+"@"+hostname+"#ProbeCounter:~");
        return in.nextLine();
    }
}
