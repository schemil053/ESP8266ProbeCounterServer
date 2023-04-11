package de.emilschlampp.probecounterserver.server;

import de.emilschlampp.probecounterserver.util.NoDOS;
import de.emilschlampp.probecounterserver.util.SConfig;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ServerMain implements Runnable {
    SConfig config = SConfig.getSConfig("config.econf");
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
                        try {
                            socket.close();
                        } catch (Exception ignored) {

                        }
                    }
                }).start();
            }

        } catch (Exception exception) {

        }
    }

    public static final Map<String, Integer> map = new HashMap<>();

    private void handleClient(Socket socket) throws Throwable {
        Scanner scanner = new Scanner(socket.getInputStream());
        PrintStream writer = new PrintStream(socket.getOutputStream());

        String ver = scanner.nextLine();

        if(ver.startsWith("WiFiProbe V0.1")) {
            map.put(scanner.nextLine(), Integer.parseInt(scanner.nextLine()));
        } else if(ver.startsWith("WiFiProbeClient V0.1")) {
            int access = Integer.parseInt(scanner.nextLine());
            for(int i = 0; i<access; i++) {
                String roomtoget = scanner.nextLine();

                writer.println(map.getOrDefault(roomtoget, 0)+"");
            }
        }
    }
}
