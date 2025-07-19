package de.example.landsystem;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.Set;

public class PlayerJoinListener implements Listener {

    private final Main plugin;

    public PlayerJoinListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        FileConfiguration config = plugin.getConfig();

        // Prüfen, ob dieser Spieler noch offene Abstimmungen hat
        if (!config.contains("pendingVotes." + uuid)) return;

        Set<String> landNames = config.getConfigurationSection("pendingVotes." + uuid).getKeys(false);
        for (String landName : landNames) {
            String requesterUUID = config.getString("pendingVotes." + uuid + "." + landName);
            String requesterName = config.getString("requests." + landName + "." + requesterUUID + ".name");

            // Sende Nachricht an Spieler
            player.sendMessage("§7Staatsbürgerschaftsanfrage von §e" + requesterName + " §7für §6" + landName + ":");

            player.spigot().sendMessage(
                    LandGUIListener.createAcceptComponent(requesterName, landName),
                    new net.md_5.bungee.api.chat.TextComponent(" "),
                    LandGUIListener.createDenyComponent(requesterName, landName)
            );
        }

        // Entferne die Benachrichtigungen, da sie nun gesendet wurden
        config.set("pendingVotes." + uuid, null);
        plugin.saveConfig();
    }
}
