package de.emilschlampp.probecounterserver.setup;

import de.emilschlampp.probecounterserver.Launcher;
import de.emilschlampp.probecounterserver.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
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

            JCheckBox box = new JCheckBox("Vollbild");

            JTextArea area = new JTextArea(8, 20);

            area.setPreferredSize(new Dimension(200, 100));

            JTextField textField = new JTextField();

            area.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            JComboBox mode = new JComboBox(Arrays.stream(Mode.values()).map(Enum::name).collect(Collectors.toList()).toArray());


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
                    area.setText(text.replaceFirst("\n", ""));
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
            frame.add(textField, gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            frame.add(box, gbc);

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
            frame.add(area, gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 6;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            frame.add(mode, gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 7;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            frame.add(button, gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 8;
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
                    config.set("mode", (String) mode.getSelectedItem());
                    config.set("fullscreen", box.isSelected());
                    config.set("background", textField.getText());
                    config.save();
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
