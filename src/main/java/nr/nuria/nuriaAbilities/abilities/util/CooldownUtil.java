package nr.nuria.nuriaAbilities.abilities.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownUtil {
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Long> messageCooldowns = new HashMap<>();
    private final int messageCooldownTime = 1;

    public void setCooldown(UUID playerUUID, int seconds) {
        if (seconds < 0) {
            return;
        }
        long cooldownTime = System.currentTimeMillis() + (seconds * 1000L);
        cooldowns.put(playerUUID, cooldownTime);
    }

    public boolean isOnCooldown(UUID playerUUID) {
        if (!cooldowns.containsKey(playerUUID)) {
            return false;
        }
        long cooldownTime = cooldowns.get(playerUUID);
        if (System.currentTimeMillis() > cooldownTime) {
            cooldowns.remove(playerUUID);
            return false;
        }
        return true;
    }

    public int getRemainingTime(UUID playerUUID) {
        if (!cooldowns.containsKey(playerUUID)) {
            return 0;
        }
        long cooldownTime = cooldowns.get(playerUUID);
        return (int) ((cooldownTime - System.currentTimeMillis()) / 1000);
    }

    public void setMessageCooldown(UUID playerUUID) {
        long cooldownTime = System.currentTimeMillis() + (messageCooldownTime * 1000L);
        messageCooldowns.put(playerUUID, cooldownTime);
    }

    public boolean isOnMessageCooldown(UUID playerUUID) {
        if (!messageCooldowns.containsKey(playerUUID)) {
            return false;
        }
        long cooldownTime = messageCooldowns.get(playerUUID);
        if (System.currentTimeMillis() > cooldownTime) {
            messageCooldowns.remove(playerUUID);
            return false;
        }
        return true;
    }
}