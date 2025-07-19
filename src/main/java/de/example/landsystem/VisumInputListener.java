package de.example.landsystem;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;

public class VisumInputListener implements Listener {

    private final Main plugin;

    public VisumInputListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Hat dieser Spieler einen Visum-Eingabemodus aktiv?
        if (!plugin.getPendingVisaInput().containsKey(player.getUniqueId())) return;

        event.setCancelled(true); // Chat blockieren

        String input = event.getMessage();
        int price;
        try {
            price = Integer.parseInt(input);
            if (price < 0) {
                player.sendMessage(ChatColor.RED + "Der Preis muss positiv sein.");
                plugin.getPendingVisaInput().remove(player.getUniqueId());
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Bitte gib eine gültige Zahl ein.");
            return;
        }

        String landName = plugin.getPendingVisaInput().remove(player.getUniqueId());

        plugin.getConfig().set("lands." + landName + ".visa_price", price);
        plugin.saveConfig();

        player.sendMessage(ChatColor.GREEN + "Der Visumspreis für §e" + landName + "§a beträgt jetzt §b" + price + " Diamanten§a für 1 Minecraft-Tag.");
    }
}
