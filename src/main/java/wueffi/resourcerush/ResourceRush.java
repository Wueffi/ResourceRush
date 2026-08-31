package wueffi.resourcerush;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wueffi.resourcerush.commands.EventCommands;
import wueffi.resourcerush.commands.HologramCommand;
import wueffi.resourcerush.utils.*;

import java.util.List;

public final class ResourceRush extends JavaPlugin {
    public Logger LOGGER = LoggerFactory.getLogger("ResourceRush");

    @Override
    public void onEnable() {
        LOGGER.info("Starting up ResourceRush");

        if (WorldSetup.setup()) LOGGER.info("Succesfully set up worlds!");
        else LOGGER.info("Failed to set up worlds!");

        ContainerHandler.init(this);
        Bukkit.getPluginManager().registerEvents(new ContainerListener(), this);
        LOGGER.info("ContainerHandler initialized!");

        PlaytimeManager.init(this);
        LOGGER.info("PlaytimeManager initialized!");

        PlaytimeScoreboard.init(this);
        LOGGER.info("PlaytimeScoreboard initialized!");

        ItemReportTask.init(this);
        LOGGER.info("ItemReportTask initialized!");

        PlayerPointsStore.init(this);
        LOGGER.info("PlayerPointsStore initialized!");

        LocationHandler.init(this);
        Bukkit.getPluginManager().registerEvents(new LocationListener(this), this);
        LOGGER.info("LocationHandler initialized!");

        DroppedItemHandler.init(this);
        getServer().getPluginManager().registerEvents(new DroppedItemListener(), this);
        LOGGER.info("DroppedItemHandler initialized!");

        InventoryCacheHandler.init(this);
        getServer().getPluginManager().registerEvents(new InventoryCacheListener(this), this);
        LOGGER.info("InventoryCacheHandler initialized!");

        GameModeHandler.init(this);
        LOGGER.info("GameModeHandler initialized!");

        ModManager.init(this);
        LOGGER.info("ModManager initialized!");

        DiscordWebhook.init(this);
        LOGGER.info("Webhook initialized!");

        Holograms.init(this);
        LOGGER.info("Holograms initialized!");

        EventCommands handler = new EventCommands();
        HologramCommand holohandler = new HologramCommand();
        PluginCommand holoCommand = getCommand("hologram");
        holoCommand.setExecutor(holohandler);
        holoCommand.setTabCompleter(holohandler);
        for (String cmd : List.of("playtime", "check", "start", "end", "leaderboard")) {
            var pluginCmd = getCommand(cmd);
            pluginCmd.setExecutor(handler);
            pluginCmd.setTabCompleter(handler);
        }
        LOGGER.info("Commands registered");
    }

    @Override
    public void onDisable() {
        if (WorldSetup.unload()) LOGGER.info("Succesfully unloaded worlds!");
        else LOGGER.info("Failed to unload worlds!");

        PlaytimeManager.shutdown();
        LOGGER.info("PlaytimeManager shutdown!");

        PlaytimeScoreboard.shutdown();
        LOGGER.info("PlaytimeScoreBoard shutdown!");

        PlayerPointsStore.shutdown();
        LOGGER.info("PlayerPointsStore shutdown!");

        ItemReportTask.shutdown();
        LOGGER.info("ItemReportTask shutdown!");

        DiscordWebhook.shutdown();
        LOGGER.info("Webhook shutdown!");

        GameModeHandler.shutdown();
        LOGGER.info("GameModeHandler shutdown!");

        Holograms.shutdown();
        LOGGER.info("Holograms shutdown!");
    }
}
