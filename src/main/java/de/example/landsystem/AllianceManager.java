package de.example.landsystem;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class AllianceManager {

    private final Main plugin;

    public AllianceManager(Main plugin) {
        this.plugin = plugin;
    }

    public boolean areAllied(String landA, String landB) {
        FileConfiguration config = plugin.getConfig();
        return config.getBoolean("alliances." + landA + "." + landB, false);
    }

    public void setAlliance(String landA, String landB, boolean allied) {
        FileConfiguration config = plugin.getConfig();
        config.set("alliances." + landA + "." + landB, allied);
        config.set("alliances." + landB + "." + landA, allied);
        plugin.saveConfig();
    }

    public void sendAllianceRequest(Player requester, String fromLand, String toLand) {
        Main plugin = this.plugin;

        Land targetLand = plugin.getLandManager().getLand(toLand);
        if (targetLand == null) {
            requester.sendMessage("§cZiel-Land nicht gefunden.");
            return;
        }

        Player president = Bukkit.getPlayer(targetLand.getPresident());
        if (president == null) {
            requester.sendMessage("§cDer Präsident von §e" + toLand + " §cist gerade nicht online.");
            return;
        }

        // Chat-Nachricht mit ClickEvent
        president.sendMessage("§eAllianzanfrage von §6" + fromLand + "§e erhalten.");
        president.spigot().sendMessage(ChatMessageUtils.getAllianceRequestMessage(fromLand, toLand));
        requester.sendMessage("§aAnfrage wurde an den Präsidenten von §e" + toLand + " §agesendet.");
    }
}
