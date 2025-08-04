package dev.twice.astrocases.managers;

import dev.twice.astrocases.CasesPlugin;
import dev.twice.astrocases.models.Case;
import dev.twice.astrocases.models.CaseReward;
import dev.twice.astrocases.models.OpeningSession;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
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

        new BukkitRunnable() {
            int progress = 0;
            int tick = 0;

            @Override
            public void run() {
                if (!player.isOnline() || !openingSessions.containsKey(player.getUniqueId())) {
                    cancel();
                    return;
                }

                tick++;

                if (tick % 20 == 0) {
                    progress += 20;
                }

                String progressBar = createProgressBar(progress);
                String titleFormat = plugin.getConfigManager().getMainConfig().getString("cases-open.title.0", "Открытие кейса");
                String title = titleFormat.replace("<progress>", progressBar).replace("%s", String.valueOf(progress));

                String subtitleText = plugin.getConfigManager().getMainConfig().getString("cases-open.subtitle", "<gray>Ждите...</gray>");

                Component titleComponent = miniMessage.deserialize(title);
                Component subtitleComponent = miniMessage.deserialize(subtitleText);

                player.showTitle(Title.title(
                        titleComponent,
                        subtitleComponent,
                        Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(200), Duration.ofMillis(100))
                ));

                if (progress >= 100) {
                    finishOpening(player, session);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private String createProgressBar(int progress) {
        int bars = progress / 10;
        StringBuilder progressBar = new StringBuilder("<green>");

        for (int i = 0; i < 10; i++) {
            if (i < bars) {
                progressBar.append("█");
            } else {
                progressBar.append("<gray>█");
            }
        }

        return progressBar + " <white>" + progress + "%";
    }

    private void finishOpening(Player player, OpeningSession session) {
        openingSessions.remove(player.getUniqueId());

        CaseReward reward = session.getReward();

        if (!reward.getTitleText().isEmpty()) {
            Component titleComponent = miniMessage.deserialize(reward.getTitleText());
            Component subtitleComponent = miniMessage.deserialize("<green>Поздравляем!</green>");
            player.showTitle(Title.title(
                    titleComponent,
                    subtitleComponent,
                    Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofSeconds(1))
            ));
        }

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