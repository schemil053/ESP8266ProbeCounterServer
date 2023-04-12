package de.emilschlampp.probecounterserver.client;

import de.emilschlampp.probecounterserver.Launcher;
import de.emilschlampp.probecounterserver.console.ConsoleThread;
import de.emilschlampp.probecounterserver.setup.SetupWindow;
import de.emilschlampp.probecounterserver.util.EJFrame;
import de.emilschlampp.probecounterserver.util.Room;
import de.emilschlampp.probecounterserver.util.SConfig;
import de.emilschlampp.probecounterserver.util.color.ConsoleColor;
import de.emilschlampp.probecounterserver.util.lang.Translation;

import javax.imageio.ImageIO;
import javax.swing.*;
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
            System.err.println(new Translation("icon.Error"));
        }

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                loadBG(frame, null);
            }
        });

        List<Room> roomList = new ArrayList<>();

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    frame.dispose();
                    System.exit(0);
                }
                if(e.getKeyCode() == KeyEvent.VK_F11) {
                    if(frame.getExtendedState() == Frame.MAXIMIZED_BOTH) {
                        frame.setExtendedState(Frame.NORMAL);
                        frame.setSize(500, 400);
                        frame.update();
                        frame.dispose();
                        frame.setUndecorated(false);
                        frame.setVisible(true);
                        loadBG(frame, null);
                    } else {
                        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                        frame.dispose();
                        frame.setUndecorated(true);
                        frame.setVisible(true);
                        loadBG(frame, null);
                    }
                    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                }
                if(e.getKeyCode() == KeyEvent.VK_F10) {
                    frame.dispose();
                    SetupWindow.startSetup();
                }
            }
        });

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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
                    roomList.add(new Room(Double.parseDouble(linesplit[0]),
                            Double.parseDouble(linesplit[1]), Integer.parseInt(linesplit[3]), linesplit[2]));
                }
            } catch (Throwable ignored) {

            }
        }

        SConfig config = Launcher.getConfig();

        Map<String, Integer> rooms = new HashMap<>();
        new Thread(() -> {
            while (true) {
                try {
                    rooms.clear();
                    config.setDefault("ip", "localhost:29000", config.getFile().isFile());
                    Socket socket = new Socket(config.getString("ip").split(":")[0], Integer.parseInt(config.getString("ip").split(":")[1]));

                    Scanner scanner = new Scanner(socket.getInputStream());
                    PrintStream stream = new PrintStream(socket.getOutputStream());

                    stream.println("WiFiProbeClient V0.1");
                    stream.println(roomList.size()+"");

                    for(Room roomA : roomList) {
                        stream.println(roomA.getName());
                        int line = Integer.parseInt(scanner.nextLine());
                        rooms.put(roomA.getName(), line);
                        if(Launcher.isDebug() && ConsoleThread.shouldLog) {
                            System.out.println(ConsoleColor.BG_LIGHT_GREEN + roomA.getName() + ";;;" + line);
                        }
                    }
                    if(Launcher.isDebug() && ConsoleThread.shouldLog) {
                        rooms.forEach((a, b) -> {
                            System.out.println(ConsoleColor.BG_LIGHT_GREEN + "A+" + a + " " + b);
                        });
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                try {
                    config.setDefault("checktime", 30, config.getFile().isFile());
                    Thread.sleep(1000L * config.getInt("checktime"));
                } catch (InterruptedException ignored) {

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

                g.drawString(rooms.get(room.getName())+"", room.getXForFrame(frame.getWidth()), room.getYForFrame(frame.getHeight()));
            }
            g.setFont(defaultf);
        });

        loadBG(frame, null);


    }

    public static void loadBG(EJFrame frame, String bg) {
        SConfig config = Launcher.getConfig();
        if(bg == null) {
            bg = config.getString("background");
        }
        String finalBg = bg;
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {

            }
            try {
                BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
                image.getGraphics().drawImage(ImageIO.read(new File(finalBg)).getScaledInstance(frame.getWidth(), frame.getHeight(), Image.SCALE_SMOOTH),
                        0,0,null);
                frame.setBackground(image);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
