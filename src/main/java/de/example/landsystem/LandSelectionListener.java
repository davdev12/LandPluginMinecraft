package de.example.landsystem;

import de.example.landsystem.Main;
import de.example.landsystem.SelectionManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class LandSelectionListener implements Listener {

    private final SelectionManager selectionManager;

    public LandSelectionListener(SelectionManager selectionManager) {
        this.selectionManager = selectionManager;
    }

    @EventHandler
    public void onPlayerUseWoodenShovel(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() != Material.WOODEN_SHOVEL) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            selectionManager.setPos1(player.getUniqueId(), clickedBlock.getLocation());
            player.sendMessage(ChatColor.GREEN + "Position 1 gesetzt bei " +
                    formatLocation(clickedBlock.getLocation()));
            event.setCancelled(true);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            selectionManager.setPos2(player.getUniqueId(), clickedBlock.getLocation());
            player.sendMessage(ChatColor.GREEN + "Position 2 gesetzt bei " +
                    formatLocation(clickedBlock.getLocation()));
            event.setCancelled(true);
        }
    }

    private String formatLocation(org.bukkit.Location loc) {
        return "§e(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
    }
}
