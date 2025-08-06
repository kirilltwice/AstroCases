package dev.twice.astrocases.managers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.twice.astrocases.CasesPlugin;
import dev.twice.astrocases.models.Case;
import dev.twice.astrocases.models.CaseReward;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class CaseManager {

    private final CasesPlugin plugin;
    private final Map<String, Case> cases = new HashMap<>();

    public CaseManager(CasesPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadCases() {
        cases.clear();
        File casesDir = new File(plugin.getDataFolder(), "cases");
        if (!casesDir.exists()) {
            casesDir.mkdirs();
            return;
        }

        File[] files = casesDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                loadCase(file);
            }
        }
    }

    private void loadCase(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String caseName = file.getName().replace(".yml", "");

        String name = config.getString("options.name", caseName);
        List<String> description = config.getStringList("options.description");
        String itemString = config.getString("options.item", "CHEST");

        ItemStack item = createItemFromString(itemString);

        List<CaseReward> rewards = new ArrayList<>();
        if (config.isConfigurationSection("rewards")) {
            for (String key : config.getConfigurationSection("rewards").getKeys(false)) {
                String path = "rewards." + key;
                double chance = config.getDouble(path + ".chance", 1.0);
                String titleText = config.getString(path + ".titleText", "");
                String subtitleText = config.getString(path + ".subtitleText", "");
                List<String> message = config.getStringList(path + ".message");
                String commandsGive = config.getString(path + ".commands-give", "");

                CaseReward reward = new CaseReward(key, chance, titleText, subtitleText, message, commandsGive);
                rewards.add(reward);
            }
        }

        Case caseObj = new Case(caseName, name, description, item, rewards);
        cases.put(caseName, caseObj);
    }

    private ItemStack createItemFromString(String itemString) {
        if (itemString.startsWith("basehead-")) {
            String texture = itemString.substring(9);
            return createSkull(texture);
        }

        try {
            Material material = Material.valueOf(itemString.toUpperCase());
            return new ItemStack(material);
        } catch (IllegalArgumentException e) {
            return new ItemStack(Material.CHEST);
        }
    }

    private ItemStack createSkull(String texture) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        try {
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", texture));

            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);

            skull.setItemMeta(meta);
        } catch (Exception ignored) {}

        return skull;
    }

    public Case getCase(String name) {
        return cases.get(name);
    }

    public Set<String> getCaseNames() {
        return cases.keySet();
    }

    public Map<String, Case> getCases() {
        return cases;
    }
}