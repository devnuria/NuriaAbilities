package nr.nuria.nuriaAbilities;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import nr.nuria.nuriaAbilities.abilities.abilities.combat.*;
import nr.nuria.nuriaAbilities.abilities.abilities.interactive.TimeMachine;
import nr.nuria.nuriaAbilities.abilities.abilities.interactive.Trapper;
import nr.nuria.nuriaAbilities.abilities.abilities.interactive.Webdel;
import nr.nuria.nuriaAbilities.abilities.command.AbilitiesCommand;
import nr.nuria.nuriaAbilities.hooks.WorldGuardHook;
import nr.nuria.nuriaAbilities.listener.PlayerJoinListener;
import nr.nuria.nuriaAbilities.managers.AbilitiesManager;
import nr.nuria.nuriaAbilities.managers.ConfigManager;
import nr.nuria.nuriaAbilities.managers.MessagesManager;
import nr.nuria.nuriaAbilities.util.VersionChecker;
import nr.nuria.nuriaAbilities.util.factory.BukkitScheduler;
import nr.nuria.nuriaAbilities.command.ReloadCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class NuriaAbilities extends JavaPlugin {

    @Getter
    private static NuriaAbilities instance;
    private BukkitScheduler bukkitScheduler;
    @Getter
    private AbilitiesManager abilitiesManager;
    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private WorldGuardHook worldGuardHook;
    private VersionChecker versionChecker;

    @Override
    public void onEnable() {
        instance = this;
        this.bukkitScheduler = new BukkitScheduler(this);
        this.abilitiesManager = new AbilitiesManager(this);
        this.configManager = new ConfigManager(this);
        this.messagesManager = new MessagesManager(this);
        this.worldGuardHook = new WorldGuardHook(this, configManager.getBlockedRegions());
        this.versionChecker = new VersionChecker(this);

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new AbilitiesCommand(configManager, messagesManager, abilitiesManager));
        manager.registerCommand(new ReloadCommand(configManager, abilitiesManager, messagesManager));

        registerEvents();
        versionChecker.checkForUpdate();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new AntiBuild(bukkitScheduler, abilitiesManager, configManager, messagesManager, worldGuardHook), this);
        getServer().getPluginManager().registerEvents(new TimeMachine(bukkitScheduler, abilitiesManager, configManager, messagesManager, worldGuardHook), this);
        getServer().getPluginManager().registerEvents(new AntiElytra(bukkitScheduler, abilitiesManager, configManager, messagesManager, worldGuardHook, this), this);
        getServer().getPluginManager().registerEvents(new Trapper(bukkitScheduler, abilitiesManager, configManager, messagesManager, worldGuardHook), this);
        getServer().getPluginManager().registerEvents(new Webdel(abilitiesManager, configManager, messagesManager, worldGuardHook, bukkitScheduler), this);
        getServer().getPluginManager().registerEvents(new Webtrap(abilitiesManager, configManager, messagesManager, worldGuardHook, bukkitScheduler), this);
        getServer().getPluginManager().registerEvents(new Revolver(abilitiesManager, configManager, messagesManager, worldGuardHook), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(versionChecker, configManager, messagesManager), this);
    }
}
