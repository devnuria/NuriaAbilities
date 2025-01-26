package nr.nuria.nuriaAbilities.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class WorldGuardHook {

    private final WorldGuardPlugin worldGuardPlugin;
    private final List<String> blockedRegions;

    public WorldGuardHook(Plugin plugin, List<String> blockedRegions) {
        this.worldGuardPlugin = getWorldGuard(plugin);
        this.blockedRegions = blockedRegions;
    }

    private WorldGuardPlugin getWorldGuard(Plugin plugin) {
        Plugin wgPlugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        if (wgPlugin == null || !(wgPlugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) wgPlugin;
    }

    public boolean canUseAbility(Location location, Player player) {
        if (worldGuardPlugin == null) return true;

        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer regionContainer = wg.getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(location.getWorld()));
        if (regionManager == null) return true;

        ApplicableRegionSet regionSet = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
        for (ProtectedRegion region : regionSet) {
            if (blockedRegions.contains(region.getId())) {
                return false;
            }
        }
        return true;
    }

    public boolean canPlaceBlock(Location location) {
        if (worldGuardPlugin == null) return true;

        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer regionContainer = wg.getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(location.getWorld()));
        if (regionManager == null) return true;

        ApplicableRegionSet regionSet = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
        for (ProtectedRegion region : regionSet) {
            if (blockedRegions.contains(region.getId())) {
                return false;
            }
        }
        return true;
    }
}