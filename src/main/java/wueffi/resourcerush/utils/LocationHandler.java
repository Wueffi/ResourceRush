package wueffi.resourcerush.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class LocationHandler {

    private static final String CONFIG_FILE_NAME = "locations.yml";
    private static JavaPlugin plugin;
    private static File configFile;
    private static FileConfiguration config;

    private LocationHandler() {}

    public static void init(JavaPlugin javaPlugin) {
        plugin = javaPlugin;

        configFile = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);

        if (!configFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create locations.yml: " + e.getMessage());
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static void saveLocation(Player player) {
        Location loc = player.getLocation();
        if (loc.getWorld().getName().equals("lobby")) return;

        String path = "locations." + player.getUniqueId();

        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", (double) loc.getYaw());
        config.set(path + ".pitch", (double) loc.getPitch());
        config.set(path + ".world", loc.getWorld().getName());

        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save location for " + player.getName() + ": " + e.getMessage());
        }
    }

    public static Location loadLocation(Player player) {
        config = YamlConfiguration.loadConfiguration(configFile);

        String path = "locations." + player.getUniqueId();
        if (!config.contains(path)) return null;

        double x = config.getDouble(path + ".x");
        double y = config.getDouble(path + ".y");
        double z = config.getDouble(path + ".z");
        float yaw = (float) config.getDouble(path + ".yaw");
        float pitch = (float) config.getDouble(path + ".pitch");
        World world = Bukkit.getWorld(config.getString(path + ".world"));

        if (config.getString(path + ".world").equals("lobby")) {
            world = Bukkit.getWorld("world");
            x = world.getSpawnLocation().x();
            y = world.getSpawnLocation().x();
            z = world.getSpawnLocation().x();
            yaw = world.getSpawnLocation().getYaw();
            pitch = world.getSpawnLocation().getPitch();
        }

        return new Location(world, x, y, z, yaw, pitch);
    }
}