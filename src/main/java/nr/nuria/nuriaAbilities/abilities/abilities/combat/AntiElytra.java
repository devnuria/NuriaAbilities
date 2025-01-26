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
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class AntiElytra implements Listener {

    private final BukkitScheduler bukkitScheduler;
    private final AbilitiesManager abilitiesManager;
    private final ConfigManager configManager;
    private final MessagesManager messagesManager;
    private final WorldGuardHook worldGuardHook;
    private final CooldownUtil cooldownUtil = new CooldownUtil();
    private final Set<UUID> notifiedPlayers = new HashSet<>();
    private final JavaPlugin plugin;
    private static final NamespacedKey EFFECT_KEY = new NamespacedKey("nuriaabilities", "antielytra_active");
    private static final NamespacedKey STORED_ELYTRA_KEY = new NamespacedKey("nuriaabilities", "stored_elytra");

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        Player target = (Player) event.getEntity();
        UUID damagerUUID = damager.getUniqueId();

        ItemStack item = damager.getInventory().getItemInMainHand();
        if (AbilitiesItemManager.isAntiElytraItem(item)) {
            if (!worldGuardHook.canUseAbility(damager.getLocation(), damager) || !worldGuardHook.canUseAbility(target.getLocation(), target)) {
                sendMessage(damager, messagesManager.getMessage("region-blocked")
                        .replace("{prefix}", configManager.getPrefix())
                        .replace("{item}", abilitiesManager.getAntiElytraName()));
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

            if (target.getInventory().getChestplate() != null && target.getInventory().getChestplate().getType() == Material.ELYTRA) {
                ItemStack elytra = target.getInventory().getChestplate();
                target.getInventory().setChestplate(null);
                target.getPersistentDataContainer().set(EFFECT_KEY, PersistentDataType.BYTE, (byte) 1);
                target.getPersistentDataContainer().set(STORED_ELYTRA_KEY, PersistentDataType.STRING, elytra.getItemMeta().getDisplayName());

                sendMessage(damager, messagesManager.getMessage("ae-message")
                        .replace("{prefix}", configManager.getPrefix())
                        .replace("{target}", target.getName()));

                playStartEffects(target);
                sendAntiElytraTitle(target);
                reduceItemAmount(damager);
                cooldownUtil.setCooldown(damagerUUID, abilitiesManager.getAntiElytraCooldown());

                scheduleEndEffects(target, elytra);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (AbilitiesItemManager.isAntiElytraItem(event.getItemInHand())) {
            Player player = event.getPlayer();
            sendMessage(player, messagesManager.getMessage("place-blocked")
                    .replace("{prefix}", configManager.getPrefix())
                    .replace("{item}", abilitiesManager.getAntiElytraName()));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (AbilitiesItemManager.isAntiElytraItem(player.getInventory().getItemInMainHand())) {
            sendMessage(player, messagesManager.getMessage("break-blocked")
                    .replace("{prefix}", configManager.getPrefix())
                    .replace("{item}", abilitiesManager.getAntiElytraName()));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.ELYTRA) {
            if (player.getPersistentDataContainer().has(EFFECT_KEY, PersistentDataType.BYTE)) {
                if (notifiedPlayers.add(player.getUniqueId())) {
                    long remainingTime = cooldownUtil.getRemainingTime(player.getUniqueId());
                    sendMessage(player, messagesManager.getMessage("ae-equip-elytra")
                            .replace("{prefix}", configManager.getPrefix())
                            .replace("{duration}", String.valueOf(remainingTime)));
                    bukkitScheduler.runTaskLater(() -> notifiedPlayers.remove(player.getUniqueId()), 20L); // 20 ticks = 1 second
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();

        if (currentItem != null && currentItem.getType() == Material.ELYTRA) {
            if (player.getPersistentDataContainer().has(EFFECT_KEY, PersistentDataType.BYTE)) {
                if (notifiedPlayers.add(player.getUniqueId())) {
                    long remainingTime = cooldownUtil.getRemainingTime(player.getUniqueId());
                    sendMessage(player, messagesManager.getMessage("ae-equip-elytra")
                            .replace("{prefix}", configManager.getPrefix())
                            .replace("{duration}", String.valueOf(remainingTime)));
                    bukkitScheduler.runTaskLater(() -> notifiedPlayers.remove(player.getUniqueId()), 20L); // 20 ticks = 1 second
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getPersistentDataContainer().has(EFFECT_KEY, PersistentDataType.BYTE)) {
            player.getPersistentDataContainer().remove(EFFECT_KEY);
            if (player.getPersistentDataContainer().has(STORED_ELYTRA_KEY, PersistentDataType.STRING)) {
                String elytraName = player.getPersistentDataContainer().get(STORED_ELYTRA_KEY, PersistentDataType.STRING);
                ItemStack storedElytra = new ItemStack(Material.ELYTRA);
                ItemMeta meta = storedElytra.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(elytraName);
                    storedElytra.setItemMeta(meta);
                }
                player.getWorld().dropItemNaturally(player.getLocation(), storedElytra);
                player.getPersistentDataContainer().remove(STORED_ELYTRA_KEY);
            }

            ItemStack chestplate = player.getInventory().getChestplate();
            if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
                player.getWorld().dropItemNaturally(player.getLocation(), chestplate);
                player.getInventory().setChestplate(null);
            }

            event.getDrops().removeIf(item -> item.getType() == Material.ELYTRA);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (player.getPersistentDataContainer().has(EFFECT_KEY, PersistentDataType.BYTE)) {
            player.getPersistentDataContainer().remove(EFFECT_KEY);
        }

        if (player.getPersistentDataContainer().has(STORED_ELYTRA_KEY, PersistentDataType.STRING)) {
            player.getPersistentDataContainer().remove(STORED_ELYTRA_KEY);
        }
    }

    private void playStartEffects(Player target) {
        Location location = target.getLocation();
        location.getWorld().playSound(location, Sound.valueOf(abilitiesManager.getAntiElytraStartSoundName()),
                abilitiesManager.getAntiElytraStartSoundVolume(), abilitiesManager.getAntiElytraStartSoundPitch());
        location.getWorld().spawnParticle(Particle.valueOf(abilitiesManager.getAntiElytraStartParticle()), location, 50, 1, 1, 1);
    }

    private void scheduleEndEffects(Player target, ItemStack elytra) {
        long duration = abilitiesManager.getAntiElytraDuration() * 20L;

        bukkitScheduler.runTaskLater(() -> {
            if (target.isDead() || !target.getPersistentDataContainer().has(EFFECT_KEY, PersistentDataType.BYTE)) {
                return;
            }

            target.getInventory().setChestplate(elytra);
            target.getPersistentDataContainer().remove(EFFECT_KEY);
            target.getPersistentDataContainer().remove(STORED_ELYTRA_KEY);
            sendElytraReturnTitle(target);
        }, duration);
    }

    private void sendAntiElytraTitle(Player target) {
        String title = messagesManager.getMessage("ae-title");
        String subtitle = messagesManager.getMessage("ae-subtitle");
        target.sendTitle(ColorUtil.format(title), ColorUtil.format(subtitle), 10, 70, 20);
    }

    private void sendElytraReturnTitle(Player target) {
        String title = messagesManager.getMessage("ae-title-b");
        String subtitle = messagesManager.getMessage("ae-subtitle-b");
        target.sendTitle(ColorUtil.format(title), ColorUtil.format(subtitle), 10, 70, 20);
    }

    private void reduceItemAmount(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        int reduceAmount = abilitiesManager.getAntiElytraReduceAmount();
        if (item.getAmount() > reduceAmount) {
            item.setAmount(item.getAmount() - reduceAmount);
        } else {
            player.getInventory().removeItem(item);
        }
    }

    private void sendMessage(Player player, String message) {
        player.sendMessage(ColorUtil.format(message));
    }
}