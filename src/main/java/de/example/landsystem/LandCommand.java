package de.example.landsystem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LandCommand implements CommandExecutor {

    private final Main plugin;
    private final Map<UUID, LocationSelector> selectors = new HashMap<>();

    public LandCommand(Main plugin) {
        this.plugin = plugin;
    }

    public static class LocationSelector {
        public org.bukkit.Location pos1;
        public org.bukkit.Location pos2;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return true;
        }

        FileConfiguration config = plugin.getConfig();

        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            LandGUI.openLandList(player, config);
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 2) {
                player.sendMessage("§cVerwendung: /land create <Name>");
                return true;
            }

            // NEU: Prüfen, ob Spieler schon in einem Land ist
            Set<String> landKeys = config.getConfigurationSection("lands") != null
                    ? config.getConfigurationSection("lands").getKeys(false)
                    : new HashSet<>();

            UUID uuid = player.getUniqueId();
            for (String key : landKeys) {
                List<String> citizens = config.getStringList("lands." + key + ".citizens");
                if (citizens.contains(uuid.toString())) {
                    player.sendMessage("§cDu bist bereits Mitglied eines Landes (§e" + key + "§c). Verlasse es zuerst mit §e/land leave§c.");
                    return true;
                }
            }

            LocationSelector selector = selectors.get(player.getUniqueId());
            if (selector == null || selector.pos1 == null || selector.pos2 == null) {
                player.sendMessage("§cBitte wähle zuerst zwei Positionen mit der Schaufel aus.");
                return true;
            }

            String landName = args[1];
            if (config.contains("lands." + landName)) {
                player.sendMessage("§cEin Land mit diesem Namen existiert bereits.");
                return true;
            }

            int minX = Math.min(selector.pos1.getBlockX(), selector.pos2.getBlockX());
            int maxX = Math.max(selector.pos1.getBlockX(), selector.pos2.getBlockX());
            int minZ = Math.min(selector.pos1.getBlockZ(), selector.pos2.getBlockZ());
            int maxZ = Math.max(selector.pos1.getBlockZ(), selector.pos2.getBlockZ());
            int blocks = (maxX - minX + 1) * (maxZ - minZ + 1);

            int diamondCount = countItem(player, Material.DIAMOND);
            if (diamondCount < blocks) {
                player.sendMessage("§cNicht genug Diamanten. §7Dir fehlen §e" + (blocks - diamondCount) + "§7.");
                return true;
            }

            removeItems(player, Material.DIAMOND, blocks);

            config.set("lands." + landName + ".owner", uuid.toString());
            config.set("lands." + landName + ".president", uuid.toString());
            config.set("lands." + landName + ".balance", 0.0);
            config.set("lands." + landName + ".build", false);
            config.set("lands." + landName + ".enter", false);
            config.set("lands." + landName + ".citizens", Collections.singletonList(uuid.toString()));
            config.set("lands." + landName + ".area.minX", minX);
            config.set("lands." + landName + ".area.maxX", maxX);
            config.set("lands." + landName + ".area.minZ", minZ);
            config.set("lands." + landName + ".area.maxZ", maxZ);
            config.set("lands." + landName + ".world", player.getWorld().getName());

            plugin.saveConfig();

            player.sendMessage("§aLand §e" + landName + " §aerfolgreich gegründet!");
            return true;
        }


        if (args[0].equalsIgnoreCase("expand")) {
            ItemStack shovel = new ItemStack(Material.WOODEN_SHOVEL);
            ItemMeta meta = shovel.getItemMeta();
            meta.setDisplayName("§6Land-Erweiterungsschaufel");
            shovel.setItemMeta(meta);
            player.getInventory().addItem(shovel);
            player.sendMessage("§aBenutze die Schaufel, um den neuen Bereich auszuwählen.");
            return true;
        }

        player.sendMessage("§cUnbekannter Subbefehl. Verwende /land list, /land create <Name> oder /land expand");
        return true;
    }

    private int countItem(Player player, Material mat) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == mat) {
                count += item.getAmount();
            }
        }
        return count;
    }

    private void removeItems(Player player, Material mat, int amount) {
        int left = amount;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == mat) {
                int remove = Math.min(item.getAmount(), left);
                item.setAmount(item.getAmount() - remove);
                left -= remove;
                if (left <= 0) break;
            }
        }
    }

    public Map<UUID, LocationSelector> getSelectors() {
        return selectors;
    }
}
