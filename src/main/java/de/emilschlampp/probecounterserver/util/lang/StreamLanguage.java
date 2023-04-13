package de.emilschlampp.probecounterserver.util.lang;

import de.emilschlampp.probecounterserver.util.color.ConsoleColor;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StreamLanguage implements Language {
    private final Map<String, String> data = new HashMap<>();

    public StreamLanguage(InputStream stream) {
        if(stream != null) {
            load(stream);
        }
    }

    private void load(InputStream stream) {
        data.clear();
        try {
            Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8);

            String currentkey = "?";
            String currentval = "";

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.startsWith("#") && !line.startsWith("##")) {
                    continue;
                }
                if(line.startsWith("#")) {
                    line = line.replaceFirst("#", "");
                }
                if(line.startsWith("=") && !line.startsWith("==")) {
                    if(!currentkey.equals("")) {
                        for(ConsoleColor color : ConsoleColor.values()) {
                            currentval = currentval.replace("%"+(color.name().toUpperCase())+"%", color.toString());
                            currentval = currentval.replace("%"+(color.name().toLowerCase())+"%", color.toString());
                        }
                        if(ConsoleColor.containsColor(currentval)) {
                            currentval = currentval+ConsoleColor.RESET;
                        }
                        data.put(currentkey, currentval);
                    }
                    currentkey = line.replaceFirst("=", "");
                    currentval = "";
                    continue;
                }
                if(line.startsWith("=")) {
                    line = line.replaceFirst("=", "");
                }
                currentval = currentval+(currentval.equals("") ? "" : "\n")+line;
            }
            if(!currentkey.equals("")) {
                for(ConsoleColor color : ConsoleColor.values()) {
                    currentval = currentval.replace("%"+(color.name().toUpperCase())+"%", color.toString());
                    currentval = currentval.replace("%"+(color.name().toLowerCase())+"%", color.toString());
                }
                if(ConsoleColor.containsColor(currentval)) {
                    currentval = currentval+ConsoleColor.RESET;
                }
                data.put(currentkey, currentval);
            }
        } catch (Throwable ignored) {
            ignored.printStackTrace();
        }
    }

    @Override
    public String getTranslation(String key) {
        return data.get(key);
    }
}
