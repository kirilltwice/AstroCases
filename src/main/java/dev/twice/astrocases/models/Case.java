package dev.twice.astrocases.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
@AllArgsConstructor
public class Case {
    private String id;
    private String name;
    private List<String> description;
    private ItemStack item;
    private List<CaseReward> rewards;

    public CaseReward getRandomReward() {
        double totalChance = rewards.stream().mapToDouble(CaseReward::getChance).sum();
        double random = Math.random() * totalChance;

        double currentChance = 0;
        for (CaseReward reward : rewards) {
            currentChance += reward.getChance();
            if (random <= currentChance) {
                return reward;
            }
        }

        return rewards.isEmpty() ? null : rewards.get(0);
    }
}