package nr.nuria.nuriaAbilities.listener;

import nr.nuria.nuriaAbilities.managers.ConfigManager;
import nr.nuria.nuriaAbilities.managers.MessagesManager;
import nr.nuria.nuriaAbilities.util.ColorUtil;
import nr.nuria.nuriaAbilities.util.VersionChecker;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoinListener implements Listener {

    private final VersionChecker versionChecker;
    private final ConfigManager configManager;
    private final MessagesManager messagesManager;

    public PlayerJoinListener(VersionChecker versionChecker, ConfigManager configManager, MessagesManager messagesManager) {
        this.versionChecker = versionChecker;
        this.configManager = configManager;
        this.messagesManager = messagesManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) {
            String newVersion = versionChecker.getNewVersion();
            String currentVersion = versionChecker.getCurrentVersion();

            if (newVersion != null && versionChecker.isUpdateAvailable()) {
                String updateMessage = messagesManager.getMessage("update-message");

                String formattedMessage = updateMessage
                        .replace("{prefix}", configManager.getPrefix())
                        .replace("{currentversion}", currentVersion)
                        .replace("{newversion}", newVersion);

                TextComponent message = ColorUtil.formatClickableText(
                        formattedMessage,
                        "OPEN_URL",
                        "https://builtbybit.com/resources/nuriaabilities-boxpvp-abilities.51792/",
                        null
                );

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        event.getPlayer().spigot().sendMessage(message);
                    }
                }.runTaskLater(Bukkit.getPluginManager().getPlugin("NuriaAbilities"), 5);
            }
        }
    }
}
