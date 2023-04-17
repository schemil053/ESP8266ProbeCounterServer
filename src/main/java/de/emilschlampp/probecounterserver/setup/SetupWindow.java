package de.emilschlampp.probecounterserver.setup;

import de.emilschlampp.probecounterserver.Launcher;
import de.emilschlampp.probecounterserver.client.ClientMain;
import de.emilschlampp.probecounterserver.console.ConsoleThread;
import de.emilschlampp.probecounterserver.util.EJFrame;
import de.emilschlampp.probecounterserver.util.Mode;
import de.emilschlampp.probecounterserver.util.SConfig;
import de.emilschlampp.probecounterserver.util.Values;
import de.emilschlampp.probecounterserver.util.lang.Translation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
        SConfig config = Launcher.getConfig();
        EJFrame frame = new EJFrame(new Translation("window.title").toString());

        frame.setBackground(Color.WHITE);

        frame.setLayout(new GridBagLayout());

        try {
            frame.setIconImage(ImageIO.read(Objects.requireNonNull(SetupWindow.class.getResourceAsStream("/icon.png"))));
        } catch (Exception ignored) {
            System.err.println(new Translation("icon.Error"));
        }

        JButton saveAndApply = new JButton(new Translation("window.saveAndApply").toString());
        JButton testbutton = new JButton(new Translation("window.testWindow").toString());
        JCheckBox fullscreenbox = new JCheckBox(new Translation("window.fullScreen").toString());
        JTextArea roomArea = new JTextArea(8, 20);
        roomArea.setPreferredSize(new Dimension(200, 100));
        JTextField backgroundField = new JTextField();
        JTextField portField = new JTextField();
        JTextField ipField = new JTextField();
        JTextField checkField = new JTextField();
        JComboBox modeBox = new JComboBox(Arrays.stream(Mode.values()).map(Enum::name).collect(Collectors.toList()).toArray());

        if(config.isSet("background")) {
            backgroundField.setText(config.getString("background"));
        }
        if(config.isSet("port")) {
            portField.setText(config.getString("port"));
        }
        if(config.isSet("ip")) {
            ipField.setText(config.getString("ip"));
        }
        if(config.isSet("checktime")) {
            checkField.setText(config.getString("checktime"));
        }
        if(config.isSet("mode")) {
            modeBox.setSelectedItem(config.getString("mode"));
        } else {
            modeBox.setSelectedItem(Mode.BOTH.name());
        }

        fullscreenbox.setSelected(true);
        roomArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        loadTextBox(roomArea);


        loadLayout(frame,
                new JLabel(new Translation("window.backGroundImage").toString()),
                backgroundField,

                fullscreenbox,

                new JLabel(new Translation("window.guiRooms").toString()),
                new JLabel(new Translation("window.guiRooms.decl").toString()),
                new JLabel(new Translation("window.guiRooms.example").toString()),
                roomArea,

                new JLabel(new Translation("window.ipAndPort").toString()),
                ipField,

                new JLabel(new Translation("window.checkTime").toString()),
                checkField,

                Box.createVerticalStrut(20),
                modeBox,
                Box.createVerticalStrut(20),

                new JLabel(new Translation("window.port").toString()),
                portField,

                saveAndApply,
                testbutton
        );

        frame.setSize(600, 500);

        if(Launcher.isDebug() && ConsoleThread.shouldLog) {
            frame.addRenderL(c -> {
                if (frame.getMousePosition() != null) {
                    c.setColor(Color.RED);
                    c.drawString("X: " + frame.getMousePosition().x + " Y: " + frame.getMousePosition().y, 10, 10);
                }
            });
        }

        frame.update();

        frame.setVisible(true);

        saveAndApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String modes = (String) modeBox.getSelectedItem();

                Mode mode = Mode.valueOf(modes);
                if(mode.equals(Mode.UNKNOWN)) {
                    return;
                }
                config.set("mode", modes);

                if(mode.hasClient()) {
                    if (backgroundField.getText().equals("") || !new File(backgroundField.getText()).isFile()) {
                        JOptionPane.showMessageDialog(frame, new Translation("window.setup.invalidBackGround").toString());
                        return;
                    }
                    try {
                        Integer.parseInt(ipField.getText().split(":", 2)[1]);
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(frame, new Translation("window.setup.invalidIP").toString());
                        return;
                    }

                    try {
                        Integer.parseInt(checkField.getText());
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(frame, new Translation("window.setup.invalidCheckTime").toString());
                        return;
                    }
                }
                if(mode.hasServer()) {
                    try {
                        Integer.parseInt(portField.getText());
                        if(Integer.parseInt(portField.getText()) < 1 ||  Integer.parseInt(portField.getText()) > Values.maxPort) {
                            throw new IllegalArgumentException("");
                        }
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(frame, new Translation("window.setup.invalidPort").toString());
                        return;
                    }
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
                    System.err.println(new Translation("window.startError"));
                    ex.printStackTrace();
                }
            }
        });
        testbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String modes = (String) modeBox.getSelectedItem();
                Mode mode = Mode.valueOf(modes);
                if(!mode.hasClient()) {
                    JOptionPane.showMessageDialog(frame, new Translation("window.testNoClient"));
                    return;
                }
                EJFrame testFrame = new EJFrame(new Translation("window.fullTest").toString());

                if(mode.hasClient()) {
                    if (backgroundField.getText().equals("") || !new File(backgroundField.getText()).isFile()) {
                        JOptionPane.showMessageDialog(frame, new Translation("window.setup.invalidBackGround").toString());
                        return;
                    }
                    try {
                        Integer.parseInt(ipField.getText().split(":", 2)[1]);
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(frame, new Translation("window.setup.invalidIP").toString());
                        return;
                    }

                    try {
                        Integer.parseInt(checkField.getText());
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(frame, new Translation("window.setup.invalidCheckTime").toString());
                        return;
                    }
                }
                if(mode.hasServer()) {
                    try {
                        Integer.parseInt(portField.getText());
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(frame, new Translation("window.setup.invalidPort").toString());
                        return;
                    }
                }

                if(fullscreenbox.isSelected()) {
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
                                ClientMain.loadBG(testFrame, backgroundField.getText());
                            } else {
                                testFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
                                testFrame.dispose();
                                testFrame.setUndecorated(true);
                                testFrame.setVisible(true);
                                ClientMain.loadBG(testFrame, backgroundField.getText());
                            }
                        }
                    }
                });

                testFrame.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        ClientMain.loadBG(testFrame, backgroundField.getText());
                    }
                });

                testFrame.addRenderL(c -> {
                    if(testFrame.getMousePosition() != null) {
                        c.setColor(Color.RED);
                        c.drawString("X: "+testFrame.getMousePosition().x+" Y: "+testFrame.getMousePosition().y+" ("+((double) testFrame.getMousePosition().x/testFrame.getWidth())+";"+((double) testFrame.getMousePosition().y/testFrame.getHeight())+")", 10, 30);
                    }
                });

                testFrame.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        roomArea.append((roomArea.getText().equals("") ? "" : "\n")+((double) e.getPoint().x/testFrame.getWidth())+";"+((double) e.getPoint().y/testFrame.getHeight()));
                    }
                });
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
