package de.example.landsystem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;

public class LandControlCommand implements CommandExecutor {

    private final Main plugin;

    public LandControlCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen.");
            return true;
        }

        Land land = plugin.getLandManager().getLandByCitizen(player.getUniqueId());

        if (land == null || !land.getPresident().equals(player.getUniqueId())) {
            player.sendMessage("§cNur der Präsident eines Landes kann die Landkontrolle öffnen.");
            return true;
        }

        Inventory gui = Bukkit.createInventory(null, 9, "§8Land Kontrolle");

        // Upgrade-Item (Slot 0)
        ItemStack upgradeItem = new ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA);
        ItemMeta upgradeMeta = upgradeItem.getItemMeta();
        upgradeMeta.setDisplayName("§dUpgrade");
        upgradeMeta.setLore(Collections.singletonList("§7Klicke, um dein Land zu erweitern"));

        // PersistentData zum Erkennen beim Klick
        upgradeMeta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "landcontrol_action"),
                PersistentDataType.STRING,
                "upgrade:" + land.getName()
        );
        upgradeItem.setItemMeta(upgradeMeta);
        gui.setItem(0, upgradeItem);

        // Visum-Item (Slot 1)
        ItemStack visumItem = new ItemStack(Material.DIAMOND);
        ItemMeta visumMeta = visumItem.getItemMeta();
        visumMeta.setDisplayName("§bVisum konfigurieren");
        visumMeta.setLore(Collections.singletonList("§7Klicke, um Visumkosten festzulegen"));

        // PersistentData zum Erkennen beim Klick
        visumMeta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "landcontrol_action"),
                PersistentDataType.STRING,
                "setvisum:" + land.getName()
        );
        visumItem.setItemMeta(visumMeta);
        gui.setItem(1, visumItem);
        // In LandControlCommand innerhalb onCommand(...) nach Visum-Item
        ItemStack allianceItem = new ItemStack(Material.GOLDEN_CHESTPLATE);
        ItemMeta allianceMeta = allianceItem.getItemMeta();
        allianceMeta.setDisplayName("§6Allianzen verwalten");
        upgradeMeta.setLore(Collections.singletonList("§7Klicke, um Allianzen zu schließen"));
        allianceMeta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "landcontrol_action"),
                PersistentDataType.STRING,
                "alliances:" + land.getName()
        );
        allianceItem.setItemMeta(allianceMeta);
        gui.setItem(2, allianceItem);

        player.openInventory(gui);
        return true;
    }
}