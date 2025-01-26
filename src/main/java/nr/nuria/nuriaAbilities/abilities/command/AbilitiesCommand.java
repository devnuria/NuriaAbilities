package nr.nuria.nuriaAbilities.abilities.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import lombok.RequiredArgsConstructor;
import nr.nuria.nuriaAbilities.abilities.manager.AbilitiesItemManager;
import nr.nuria.nuriaAbilities.managers.AbilitiesManager;
import nr.nuria.nuriaAbilities.managers.ConfigManager;
import nr.nuria.nuriaAbilities.managers.MessagesManager;
import nr.nuria.nuriaAbilities.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("habilidades|ab|abilities|ability|ppi|ppitems")
@CommandPermission("nuriaabilities.admin")
@RequiredArgsConstructor
public class AbilitiesCommand extends BaseCommand {

    private final ConfigManager configManager;
    private final MessagesManager messagesManager;
    private final AbilitiesManager abilitiesManager;

    @Default
    public void onDefaultCommand(Player player) {
        player.sendMessage(ColorUtil.format(""));
        player.sendMessage(ColorUtil.format("#82c2ffAvailable commands:"));
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
    @Subcommand("get revolver")
    public void onRevolverCommand(Player player, @Default("1") int amount) {
        ItemStack revolverItem = AbilitiesItemManager.createRevolverItem();
        revolverItem.setAmount(amount);
        player.getInventory().addItem(revolverItem);
        String message = messagesManager.getMessage("get-ability")
                .replace("{prefix}", configManager.getPrefix())
                .replace("{amount}", String.valueOf(amount))
                .replace("{item}", abilitiesManager.getRevolverName());
        player.sendMessage(ColorUtil.format(message));
    }
    @Subcommand("get webtrap")
    public void onWebtrapCommand(Player player, @Default("1") int amount) {
        ItemStack webtrapItem = AbilitiesItemManager.createWebtrapItem();
        webtrapItem.setAmount(amount);
        player.getInventory().addItem(webtrapItem);
        String message = messagesManager.getMessage("get-ability")
                .replace("{prefix}", configManager.getPrefix())
                .replace("{amount}", String.valueOf(amount))
                .replace("{item}", abilitiesManager.getWebtrapName());
        player.sendMessage(ColorUtil.format(message));
    }
    @Subcommand("get webdel")
    public void onWebdelCommand(Player player, @Default("1") int amount) {
        ItemStack webdelItem = AbilitiesItemManager.createWebdelItem();
        webdelItem.setAmount(amount);
        player.getInventory().addItem(webdelItem);
        String message = messagesManager.getMessage("get-ability")
                .replace("{prefix}", configManager.getPrefix())
                .replace("{amount}", String.valueOf(amount))
                .replace("{item}", abilitiesManager.getWebdelName());
        player.sendMessage(ColorUtil.format(message));
    }
    @Subcommand("get trapper")
    public void onTrapperCommand(Player player, @Default("1") int amount) {
        ItemStack trapperItem = AbilitiesItemManager.createTrapperItem();
        trapperItem.setAmount(amount);
        player.getInventory().addItem(trapperItem);
        String message = messagesManager.getMessage("get-ability")
                .replace("{prefix}", configManager.getPrefix())
                .replace("{amount}", String.valueOf(amount))
                .replace("{item}", abilitiesManager.getTrapperName());
        player.sendMessage(ColorUtil.format(message));
    }
    @Subcommand("get antielytra")
    public void onAntiElytraCommand(Player player, @Default("1") int amount) {
        ItemStack antiElytraItem = AbilitiesItemManager.createAntiElytraItem();
        antiElytraItem.setAmount(amount);
        player.getInventory().addItem(antiElytraItem);
        String message = messagesManager.getMessage("get-ability")
                .replace("{prefix}", configManager.getPrefix())
                .replace("{amount}", String.valueOf(amount))
                .replace("{item}", abilitiesManager.getAntiElytraName());
        player.sendMessage(ColorUtil.format(message));
    }
    @Subcommand("get timemachine")
    public void onTimeMachineCommand(Player player, @Default("1") int amount) {
        ItemStack timeMachineItem = AbilitiesItemManager.createTimeMachineItem();
        timeMachineItem.setAmount(amount);
        player.getInventory().addItem(timeMachineItem);
        String message = messagesManager.getMessage("get-ability")
                .replace("{prefix}", configManager.getPrefix())
                .replace("{amount}", String.valueOf(amount))
                .replace("{item}", abilitiesManager.getTimeMachineName());
        player.sendMessage(ColorUtil.format(message));
    }

    @Subcommand("get antibuild")
    public void onAntiBuildCommand(Player player, @Default("1") int amount) {
        ItemStack antiBuildItem = AbilitiesItemManager.createAntiBuildItem();
        antiBuildItem.setAmount(amount);
        player.getInventory().addItem(antiBuildItem);
        String message = messagesManager.getMessage("get-ability")
                .replace("{prefix}", configManager.getPrefix())
                .replace("{amount}", String.valueOf(amount))
                .replace("{item}", abilitiesManager.getAntiBuildName());
        player.sendMessage(ColorUtil.format(message));
    }



    @Subcommand("get all")
    public void onGetAllCommand(Player player) {
        player.getInventory().addItem(AbilitiesItemManager.createAntiBuildItem());
        player.getInventory().addItem(AbilitiesItemManager.createTimeMachineItem());
        player.getInventory().addItem(AbilitiesItemManager.createAntiElytraItem());
        player.getInventory().addItem(AbilitiesItemManager.createTrapperItem());
        player.getInventory().addItem(AbilitiesItemManager.createRevolverItem());
        player.getInventory().addItem(AbilitiesItemManager.createWebtrapItem());
        player.getInventory().addItem(AbilitiesItemManager.createWebdelItem());
        String getAllAbilitys = messagesManager.getMessage("get-all-abilitys")
                .replace("{prefix}", configManager.getPrefix());
        player.sendMessage(ColorUtil.format(getAllAbilitys));
    }

}