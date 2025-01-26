package nr.nuria.nuriaAbilities.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lombok.RequiredArgsConstructor;
import nr.nuria.nuriaAbilities.managers.AbilitiesManager;
import nr.nuria.nuriaAbilities.managers.ConfigManager;
import nr.nuria.nuriaAbilities.managers.MessagesManager;
import nr.nuria.nuriaAbilities.util.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("nuriaabilities|nra")
@RequiredArgsConstructor
public class ReloadCommand extends BaseCommand {

    private final ConfigManager configManager;
    private final AbilitiesManager abilitiesManager;
    private final MessagesManager messagesManager;

    @Default
    public void onDefaultCommand(Player player) {
        player.sendMessage(ColorUtil.format(""));
        player.sendMessage(ColorUtil.format("#82c2ffAvailable commands:"));
        player.sendMessage(ColorUtil.format(""));
        player.sendMessage(ColorUtil.format("&7/nuriaabilities reload #82c2ff- Reload config."));
        player.sendMessage(ColorUtil.format("&7/nra reload #82c2ff- Reload config."));
        player.sendMessage(ColorUtil.format(""));
        player.sendMessage(ColorUtil.format("&7/abilities get antibuild #82c2ff- Get AntiBuild item."));
        player.sendMessage(ColorUtil.format("&7/abilities get timemachine #82c2ff- Get TimeMachine item."));
        player.sendMessage(ColorUtil.format("&7/abilities get antielytra #82c2ff- Get AntiElytra item."));
        player.sendMessage(ColorUtil.format("&7/abilities get trapper #82c2ff- Get Trapper item."));
        player.sendMessage(ColorUtil.format("&7/abilities get revolver #82c2ff- Get Revolver item."));
        player.sendMessage(ColorUtil.format("&7/abilities get webtrap #82c2ff- Get Webtrap item."));
        player.sendMessage(ColorUtil.format("&7/abilities get webdel #82c2ff- Get Webdel item."));
        player.sendMessage(ColorUtil.format("&7/abilities get all #82c2ff- Get all items."));
        player.sendMessage(ColorUtil.format(""));
    }

    @Subcommand("reload")
    @CommandPermission("nuriaabilities.reload")
    public void onReload(CommandSender sender) {
        configManager.reloadConfigFile();
        abilitiesManager.reloadAbilitiesFile();
        messagesManager.reloadMessagesFile();
        String reloadMessage = messagesManager.getMessage("reload-config")
                .replace("{prefix}", configManager.getPrefix());
        sender.sendMessage(ColorUtil.format(reloadMessage));
    }
}