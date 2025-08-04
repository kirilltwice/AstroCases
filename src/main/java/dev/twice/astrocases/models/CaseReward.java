package dev.twice.astrocases.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CaseReward {
    private String id;
    private double chance;
    private String titleText;
    private List<String> message;
    private String commandsGive;
}