package de.emilschlampp.probecounterserver.setup;

import de.emilschlampp.probecounterserver.Launcher;
import de.emilschlampp.probecounterserver.client.ClientMain;
import de.emilschlampp.probecounterserver.util.EJFrame;
import de.emilschlampp.probecounterserver.util.Mode;
import de.emilschlampp.probecounterserver.util.SConfig;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SetupWindow {
    public static void startSetup()  {
        if(GraphicsEnvironment.isHeadless() || (!System.getProperty("setupHeadless", "?").equals("?"))) {
            CLISetup.startCLISetup();
        } else {
            startGUISetup();
        }
    }

    private static void startGUISetup() {
        SConfig config = SConfig.getSConfig("config.econf");
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
        JTextField checkField = new JTextField();
        JComboBox modeBox = new JComboBox(Arrays.stream(Mode.values()).map(Enum::name).collect(Collectors.toList()).toArray());

        fullscreenbox.setSelected(true);
        roomArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        loadTextBox(roomArea);


        loadLayout(frame,
                new JLabel("Hintergrundbild"),
                backgroundField,

                fullscreenbox,

                new JLabel("GUI-Räume"),
                new JLabel("X;Y;RAUM;SCHRIFTGRÖßE"),
                new JLabel("0.2;0.4;Raum1;20"),
                roomArea,

                new JLabel("IP:Port"),
                ipField,

                new JLabel("Checkzeit (in Sekunden)"),
                checkField,

                Box.createVerticalStrut(20),
                modeBox,
                Box.createVerticalStrut(20),

                new JLabel("Port"),
                portField,

                button,
                testbutton
        );

        frame.setSize(600, 500);
        //frame.setResizable(false);

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
                config.set("mode", modes);

                if(backgroundField.getText().equals("") || !new File(backgroundField.getText()).isFile()) {
                    JOptionPane.showMessageDialog(frame, "Bitte überprüfe den eingegebenen Hintergrund!");
                    return;
                }
                try{
                   Integer.parseInt(ipField.getText().split(":", 2)[1]);
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(frame, "Bitte überprüfe die eingegebene IP!");
                    return;
                }

                try {
                    Integer.parseInt(checkField.getText());
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(frame, "Bitte überprüfe die eingegebene Checkzeit!");
                    return;
                }

                if(mode.hasClient()) {
                    config.set("fullscreen", fullscreenbox.isSelected());
                    config.set("background", backgroundField.getText());
                    config.set("ip", ipField.getText());
                    config.set("checktime", Integer.parseInt(checkField.getText()));
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
                } catch (IOException ignored) {

                }

                config.save();

                frame.dispose();

                try {
                    Launcher.main(new String[0]);
                } catch (Throwable ex) {
                    System.err.println("Beim starten ist ein Fehler aufgetreten:");
                    ex.printStackTrace();
                }
            }
        });
        testbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EJFrame testFrame = new EJFrame("Full Test");

                if(backgroundField.getText().equals("") || !new File(backgroundField.getText()).isFile()) {
                    JOptionPane.showMessageDialog(frame, "Bitte überprüfe den eingegebenen Hintergrund!");
                    return;
                }
                try{
                    Integer.parseInt(ipField.getText().split(":", 2)[1]);
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(frame, "Bitte überprüfe die eingegebene IP!");
                    return;
                }

                try {
                    Integer.parseInt(checkField.getText());
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(frame, "Bitte überprüfe die eingegebene Checkzeit!");
                    return;
                }

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
                        if(e.getKeyCode() == KeyEvent.VK_F11) {
                            if(testFrame.getExtendedState() == Frame.MAXIMIZED_BOTH) {
                                testFrame.setExtendedState(Frame.NORMAL);
                                testFrame.setSize(500, 400);
                                testFrame.update();
                                testFrame.dispose();
                                testFrame.setUndecorated(false);
                                testFrame.setVisible(true);
                                ClientMain.loadBG(testFrame, null);
                            } else {
                                testFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
                                testFrame.dispose();
                                testFrame.setUndecorated(true);
                                testFrame.setVisible(true);
                                ClientMain.loadBG(testFrame, null);
                            }
                        }
                    }
                });

                testFrame.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        ClientMain.loadBG(frame, null);
                    }
                });

                testFrame.addRenderL(c -> {
                    if(testFrame.getMousePosition() != null) {
                        c.setColor(Color.RED);
                        c.drawString("X: "+testFrame.getMousePosition().x+" Y: "+testFrame.getMousePosition().y+" ("+((double) testFrame.getMousePosition().x/testFrame.getWidth())+";"+((double) testFrame.getMousePosition().y/testFrame.getHeight())+")", 10, 20);
                    }
                });

                testFrame.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        roomArea.append("\n"+((double) e.getPoint().x/testFrame.getWidth())+";"+((double) e.getPoint().y/testFrame.getHeight()));
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

    private static void loadTextBox(JTextArea roomArea) {
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
    }

    private static void loadLayout(EJFrame frame, Component... components) {
        int gridy = 0;
        for(Component component : components) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = gridy;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            if(component instanceof JTextField || component instanceof JTextArea) {
                gbc.anchor = GridBagConstraints.WEST;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weightx = 1.0;
            }
            frame.add(component, gbc);
            gridy++;
        }
    }
}
