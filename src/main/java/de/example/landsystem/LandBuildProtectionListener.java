package de.example.landsystem;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.entity.Player;

public class LandBuildProtectionListener implements Listener {

    private final Main plugin;

    public LandBuildProtectionListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        Land land = plugin.getLandManager().getLandAt(event.getBlock().getLocation());
        if (land == null) return; // kein Land → kein Schutz

        if (!land.getCitizens().contains(player.getUniqueId()) &&
                !land.getPresident().equals(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage("§cDu darfst in diesem Land nichts abbauen.");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        Land land = plugin.getLandManager().getLandAt(event.getBlock().getLocation());
        if (land == null) return;

        if (!land.getCitizens().contains(player.getUniqueId()) &&
                !land.getPresident().equals(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage("§cDu darfst in diesem Land nichts bauen.");
        }
    }
}
