package de.emilschlampp.probecounterserver.console.impl.server;

import de.emilschlampp.probecounterserver.console.Command;
import de.emilschlampp.probecounterserver.server.ServerMain;
import de.emilschlampp.probecounterserver.util.lang.Translation;

import java.util.HashMap;
import java.util.Map;

public class ListDevicesCommand extends Command {
    public ListDevicesCommand() {
        super("listdevices");
    }

    @Override
    public void run(String[] args) {
        if(args.length != 1) {
            System.out.println(new Translation("command.listdevices.help"));
        } else {
            System.out.printf("| %-4s | %-16s  |%n", "RSSI", "MAC");
            for(Map.Entry<String, Integer> device : ServerMain.devicesInRoom.getOrDefault(args[0], new HashMap<>()).entrySet()) {
                System.out.printf("| %-4s | %-16s |%n", device.getValue(), device.getKey());
            }
            System.out.println(ServerMain.map.getOrDefault(args[0], 0));
        }
    }
}
