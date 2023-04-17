package de.emilschlampp.probecounterserver.console.impl.both;

import de.emilschlampp.probecounterserver.console.Command;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop");
    }

    @Override
    public void run(String[] args) {
        System.exit(0);
    }
}
