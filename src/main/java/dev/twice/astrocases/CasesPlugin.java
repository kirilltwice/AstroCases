package dev.twice.astrocases;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import dev.twice.astrocases.commands.CaseCommand;
import dev.twice.astrocases.config.ConfigManager;
import dev.twice.astrocases.listeners.CaseInteractListener;
import dev.twice.astrocases.listeners.PlayerQuitListener;
import dev.twice.astrocases.managers.CaseManager;
import dev.twice.astrocases.managers.OpeningManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CasesPlugin extends JavaPlugin {

    private static CasesPlugin instance;

    private ConfigManager configManager;
    private CaseManager caseManager;
    private OpeningManager openingManager;
    private CaseInteractListener caseInteractListener;
    private LiteCommands<org.bukkit.command.CommandSender> liteCommands;

    @Override
    public void onEnable() {
        instance = this;

        this.configManager = new ConfigManager(this);
        this.caseManager = new CaseManager(this);
        this.openingManager = new OpeningManager(this);
        this.caseInteractListener = new CaseInteractListener(this);

        configManager.loadConfigs();
        caseManager.loadCases();

        this.liteCommands = LiteCommandsBukkit.builder()
                .commands(new CaseCommand(this))
                .build();

        getServer().getPluginManager().registerEvents(caseInteractListener, this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
    }

    @Override
    public void onDisable() {
        if (liteCommands != null) {
            liteCommands.unregister();
        }

        if (openingManager != null) {
            openingManager.returnAllCases();
        }
    }

    public static CasesPlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CaseManager getCaseManager() {
        return caseManager;
    }

    public OpeningManager getOpeningManager() {
        return openingManager;
    }

    public CaseInteractListener getCaseInteractListener() {
        return caseInteractListener;
    }

    public LiteCommands<org.bukkit.command.CommandSender> getLiteCommands() {
        return liteCommands;
    }
}