package de.example.landsystem;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import de.example.landsystem.LandEnterListener;
public class Main extends JavaPlugin {

    private SelectionManager selectionManager;
    private LandManager landManager;
    private LandUpgradeCommand landUpgradeCommand;
    private Economy economy;
    private final HashMap<UUID, String> expandingLandMap = new HashMap<>();
    private final Map<UUID, String> visumInput = new HashMap<>();
    private final Map<String, Integer> landVisumPrices = new HashMap<>();
    private static Main instance;
    private LandUpgradeHandler landUpgradeHandler;
    private Map<UUID, String> pendingVisaInput = new HashMap<>();
    private AllianceManager allianceManager;
    @Override
    public void onEnable() {
        instance = this;

        // Economy Setup via Vault
        if (!setupEconomy()) {
            getLogger().warning("Vault oder Economy Plugin nicht gefunden! Wirtschaftsfunktionen sind deaktiviert.");
            economy = null;
        }

        // Manager initialisieren
        landManager = new LandManager(this);
        selectionManager = new SelectionManager();
        this.landUpgradeHandler = new LandUpgradeHandler(this);
        this.pendingVisaInput = new HashMap<>();
        // Commands initialisieren
        landUpgradeCommand = new LandUpgradeCommand(this);

        // Partikel Task starten
        new LandParticleBorderTask(this).runTaskTimer(this, 0L, 4L);

        // Commands registrieren
        getCommand("landalliance").setExecutor(new LandAllianceCommand(this));
        getCommand("landcreate").setExecutor(new LandCreateCommand(this, selectionManager));
        getCommand("upgrade").setExecutor(landUpgradeCommand);
        getCommand("landselect").setExecutor(new LandSelectCommand());
        getCommand("landvote").setExecutor(new LandVoteCommand(this));
        getCommand("landpay").setExecutor(new LandPayCommand(this, economy));
        getCommand("land").setExecutor(new LandCommand(this));
        getCommand("leave").setExecutor(new LandLeaveCommand(this));
        getCommand("landcontrol").setExecutor(new LandControlCommand(this));
        getCommand("visum").setExecutor(new VisumCommand(this));
        getServer().getPluginManager().registerEvents(new LandEnterListener(this), this);
        Bukkit.getPluginManager().registerEvents(new VisumInputListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LandGUIListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LandSelectionListener(selectionManager), this);
        getServer().getPluginManager().registerEvents(new LandControlListener(this), this);
        getServer().getPluginManager().registerEvents(new LandEnterListener(this), this);        // Config laden
        getServer().getPluginManager().registerEvents(new PvpListener(this), this);
        getCommand("landalliance").setExecutor(new LandAllianceCommand(this));
        Bukkit.getPluginManager().registerEvents(new AllianceClickListener(this), this);
        saveDefaultConfig();

        getLogger().info("LandSystem erfolgreich geladen!");
    }

    @Override
    public void onDisable() {
        getLogger().info("LandSystem wurde deaktiviert.");
    }

    // Economy Setup (Vault)
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Externes Geldplugin nicht gefunden!");
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().severe("Kein Economy-Provider gefunden!");
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    public Economy getEconomy() {
        return economy;
    }

    public LandUpgradeHandler getLandUpgradeHandler() {
        return landUpgradeHandler;
    }


    public Map<UUID, String> getPendingVisaInput() {
        return pendingVisaInput;
    }
    public Map<UUID, String> getVisumInput() {
        return visumInput;
    }
    public Map<String, Integer> getLandVisumPrices() {
        return landVisumPrices;
    }
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public LandManager getLandManager() {
        return landManager;
    }

    public LandUpgradeCommand getLandUpgradeCommand() {
        return landUpgradeCommand;
    }

    public HashMap<UUID, String> getExpandingLandMap() {
        return expandingLandMap;
    }
    public AllianceManager getAllianceManager() {
        return allianceManager;
    }
    public static Main getInstance() {
        return instance;
    }
}
