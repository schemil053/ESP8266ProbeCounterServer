package de.emilschlampp.probecounterserver.util.color;


public enum ConsoleColor {
    RESET("[0m"),
    NEGATIVE("[7m"),
    POSITIVE("[27m"),
    NO_UNDERLINE("[24m"),
    UNDERLINE("[4m"),
    BOLD("[1m"),
    WHITE("[97m"),
    BG_WHITE("[107m"),
    LIGHT_CYAN("[96m"),
    BG_LIGHT_CYAN("[106m"),
    LIGHT_MAGENTA("[95m"),
    BG_LIGHT_MAGENTA("[105m"),
    LIGHT_BLUE("[94m"),
    BG_LIGHT_BLUE("[104m"),
    LIGHT_YELLOW("[93m"),
    BG_LIGHT_YELLOW("[103m"),
    LIGHT_GREEN("[92m"),
    BG_LIGHT_GREEN("[102m"),
    LIGHT_RED("[91m"),
    BG_LIGHT_RED("[101m"),
    DARK_GRAY("[90m"),
    BG_DARK_GRAY("[100m"),
    LIGHT_GRAY("[37m"),
    BG_LIGHT_GRAY("[47m"),
    CYAN("[36m"),
    BG_CYAN("[46m"),
    MAGENTA("[35m"),
    BG_MAGENTA("[45m"),
    BLUE("[34m"),
    BG_BLUE("[44m"),
    YELLOW("[33m"),
    BG_YELLOW("[43m"),
    GREEN("[32m"),
    BG_GREEN("[42m"),
    RED("[31m"),
    BG_RED("[41m"),
    BLACK("[30m"),
    BG_BLACK("[40m")


    ;


    ConsoleColor(String a) {
        char character = 27;
        this.a = character+a;
    }

    private final String a;
    @Override
    public String toString() {
        return a;
    }

    public static String stripColor(String text) {
        for(ConsoleColor color : ConsoleColor.values()) {
            text = text.replace(color.toString(), "");
        }
        return text;
    }

    public static boolean containsColor(String text) {
        for(ConsoleColor color : ConsoleColor.values()) {
            if(text.contains(color.toString())) {
                return true;
            }
        }
        return false;
    }

    public static void testColors() {
        System.out.println("Testing colors...  ");
        for(ConsoleColor color : ConsoleColor.values()) {
            System.out.println(color+"This is a Test"+ConsoleColor.RESET);
        }
    }

    public static void testColors(String s) {
        for(ConsoleColor color : ConsoleColor.values()) {
            System.out.println(color+s+ConsoleColor.RESET);
        }
    }
}
