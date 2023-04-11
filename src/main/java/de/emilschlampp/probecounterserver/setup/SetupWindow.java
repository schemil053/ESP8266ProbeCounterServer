package de.emilschlampp.probecounterserver.setup;

import de.emilschlampp.probecounterserver.Launcher;
import de.emilschlampp.probecounterserver.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SetupWindow {
    public static void startSetup()  {
        SConfig config = SConfig.getSConfig("config.econf");
        if(GraphicsEnvironment.isHeadless() || (!System.getProperty("setupHeadless", "?").equals("?"))) {
            System.out.println("Willkommen bei ESP8266ProbeCounterServer");
            System.out.println("Du befindest dich im Einrichtungsmodus.");
            System.out.println("Weil du scheinbar keine grafische Nutzeroberfläche hast, kannst du nur den Server einrichten.");
            System.out.println("Bitte gebe den Port des Servers ein:");
            int port = 29000;
            try {
                port = Integer.parseInt(ConsoleUtil.nextLine());
                if(port < 1 || port > Values.maxPort) {
                    throw new IllegalArgumentException("");
                }
            } catch (Exception exception) {
                System.out.println("Setup abgebrochen. Bitte gebe einen validen Port an.");
                System.exit(1);
            }
            System.out.println("Port wurde gesetzt. Der Server wird gestartet.");
            config.set("mode", Mode.SERVER.name());
            config.set("port", port);
            config.save();
            try {
                Launcher.main(new String[0]);
            } catch (Throwable e) {
                System.err.println("Beim starten des Server ist ein Problem aufgetreten:");
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            EJFrame frame = new EJFrame("SchemilESPWiFiProbeCounter");

            frame.setBackground(Color.WHITE);

            frame.setLayout(new GridBagLayout());

            try {
                frame.setIconImage(ImageIO.read(Objects.requireNonNull(SetupWindow.class.getResourceAsStream("/icon.png"))));
            } catch (Exception ignored) {
                System.err.println("Icon konnte nicht geladen werden.");
            }

            JButton button = new JButton("Speichern und Anwenden");

            JButton testbutton = new JButton("Vollbild-Testfenster (Für Koordinaten)");

            JCheckBox fullscreenbox = new JCheckBox("Vollbild");

            JTextArea roomArea = new JTextArea(8, 20);

            roomArea.setPreferredSize(new Dimension(200, 100));

            JTextField backgroundField = new JTextField();
            JTextField portField = new JTextField();
            JTextField ipField = new JTextField();

            roomArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            JComboBox modeBox = new JComboBox(Arrays.stream(Mode.values()).map(Enum::name).collect(Collectors.toList()).toArray());


            File file = new File("rooms.speedconf");
            if(file.isFile()) {
                try {
                    Scanner scanner = new Scanner(file);
                    String text = "";
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if(line.equals("") || line.startsWith("#") || line.startsWith("//")) {
                            continue;
                        }
                        text = text+"\n"+line;
                    }
                    roomArea.setText(text.replaceFirst("\n", ""));
                } catch (Throwable ignored) {

                }
            }

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            frame.add(new JLabel("Hintergrundbild"), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            frame.add(backgroundField, gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            frame.add(fullscreenbox, gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            frame.add(new JLabel("GUI-Räume"), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            frame.add(new JLabel("X;Y;RAUM;SCHRIFTGRÖßE"), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            frame.add(roomArea, gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 6;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            frame.add(new JLabel("IP:Port"), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 7;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            frame.add(ipField, gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 8;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            frame.add(modeBox, gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 9;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            frame.add(new JLabel("Port"), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 10;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            frame.add(portField, gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 11;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            frame.add(button, gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 12;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            frame.add(testbutton, gbc);

            frame.setSize(300, 500);

            frame.setResizable(false);

            frame.addRenderL(c -> {
                if(frame.getMousePosition() != null) {
                    c.setColor(Color.RED);
                    c.drawString("X: "+frame.getMousePosition().x+" Y: "+frame.getMousePosition().y, 10, 10);
                }
            });

            frame.update();

            frame.setVisible(true);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String modes = (String) modeBox.getSelectedItem();

                    Mode mode = Mode.valueOf(modes);
                    if(mode.equals(Mode.UNKNOWN)) {
                        return;
                    }
                    // TODO: 11.04.2023 Validierung
                    config.set("mode", modes);

                    if(mode.hasClient()) {
                        config.set("fullscreen", fullscreenbox.isSelected());
                        config.set("background", backgroundField.getText());
                        config.set("ip", ipField.getText());
                    }

                    if(mode.hasServer()) {
                        config.set("port", Integer.parseInt(portField.getText()));
                    }

                    try {
                        FileWriter writer = new FileWriter("rooms.speedconf");
                        for (String s : roomArea.getText().split("\n")) {
                            if(s.equals("")) {
                                continue;
                            }
                            writer.write(s+"\n");
                        }
                        writer.flush();
                        writer.close();
                    } catch (IOException ex) {

                    }

                    config.save();

                    frame.dispose();

                    try {
                        Launcher.main(new String[0]);
                    } catch (Throwable ex) {

                    }
                }
            });
            testbutton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EJFrame testFrame = new EJFrame("Full Test");
                    if(fullscreenbox.isSelected()) {
                        testFrame.setResizable(false);
                        testFrame.setUndecorated(true);
                        testFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
                    }

                    testFrame.setVisible(true);
                    testFrame.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                            if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                                testFrame.dispose();
                            }
                        }
                    });

                    testFrame.addRenderL(c -> {
                        if(testFrame.getMousePosition() != null) {
                            c.setColor(Color.RED);
                            c.drawString("X: "+testFrame.getMousePosition().x+" Y: "+testFrame.getMousePosition().y, 10, 10);
                        }
                    });

                    testFrame.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            roomArea.append("\n"+e.getPoint().x+";"+e.getPoint().getY());
                        }
                    });

                    new Thread(() -> {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ignored) {

                        }
                        try {
                            BufferedImage image = new BufferedImage(testFrame.getWidth(), testFrame.getHeight(), BufferedImage.TYPE_INT_RGB);
                            image.getGraphics().drawImage(ImageIO.read(new File(backgroundField.getText())).getScaledInstance(testFrame.getWidth(), testFrame.getHeight(), Image.SCALE_SMOOTH),
                                    0,0,null);
                            testFrame.setBackground(image);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                }
            });
        }
    }

    private static GridBagConstraints makeContainer(int x, int y, int width, int height){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        if(width != -1) {
            gbc.gridwidth = width;
        }
        if(height != -1) {
            gbc.gridheight = height;
        }
        gbc.insets = new Insets(3,3,3,3);

        return gbc;
    }
}
