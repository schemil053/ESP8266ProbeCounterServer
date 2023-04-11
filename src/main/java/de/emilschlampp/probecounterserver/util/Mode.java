package de.emilschlampp.probecounterserver.util;

public enum Mode {
    SERVER(() -> {

    }),
    CLIENT(() -> {

    }),
    BOTH(() -> {
        new Thread(() -> {
            Mode.CLIENT.init();
        }).start();
        new Thread(() -> {
            Mode.SERVER.init();
        }).start();
    }),
    UNKNOWN(() -> {
        System.err.println("Mode not found, stopping...");
        System.exit(1);
    });

    private Runnable runnable;
    Mode(Runnable runnable) {
        this.runnable = runnable;
    }

    public void init() {
        runnable.run();
    }
}
