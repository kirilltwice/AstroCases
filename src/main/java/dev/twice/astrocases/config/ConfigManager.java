package dev.twice.astrocases.config;

import dev.twice.astrocases.CasesPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

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
            } catch (Exception ignored) {}
        }
        offlineReturnsConfig = YamlConfiguration.loadConfiguration(offlineReturnsFile);
    }

    public String getMessage(String path) {
        String message = mainConfig.getString("messages." + path);
        if (message == null) {
            return getDefaultMessage(path);
        }
        return message;
    }

    public List<String> getMessageList(String path) {
        List<String> messages = mainConfig.getStringList("messages." + path);
        if (messages.isEmpty()) {
            return getDefaultMessageList(path);
        }
        return messages;
    }

    private String getDefaultMessage(String path) {
        switch (path) {
            case "case-opening.button.yes": return "<green><bold>[ДА]</bold></green>";
            case "case-opening.button.no": return "<red><bold>[НЕТ]</bold></red>";
            case "case-opening.cancel": return "<gray>Открытие кейса отменено.";
            case "case-opening.already-opening": return "<red>Вы уже открываете кейс!";
            case "case-opening.unknown-case": return "<red>Неизвестный кейс!";
            case "case-opening.cant-drop": return "<red>Нельзя выбрасывать кейсы!";
            case "case-opening.cant-store": return "<red>Нельзя помещать кейсы в хранилища!";
            case "case-opening.offline-return": return "<green>Вам возвращены кейсы, которые не были открыты!";
            case "commands.case-not-found": return "<red>Кейс с именем \"%case%\" не найден!";
            case "commands.case-given": return "<green>Кейс \"%case%\" выдан игроку %player%";
            case "commands.case-received": return "<green>Вы получили кейс: %case%";
            case "commands.config-reloaded": return "<green>Конфигурация кейсов перезагружена!";
            case "commands.no-cases-found": return "<red>Кейсов не найдено!";
            case "commands.available-cases": return "<yellow>Доступные кейсы:";
            case "commands.case-list-format": return "<gray>- %id% (%name%)";
            default: return "Message not found: " + path;
        }
    }

    private List<String> getDefaultMessageList(String path) {
        List<String> messages = new ArrayList<>();
        if ("case-opening.confirm-message".equals(path)) {
            messages.add("<yellow>Вы решили открыть кейс");
            messages.add("<yellow>Ну что открываем?");
            messages.add("%s или %n");
        }
        return messages;
    }

    public YamlConfiguration getMainConfig() {
        return mainConfig;
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
        } catch (Exception ignored) {}
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
        } catch (Exception ignored) {}
    }
}