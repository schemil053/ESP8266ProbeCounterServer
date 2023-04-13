package de.emilschlampp.probecounterserver.console;

public abstract class Command {
    private final String name;
    public Command(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public abstract void run(String[] args);
}
