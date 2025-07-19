package de.example.landsystem;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.entity.Player;

public class AllianceClickListener implements Listener {

    private final Main plugin;

    public AllianceClickListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals("§8Allianzen schließen")) return;
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String toLand = clicked.getItemMeta().getDisplayName().replace("§a", "");
        Land yourLand = plugin.getLandManager().getLandByCitizen(player.getUniqueId());

        if (yourLand == null) {
            player.sendMessage("§cDu bist in keinem Land.");
            return;
        }

        plugin.getAllianceManager().sendAllianceRequest(player, yourLand.getName(), toLand);
        player.closeInventory();
    }
}
