package de.example.landsystem;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.Material;
import java.util.ArrayList;
import java.util.List;

public class LandControlListener implements Listener {
    private final Main plugin;

    public LandControlListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equalsIgnoreCase("§8Land Kontrolle")) return;
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getInventory())) return;

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        ItemMeta meta = clicked.getItemMeta();
        String action = meta.getPersistentDataContainer().get(
                new NamespacedKey(plugin, "landcontrol_action"),
                PersistentDataType.STRING
        );
        if (action == null) return;

        Bukkit.getLogger().info("[LandControl] Klick auf Action: " + action + " durch " + player.getName());
        player.closeInventory();

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (action.startsWith("upgrade:")) {
                String landName = action.substring("upgrade:".length());
                plugin.getLandUpgradeHandler().upgradeLand(player, landName);
            } else if (action.startsWith("setvisum:")) {
                String landName = action.substring("setvisum:".length());
                plugin.getPendingVisaInput().put(player.getUniqueId(), landName);
                player.sendMessage("§7Bitte gib die Anzahl an Diamanten ein, die für ein Visum in §e" + landName + " §7einzugeben:");
            } else if (action.startsWith("alliances:")) {
            String landName = action.substring("alliances:".length());
            new AllianceGUI(plugin, player, landName).open();
            } else if (action.startsWith("war:")) {
                String landName = action.substring("war:".length());
                openWarGui(player, landName);
            }
        });
    }

    private void openAllianceGUI(Player player, String landName) {
        List<Land> allLands = new ArrayList<>(plugin.getLandManager().getAllLands().values());
        Inventory inv = Bukkit.createInventory(null, (allLands.size()/9 +1)*9, "§6Wähle Land für Allianz");

        for (int i = 0; i < allLands.size(); i++) {
            Land l = allLands.get(i);
            if (l.getName().equalsIgnoreCase(landName)) continue;
            ItemStack is = new ItemStack(Material.GREEN_BANNER, 1);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName("§a" + l.getName());
            im.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "landcontrol_action"),
                    PersistentDataType.STRING,
                    "requestAlliance:" + l.getName() + ":from:" + landName
            );
            is.setItemMeta(im);
            inv.setItem(i, is);
        }

        player.openInventory(inv);
    }
    private void openWarGui(Player player, String landName) {
        Inventory warGui = Bukkit.createInventory(null, 9, "§4Krieg gegen...");
        var allies = plugin.getAllianceManager().getAllies(landName);

        int slot = 0;
        for (String ally : allies) {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            var meta = skull.getItemMeta();
            meta.setDisplayName("§c" + ally);
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "landcontrol_action"),
                    PersistentDataType.STRING,
                    "declarewar:" + landName + ":" + ally
            );
            skull.setItemMeta(meta);
            warGui.setItem(slot++, skull);
        }
        player.openInventory(warGui);
    }


}
