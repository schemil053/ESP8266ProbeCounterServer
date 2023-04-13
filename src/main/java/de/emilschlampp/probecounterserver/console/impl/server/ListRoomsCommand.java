package de.emilschlampp.probecounterserver.console.impl.server;

import de.emilschlampp.probecounterserver.console.Command;
import de.emilschlampp.probecounterserver.server.ServerMain;

public class ListRoomsCommand extends Command {

    public ListRoomsCommand() {
        super("listrooms");
    }

    @Override
    public void run(String[] args) {
        for (String s : ServerMain.devicesInRoom.keySet()) {
            System.out.println(s);
        }
    }
}