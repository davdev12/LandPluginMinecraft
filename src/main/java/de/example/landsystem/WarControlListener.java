package de.example.landsystem;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class WarControlListener implements Listener {
    private final Main plugin;

    public WarControlListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWarClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player p)) return;
        if (!event.getView().getTitle().equals("§4Krieg gegen...")) return;
        event.setCancelled(true);

        ItemStack i = event.getCurrentItem();
        if (i == null || !i.hasItemMeta()) return;
        var meta = i.getItemMeta();
        String action = meta.getPersistentDataContainer().get(
                new NamespacedKey(plugin, "landcontrol_action"),
                PersistentDataType.STRING
        );
        if (action == null || !action.startsWith("declarewar:")) return;
        p.closeInventory();

        String[] parts = action.split(":");
        String from = parts[1], to = parts[2];

        plugin.getAllianceManager().removeAlliance(from, to);
        plugin.getConfig().set("alliances." + from + "." + to, false);
        plugin.saveConfig();

        plugin.getWarManager().startWar(from, to);
        p.sendMessage("§cKrieg gegen " + to + " erklärt!");

        Bukkit.broadcast("§c" + from + " hat Krieg gegen " + to + " erklärt!", "landsystem.war.notify");
    }
}
