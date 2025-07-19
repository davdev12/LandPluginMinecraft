package de.example.landsystem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LandGUI {

    public static void openLandList(Player player, FileConfiguration config) {
        Map<String, Object> landSections = config.getConfigurationSection("lands") != null
                ? config.getConfigurationSection("lands").getValues(false)
                : Collections.emptyMap();

        Inventory gui = Bukkit.createInventory(null, 54, "§6Länderübersicht");

        for (String landName : landSections.keySet()) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();

            double balance = config.getDouble("lands." + landName + ".balance");
            String presidentUUID = config.getString("lands." + landName + ".president");
            String presidentName = "Unbekannt";
            if (presidentUUID != null) {
                UUID uuid = UUID.fromString(presidentUUID);
                OfflinePlayer offlinePresident = Bukkit.getOfflinePlayer(uuid);
                if (offlinePresident.getName() != null) {
                    presidentName = offlinePresident.getName();
                }
            }

            meta.setDisplayName("§a" + landName);
            List<String> lore = new ArrayList<>();
            lore.add("§7Gründer: §e" + presidentName);
            lore.add("§7Guthaben: §6" + balance + "$");
            lore.add("§8Klicke, um Staatsbürgerschaft zu beantragen");
            meta.setLore(lore);

            item.setItemMeta(meta);
            gui.addItem(item);
        }

        player.openInventory(gui);
    }
}
