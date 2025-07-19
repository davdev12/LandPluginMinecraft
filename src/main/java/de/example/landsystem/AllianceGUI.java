package de.example.landsystem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class AllianceGUI {

    private final Main plugin;
    private final Player player;
    private final String yourLand;

    public AllianceGUI(Main plugin, Player player, String yourLand) {
        this.plugin = plugin;
        this.player = player;
        this.yourLand = yourLand;
    }

    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, "§8Allianzen schließen");

        for (Land land : plugin.getLandManager().getAllLands().values()) {
            if (land.getName().equalsIgnoreCase(yourLand)) continue;

            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§a" + land.getName());
            item.setItemMeta(meta);
            gui.addItem(item);
        }

        player.openInventory(gui);
    }
}
