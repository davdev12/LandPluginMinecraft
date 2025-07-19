package de.example.landsystem;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class LandEnterListener implements Listener {

    private final Main plugin;

    public LandEnterListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();

        if (to == null || to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()) return;

        // Finde das Land, in das der Spieler sich bewegt
        Land targetLand = plugin.getLandManager().getAllLands().values().stream()
                .filter(land -> isInside(to, land.getPos1(), land.getPos2()))
                .findFirst()
                .orElse(null);

        // Kein Land → keine Einschränkung
        if (targetLand == null) return;

        UUID uuid = player.getUniqueId();

        // Ist Präsident oder Bürger → erlaubt
        if (targetLand.getPresident().equals(uuid) || targetLand.isCitizen(uuid)) return;

        // Hat Visum?
        if (hasValidVisa(player, targetLand.getName())) return;

        // Kein Zutritt → zurück teleportieren
        event.setCancelled(true);
        player.teleport(from);
        player.sendMessage("§cDu darfst das Land §e" + targetLand.getName() + "§c nicht betreten ohne Visum!");
    }

    private boolean isInside(Location loc, Location pos1, Location pos2) {
        if (pos1 == null || pos2 == null || !pos1.getWorld().equals(loc.getWorld())) return false;

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        return x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ;
    }

    private boolean hasValidVisa(Player player, String landName) {
        long time = plugin.getConfig().getLong("visa." + landName + "." + player.getUniqueId(), -1);
        return time != -1 && time > System.currentTimeMillis();
    }
}
