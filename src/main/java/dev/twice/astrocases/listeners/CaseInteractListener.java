package dev.twice.astrocases.listeners;

import dev.twice.astrocases.CasesPlugin;
import dev.twice.astrocases.models.Case;
import dev.twice.astrocases.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CaseInteractListener implements Listener {

    private final CasesPlugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final Map<UUID, PendingCase> pendingCases = new HashMap<>();

    public CaseInteractListener(CasesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !ItemUtils.isCaseItem(item)) {
            return;
        }

        event.setCancelled(true);

        if (plugin.getOpeningManager().isOpening(player.getUniqueId())) {
            String message = plugin.getConfigManager().getMessage("case-opening.already-opening");
            player.sendMessage(miniMessage.deserialize(message));
            return;
        }

        String caseId = ItemUtils.getCaseId(item);
        Case caseObj = plugin.getCaseManager().getCase(caseId);

        if (caseObj == null) {
            String message = plugin.getConfigManager().getMessage("case-opening.unknown-case");
            player.sendMessage(miniMessage.deserialize(message));
            return;
        }

        item.setAmount(item.getAmount() - 1);
        ItemStack caseItem = item.clone();
        caseItem.setAmount(1);

        showCaseConfirmation(player, caseObj, caseItem);
    }

    private void showCaseConfirmation(Player player, Case caseObj, ItemStack caseItem) {
        List<String> messages = plugin.getConfigManager().getMessageList("case-opening.confirm-message");
        String yesButtonText = plugin.getConfigManager().getMessage("case-opening.button.yes");
        String noButtonText = plugin.getConfigManager().getMessage("case-opening.button.no");

        pendingCases.put(player.getUniqueId(), new PendingCase(caseObj, caseItem));

        for (String msg : messages) {
            if (msg.contains("%s") || msg.contains("%n")) {
                Component yesComponent = miniMessage.deserialize(yesButtonText)
                        .hoverEvent(HoverEvent.showText(miniMessage.deserialize("<green>Нажмите чтобы открыть кейс")))
                        .clickEvent(ClickEvent.callback(audience -> {
                            if (audience instanceof Player p) {
                                handleConfirmation(p, true);
                            }
                        }));

                Component noComponent = miniMessage.deserialize(noButtonText)
                        .hoverEvent(HoverEvent.showText(miniMessage.deserialize("<red>Нажмите чтобы отменить")))
                        .clickEvent(ClickEvent.callback(audience -> {
                            if (audience instanceof Player p) {
                                handleConfirmation(p, false);
                            }
                        }));

                String processedMsg = msg.replace("%s", "").replace("%n", "");
                Component message = miniMessage.deserialize(processedMsg)
                        .append(Component.space())
                        .append(yesComponent)
                        .append(Component.text(" "))
                        .append(noComponent);

                player.sendMessage(message);
            } else {
                player.sendMessage(miniMessage.deserialize(msg));
            }
        }
    }

    private void handleConfirmation(Player player, boolean confirmed) {
        PendingCase pending = pendingCases.remove(player.getUniqueId());

        if (pending == null) {
            return;
        }

        if (confirmed) {
            plugin.getOpeningManager().startOpening(player, pending.caseObj, pending.caseItem);
        } else {
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(pending.caseItem);
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), pending.caseItem);
            }
            String message = plugin.getConfigManager().getMessage("case-opening.cancel");
            player.sendMessage(miniMessage.deserialize(message));
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (ItemUtils.isCaseItem(item)) {
            event.setCancelled(true);
            String message = plugin.getConfigManager().getMessage("case-opening.cant-drop");
            event.getPlayer().sendMessage(miniMessage.deserialize(message));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item != null && ItemUtils.isCaseItem(item)) {
            InventoryType inventoryType = event.getInventory().getType();

            if (inventoryType != InventoryType.PLAYER && inventoryType != InventoryType.CRAFTING) {
                event.setCancelled(true);
                String message = plugin.getConfigManager().getMessage("case-opening.cant-store");
                event.getWhoClicked().sendMessage(miniMessage.deserialize(message));
                return;
            }
        }

        ItemStack cursor = event.getCursor();
        if (cursor != null && ItemUtils.isCaseItem(cursor)) {
            InventoryType inventoryType = event.getInventory().getType();
            if (inventoryType != InventoryType.PLAYER && inventoryType != InventoryType.CRAFTING) {
                event.setCancelled(true);
                String message = plugin.getConfigManager().getMessage("case-opening.cant-store");
                event.getWhoClicked().sendMessage(miniMessage.deserialize(message));
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<ItemStack> offlineReturns = plugin.getConfigManager().getOfflineReturns(player.getUniqueId());

        if (!offlineReturns.isEmpty()) {
            for (ItemStack item : offlineReturns) {
                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(item);
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
            }

            plugin.getConfigManager().clearOfflineReturns(player.getUniqueId());
            String message = plugin.getConfigManager().getMessage("case-opening.offline-return");
            player.sendMessage(miniMessage.deserialize(message));
        }
    }

    private record PendingCase(Case caseObj, ItemStack caseItem) {}
}