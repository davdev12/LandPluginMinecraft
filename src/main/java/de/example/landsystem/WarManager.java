package de.example.landsystem;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;

public class WarManager {

    private final Main plugin;

    public WarManager(Main plugin) {
        this.plugin = plugin;
    }

    public void startWar(String landA, String landB) {
        FileConfiguration config = plugin.getConfig();
        config.set("wars." + landA + "." + landB, true);
        config.set("wars." + landB + "." + landA, true);
        plugin.saveConfig();
    }

    public boolean isAtWar(String landA, String landB) {
        FileConfiguration config = plugin.getConfig();
        return config.getBoolean("wars." + landA + "." + landB, false)
                || config.getBoolean("wars." + landB + "." + landA, false);
    }

    public void endWar(String landA, String landB) {
        plugin.getConfig().set("wars." + landA + "." + landB, false);
        plugin.getConfig().set("wars." + landB + "." + landA, false);
        plugin.saveConfig();
    }

    public Set<String> getEnemies(String landName) {
        Set<String> enemies = new HashSet<>();
        if (!plugin.getConfig().contains("wars." + landName)) return enemies;

        for (String other : plugin.getConfig().getConfigurationSection("wars." + landName).getKeys(false)) {
            if (plugin.getConfig().getBoolean("wars." + landName + "." + other)) {
                enemies.add(other);
            }
        }
        return enemies;
    }
}
