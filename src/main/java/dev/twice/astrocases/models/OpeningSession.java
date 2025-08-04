package dev.twice.astrocases.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Data
@AllArgsConstructor
public class OpeningSession {
    private UUID playerId;
    private Case caseObj;
    private ItemStack caseItem;
    private CaseReward reward;
}