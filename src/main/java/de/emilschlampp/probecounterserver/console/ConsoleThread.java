package de.emilschlampp.probecounterserver.console;

import de.emilschlampp.probecounterserver.Launcher;
import de.emilschlampp.probecounterserver.console.impl.StopCommand;
import de.emilschlampp.probecounterserver.console.impl.server.ListDevicesCommand;
import de.emilschlampp.probecounterserver.console.impl.server.ListRoomsCommand;
import de.emilschlampp.probecounterserver.util.ConsoleUtil;
import de.emilschlampp.probecounterserver.util.Mode;
import de.emilschlampp.probecounterserver.util.SConfig;
import de.emilschlampp.probecounterserver.util.lang.Translation;

import java.util.*;

public class ConsoleThread extends Thread {

    public static boolean shouldLog = true;

    private static final Map<String, Command> clientCommandMap = new HashMap<>();
    private static final Map<String, Command> serverCommandMap = new HashMap<>();


    @Override
    public void run() {
        SConfig config = Launcher.getConfig();
        config.setDefault("mode", Mode.SERVER.name(), config.getFile().isFile());

        Mode mode = Mode.UNKNOWN;

        try {
            mode = Mode.valueOf(config.getString("mode"));
        } catch (IllegalArgumentException ignored) {}

        Scanner scanner = new Scanner(System.in);
        while (true) {
            shouldLog = true;
            String line = scanner.nextLine();

            if(line.equals("")) {
                shouldLog = false;
                if(mode.hasClient() && mode.hasServer()) {
                    boolean serv = false;
                    while (true) {
                        System.out.println(new Translation("console.choose"));
                        String console = ConsoleUtil.nextLine("console");
                        if(Arrays.asList("client", "server").contains(console.toLowerCase(Locale.ROOT))) {
                           serv = "server".equals(console.toLowerCase(Locale.ROOT));
                           break;
                        }
                    }
                    if(serv) {
                        while (true) {
                            String command = ConsoleUtil.nextLine("server");
                            if(command.equals("")) {
                                break;
                            }
                            String[] split = command.split(" ");
                            if(serverCommandMap.containsKey(split[0])) {
                                try {
                                    serverCommandMap.get(split[0]).run(removeFirstElement(split));
                                } catch (Throwable throwable) {
                                    System.err.println(new Translation("console.command.error").format(split[0]));
                                    throwable.printStackTrace();
                                }
                            } else {
                                System.out.println(new Translation("console.command.notFound").format(split[0]));
                            }
                        }
                    } else {
                        while (true) {
                            String command = ConsoleUtil.nextLine("client");
                            if(command.equals("")) {
                                break;
                            }
                            String[] split = command.split(" ");
                            if(clientCommandMap.containsKey(split[0])) {
                                try {
                                    clientCommandMap.get(split[0]).run(removeFirstElement(split));
                                } catch (Throwable throwable) {
                                    System.err.println(new Translation("console.command.error").format(split[0]));
                                    throwable.printStackTrace();
                                }
                            } else {
                                System.out.println(new Translation("console.command.notFound").format(split[0]));
                            }
                        }
                    }
                }
            }
        }
    }

    public static void registerServer(Command command){
        serverCommandMap.put(command.getName(), command);
    }

    public static void registerClient(Command command){
        clientCommandMap.put(command.getName(), command);
    }

    public static void registerBoth(Command command) {
        registerClient(command);
        registerServer(command);
    }

    static {
        registerBoth(new StopCommand());

        registerServer(new ListDevicesCommand());
        registerServer(new ListRoomsCommand());
    }

    private static String[] removeFirstElement(String[] arr) {

        if(arr.length == 0) {
            return arr;
        }

        String newArr[] = new String[arr.length - 1];
        for (int i = 1; i < arr.length; i++) {
            newArr[i-1] = arr[i];
        }
        return newArr;
    }
}
