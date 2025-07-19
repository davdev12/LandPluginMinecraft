package de.example.landsystem;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class LandGUIListener implements Listener {

    private final Main plugin;

    public LandGUIListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();
        if (title.equals("§6Länderübersicht")) {
            event.setCancelled(true); // Verhindert das Herausnehmen von Items

            if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
                String landName = event.getCurrentItem().getItemMeta().getDisplayName().replace("§a", "");

                FileConfiguration config = plugin.getConfig();
                List<String> citizens = config.getStringList("lands." + landName + ".citizens");

                if (citizens.contains(player.getUniqueId().toString())) {
                    player.sendMessage("§cDu bist bereits Staatsbürger von §e" + landName + "§c.");
                    return;
                }

                // Wenn kein Bürger, dann Anfrage senden
                sendCitizenshipRequest(player, landName);
            }
        }
    }

    private void sendCitizenshipRequest(Player requester, String landName) {
        FileConfiguration config = plugin.getConfig();
        String requesterUUID = requester.getUniqueId().toString();

        List<String> citizens = config.getStringList("lands." + landName + ".citizens");

        // Schon Bürger?
        if (citizens.contains(requesterUUID)) {
            requester.sendMessage("§eDu bist bereits Bürger von §a" + landName + "§e.");
            return;
        }

        // Existiert bereits eine Anfrage?
        if (config.contains("requests." + landName + "." + requesterUUID)) {
            requester.sendMessage("§eDu hast bereits eine laufende Anfrage für §a" + landName + "§e.");
            return;
        }

        requester.sendMessage("§aDeine Anfrage zur Staatsbürgerschaft wurde gesendet!");

        // Anfrage speichern
        config.set("requests." + landName + "." + requesterUUID + ".name", requester.getName());
        config.set("requests." + landName + "." + requesterUUID + ".votes.accept", new ArrayList<String>());
        config.set("requests." + landName + "." + requesterUUID + ".votes.deny", new ArrayList<String>());

        // Nachricht an Bürger schicken (nur online)
        for (String uuidStr : citizens) {
            UUID uuid = UUID.fromString(uuidStr);
            Player citizen = Bukkit.getPlayer(uuid);
            if (citizen != null && citizen.isOnline()) {
                sendVoteMessage(citizen, requester.getName(), landName);
            } else {
                // Offline-Bürger merken, dass sie beim nächsten Login abstimmen sollen
                config.set("pendingVotes." + uuidStr + "." + landName, requesterUUID);
            }
        }

        plugin.saveConfig();
    }

    private void sendVoteMessage(Player citizen, String requesterName, String landName) {
        TextComponent accept = new TextComponent("§a[Akzeptieren]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/landvote accept " + requesterName + " " + landName));

        TextComponent deny = new TextComponent("§c[Ablehnen]");
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/landvote deny " + requesterName + " " + landName));

        citizen.sendMessage("§7Staatsbürgerschaftsanfrage von §e" + requesterName + " §7für §6" + landName + ": ");
        citizen.spigot().sendMessage(accept, new TextComponent(" "), deny);
    }

    public static TextComponent createAcceptComponent(String requesterName, String landName) {
        TextComponent accept = new TextComponent("§a[Akzeptieren]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/landvote accept " + requesterName + " " + landName));
        return accept;
    }

    public static TextComponent createDenyComponent(String requesterName, String landName) {
        TextComponent deny = new TextComponent("§c[Ablehnen]");
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/landvote deny " + requesterName + " " + landName));
        return deny;
    }

}
