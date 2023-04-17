package de.emilschlampp.probecounterserver.console.impl.both;

import de.emilschlampp.probecounterserver.console.Command;

public class ClearCommand extends Command {
    public ClearCommand() {
        super("clear");
    }

    @Override
    public void run(String[] args) {
        System.out.println("\r");
        for(int clear = 0; clear < 1000; clear++)
        {
            System.out.println("\b") ;
        }
        System.out.println("\f") ;
        System.out.print('\u000C');
        System.out.print("\033\143");
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println();
    }
}
