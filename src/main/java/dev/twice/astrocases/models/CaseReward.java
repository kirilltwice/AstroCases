package dev.twice.astrocases.models;

import java.util.List;

public class CaseReward {
    private String id;
    private double chance;
    private String titleText;
    private String subtitleText;
    private List<String> message;
    private String commandsGive;

    public CaseReward(String id, double chance, String titleText, String subtitleText, List<String> message, String commandsGive) {
        this.id = id;
        this.chance = chance;
        this.titleText = titleText;
        this.subtitleText = subtitleText;
        this.message = message;
        this.commandsGive = commandsGive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getSubtitleText() {
        return subtitleText;
    }

    public void setSubtitleText(String subtitleText) {
        this.subtitleText = subtitleText;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    public String getCommandsGive() {
        return commandsGive;
    }

    public void setCommandsGive(String commandsGive) {
        this.commandsGive = commandsGive;
    }
}