package de.example.landsystem;

import de.example.landsystem.Main;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LandPayCommand implements CommandExecutor {

    private final Main plugin;
    private final Economy economy;

    public LandPayCommand(Main plugin, Economy economy) {
        this.plugin = plugin;
        this.economy = economy;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl benutzen.");
            return true;
        }

        if (economy == null) {
            sender.sendMessage("Economy-System ist nicht verfügbar.");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage("§cBenutze: /landpay <landname> <betrag>");
            return true;
        }

        String landName = args[0];
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount <= 0) {
                player.sendMessage("§cDer Betrag muss größer als 0 sein.");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§cUngültiger Betrag.");
            return true;
        }

        if (!plugin.getConfig().contains("lands." + landName)) {
            player.sendMessage("§cDieses Land existiert nicht.");
            return true;
        }

        if (economy.getBalance(player) < amount) {
            player.sendMessage("§cDu hast nicht genug Geld.");
            return true;
        }

        // Geld abziehen vom Spieler
        EconomyResponse response = economy.withdrawPlayer(player, amount);
        if (!response.transactionSuccess()) {
            player.sendMessage("§cFehler bei der Zahlung: " + response.errorMessage);
            return true;
        }

        // Geld dem Land gutschreiben
        double currentMoney = plugin.getConfig().getDouble("lands." + landName + ".money", 0);
        plugin.getConfig().set("lands." + landName + ".money", currentMoney + amount);
        plugin.saveConfig();

        player.sendMessage("§aDu hast §e" + amount + " §a an das Land §6" + landName + " §agezahlt.");
        return true;
    }
}
