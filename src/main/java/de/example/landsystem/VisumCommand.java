package de.example.landsystem;

import de.example.landsystem.Main;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VisumCommand implements CommandExecutor {

    private final Main plugin;

    public VisumCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl benutzen.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§cVerwendung: /visum <Land>");
            return true;
        }

        String landName = args[0];
        FileConfiguration config = plugin.getConfig();

        if (!config.contains("lands." + landName)) {
            player.sendMessage("§cDas Land §e" + landName + " §cexistiert nicht.");
            return true;
        }

        // Hat der Landesgründer einen Visum-Preis gesetzt?
        if (!config.contains("lands." + landName + ".visumPrice")) {
            player.sendMessage("§cFür dieses Land ist kein Visum verfügbar.");
            return true;
        }

        int preis = config.getInt("lands." + landName + ".visumPrice");

        // Prüfen ob Spieler genug Diamanten hat
        int diamonds = countDiamonds(player);
        if (diamonds < preis) {
            player.sendMessage("§cDu brauchst §e" + preis + " §cDiamanten für das Visum.");
            return true;
        }

        // Diamanten entfernen
        removeDiamonds(player, preis);

        // Visum setzen für 1 Minecraft-Tag (20 Minuten)
        long validUntil = System.currentTimeMillis() + (20 * 60 * 1000);
        config.set("visa." + landName + "." + player.getUniqueId(), validUntil);
        plugin.saveConfig();

        player.sendMessage("§aVisum für §e" + landName + " §aerhalten! Gültig für einen Minecraft-Tag.");
        return true;
    }

    private int countDiamonds(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.DIAMOND) {
                count += item.getAmount();
            }
        }
        return count;
    }

    private void removeDiamonds(Player player, int amount) {
        int left = amount;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() != Material.DIAMOND) continue;

            int remove = Math.min(item.getAmount(), left);
            item.setAmount(item.getAmount() - remove);
            left -= remove;

            if (left <= 0) break;
        }
    }
}
