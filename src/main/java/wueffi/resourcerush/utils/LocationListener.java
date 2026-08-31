package wueffi.resourcerush.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class LocationListener implements Listener {

    private final JavaPlugin plugin;

    public LocationListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (!player.getWorld().getName().equals("lobby")) return;

        World world = Bukkit.getWorld("world");
        if (world == null) return;

        event.setRespawnLocation(world.getSpawnLocation());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        LocationHandler.saveLocation(event.getPlayer());
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getWorld().getName().equals("lobby")) {
            event.setCancelled(true);
            Location location = LocationHandler.loadLocation(player);
            player.teleport(Objects.requireNonNullElseGet(location, () -> Bukkit.getWorld("world").getSpawnLocation()));
        } else {
            LocationHandler.saveLocation(event.getPlayer());
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (!event.getPlugin().equals(plugin)) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            LocationHandler.saveLocation(player);
        }
    }
}