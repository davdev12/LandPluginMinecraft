package de.example.landsystem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

public class LandVoteCommand implements CommandExecutor {

    private final Main plugin;

    public LandVoteCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können abstimmen.");
            return true;
        }

        if (args.length != 3) {
            player.sendMessage("§cBenutzung: /landvote <accept|deny> <Spielername> <Land>");
            return true;
        }

        String voteType = args[0];
        String requesterName = args[1];
        String landName = args[2];

        FileConfiguration config = plugin.getConfig();
        String voterUUID = player.getUniqueId().toString();

        String path = "requests." + landName;

        // Finde UUID des Anfragenden
        String requesterUUID = null;
        if (config.contains(path)) {
            for (String uuid : config.getConfigurationSection(path).getKeys(false)) {
                String name = config.getString(path + "." + uuid + ".name");
                if (name != null && name.equalsIgnoreCase(requesterName)) {
                    requesterUUID = uuid;
                    break;
                }
            }
        }

        if (requesterUUID == null) {
            player.sendMessage("§cAnfrage nicht gefunden.");
            return true;
        }

        List<String> citizens = config.getStringList("lands." + landName + ".citizens");
        if (!citizens.contains(voterUUID)) {
            player.sendMessage("§cDu bist kein Bürger dieses Landes.");
            return true;
        }

        List<String> acceptVotes = config.getStringList(path + "." + requesterUUID + ".votes.accept");
        List<String> denyVotes = config.getStringList(path + "." + requesterUUID + ".votes.deny");

        if (acceptVotes.contains(voterUUID) || denyVotes.contains(voterUUID)) {
            player.sendMessage("§cDu hast bereits abgestimmt.");
            return true;
        }

        // Stimme speichern
        if (voteType.equalsIgnoreCase("accept")) {
            acceptVotes.add(voterUUID);
            config.set(path + "." + requesterUUID + ".votes.accept", acceptVotes);
            player.sendMessage("§aDu hast für den Antrag gestimmt.");
        } else if (voteType.equalsIgnoreCase("deny")) {
            denyVotes.add(voterUUID);
            config.set(path + "." + requesterUUID + ".votes.deny", denyVotes);
            player.sendMessage("§cDu hast gegen den Antrag gestimmt.");
        } else {
            player.sendMessage("§cUngültiger Befehl. Benutze accept oder deny.");
            return true;
        }

        // Entscheidung treffen, wenn alle Bürger abgestimmt haben
        int totalVotes = acceptVotes.size() + denyVotes.size();
        if (totalVotes >= citizens.size()) {
            if (acceptVotes.size() > citizens.size() / 2) {
                // Angenommen
                List<String> updatedCitizens = config.getStringList("lands." + landName + ".citizens");
                if (!updatedCitizens.contains(requesterUUID)) {
                    updatedCitizens.add(requesterUUID);
                    config.set("lands." + landName + ".citizens", updatedCitizens);

                    Player target = Bukkit.getPlayer(UUID.fromString(requesterUUID));
                    if (target != null && target.isOnline()) {
                        target.sendMessage("§aDeine Staatsbürgerschaft in §6" + landName + " §awurde angenommen!");
                    }
                }
            } else {
                Player target = Bukkit.getPlayer(UUID.fromString(requesterUUID));
                if (target != null && target.isOnline()) {
                    target.sendMessage("§cDeine Anfrage zur Staatsbürgerschaft in §6" + landName + " §cwurde abgelehnt.");
                }
            }

            // Anfrage entfernen
            config.set(path + "." + requesterUUID, null);
        }

        plugin.saveConfig();
        return true;
    }
}
