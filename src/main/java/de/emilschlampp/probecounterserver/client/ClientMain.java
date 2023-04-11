package de.emilschlampp.probecounterserver.client;

import de.emilschlampp.probecounterserver.setup.SetupWindow;
import de.emilschlampp.probecounterserver.util.EJFrame;
import de.emilschlampp.probecounterserver.util.Room;
import de.emilschlampp.probecounterserver.util.SConfig;
import de.emilschlampp.probecounterserver.util.color.ConsoleColor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;
import java.util.*;

public class ClientMain implements Runnable {
    @Override
    public void run() {
        EJFrame frame = new EJFrame("ESP8266 ProbeCounter");

        frame.setUndecorated(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);

        frame.setVisible(true);

        try {
            frame.setIconImage(ImageIO.read(Objects.requireNonNull(SetupWindow.class.getResourceAsStream("/icon.png"))));
        } catch (Exception ignored) {
            System.err.println("Icon konnte nicht geladen werden.");
        }

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                loadBG(frame);
            }
        });

        List<Room> roomList = new ArrayList<>();

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    frame.dispose();
                }
                if(e.getKeyCode() == KeyEvent.VK_F11) {
                    if(frame.getExtendedState() == Frame.MAXIMIZED_BOTH) {
                        frame.setExtendedState(Frame.NORMAL);
                        frame.setSize(500, 400);
                        frame.update();
                        frame.dispose();
                        frame.setUndecorated(false);
                        frame.setVisible(true);
                        loadBG(frame);
                    } else {
                        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                        frame.dispose();
                        frame.setUndecorated(true);
                        frame.setVisible(true);
                        loadBG(frame);
                    }
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
                        stream.println(roomA.getName());
                        int line = Integer.parseInt(scanner.nextLine());
                        rooms.put(roomA.getName(), line);
                        System.out.println(ConsoleColor.BG_LIGHT_GREEN +roomA.getName()+";;;"+line);
                    }
                    rooms.forEach((a, b) -> {
                        System.out.println(ConsoleColor.BG_LIGHT_GREEN+"A+"+a+" "+b);
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

        loadBG(frame);


    }

    private void loadBG(EJFrame frame) {
        SConfig config = SConfig.getSConfig("config.econf");
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {

            }
            try {
                BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
                image.getGraphics().drawImage(ImageIO.read(new File(config.getString("background"))).getScaledInstance(frame.getWidth(), frame.getHeight(), Image.SCALE_SMOOTH),
                        0,0,null);
                frame.setBackground(image);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
