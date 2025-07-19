package de.example.landsystem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LandLeaveCommand implements CommandExecutor {
    private final Map<String, Land> lands = new HashMap<>();

    public Map<String, Land> getAllLands() {
        return lands;
    }


    private final Main plugin;

    public LandLeaveCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können das ausführen.");
            return true;
        }

        UUID uuid = player.getUniqueId();
        Land toLeave = null;

        for (Land land : plugin.getLandManager().getAllLands().values()) {
            if (land.isCitizen(uuid)) {
                toLeave = land;
                break;
            }
        }

        if (toLeave == null) {
            player.sendMessage(ChatColor.RED + "Du bist in keinem Land.");
            return true;
        }

        toLeave.removeCitizen(uuid);
        player.sendMessage(ChatColor.YELLOW + "Du hast das Land §6" + toLeave.getName() + " §everlassen.");

        // Wenn Land keine Bürger mehr hat → löschen
        if (toLeave.getCitizens().isEmpty()) {
            plugin.getLandManager().removeLand(toLeave.getName());
            plugin.getConfig().set("lands." + toLeave.getName(), null);
            plugin.saveConfig();
            Bukkit.getLogger().info("Land " + toLeave.getName() + " wurde gelöscht (keine Bürger mehr).");
        }

        return true;
    }
}
