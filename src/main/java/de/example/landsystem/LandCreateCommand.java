package de.example.landsystem;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Random;



public class LandCreateCommand implements CommandExecutor {

    private final Main plugin;
    private final SelectionManager selectionManager;
    private Location center;
    public LandCreateCommand(Main plugin, SelectionManager selectionManager) {
        this.plugin = plugin;
        this.selectionManager = selectionManager;
        this.center = null;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Nur Spieler können diesen Befehl ausführen.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Benutzung: /landcreate <Name>");
            return true;
        }

        String landName = args[0];
        for (Land land : plugin.getLandManager().getAllLands().values()) {
            if (land.isCitizen(player.getUniqueId())) {
                player.sendMessage("§cDu bist bereits in einem Land. Verlasse es erst mit /leave.");
                return true;
            }
        }

        if (!selectionManager.hasBothPositions(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Du musst zuerst zwei Positionen mit der Holzschaufel setzen!");
            return true;
        }

        if (plugin.getLandManager().landExists(landName)) {
            player.sendMessage(ChatColor.RED + "Ein Land mit diesem Namen existiert bereits.");
            return true;
        }

        Land land = new Land(landName, player.getUniqueId(), center, 50, 100.0);
        land.addCitizen(player.getUniqueId());
        plugin.getLandManager().addLand(land);
        Location pos1 = selectionManager.getPos1(player.getUniqueId());
        Location pos2 = selectionManager.getPos2(player.getUniqueId());
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        // Überprüfe ob Spieler schon Bürger eines Landes ist


        World world = pos1.getWorld();

        center = pos1.clone().add(pos2).multiply(0.5);

        // Land erstellen
        land.setPos1(pos1);
        land.setPos2(pos2);

        // Config speichern
        plugin.getConfig().set("lands." + landName + ".pos1", pos1);
        plugin.getConfig().set("lands." + landName + ".pos2", pos2);
        plugin.getConfig().set("lands." + landName + ".president", player.getUniqueId().toString());
        plugin.getConfig().set("lands." + landName + ".citizens", Collections.singletonList(player.getUniqueId().toString()));
        plugin.saveConfig();

        player.sendMessage(ChatColor.GREEN + "Das Land §e" + landName + "§a wurde erfolgreich gegründet!");
        return true;
    }
}
