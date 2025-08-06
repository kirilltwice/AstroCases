package dev.twice.astrocases.managers;

import dev.twice.astrocases.CasesPlugin;
import dev.twice.astrocases.models.Case;
import dev.twice.astrocases.models.CaseReward;
import dev.twice.astrocases.models.OpeningSession;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OpeningManager {

    private final CasesPlugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final Map<UUID, OpeningSession> openingSessions = new HashMap<>();

    public OpeningManager(CasesPlugin plugin) {
        this.plugin = plugin;
    }

    public void startOpening(Player player, Case caseObj, ItemStack caseItem) {
        if (openingSessions.containsKey(player.getUniqueId())) {
            return;
        }

        CaseReward reward = caseObj.getRandomReward();
        if (reward == null) {
            return;
        }

        OpeningSession session = new OpeningSession(player.getUniqueId(), caseObj, caseItem, reward);
        openingSessions.put(player.getUniqueId(), session);

        finishOpening(player, session);
    }

    private void finishOpening(Player player, OpeningSession session) {
        openingSessions.remove(player.getUniqueId());

        CaseReward reward = session.getReward();

        String titleText = reward.getTitleText();
        String subtitleText = reward.getSubtitleText();

        Component titleComponent = miniMessage.deserialize(titleText);
        Component subtitleComponent = miniMessage.deserialize(subtitleText);

        player.showTitle(Title.title(
                titleComponent,
                subtitleComponent,
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofSeconds(1))
        ));

        for (String msg : reward.getMessage()) {
            String message = msg.replace("%username%", player.getName())
                    .replace("%p", player.getName());
            Bukkit.broadcast(miniMessage.deserialize(message));
        }

        if (!reward.getCommandsGive().isEmpty()) {
            String command = reward.getCommandsGive().replace("%p", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public void cancelOpening(Player player) {
        OpeningSession session = openingSessions.remove(player.getUniqueId());
        if (session != null) {
            returnCaseToPlayer(player, session.getCaseItem());
        }
    }

    public void returnAllCases() {
        for (OpeningSession session : openingSessions.values()) {
            Player player = Bukkit.getPlayer(session.getPlayerId());
            if (player != null && player.isOnline()) {
                returnCaseToPlayer(player, session.getCaseItem());
            } else {
                plugin.getConfigManager().addOfflineReturn(session.getPlayerId(), session.getCaseItem());
            }
        }
        openingSessions.clear();
    }

    private void returnCaseToPlayer(Player player, ItemStack caseItem) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(caseItem);
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), caseItem);
        }
    }

    public boolean isOpening(UUID playerId) {
        return openingSessions.containsKey(playerId);
    }
}