package nr.nuria.nuriaAbilities.abilities.abilities.interactive;

import lombok.RequiredArgsConstructor;
import nr.nuria.nuriaAbilities.abilities.manager.AbilitiesItemManager;
import nr.nuria.nuriaAbilities.abilities.util.CooldownUtil;
import nr.nuria.nuriaAbilities.hooks.WorldGuardHook;
import nr.nuria.nuriaAbilities.managers.AbilitiesManager;
import nr.nuria.nuriaAbilities.managers.ConfigManager;
import nr.nuria.nuriaAbilities.managers.MessagesManager;
import nr.nuria.nuriaAbilities.util.ColorUtil;
import nr.nuria.nuriaAbilities.util.factory.BukkitScheduler;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class TimeMachine implements Listener {

    private final BukkitScheduler bukkitScheduler;
    private final AbilitiesManager abilitiesManager;
    private final ConfigManager configManager;
    private final MessagesManager messagesManager;
    private final WorldGuardHook worldGuardHook;
    private final CooldownUtil cooldownUtil = new CooldownUtil();

    private final Map<UUID, Location> previousLocations = new HashMap<>();
    private final Map<UUID, Long> lastUseTimes = new HashMap<>();

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        ItemStack item = event.getItem();

        if (AbilitiesItemManager.isTimeMachineItem(item)) {
            Location currentLocation = player.getLocation();

            if (!worldGuardHook.canUseAbility(currentLocation, player)) {
                sendMessage(player, "region-blocked", "{item}", abilitiesManager.getTimeMachineName());
                return;
            }

            if (cooldownUtil.isOnCooldown(playerUUID)) {
                int remainingTime = cooldownUtil.getRemainingTime(playerUUID);
                sendMessage(player, "cooldown-message", "{time}", String.valueOf(remainingTime));
                return;
            }

            previousLocations.put(playerUUID, currentLocation);
            lastUseTimes.put(playerUUID, System.currentTimeMillis());

            reduceItemAmount(player);

            int durationSeconds = abilitiesManager.getTimeMachineDuration() / 20;
            sendTitle(player, "tm-title", "tm-subtitle", "{seconds}", String.valueOf(durationSeconds));

            event.setCancelled(true);
            playStartEffects(currentLocation);

            bukkitScheduler.runTaskLater(() -> {
                if (lastUseTimes.containsKey(playerUUID) && System.currentTimeMillis() - lastUseTimes.get(playerUUID) >= abilitiesManager.getTimeMachineDuration()) {
                    returnToPreviousLocation(player);
                }
                playEndEffects(player.getLocation());
                sendTitle(player, "tm-title-b", "tm-subtitle-b", null, null);
            }, abilitiesManager.getTimeMachineDuration());

            if (abilitiesManager.getTimeMachineCooldown() > 0) {
                cooldownUtil.setCooldown(playerUUID, abilitiesManager.getTimeMachineCooldown());
            }
        }
    }

    private void reduceItemAmount(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        int reduceAmount = abilitiesManager.getTimeMachineReduceAmount();

        if (item.getAmount() > reduceAmount) {
            item.setAmount(item.getAmount() - reduceAmount);
        } else {
            player.getInventory().removeItem(item);
        }
    }

    private void returnToPreviousLocation(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (previousLocations.containsKey(playerUUID)) {
            Location previousLocation = previousLocations.get(playerUUID);
            player.teleport(previousLocation);
            sendMessage(player, "timetravel-success", "{item}", abilitiesManager.getTimeMachineName());
        } else {
            sendMessage(player, "no-previous-location", null, null);
        }
        previousLocations.remove(playerUUID);
        lastUseTimes.remove(playerUUID);
    }

    // Method to send a title and subtitle
    private void sendTitle(Player player, String titleKey, String subtitleKey, String placeholder, String replacement) {
        String title = messagesManager.getMessage(titleKey);
        String subtitle = messagesManager.getMessage(subtitleKey);

        if (placeholder != null && replacement != null) {
            if (title != null) {
                title = title.replace(placeholder, replacement);
            }
            if (subtitle != null) {
                subtitle = subtitle.replace(placeholder, replacement);
            }
        }

        if (title != null && subtitle != null) {
            player.sendTitle(ColorUtil.format(title), ColorUtil.format(subtitle), 10, 70, 20);
        }
    }

    private void sendMessage(Player player, String messageKey, String placeholder, String replacement) {
        String message = messagesManager.getMessage(messageKey);
        if (message != null) {
            message = message.replace("{prefix}", configManager.getPrefix());
            if (placeholder != null && replacement != null) {
                message = message.replace(placeholder, replacement);
            }
            player.sendMessage(ColorUtil.format(message));
        }
    }

    private void playStartEffects(Location location) {
        location.getWorld().playSound(location, Sound.valueOf(abilitiesManager.getTimeMachineStartSoundName()), abilitiesManager.getTimeMachineStartSoundVolume(), abilitiesManager.getTimeMachineStartSoundPitch());
        location.getWorld().spawnParticle(Particle.valueOf(abilitiesManager.getTimeMachineStartParticle()), location, 50, 1, 1, 1);
    }

    private void playEndEffects(Location location) {
        location.getWorld().playSound(location, Sound.valueOf(abilitiesManager.getTimeMachineEndSoundName()), abilitiesManager.getTimeMachineEndSoundVolume(), abilitiesManager.getTimeMachineEndSoundPitch());
        location.getWorld().spawnParticle(Particle.valueOf(abilitiesManager.getTimeMachineEndParticle()), location, 50, 1, 1, 1);
    }
}
