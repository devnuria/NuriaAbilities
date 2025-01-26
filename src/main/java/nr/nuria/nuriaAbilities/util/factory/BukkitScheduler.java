package nr.nuria.nuriaAbilities.util.factory;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BukkitScheduler {

    private final Plugin plugin;

    public void runTaskLater(Runnable task, long delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTaskLater(plugin, delay);
    }
}
