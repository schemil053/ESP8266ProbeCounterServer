package de.emilschlampp.probecounterserver.util;

public class Room {
    public int x, y, fontsize;
    public String name = "?";
    public Room() {

    }

    public Room(int x, int y, int fontsize, String name) {
        this.x = x;
        this.y = y;
        this.fontsize = fontsize;
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
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
}
