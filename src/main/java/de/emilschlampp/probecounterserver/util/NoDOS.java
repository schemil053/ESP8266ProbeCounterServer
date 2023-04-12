package de.emilschlampp.probecounterserver.util;


import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class NoDOS {
    private static final Map<String, Integer> map = new HashMap<>();
    private static final Map<String, Long> lastpinged = new HashMap<>();
    private static final Map<String,Long> blocked = new HashMap<>();

    private static boolean usedosprotection = true;


    private static final SConfig config = SConfig.getSConfig("ip-blacklist.econf");

    public static boolean checkDDOS(Socket socket) {
        return checkDDOS(getIP(socket));
    }

    private static String getIP(Socket socket) {
        return socket.getInetAddress().getHostAddress();
    }

    private static boolean ini = false;
    public static void initADOS() {
        if(ini) {
            return;
        }
        ini = true;
        new Thread(() -> {
            config.setDefault("use", true, true);
            usedosprotection = config.getBoolean("use");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                config.clearAll();
                config.addComment("File for the IP-Blacklist");
                config.set("use", usedosprotection);
                for (String ip : blocked.keySet()) {
                    config.set(ip, blocked.get(ip));
                }
                config.save();
            }, "NoDDOS-Shutdown"));
            for(String s : config.getKeySet()) {
                if(config.isLong(s)) {
                    if(config.getLong(s) > System.currentTimeMillis()) {
                        blocked.put(s, config.getLong(s));
                    }
                }
            }
        }, "NoDDOS-Thread").start();
    }

    private static boolean checkDDOS(String ip) {
        if(!usedosprotection) {
            return false;
        }
        if(blocked.containsKey(ip)) {
            if(blocked.get(ip) > System.currentTimeMillis()) {
                return true;
            } else {
                blocked.remove(ip);
            }
        }
        if(lastpinged.containsKey(ip)) {
            if(lastpinged.get(ip) > System.currentTimeMillis()) {
                map.put(ip, map.getOrDefault(ip, 0)+1);
                if(map.get(ip) > 10) {
                    map.remove(ip);
                    blocked.put(ip, System.currentTimeMillis()+900000);
                    return true;
                }
            } else {
                map.remove(ip);
            }
        } else {
            map.remove(ip);
        }
        lastpinged.put(ip,System.currentTimeMillis()+100);
        return false;
    }

    static {
        initADOS();
    }
}
