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
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class Webtrap implements Listener {

    private final AbilitiesManager abilitiesManager;
    private final ConfigManager configManager;
    private final MessagesManager messagesManager;
    private final WorldGuardHook worldGuardHook;
    private final CooldownUtil cooldownUtil = new CooldownUtil();
    private final BukkitScheduler bukkitScheduler;

    private final Map<UUID, Integer> hitCountMap = new HashMap<>();
    private final Map<Location, Material> originalMaterials = new HashMap<>();

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        Player target = (Player) event.getEntity();
        UUID damagerUUID = damager.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        ItemStack item = damager.getInventory().getItemInMainHand();
        if (AbilitiesItemManager.isWebtrapItem(item)) {
            if (!worldGuardHook.canUseAbility(damager.getLocation(), damager) || !worldGuardHook.canUseAbility(target.getLocation(), target)) {
                sendMessage(damager, messagesManager.getMessage("region-blocked")
                        .replace("{prefix}", configManager.getPrefix())
                        .replace("{item}", abilitiesManager.getWebtrapName()));
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
            if (hitCountMap.get(targetUUID) >= abilitiesManager.getWebtrapHits()) {
                playStartEffects(target.getLocation());
                placeCobwebs(target.getLocation());
                cooldownUtil.setCooldown(damagerUUID, abilitiesManager.getWebtrapCooldown());
                reduceItemAmount(damager);
                sendMessage(damager, messagesManager.getMessage("wt-message")
                        .replace("{target}", target.getName()));
                if (abilitiesManager.getWebtrapDuration() != -1) {
                    scheduleCobwebRemoval(target.getLocation());
                }
                hitCountMap.remove(targetUUID);
            }
        }
    }

    private void placeCobwebs(Location targetLocation) {
        int width = abilitiesManager.getWebtrapWidth();
        int height = abilitiesManager.getWebtrapHeight();
        List<Material> replaceableMaterials = abilitiesManager.getWebtrapReplaceableMaterials();

        int halfWidth = width / 2;
        int halfHeight = height / 2;

        for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int y = -halfHeight; y <= halfHeight; y++) {
                for (int z = -halfWidth; z <= halfWidth; z++) {
                    Location blockLocation = targetLocation.clone().add(x, y, z);
                    Block block = blockLocation.getBlock();

                    if (replaceableMaterials.contains(block.getType())) {
                        originalMaterials.put(blockLocation, block.getType());
                        block.setType(Material.COBWEB);
                    }
                }
            }
        }
    }


    private void scheduleCobwebRemoval(Location targetLocation) {
        int duration = abilitiesManager.getWebtrapDuration();

        if (duration == -1) {
            return;
        }

        int width = abilitiesManager.getWebtrapWidth();
        int height = abilitiesManager.getWebtrapHeight();
        int halfWidth = width / 2;
        int halfHeight = height / 2;

        bukkitScheduler.runTaskLater(() -> {
            for (int x = -halfWidth; x <= halfWidth; x++) {
                for (int y = -halfHeight; y <= halfHeight; y++) {
                    for (int z = -halfWidth; z <= halfWidth; z++) {
                        Location blockLocation = targetLocation.clone().add(x, y, z);
                        Block block = blockLocation.getBlock();

                        Material originalMaterial = originalMaterials.get(blockLocation);
                        if (originalMaterial != null) {
                            block.setType(originalMaterial);
                            originalMaterials.remove(blockLocation);
                        }
                    }
                }
            }
            playEndEffects(targetLocation);
        }, duration * 20L);
    }


    private void reduceItemAmount(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        int reduceAmount = abilitiesManager.getWebtrapReduceAmount();
        if (item.getAmount() > reduceAmount) {
            item.setAmount(item.getAmount() - reduceAmount);
        } else {
            player.getInventory().removeItem(item);
        }
    }

    private void playStartEffects(Location location) {
        location.getWorld().playSound(location, Sound.valueOf(abilitiesManager.getWebtrapStartSoundName()), abilitiesManager.getWebtrapStartSoundVolume(), abilitiesManager.getWebtrapStartSoundPitch());
        location.getWorld().spawnParticle(Particle.valueOf(abilitiesManager.getWebtrapStartParticle()), location, 50, 1, 1, 1);
    }

    private void playEndEffects(Location location) {
        location.getWorld().playSound(location, Sound.valueOf(abilitiesManager.getWebtrapEndSoundName()), abilitiesManager.getWebtrapEndSoundVolume(), abilitiesManager.getWebtrapEndSoundPitch());
        location.getWorld().spawnParticle(Particle.valueOf(abilitiesManager.getWebtrapEndParticle()), location, 50, 1, 1, 1);
    }

    private void sendMessage(Player player, String message) {
        player.sendMessage(ColorUtil.format(message));
    }
}