package dev.twice.astrocases.config;

import dev.twice.astrocases.CasesPlugin;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

@Getter
public class ConfigManager {

    private final CasesPlugin plugin;
    private YamlConfiguration mainConfig;
    private YamlConfiguration offlineReturnsConfig;
    private File offlineReturnsFile;

    public ConfigManager(CasesPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        saveDefaultConfig();
        loadMainConfig();
        loadOfflineReturnsConfig();
    }

    private void saveDefaultConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
    }

    private void loadMainConfig() {
        plugin.reloadConfig();
        mainConfig = (YamlConfiguration) plugin.getConfig();
    }

    private void loadOfflineReturnsConfig() {
        offlineReturnsFile = new File(plugin.getDataFolder(), "offline-returns.yml");
        if (!offlineReturnsFile.exists()) {
            try {
                offlineReturnsFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        offlineReturnsConfig = YamlConfiguration.loadConfiguration(offlineReturnsFile);
    }

    public void addOfflineReturn(UUID playerId, ItemStack item) {
        String path = "returns." + playerId.toString();
        List<ItemStack> items = new ArrayList<>();

        if (offlineReturnsConfig.contains(path)) {
            items = (List<ItemStack>) offlineReturnsConfig.getList(path, new ArrayList<>());
        }

        items.add(item);
        offlineReturnsConfig.set(path, items);

        try {
            offlineReturnsConfig.save(offlineReturnsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ItemStack> getOfflineReturns(UUID playerId) {
        String path = "returns." + playerId.toString();
        return (List<ItemStack>) offlineReturnsConfig.getList(path, new ArrayList<>());
    }

    public void clearOfflineReturns(UUID playerId) {
        String path = "returns." + playerId.toString();
        offlineReturnsConfig.set(path, null);

        try {
            offlineReturnsConfig.save(offlineReturnsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}