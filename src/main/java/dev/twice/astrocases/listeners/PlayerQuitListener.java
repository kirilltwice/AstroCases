package dev.twice.astrocases.listeners;

import dev.twice.astrocases.CasesPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final CasesPlugin plugin;

    public PlayerQuitListener(CasesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getOpeningManager().cancelOpening(player);
    }
}