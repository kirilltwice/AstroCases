package dev.twice.astrocases.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import dev.twice.astrocases.CasesPlugin;
import dev.twice.astrocases.models.Case;
import dev.twice.astrocases.utils.ItemUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
            sender.sendMessage(miniMessage.deserialize("<red>Кейс с именем '" + caseName + "' не найден!"));
            return;
        }

        ItemStack caseItem = ItemUtils.createCaseItem(caseObj);

        if (target.getInventory().firstEmpty() == -1) {
            target.getWorld().dropItemNaturally(target.getLocation(), caseItem);
        } else {
            target.getInventory().addItem(caseItem);
        }

        sender.sendMessage(miniMessage.deserialize("<green>Кейс '" + caseObj.getName() + "' выдан игроку " + target.getName()));
        target.sendMessage(miniMessage.deserialize("<green>Вы получили кейс: " + caseObj.getName()));
    }

    @Execute(name = "reload")
    public void reloadCases(@Context Player sender) {
        plugin.getConfigManager().loadConfigs();
        plugin.getCaseManager().loadCases();

        sender.sendMessage(miniMessage.deserialize("<green>Конфигурация кейсов перезагружена!"));
    }

    @Execute(name = "list")
    public void listCases(@Context Player sender) {
        if (plugin.getCaseManager().getCases().isEmpty()) {
            sender.sendMessage(miniMessage.deserialize("<red>Кейсов не найдено!"));
            return;
        }

        sender.sendMessage(miniMessage.deserialize("<yellow>Доступные кейсы:"));

        for (String caseName : plugin.getCaseManager().getCaseNames()) {
            Case caseObj = plugin.getCaseManager().getCase(caseName);
            sender.sendMessage(miniMessage.deserialize("<gray>- " + caseName + " (" + caseObj.getName() + ")"));
        }
    }
}