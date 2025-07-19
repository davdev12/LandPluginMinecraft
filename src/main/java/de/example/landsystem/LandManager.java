package de.example.landsystem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LandManager {

    private final Map<String, Land> lands = new HashMap<>();
    private final Main plugin;

    public LandManager(Main plugin) {
        this.plugin = plugin;
    }

    public void addLand(Land land) {
        lands.put(land.getName().toLowerCase(), land);
    }

    public Land getLand(String name) {
        return lands.get(name.toLowerCase());
    }

    public boolean landExists(String name) {
        return lands.containsKey(name.toLowerCase());
    }

    public Map<String, Land> getAllLands() {
        return lands;
    }

    public void removeLand(String name) {
        lands.remove(name.toLowerCase());
        plugin.getConfig().set("lands." + name.toLowerCase(), null);
        plugin.saveConfig();
    }

    public Land getLandByCitizen(UUID playerId) {
        for (Land land : lands.values()) {
            if (land.getCitizens().contains(playerId) || land.getPresident().equals(playerId)) {
                return land;
            }
        }
        return null;
    }
}
