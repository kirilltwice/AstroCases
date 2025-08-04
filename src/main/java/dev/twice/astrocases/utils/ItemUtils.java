package dev.twice.astrocases.utils;

import dev.twice.astrocases.models.Case;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    private static final NamespacedKey CASE_KEY = new NamespacedKey("astrocases", "case_id");
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static ItemStack createCaseItem(Case caseObj) {
        ItemStack item = caseObj.getItem().clone();
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(miniMessage.deserialize(caseObj.getName()));

            List<Component> lore = new ArrayList<>();
            for (String line : caseObj.getDescription()) {
                lore.add(miniMessage.deserialize(line));
            }
            meta.lore(lore);

            meta.getPersistentDataContainer().set(CASE_KEY, PersistentDataType.STRING, caseObj.getId());

            item.setItemMeta(meta);
        }

        return item;
    }

    public static String getCaseId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(CASE_KEY, PersistentDataType.STRING);
    }

    public static boolean isCaseItem(ItemStack item) {
        return getCaseId(item) != null;
    }
}