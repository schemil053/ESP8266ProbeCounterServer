package de.emilschlampp.probecounterserver.client;

import de.emilschlampp.probecounterserver.util.EJFrame;
import de.emilschlampp.probecounterserver.util.Room;
import de.emilschlampp.probecounterserver.util.SConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.List;

public class ClientMain implements Runnable {
    @Override
    public void run() {
        EJFrame frame = new EJFrame("ESP8266 ProbeCounter");

        frame.setUndecorated(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);

        frame.setVisible(true);

        List<Room> roomList = new ArrayList<>();

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    frame.dispose();
                }
            }
        });

        File file = new File("rooms.speedconf");
        if(file.isFile()) {
            try {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if(line.equals("") || line.startsWith("#") || line.startsWith("//")) {
                        continue;
                    }
                    String[] linesplit = line.split(";");
                    roomList.add(new Room(Integer.parseInt(linesplit[0]),
                            Integer.parseInt(linesplit[1]), Integer.parseInt(linesplit[3]), linesplit[2]));
                }
            } catch (Throwable ignored) {

            }
        }

        SConfig config = SConfig.getSConfig("config.econf");

        Map<String, Integer> rooms = new HashMap<>();
        new Thread(() -> {
            while (true) {
                try {
                    rooms.clear();
                    Socket socket = new Socket(config.getString("ip").split(":")[0], Integer.parseInt(config.getString("ip").split(":")[1]));

                    Scanner scanner = new Scanner(socket.getInputStream());
                    PrintStream stream = new PrintStream(socket.getOutputStream());

                    stream.println("WiFiProbeClient V0.1");
                    stream.println(roomList.size()+"");

                    for(Room roomA : roomList) {
                        stream.println(roomA);
                        int line = Integer.parseInt(scanner.nextLine());
                        rooms.put(roomA.getName(), line);
                        System.out.println(roomA+";;;"+line);
                    }
                    rooms.forEach((a, b) -> {
                        System.out.println("A+"+a+" "+b);
                    });
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                try {
                    Thread.sleep(1000*30);
                } catch (InterruptedException e) {

                }
            }
        }).start();


        frame.addRenderL(g -> {
            Font defaultf = g.getFont();

            for (Room room : roomList) {
                if(room.getFontsize() != -1) {
                    Font font = new Font(defaultf.getName(), defaultf.getStyle(), room.getFontsize());
                    g.setFont(font);
                } else {
                    g.setFont(defaultf);
                }

                g.drawString(rooms.get(room.getName())+"", room.getX(), room.getY());
            }
            g.setFont(defaultf);
        });

        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {

            }
            try {
                BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
                image.getGraphics().drawImage(ImageIO.read(new File("icon.png")).getScaledInstance(frame.getWidth(), frame.getHeight(), Image.SCALE_SMOOTH),
                        0,0,null);
                frame.setBackground(image);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
