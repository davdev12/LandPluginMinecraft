package de.example.landsystem;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LandUpgradeHandler {

    private final Main plugin;

    public LandUpgradeHandler(Main plugin) {
        this.plugin = plugin;
    }

    public void upgradeLand(Player player, String landName) {
        if (!plugin.getLandManager().landExists(landName)) {
            player.sendMessage(ChatColor.RED + "Das Land existiert nicht.");
            return;
        }

        Land land = plugin.getLandManager().getLand(landName);

        if (!land.getPresident().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Nur der Präsident kann das Land upgraden.");
            return;
        }

        Location pos1 = land.getPos1();
        Location pos2 = land.getPos2();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        int expandedMinX = minX - 1;
        int expandedMaxX = maxX + 1;
        int expandedMinZ = minZ - 1;
        int expandedMaxZ = maxZ + 1;

        Location newPos1 = new Location(pos1.getWorld(), expandedMinX, pos1.getBlockY(), expandedMinZ);
        Location newPos2 = new Location(pos2.getWorld(), expandedMaxX, pos2.getBlockY(), expandedMaxZ);

        // Prüfe, ob erweitertes Land mit einem anderen Land kollidiert
        for (Land other : plugin.getLandManager().getAllLands().values()) {
            if (other == land) continue; // eigenes Land ignorieren
            if (RegionUtils.regionsOverlap(newPos1, newPos2, other.getPos1(), other.getPos2())) {
                player.sendMessage(ChatColor.RED + "Dein Land kann nicht mehr upgegradet werden – es würde ein anderes Land überlappen.");
                return;
            }
        }

        int newBlocks = ((expandedMaxX - expandedMinX + 1) * (expandedMaxZ - expandedMinZ + 1))
                - ((maxX - minX + 1) * (maxZ - minZ + 1));

        int diamondsNeeded = newBlocks;

        int playerDiamonds = countDiamonds(player);
        if (playerDiamonds < diamondsNeeded) {
            player.sendMessage(ChatColor.RED + "Du benötigst " + diamondsNeeded + " Diamanten, hast aber nur " + playerDiamonds + ".");
            return;
        }

        removeDiamonds(player, diamondsNeeded);

        land.setPos1(newPos1);
        land.setPos2(newPos2);

        int newLevel = land.getLevel() + 1;
        land.setLevel(newLevel);

        plugin.getConfig().set("lands." + landName + ".pos1", land.getPos1());
        plugin.getConfig().set("lands." + landName + ".pos2", land.getPos2());
        plugin.getConfig().set("lands." + landName + ".level", newLevel);
        plugin.saveConfig();

        player.sendMessage(ChatColor.GREEN + "Dein Land §e" + landName + " §awurde erfolgreich auf Level §e" + newLevel + " §aupgegradet.");
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
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == Material.DIAMOND) {
                int remove = Math.min(item.getAmount(), left);
                item.setAmount(item.getAmount() - remove);
                left -= remove;
                if (left <= 0) break;
            }
        }
        player.updateInventory();
    }
}
