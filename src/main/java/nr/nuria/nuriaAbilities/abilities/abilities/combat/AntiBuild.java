package nr.nuria.nuriaAbilities.abilities.abilities.combat;

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
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

@RequiredArgsConstructor
public class AntiBuild implements Listener {


    private final BukkitScheduler bukkitScheduler;
    private final AbilitiesManager abilitiesManager;
    private final ConfigManager configManager;
    private final MessagesManager messagesManager;
    private final WorldGuardHook worldGuardHook;
    private final CooldownUtil cooldownUtil = new CooldownUtil();

    private final Map<UUID, Integer> hitCountMap = new HashMap<>();
    private final Map<UUID, Long> restrictedUntilMap = new HashMap<>();

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        Player target = (Player) event.getEntity();
        UUID damagerUUID = damager.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        ItemStack item = damager.getInventory().getItemInMainHand();
        if (AbilitiesItemManager.isAntiBuildItem(item)) {
            if (!worldGuardHook.canUseAbility(damager.getLocation(), damager) || !worldGuardHook.canUseAbility(target.getLocation(), target)) {
                sendMessage(damager, messagesManager.getMessage("region-blocked")
                        .replace("{prefix}", configManager.getPrefix())
                        .replace("{item}", abilitiesManager.getAntiBuildName()));
                event.setCancelled(true);
                return;
            }

            if (cooldownUtil.isOnCooldown(damagerUUID)) {
                int remainingTime = cooldownUtil.getRemainingTime(damagerUUID);
                sendMessage(damager, messagesManager.getMessage("cooldown-message")
                        .replace("{prefix}", configManager.getPrefix())
                        .replace("{time}", String.valueOf(remainingTime)));
                event.setCancelled(true);
                return;
            }

            hitCountMap.put(targetUUID, hitCountMap.getOrDefault(targetUUID, 0) + 1);

            if (hitCountMap.get(targetUUID) >= abilitiesManager.getAntiBuildHits()) {
                applyRestriction(targetUUID);
                sendMessage(damager, messagesManager.getMessage("ability-activated")
                        .replace("{prefix}", configManager.getPrefix())
                        .replace("{item}", abilitiesManager.getAntiBuildName()));

                reduceItemAmount(damager);
                playStartEffects(target.getLocation());
                sendAntiBuildTitle(target);
                hitCountMap.remove(targetUUID);
                cooldownUtil.setCooldown(damagerUUID, abilitiesManager.getAntiBuildCooldown());
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        Material blockType = event.getBlock().getType();

        if (AbilitiesItemManager.isAntiBuildItem(player.getInventory().getItemInMainHand())) {
            event.setCancelled(true);
            sendMessage(player, messagesManager.getMessage("place-blocked")
                    .replace("{prefix}", configManager.getPrefix())
                    .replace("{item}", abilitiesManager.getAntiBuildName()));
            return;
        }
        if (isRestricted(playerUUID) && isRestrictedBlock(blockType)) {
            event.setCancelled(true);
            sendMessage(player, messagesManager.getMessage("ab-place-blocked")
                    .replace("{prefix}", configManager.getPrefix()));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        Material blockType = event.getBlock().getType();

        if (AbilitiesItemManager.isAntiBuildItem(player.getInventory().getItemInMainHand())) {
            event.setCancelled(true);
            sendMessage(player, messagesManager.getMessage("break-blocked")
                    .replace("{prefix}", configManager.getPrefix())
                    .replace("{item}", abilitiesManager.getAntiBuildName()));
            return;
        }

        if (isRestricted(playerUUID) && isRestrictedBlock(blockType)) {
            event.setCancelled(true);
            sendMessage(player, messagesManager.getMessage("ab-break-blocked")
                    .replace("{prefix}", configManager.getPrefix()));
        }
    }

    private void applyRestriction(UUID playerUUID) {
        long restrictionDuration = abilitiesManager.getAntiBuildDuration() * 1000L;
        restrictedUntilMap.put(playerUUID, System.currentTimeMillis() + restrictionDuration);

        bukkitScheduler.runTaskLater(() -> {
            restrictedUntilMap.remove(playerUUID);
            Player player = getServer().getPlayer(playerUUID);
            if (player != null) {
                playEndEffects(player.getLocation());
                sendMessage(player, messagesManager.getMessage("ab-finish")
                        .replace("{prefix}", configManager.getPrefix())
                        .replace("{item}", abilitiesManager.getAntiBuildName()));
            }
        }, abilitiesManager.getAntiBuildDuration() * 20L);
    }

    private boolean isRestricted(UUID playerUUID) {
        return restrictedUntilMap.containsKey(playerUUID) && restrictedUntilMap.get(playerUUID) > System.currentTimeMillis();
    }

    private boolean isRestrictedBlock(Material blockType) {
        List<Material> restrictedMaterials = abilitiesManager.getAntiBuildRestrictedMaterials();
        return restrictedMaterials.contains(blockType);
    }

    private void reduceItemAmount(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        int reduceAmount = abilitiesManager.getAntiBuildReduceAmount();
        if (item.getAmount() > reduceAmount) {
            item.setAmount(item.getAmount() - reduceAmount);
        } else {
            player.getInventory().removeItem(item);
        }
    }

    private void playStartEffects(Location location) {
        location.getWorld().playSound(location, Sound.valueOf(abilitiesManager.getAntiBuildStartSoundName()), abilitiesManager.getAntiBuildStartSoundVolume(), abilitiesManager.getAntiBuildStartSoundPitch());
        location.getWorld().spawnParticle(Particle.valueOf(abilitiesManager.getAntiBuildStartParticle()), location, 50, 1, 1, 1);
    }

    private void playEndEffects(Location location) {
        location.getWorld().playSound(location, Sound.valueOf(abilitiesManager.getAntiBuildEndSoundName()), abilitiesManager.getAntiBuildEndSoundVolume(), abilitiesManager.getAntiBuildEndSoundPitch());
        location.getWorld().spawnParticle(Particle.valueOf(abilitiesManager.getAntiBuildEndParticle()), location, 50, 1, 1, 1);
    }

    private void sendAntiBuildTitle(Player target) {
        String title = messagesManager.getMessage("ab-title").replace("{item}", abilitiesManager.getAntiBuildName());
        String subtitle = messagesManager.getMessage("ab-subtitle");

        target.sendTitle(ColorUtil.format(title), ColorUtil.format(subtitle), 10, 70, 20);
    }

    private void sendMessage(Player player, String message) {
        player.sendMessage(ColorUtil.format(message));
    }
}