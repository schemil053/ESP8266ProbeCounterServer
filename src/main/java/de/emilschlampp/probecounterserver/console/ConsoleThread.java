package de.emilschlampp.probecounterserver.console;

import de.emilschlampp.probecounterserver.Launcher;
import de.emilschlampp.probecounterserver.console.impl.both.ClearCommand;
import de.emilschlampp.probecounterserver.console.impl.both.StopCommand;
import de.emilschlampp.probecounterserver.console.impl.server.ListDevicesCommand;
import de.emilschlampp.probecounterserver.console.impl.server.ListRoomsCommand;
import de.emilschlampp.probecounterserver.util.ConsoleUtil;
import de.emilschlampp.probecounterserver.util.Mode;
import de.emilschlampp.probecounterserver.util.SConfig;
import de.emilschlampp.probecounterserver.util.color.ConsoleColor;
import de.emilschlampp.probecounterserver.util.lang.Translation;

import java.lang.management.ManagementFactory;
import java.util.*;

public class ConsoleThread extends Thread {
    public static boolean shouldLog = true;

    private static final Map<String, Command> clientCommandMap = new HashMap<>();
    private static final Map<String, Command> serverCommandMap = new HashMap<>();

    static {
        registerBoth(new StopCommand());
        registerBoth(new ClearCommand());

        registerServer(new ListDevicesCommand());
        registerServer(new ListRoomsCommand());
    }


    @Override
    public void run() {
        SConfig config = Launcher.getConfig();
        config.setDefault("mode", Mode.SERVER.name(), config.getFile().isFile());

        Mode mode = Mode.UNKNOWN;

        boolean errorwritten = false;

        try {
            mode = Mode.valueOf(config.getString("mode"));
        } catch (IllegalArgumentException ignored) {}

        long start = ManagementFactory.getRuntimeMXBean().getStartTime();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            shouldLog = true;
            String line = "?";
            try {
                line = scanner.nextLine();
            } catch (Exception exception) {
                if(System.currentTimeMillis() > start+500) {
                    if (!errorwritten) {
                        System.err.println(new Translation("console.not.available"));
                        errorwritten = true;
                    }
                }
                continue;
            }


            if(!mode.hasClient() && !mode.hasServer()) {
                if(System.currentTimeMillis() > start+500) {
                    if (!errorwritten) {
                        System.err.println(new Translation("console.not.available"));
                        errorwritten = true;
                    }
                }
                continue;
            }

            if(line.equals("")) {
                shouldLog = false;
                boolean serv = false;
                if(mode.hasClient() && mode.hasServer()) {
                    while (true) {
                        System.out.println(new Translation("console.choose"));
                        String console = ConsoleUtil.nextLine("console");
                        if (console.equals("")) {
                            scanner.nextLine();
                            continue;
                        }
                        if (Arrays.asList("client", "server").contains(console.toLowerCase(Locale.ROOT))) {
                            serv = "server".equals(console.toLowerCase(Locale.ROOT));
                            break;
                        }
                    }
                } else {
                    serv = mode.hasServer() && !mode.hasClient();
                }
                if(serv) {
                    System.out.println(new Translation("console.server.welcome"));
                    while (true) {
                        String command = ConsoleUtil.nextLine("server");

                        if(command.equals("")) {
                            scanner.nextLine();
                            continue;
                        }

                        String[] split = command.split(" ");
                        if(split[0].equals("commands")) {
                            for (String s : serverCommandMap.keySet()) {
                                System.out.println(ConsoleColor.LIGHT_BLUE+s+ConsoleColor.RESET);
                            }
                            System.out.println(ConsoleColor.LIGHT_BLUE+"exit"+ConsoleColor.RESET);
                            continue;
                        }
                        if(split[0].equals("exit")) {
                            break;
                        }
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
                    System.out.println(new Translation("console.client.welcome"));
                    while (true) {
                        String command = ConsoleUtil.nextLine("client");

                        if(command.equals("")) {
                            scanner.nextLine();
                            continue;
                        }

                        String[] split = command.split(" ");
                        if(split[0].equals("commands")) {
                            for (String s : clientCommandMap.keySet()) {
                                System.out.println(ConsoleColor.LIGHT_BLUE+s+ConsoleColor.RESET);
                            }
                            System.out.println(ConsoleColor.LIGHT_BLUE+"exit"+ConsoleColor.RESET);
                            continue;
                        }
                        if(split[0].equals("exit")) {
                            break;
                        }
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
