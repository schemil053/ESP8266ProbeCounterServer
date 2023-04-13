package de.emilschlampp.probecounterserver.server;

import de.emilschlampp.probecounterserver.Launcher;
import de.emilschlampp.probecounterserver.console.ConsoleThread;
import de.emilschlampp.probecounterserver.util.NoDOS;
import de.emilschlampp.probecounterserver.util.SConfig;
import de.emilschlampp.probecounterserver.util.color.ConsoleColor;
import de.emilschlampp.probecounterserver.util.lang.Translation;

import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ServerMain implements Runnable {
    SConfig config = Launcher.getConfig();
    @Override
    public void run() {
        config.setDefault("port", 29000, true);
        int port = config.getInt("port");

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (serverSocket.isBound()) {
                Socket socket = serverSocket.accept();
                if(NoDOS.checkDDOS(socket)) {
                    socket.close();
                    continue;
                }

                new Thread(() -> {
                    try {
                        handleClient(socket);
                        socket.close();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        try {
                            socket.close();
                        } catch (Exception ignored) {

                        }
                    }
                }).start();
            }

        } catch (Exception ignored) {

        }
    }

    public static final Map<String, Integer> map = new HashMap<>();
    public static final Map<String, Map<String, Integer>> devicesInRoom = new HashMap<>();

    private void handleClient(Socket socket) throws Throwable {
        if(ConsoleThread.shouldLog) {
            if(!(socket.getInetAddress().getHostAddress().equals("127.0.0.1") && Launcher.getMode().hasClient())) {
                System.out.println(ConsoleColor.LIGHT_GREEN + new Translation("server.newClient").format(socket.getInetAddress().getHostAddress()));
            }
        }
        Scanner scanner = new Scanner(socket.getInputStream());
        PrintStream writer = new PrintStream(socket.getOutputStream());

        String ver = scanner.nextLine();

        if(ver.startsWith("WiFiProbe V0.1.1")) {
            String p = scanner.nextLine();
            int devices = Integer.parseInt(scanner.nextLine());
            map.put(p, devices);
            if(Launcher.isDebug() && ConsoleThread.shouldLog) {
                System.out.println(ConsoleColor.BG_LIGHT_RED + p + ";" + map.getOrDefault(p, 0));
            }
            Map<String, Integer> rssimap = new HashMap<>();
            for(int i = 0; i<devices; i++) {
                rssimap.put(scanner.nextLine(), Integer.parseInt(scanner.nextLine()));
            }
            devicesInRoom.put(p, rssimap);
        } else if(ver.startsWith("WiFiProbeClient V0.1")) {
            int access = Integer.parseInt(scanner.nextLine());
            for(int i = 0; i<access; i++) {
                String roomtoget = scanner.nextLine();

                writer.println(map.getOrDefault(roomtoget, 0)+"");
            }
        }
    }
}
