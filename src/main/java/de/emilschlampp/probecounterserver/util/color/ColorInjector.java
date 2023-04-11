package de.emilschlampp.probecounterserver.util.color;

import java.io.PrintStream;

public class ColorInjector {
    private static boolean injected = false;
    public static void inject() {
        if(injected) {
            return;
        }
        injected = true;
        if(SystemUtils.getOS().equals(SystemUtils.OS.WINDOWS)) {
            try {
                Runtime.getRuntime().exec("reg add HKEY_CURRENT_USER\\Console /v VirtualTerminalLevel /t REG_DWORD /d 0x00000001 /f");
            } catch (Exception e) {

            }
        }
        System.setOut(new PrintStream(System.out) {
            @Override
            public void println(String x) {
                String y = x;
                if(!SystemUtils.acceptColors()) {
                    y = ConsoleColor.stripColor(y);
                }
                if(ConsoleColor.containsColor(y)) {
                    y = y+ConsoleColor.RESET;
                }
                super.println(y);
            }

            public void print(String s) {
                String y = s;
                if(!SystemUtils.acceptColors()) {
                    y = ConsoleColor.stripColor(y);
                }
                super.print(y);
            }
        });
        System.setErr(new PrintStream(System.err) {
            @Override
            public void println(String x) {
                String y = x;
                y = ConsoleColor.LIGHT_RED+y+ConsoleColor.RESET;
                if(!SystemUtils.acceptColors()) {
                    y = ConsoleColor.stripColor(y);
                }
                super.println(y);
            }
            public void print(String s) {
                String y = s;
                if(!SystemUtils.acceptColors()) {
                    y = ConsoleColor.stripColor(y);
                }
                if(ConsoleColor.containsColor(y)) {
                    y = y+ConsoleColor.RESET;
                }
                super.print(y);
            }
        });
    }
}
