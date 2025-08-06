package dev.twice.astrocases.models;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class OpeningSession {
    private UUID playerId;
    private Case caseObj;
    private ItemStack caseItem;
    private CaseReward reward;

    public OpeningSession(UUID playerId, Case caseObj, ItemStack caseItem, CaseReward reward) {
        this.playerId = playerId;
        this.caseObj = caseObj;
        this.caseItem = caseItem;
        this.reward = reward;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public Case getCaseObj() {
        return caseObj;
    }

    public void setCaseObj(Case caseObj) {
        this.caseObj = caseObj;
    }

    public ItemStack getCaseItem() {
        return caseItem;
    }

    public void setCaseItem(ItemStack caseItem) {
        this.caseItem = caseItem;
    }

    public CaseReward getReward() {
        return reward;
    }

    public void setReward(CaseReward reward) {
        this.reward = reward;
    }
}