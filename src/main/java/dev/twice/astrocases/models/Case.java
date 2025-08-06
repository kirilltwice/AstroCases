package dev.twice.astrocases.models;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Case {
    private String id;
    private String name;
    private List<String> description;
    private ItemStack item;
    private List<CaseReward> rewards;

    public Case(String id, String name, List<String> description, ItemStack item, List<CaseReward> rewards) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.item = item;
        this.rewards = rewards;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public List<CaseReward> getRewards() {
        return rewards;
    }

    public void setRewards(List<CaseReward> rewards) {
        this.rewards = rewards;
    }

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
