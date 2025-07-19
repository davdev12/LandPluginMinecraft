package de.example.landsystem;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LandUpgradeCommand implements CommandExecutor {

    private final Main plugin;

    public LandUpgradeCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Nur Spieler können diesen Befehl verwenden.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Benutzung: /upgrade <Landname>");
            return true;
        }

        String landName = args[0];

        // Prüfe ob Land existiert
        if (!plugin.getLandManager().landExists(landName)) {
            player.sendMessage(ChatColor.RED + "Das Land existiert nicht.");
            return true;
        }

        Land land = plugin.getLandManager().getLand(landName);

        // Optional: Prüfe ob Spieler Bürger oder Präsident ist (hier Präsident)
        if (!land.getPresident().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Nur der Präsident kann das Land upgraden.");
            return true;
        }

        Location pos1 = land.getPos1();
        Location pos2 = land.getPos2();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        int currentLevel = land.getLevel();

        // Anzahl der Blöcke, die außen rum erweitert werden (1 Block in jede Richtung)
        int expandedMinX = minX - 1;
        int expandedMaxX = maxX + 1;
        int expandedMinZ = minZ - 1;
        int expandedMaxZ = maxZ + 1;

        // Berechne Anzahl der neuen Blocks
        // Randblöcke um das bestehende Rechteck (ohne überschneiden)
        int width = maxX - minX + 1;
        int height = maxZ - minZ + 1;
        int borderBlocks = (width + 2) * 2 + (height * 2);

        // Alternativ genau:
        // 4 Ecken + neue Randblöcke
        int newBorderBlocks = 2 * ( (expandedMaxX - expandedMinX + 1) + (expandedMaxZ - expandedMinZ + 1) ) * 2 - (width + height) * 2;

        // Für Vereinfachung nehmen wir:
        int newBlocks = ( (expandedMaxX - expandedMinX + 1) * (expandedMaxZ - expandedMinZ + 1) )
                - ( (maxX - minX + 1) * (maxZ - minZ + 1) );

        // Preis: 1 Diamant pro Block
        int diamondsNeeded = newBlocks;

        // Prüfen, ob Spieler genug Diamanten hat
        int playerDiamonds = countDiamonds(player);
        if (playerDiamonds < diamondsNeeded) {
            player.sendMessage(ChatColor.RED + "Du benötigst " + diamondsNeeded + " Diamanten, hast aber nur " + playerDiamonds + ".");
            return true;
        }

        // Diamanten entfernen
        removeDiamonds(player, diamondsNeeded);

        // Land Koordinaten erweitern
        Location world = pos1.getWorld().getSpawnLocation(); // dummy nur Welt holen
        Location newPos1 = new Location(pos1.getWorld(), expandedMinX, pos1.getBlockY(), expandedMinZ);
        Location newPos2 = new Location(pos2.getWorld(), expandedMaxX, pos2.getBlockY(), expandedMaxZ);

        land.setPos1(newPos1);
        land.setPos2(newPos2);

        // Level erhöhen
        land.setLevel(currentLevel + 1);

        // Speichern ins Config
        plugin.getConfig().set("lands." + landName + ".pos1", newPos1);
        plugin.getConfig().set("lands." + landName + ".pos2", newPos2);
        plugin.getConfig().set("lands." + landName + ".level", land.getLevel());
        plugin.saveConfig();

        player.sendMessage(ChatColor.GREEN + "Das Land §e" + landName + "§a ist nun Level " + land.getLevel() + ".");

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
        int toRemove = amount;
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == Material.DIAMOND) {
                int stackAmount = item.getAmount();
                if (stackAmount <= toRemove) {
                    toRemove -= stackAmount;
                    player.getInventory().clear(i);
                } else {
                    item.setAmount(stackAmount - toRemove);
                    toRemove = 0;
                }
                if (toRemove == 0) break;
            }
        }
        player.updateInventory();
    }
}
