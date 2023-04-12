package de.emilschlampp.probecounterserver.util;

import java.io.*;
import java.util.*;

public class SConfig {
    private static final Map<String, SConfig> stringsConfigHashMap = new HashMap<>();
    public static SConfig getSConfig(String name) {
        if(stringsConfigHashMap.get(name) == null) {
            stringsConfigHashMap.put(name, new SConfig(new File(name)));
        }
        stringsConfigHashMap.get(name).name = name;
        return stringsConfigHashMap.get(name);
    }

    public static List<SConfig> getCache() {
        return new ArrayList<>(stringsConfigHashMap.values());
    }

    public static void reloadALL() {
        for(SConfig config : SConfig.getCache()) {
            if(config.canReload()) {
                config.reload();
            }
        }
    }

    private boolean canReload = true;

    public boolean canReload() {
        return canReload;
    }

    public void setCanReload(boolean canReload) {
        this.canReload = canReload;
    }

    public List<String> getComments() {
        return new ArrayList<>(comments);
    }

    private final Map<String, String> data = new HashMap<>();
    private final File file;
    private final List<String> comments = new ArrayList<>();
    private String name;

    public SConfig(File file) {
        this.file = file;
        read();
    }

    public void reload() {
        read();
    }

    public void addComment(String line) {
        String s;
        if(line.startsWith("#")) {
            s = line.replaceFirst("#", "");
        } else {
            s = line;
        }
        comments.add(s);
    }

    public void removeComment(String line) {
        String s;
        if(line.startsWith("#")) {
            s = line.replaceFirst("#", "");
        } else {
            s = line;
        }
        while(comments.remove(s));
    }

    public void clearComments() {
        comments.clear();
    }

    public void clearAll() {
        data.clear();
        comments.clear();
    }

    public void clearData() {
        data.clear();
    }

    private void read() {
        comments.clear();
        data.clear();
        if(!file.exists()) {
            return;
        }
        Scanner scan = null;
        try {
            scan = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        while (scan.hasNext()) {
            String line = scan.nextLine();

            if(line.startsWith("#")) {
                addComment(line);
                continue;
            }
            if(!line.contains("=")) {
                addComment(line);
                continue;
            }

            String[] strings = line.split("=", 2);

            if(strings.length == 1) {
                data.put(replacesw(strings[0], true), "");
                continue;
            }


            data.put(replacesw(strings[0], true), replacesw(strings[1], true));
        }


        scan.close();
    }

    public Set<String> getKeySet() {
        return data.keySet();
    }

    public File getFile() {
        return file;
    }

    public void delete() {
        file.delete();
        if(name != null) {
            stringsConfigHashMap.remove(name);
        }
        clearAll();
    }

    public void save() {
        try {

            if(file.exists()) {
                file.delete();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));


            for(String s : comments) {
                writer.write("#"+s);
                writer.newLine();
            }

            for(String key : data.keySet()) {
                writer.write(replacesw(key, false)+"="+replacesw(data.getOrDefault(key, "none"), false));
                writer.newLine();
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String replacesw(String s, boolean dec) {
        String object1 = s;
        if(dec) {
            object1 = object1.replace("*(gleichheitszeichen)*", "=");
        } else {
            object1 = object1.replace("=", "*(gleichheitszeichen)*");
        }

        return object1;
    }

    public void set(String key, String object) {
        if(data.get(key) != null) {
            data.remove(key);
        }
        data.put(key, object);
    }

    public void set(String key, Boolean object) {
        set(key, object+"");
    }

    public void set(String key, Double object) {
        set(key, object+"");
    }

    public void set(String key, Long object) {
        set(key, object+"");
    }

    public void set(String key, Integer object) {
        set(key, object+"");
    }

    public void unset(String key) {
        data.remove(key);
    }

    public String getString(String key) {
        return data.get(key);
    }

    public Boolean isSet(String key) {
        return data.get(key) != null;
    }

    public void setDefault(String key, String value, boolean save) {
        if(isSet(key)) {
            return;
        }
        set(key, value);
        if(save) {
            save();
        }
    }

    public void setDefault(String key, Integer value, boolean save) {
        if(isInt(key)) {
            return;
        }
        set(key, value+"");
        if(save) {
            save();
        }
    }

    public void setDefault(String key, Double value, boolean save) {
        if(isDouble(key)) {
            return;
        }
        set(key, value+"");
        if(save) {
            save();
        }
    }

    public void setDefault(String key, Boolean value, boolean save) {
        if(isBoolean(key)) {
            return;
        }
        set(key, value+"");
        if(save) {
            save();
        }
    }

    public void setDefault(String key, Long value, boolean save) {
        if(isLong(key)) {
            return;
        }
        set(key, value+"");
        if(save) {
            save();
        }
    }

    public boolean isLong(String key) {
        if(!isSet(key)) {
            return false;
        }
        try {
            Long.parseLong(getString(key));
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public Long getLong(String key) {
        if(!isLong(key)) {
            return null;
        }
        return Long.parseLong(getString(key));
    }

    public boolean isInt(String key) {
        if(!isSet(key)) {
            return false;
        }
        try {
            Integer.parseInt(getString(key));
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public Integer getInt(String key) {
        if(!isInt(key)) {
            return null;
        }
        return Integer.parseInt(getString(key));
    }

    public boolean isDouble(String key) {
        if(!isSet(key)) {
            return false;
        }
        try {
            Double.parseDouble(getString(key));
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public Double getDouble(String key) {
        if(!isDouble(key)) {
            return null;
        }
        return Double.parseDouble(getString(key));
    }


    public boolean isBoolean(String key) {
        if(!isSet(key)) {
            return false;
        }
        return Arrays.asList("true", "false").contains(getString(key));
    }

    public Boolean getBoolean(String key) {
        if(!isBoolean(key)) {
            return null;
        }
        return getString(key).equalsIgnoreCase("true");
    }
}


