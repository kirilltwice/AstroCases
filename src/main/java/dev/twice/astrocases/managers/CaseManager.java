package dev.twice.astrocases.managers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.twice.astrocases.CasesPlugin;
import dev.twice.astrocases.models.Case;
import dev.twice.astrocases.models.CaseReward;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

@Getter
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
            createDefaultCase();
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
        try {
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
                    List<String> message = config.getStringList(path + ".message");
                    String commandsGive = config.getString(path + ".commands-give", "");

                    CaseReward reward = new CaseReward(key, chance, titleText, message, commandsGive);
                    rewards.add(reward);
                }
            }

            Case caseObj = new Case(caseName, name, description, item, rewards);
            cases.put(caseName, caseObj);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return skull;
    }

    private void createDefaultCase() {
        File defaultCase = new File(plugin.getDataFolder(), "cases/test.yml");
        try {
            defaultCase.getParentFile().mkdirs();
            plugin.saveResource("cases/test.yml", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Case getCase(String name) {
        return cases.get(name);
    }

    public Set<String> getCaseNames() {
        return cases.keySet();
    }
}