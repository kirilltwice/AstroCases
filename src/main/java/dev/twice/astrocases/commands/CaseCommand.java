package dev.twice.astrocases.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import dev.twice.astrocases.CasesPlugin;
import dev.twice.astrocases.models.Case;
import dev.twice.astrocases.utils.ItemUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Command(name = "case")
@Permission("astrocases.admin")
public class CaseCommand {

    private final CasesPlugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public CaseCommand(CasesPlugin plugin) {
        this.plugin = plugin;
    }

    @Execute(name = "give")
    public void giveCase(@Context Player sender, @Arg Player target, @Arg String caseName) {
        Case caseObj = plugin.getCaseManager().getCase(caseName);

        if (caseObj == null) {
            String message = plugin.getConfigManager().getMessage("commands.case-not-found")
                    .replace("%case%", caseName);
            sender.sendMessage(miniMessage.deserialize(message));
            return;
        }

        ItemStack caseItem = ItemUtils.createCaseItem(caseObj);

        if (target.getInventory().firstEmpty() == -1) {
            target.getWorld().dropItemNaturally(target.getLocation(), caseItem);
        } else {
            target.getInventory().addItem(caseItem);
        }

        String senderMessage = plugin.getConfigManager().getMessage("commands.case-given")
                .replace("%case%", caseObj.getName())
                .replace("%player%", target.getName());
        sender.sendMessage(miniMessage.deserialize(senderMessage));

        String targetMessage = plugin.getConfigManager().getMessage("commands.case-received")
                .replace("%case%", caseObj.getName());
        target.sendMessage(miniMessage.deserialize(targetMessage));
    }

    @Execute(name = "reload")
    public void reloadCases(@Context Player sender) {
        plugin.getConfigManager().loadConfigs();
        plugin.getCaseManager().loadCases();

        String message = plugin.getConfigManager().getMessage("commands.config-reloaded");
        sender.sendMessage(miniMessage.deserialize(message));
    }

    @Execute(name = "list")
    public void listCases(@Context Player sender) {
        if (plugin.getCaseManager().getCases().isEmpty()) {
            String message = plugin.getConfigManager().getMessage("commands.no-cases-found");
            sender.sendMessage(miniMessage.deserialize(message));
            return;
        }

        String headerMessage = plugin.getConfigManager().getMessage("commands.available-cases");
        sender.sendMessage(miniMessage.deserialize(headerMessage));

        String listFormat = plugin.getConfigManager().getMessage("commands.case-list-format");
        for (String caseName : plugin.getCaseManager().getCaseNames()) {
            Case caseObj = plugin.getCaseManager().getCase(caseName);
            String message = listFormat
                    .replace("%id%", caseName)
                    .replace("%name%", caseObj.getName());
            sender.sendMessage(miniMessage.deserialize(message));
        }
    }

    @Execute
    public void defaultCommand(@Context Player sender) {
        sender.sendMessage(miniMessage.deserialize("<yellow>Использование команд:"));
        sender.sendMessage(miniMessage.deserialize("<gray>/case give <игрок> <кейс> - Выдать кейс"));
        sender.sendMessage(miniMessage.deserialize("<gray>/case list - Список кейсов"));
        sender.sendMessage(miniMessage.deserialize("<gray>/case reload - Перезагрузить конфиги"));
    }

    public SuggestionResult suggestCaseNames(SuggestionContext context) {
        List<String> caseNames = new ArrayList<>(plugin.getCaseManager().getCaseNames());
        return SuggestionResult.of(caseNames);
    }
}