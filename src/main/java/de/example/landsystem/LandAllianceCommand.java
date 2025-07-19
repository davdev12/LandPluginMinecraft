package de.example.landsystem;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class LandAllianceCommand implements CommandExecutor {

    private final Main plugin;

    public LandAllianceCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length < 2) {
            player.sendMessage("§cVerwendung: /alliances <accept|deny> <Land>");
            return true;
        }

        String sub = args[0];
        String fromLand = args[1];

        Land yourLand = plugin.getLandManager().getLandByCitizen(player.getUniqueId());
        if (yourLand == null || !yourLand.getPresident().equals(player.getUniqueId())) {
            player.sendMessage("§cNur Präsidenten können Allianzen annehmen oder ablehnen.");
            return true;
        }

        AllianceManager allianceManager = new AllianceManager(plugin);

        if (sub.equalsIgnoreCase("accept")) {
            allianceManager.setAlliance(yourLand.getName(), fromLand, true);
            player.sendMessage("§aAllianz mit §e" + fromLand + " §awurde geschlossen.");
        } else if (sub.equalsIgnoreCase("deny")) {
            player.sendMessage("§cAllianz-Anfrage von §e" + fromLand + " §cwurde abgelehnt.");
        } else {
            player.sendMessage("§cUngültiger Befehl.");
        }
        return true;
    }
}
