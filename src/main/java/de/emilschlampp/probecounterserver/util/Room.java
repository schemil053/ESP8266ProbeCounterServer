package de.emilschlampp.probecounterserver.util;

public class Room {
    public double x;
    public double y;
    public int fontsize;
    public String name = "?";

    public Room(double x, double y, int fontsize, String name) {
        this.x = x;
        this.y = y;
        this.fontsize = fontsize;
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getFontsize() {
        return fontsize;
    }

    public void setFontsize(int fontsize) {
        this.fontsize = fontsize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getXForFrame(int width) {
        return (int) (x*width);
    }

    public int getYForFrame(int height) {
        return (int) (y*height);
    }
}
