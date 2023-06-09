package de.emilschlampp.probecounterserver.util;

import de.emilschlampp.probecounterserver.client.ClientMain;
import de.emilschlampp.probecounterserver.server.ServerMain;
import de.emilschlampp.probecounterserver.util.lang.Translation;

public enum Mode {
    SERVER(new ServerMain()),
    CLIENT(new ClientMain()),
    BOTH(() -> {
        new Thread(Mode.CLIENT::init).start();
        new Thread(Mode.SERVER::init).start();
    }),
    UNKNOWN(() -> {
        System.err.println(new Translation("mode.notFound"));
        System.exit(1);
    });

    private final Runnable runnable;
    Mode(Runnable runnable) {
        this.runnable = runnable;
    }

    public void init() {
        runnable.run();
    }

    public boolean hasServer() {
        return this.name().equals("SERVER") || this.name().equals("BOTH");
    }

    public boolean hasClient() {
        return this.name().equals("CLIENT") || this.name().equals("BOTH");
    }
}
