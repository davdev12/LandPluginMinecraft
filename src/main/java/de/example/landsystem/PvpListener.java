package de.example.landsystem;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;

public class PvpListener implements Listener {

    private final Main plugin;

    public PvpListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player attacker)) return;
        if (!(e.getEntity() instanceof Player victim)) return;

        Land landA = plugin.getLandManager().getLandByCitizen(attacker.getUniqueId());
        Land landB = plugin.getLandManager().getLandByCitizen(victim.getUniqueId());

        if (landA != null && landA.equals(landB)) {
            e.setCancelled(true);
            attacker.sendMessage("§cDu kannst keine Mitbürger angreifen.");
            return;
        }

        if (landA != null && landB != null) {
            boolean allied = plugin.getConfig().getBoolean("alliances." + landA.getName() + "." + landB.getName(), false);
            if (allied) {
                e.setCancelled(true);
                attacker.sendMessage("§cDein Land ist mit dem Ziel-Land verbündet.");
            }
        }
        if (landA.equals(landB) || plugin.getAllianceManager().areAllied("landA", "landB")) {
            e.setCancelled(true);
            attacker.sendMessage("§cAngriffe auf Mitbürger oder Alliierte sind verboten.");
            return;
        }

        if (!plugin.getWarManager().isAtWar(landA.getName(), landB.getName())) {
            e.setCancelled(true);
            attacker.sendMessage("§cDu kannst keine Spieler aus friedlichen Ländern angreifen.");
        }
    }
}
