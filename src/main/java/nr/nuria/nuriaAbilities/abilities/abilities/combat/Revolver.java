package nr.nuria.nuriaAbilities.abilities.abilities.combat;

import lombok.RequiredArgsConstructor;
import nr.nuria.nuriaAbilities.abilities.manager.AbilitiesItemManager;
import nr.nuria.nuriaAbilities.abilities.util.CooldownUtil;
import nr.nuria.nuriaAbilities.hooks.WorldGuardHook;
import nr.nuria.nuriaAbilities.managers.AbilitiesManager;
import nr.nuria.nuriaAbilities.managers.ConfigManager;
import nr.nuria.nuriaAbilities.managers.MessagesManager;
import nr.nuria.nuriaAbilities.util.ColorUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@RequiredArgsConstructor
public class Revolver implements Listener {

    private final AbilitiesManager abilitiesManager;
    private final ConfigManager configManager;
    private final MessagesManager messagesManager;
    private final WorldGuardHook worldGuardHook;
    private final CooldownUtil cooldownUtil = new CooldownUtil();

    private final Map<UUID, Integer> hitCountMap = new HashMap<>();

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        Player target = (Player) event.getEntity();
        UUID damagerUUID = damager.getUniqueId();
        UUID targetUUID = target.getUniqueId();
        ItemStack item = damager.getInventory().getItemInMainHand();

        if (AbilitiesItemManager.isRevolverItem(item)) {
            if (!worldGuardHook.canUseAbility(damager.getLocation(), damager) || !worldGuardHook.canUseAbility(target.getLocation(), target)) {
                sendMessage(damager, messagesManager.getMessage("region-blocked")
                        .replace("{prefix}", configManager.getPrefix())
                        .replace("{item}", abilitiesManager.getRevolverName()));
                event.setCancelled(true);  // Cancel the hit event
                return;
            }
            if (cooldownUtil.isOnCooldown(damagerUUID)) {
                int remainingTime = cooldownUtil.getRemainingTime(damagerUUID);
                sendMessage(damager, messagesManager.getMessage("cooldown-message")
                        .replace("{prefix}", configManager.getPrefix())
                        .replace("{time}", String.valueOf(remainingTime)));
                event.setCancelled(true);  // Cancel the hit event
                return;
            }

            hitCountMap.put(targetUUID, hitCountMap.getOrDefault(targetUUID, 0) + 1);

            if (hitCountMap.get(targetUUID) >= abilitiesManager.getRevolverHits()) {
                executeRevolver(target);
                sendMessage(damager, messagesManager.getMessage("ability-activated")
                        .replace("{prefix}", configManager.getPrefix())
                        .replace("{item}", abilitiesManager.getRevolverName()));

                playStartEffects(target.getLocation());
                sendRevolverTitle(target);
                reduceItemAmount(damager);
                cooldownUtil.setCooldown(damagerUUID, abilitiesManager.getRevolverCooldown());
                hitCountMap.remove(targetUUID);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (AbilitiesItemManager.isRevolverItem(itemInHand)) {
            event.setCancelled(true);
            sendMessage(player, messagesManager.getMessage("place-blocked")
                    .replace("{prefix}", configManager.getPrefix())
                    .replace("{item}", abilitiesManager.getRevolverName()));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (AbilitiesItemManager.isRevolverItem(itemInHand)) {
            event.setCancelled(true);
            sendMessage(player, messagesManager.getMessage("break-blocked")
                    .replace("{prefix}", configManager.getPrefix())
                    .replace("{item}", abilitiesManager.getRevolverName()));
        }
    }

    private void executeRevolver(Player target) {
        String mode = abilitiesManager.getRevolverMode();

        switch (mode.toLowerCase()) {
            case "hotbar":
                shuffleHotbar(target);
                break;
            case "inventory":
                shuffleInventory(target);
                break;
            case "hotbar+offhand":
                shuffleHotbarAndOffhand(target);
                break;
            case "inventory+offhand":
                shuffleInventoryAndOffhand(target);
                break;
            case "full":
                shuffleFullInventory(target);
                break;
            default:
                sendMessage(target, messagesManager.getMessage("invalid-mode")
                        .replace("{prefix}", configManager.getPrefix()));
                break;
        }

        playEndEffects(target.getLocation());
    }

    private void shuffleHotbar(Player player) {
        Inventory inventory = player.getInventory();
        List<ItemStack> hotbarList = new ArrayList<>(Arrays.asList(inventory.getContents()).subList(0, 9));
        Collections.shuffle(hotbarList);
        for (int i = 0; i < hotbarList.size(); i++) {
            inventory.setItem(i, hotbarList.get(i));
        }
    }

    private void shuffleInventory(Player player) {
        Inventory inventory = player.getInventory();
        List<ItemStack> inventoryList = new ArrayList<>(Arrays.asList(inventory.getContents()).subList(9, 36));
        Collections.shuffle(inventoryList);
        for (int i = 0; i < inventoryList.size(); i++) {
            inventory.setItem(i + 9, inventoryList.get(i));
        }
    }

    private void shuffleHotbarAndOffhand(Player player) {
        Inventory inventory = player.getInventory();
        List<ItemStack> hotbarList = new ArrayList<>(Arrays.asList(inventory.getContents()).subList(0, 9));
        hotbarList.add(player.getInventory().getItemInOffHand());
        Collections.shuffle(hotbarList);
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, hotbarList.get(i));
        }
        player.getInventory().setItemInOffHand(hotbarList.get(9));
    }

    private void shuffleInventoryAndOffhand(Player player) {
        Inventory inventory = player.getInventory();
        List<ItemStack> inventoryList = new ArrayList<>(Arrays.asList(inventory.getContents()).subList(9, 36));
        inventoryList.add(player.getInventory().getItemInOffHand());
        Collections.shuffle(inventoryList);
        for (int i = 0; i < inventoryList.size() - 1; i++) {
            inventory.setItem(i + 9, inventoryList.get(i));
        }
        player.getInventory().setItemInOffHand(inventoryList.get(inventoryList.size() - 1));
    }

    private void shuffleFullInventory(Player player) {
        Inventory inventory = player.getInventory();
        List<ItemStack> allList = new ArrayList<>(Arrays.asList(inventory.getContents()).subList(0, 36));
        allList.add(player.getInventory().getItemInOffHand());
        Collections.shuffle(allList);
        for (int i = 0; i < allList.size() - 1; i++) {
            inventory.setItem(i, allList.get(i));
        }
        player.getInventory().setItemInOffHand(allList.get(allList.size() - 1));
    }

    private void reduceItemAmount(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        int reduceAmount = abilitiesManager.getRevolverReduceAmount();
        if (item.getAmount() > reduceAmount) {
            item.setAmount(item.getAmount() - reduceAmount);
        } else {
            player.getInventory().removeItem(item); 
        }
    }

    private void playStartEffects(Location location) {
        location.getWorld().playSound(location, Sound.valueOf(abilitiesManager.getRevolverStartSoundName()), abilitiesManager.getRevolverStartSoundVolume(), abilitiesManager.getRevolverStartSoundPitch());
        location.getWorld().spawnParticle(Particle.valueOf(abilitiesManager.getRevolverStartParticle()), location, 50, 1, 1, 1);
    }

    private void playEndEffects(Location location) {
        location.getWorld().playSound(location, Sound.valueOf(abilitiesManager.getRevolverEndSoundName()), abilitiesManager.getRevolverEndSoundVolume(), abilitiesManager.getRevolverEndSoundPitch());
        location.getWorld().spawnParticle(Particle.valueOf(abilitiesManager.getRevolverEndParticle()), location, 50, 1, 1, 1);
    }

    private void sendRevolverTitle(Player target) {
        String title = messagesManager.getMessage("rv-title");
        String subtitle = messagesManager.getMessage("rv-subtitle");

        target.sendTitle(ColorUtil.format(title), ColorUtil.format(subtitle), 10, 70, 20);
    }

    private void sendMessage(Player player, String message) {
        player.sendMessage(ColorUtil.format(message));
    }
}