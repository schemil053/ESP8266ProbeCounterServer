package de.emilschlampp.probecounterserver.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class EJFrame extends JFrame {
    public EJFrame() {
        this("EJFrame");
    }

    private BufferedImage background = null;
    private final List<Consumer<Graphics>> a = new ArrayList<>();
    private final List<Consumer<Graphics>> b = new ArrayList<>();
    private final JComponent component;

    public void setBG(Color color) {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x <image.getWidth(); x++) {
            for(int y = 0; y <image.getHeight(); y++) {
                image.setRGB(x, y, color.getRGB());
            }
        }
        setBackground(image);
    }

    public EJFrame(String title) {
        super(title);
        JComponent component = new JComponent() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if(background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                }
                a.forEach(s -> { try {
                    s.accept(g);
                } catch (Exception ignored){}});
            }

            @Override
            public void update(Graphics g) {
                super.update(g);
                a.forEach(s -> { try {
                    s.accept(g);
                } catch (Exception ignored){}});
            }

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                a.forEach(s -> { try {
                    s.accept(g);
                } catch (Exception ignored){}});
            }

            @Override
            public void print(Graphics g) {
                super.print(g);
                a.forEach(s -> { try {
                    s.accept(g);
                } catch (Exception ignored){}});
            }
        };
        this.component = component;
        setContentPane(component);
        new Thread(() -> {
            while (true) {
                if(a.isEmpty()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored) {

                    }
                    continue;
                }
                component.repaint();
                Arrays.stream(getComponents()).filter(component::equals).forEach(Component::repaint);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {

                }
            }
        }, "Repainter").start();
        getContentPane().setLayout(null);
        setLayout(null);
        setBackground(Color.BLACK);
    }

    public JComponent getComponent() {
        return component;
    }

    private Point mouse = null;

    public final Point getMouse() {
        return mouse;
    }

    public final void setMouse(Point mouse) {
        this.mouse = mouse;
    }

    @Override
    public Point getMousePosition() throws HeadlessException {
        if(mouse == null) {
            try {
                return super.getMousePosition();
            } catch (Exception exception) {
                return new Point();
            }
        } else {
            return mouse;
        }
    }

    public void addRenderL(Consumer<Graphics> graphicsConsumer) {
        a.add(graphicsConsumer);
    }
    public void addRenderLAfter(Consumer<Graphics> graphicsConsumer) {
        b.add(graphicsConsumer);
    }

    public void specialKeyListen(KeyListener keyListener) {
        component.addKeyListener(keyListener);
    }

    public void setBackground(BufferedImage background) {
        this.background = background;
    }

    public BufferedImage getBackgroundImage() {
        return background;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        b.forEach(c -> {
            c.accept(g);
        });
    }

    public void update() {
        revalidate();
      //  invalidate();
      //  validate();
        repaint();
    }
}
