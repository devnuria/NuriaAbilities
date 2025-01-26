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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class Webdel implements Listener {

    private final AbilitiesManager abilitiesManager;
    private final ConfigManager configManager;
    private final MessagesManager messagesManager;
    private final WorldGuardHook worldGuardHook;
    private final CooldownUtil cooldownUtil = new CooldownUtil();
    private final BukkitScheduler bukkitScheduler;

    @EventHandler
    public void onPlayerUseWebdel(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (AbilitiesItemManager.isWebdelItem(item)) {
            if (!worldGuardHook.canUseAbility(player.getLocation(), player)) {
                sendMessage(player, "region-blocked", "{item}", abilitiesManager.getWebdelName());
                event.setCancelled(true);
                return;
            }

            if (cooldownUtil.isOnCooldown(player.getUniqueId())) {
                int remainingTime = cooldownUtil.getRemainingTime(player.getUniqueId());
                sendMessage(player, "cooldown-message", "{time}", String.valueOf(remainingTime));
                event.setCancelled(true);
                return;
            }

            playStartEffects(player.getLocation());
            if (worldGuardHook.canUseAbility(player.getLocation(), player)) {
                clearCobwebs(player.getLocation(), player);
            } else {
                sendMessage(player, "region-blocked", "{item}", abilitiesManager.getWebdelName());
            }
            cooldownUtil.setCooldown(player.getUniqueId(), abilitiesManager.getWebdelCooldown());
            reduceItemAmount(player);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (AbilitiesItemManager.isWebdelItem(item)) {
            sendMessage(player, "place-blocked", "{item}", abilitiesManager.getWebdelName());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (AbilitiesItemManager.isWebdelItem(item)) {
            sendMessage(player, "break-blocked", "{item}", abilitiesManager.getWebdelName());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (AbilitiesItemManager.isWebdelItem(item)) {
                sendMessage(player, "attack-blocked", "{item}", abilitiesManager.getWebdelName());
                event.setCancelled(true);
            }
        }
    }

    private void clearCobwebs(Location location, Player player) {
        int width = abilitiesManager.getWebdelWidth();
        int height = abilitiesManager.getWebdelHeight();
        int clearedCobwebs = 0;

        int halfWidth = width / 2;
        int startY = location.getBlockY();
        int endY = startY + height;

        for (int y = startY; y < endY; y++) {
            for (int x = -halfWidth; x <= halfWidth; x++) {
                for (int z = -halfWidth; z <= halfWidth; z++) {
                    Location blockLocation = location.clone().add(x, y - startY, z);
                    if (!worldGuardHook.canUseAbility(blockLocation, null)) {
                        continue;
                    }
                    if (blockLocation.getBlock().getType() == Material.COBWEB) {
                        blockLocation.getBlock().setType(Material.AIR);
                        clearedCobwebs++;
                    }
                }
            }
        }

        if (clearedCobwebs > 0) {
            sendMessage(player, "wb-message", "{amount}", String.valueOf(clearedCobwebs));
        }
    }

    private void playStartEffects(Location location) {
        location.getWorld().playSound(location, Sound.valueOf(abilitiesManager.getWebdelStartSoundName()), abilitiesManager.getWebdelStartSoundVolume(), abilitiesManager.getWebdelStartSoundPitch());
        location.getWorld().spawnParticle(Particle.valueOf(abilitiesManager.getWebdelStartParticle()), location, 50, 1, 1, 1);
    }

    private void reduceItemAmount(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        int reduceAmount = abilitiesManager.getWebdelReduceAmount();
        if (item.getAmount() > reduceAmount) {
            item.setAmount(item.getAmount() - reduceAmount);
        } else {
            player.getInventory().removeItem(item);
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
}
