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
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class Trapper implements Listener {

    private final BukkitScheduler bukkitScheduler;
    private final AbilitiesManager abilitiesManager;
    private final ConfigManager configManager;
    private final MessagesManager messagesManager;
    private final WorldGuardHook worldGuardHook;
    private final CooldownUtil cooldownUtil = new CooldownUtil();

    private final Map<Location, Location> trapperCenters = new HashMap<>();
    private final Map<Location, Material> originalMaterials = new HashMap<>();

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        ItemStack item = event.getItem();
        Location location = player.getLocation();
        Action action = event.getAction();

        if (AbilitiesItemManager.isTrapperItem(item) && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            if (!worldGuardHook.canUseAbility(location, player)) {
                sendMessage(player, "region-blocked", "{item}", abilitiesManager.getTrapperName());
                return;
            }

            if (cooldownUtil.isOnCooldown(playerUUID)) {
                int remainingTime = cooldownUtil.getRemainingTime(playerUUID);
                sendMessage(player, "cooldown-message", "{time}", String.valueOf(remainingTime));
                return;
            }

            event.setCancelled(true);

            createGlassSphere(location);
            reduceItemAmount(player);
            playStartEffects(location);

            bukkitScheduler.runTaskLater(() -> {
                removeGlassSphere(location);
                playEndEffects(location);
            }, abilitiesManager.getTrapperSphereDuration());

            cooldownUtil.setCooldown(playerUUID, abilitiesManager.getTrapperCooldown());
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

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (AbilitiesItemManager.isTrapperItem(event.getItemInHand())) {
            Player player = event.getPlayer();
            sendMessage(player, "place-blocked", "{item}", abilitiesManager.getTrapperName());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (AbilitiesItemManager.isTrapperItem(player.getInventory().getItemInMainHand())) {
            sendMessage(player, "break-blocked", "{item}", abilitiesManager.getTrapperName());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (AbilitiesItemManager.isTrapperItem(player.getInventory().getItemInMainHand())) {
                sendMessage(player, "attack-blocked", "{item}", abilitiesManager.getTrapperName());
                event.setCancelled(true);
            }
        }
    }

    private void reduceItemAmount(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        int reduceAmount = abilitiesManager.getTrapperReduceAmount();
        if (item.getAmount() > reduceAmount) {
            item.setAmount(item.getAmount() - reduceAmount);
        } else {
            player.getInventory().remove(item);
        }
    }

    private void createGlassSphere(Location center) {
        int radius = abilitiesManager.getTrapperSphereRadius();
        BlockData sphereMaterial = abilitiesManager.getTrapperSphereMaterial().createBlockData();
        List<Material> replaceableMaterials = abilitiesManager.getTrapperReplaceableMaterials();
        trapperCenters.put(center, center);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radius * radius) {
                        Location loc = center.clone().add(new Vector(x, y, z));

                        if (worldGuardHook.canPlaceBlock(loc)) {
                            Block block = loc.getBlock();
                            Material currentMaterial = block.getType();

                            if (replaceableMaterials.contains(currentMaterial) && x * x + y * y + z * z >= (radius - 1) * (radius - 1)) {
                                originalMaterials.put(loc, currentMaterial);
                                block.setBlockData(sphereMaterial);
                            }
                        }
                    }
                }
            }
        }
    }

    private void removeGlassSphere(Location center) {
        int radius = abilitiesManager.getTrapperSphereRadius();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radius * radius) {
                        Location loc = center.clone().add(new Vector(x, y, z));
                        Block block = loc.getBlock();

                        Material originalMaterial = originalMaterials.get(loc);
                        if (originalMaterial != null) {
                            block.setType(originalMaterial);
                            originalMaterials.remove(loc);
                        }
                    }
                }
            }
        }
        trapperCenters.remove(center);
    }


    private void playStartEffects(Location location) {
        location.getWorld().playSound(location, Sound.valueOf(abilitiesManager.getTrapperStartSoundName()), abilitiesManager.getTrapperStartSoundVolume(), abilitiesManager.getTrapperStartSoundPitch());
        location.getWorld().spawnParticle(Particle.valueOf(abilitiesManager.getTrapperStartParticle()), location, 50, 1, 1, 1);
    }

    private void playEndEffects(Location location) {
        location.getWorld().playSound(location, Sound.valueOf(abilitiesManager.getTrapperEndSoundName()), abilitiesManager.getTrapperEndSoundVolume(), abilitiesManager.getTrapperEndSoundPitch());
        location.getWorld().spawnParticle(Particle.valueOf(abilitiesManager.getTrapperEndParticle()), location, 50, 1, 1, 1);
    }
}
