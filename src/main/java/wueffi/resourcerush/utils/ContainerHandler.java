package wueffi.resourcerush.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ContainerHandler {
    private static final String CONFIG_FILE_NAME = "containers.yml";
    private static JavaPlugin plugin;
    private static File configFile;
    private static FileConfiguration config;

    private ContainerHandler() {}

    public static void init(JavaPlugin javaPlugin) {
        plugin = javaPlugin;
        configFile = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
        if (!configFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create containers.yml: " + e.getMessage());
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static void addContainer(UUID uuid, String key) {
        String path = "containers." + uuid;

        List<String> list = config.getStringList(path);
        if (list.contains(key)) return;

        list.add(key);
        config.set(path, list);
        save();
    }

    public static void removeContainer(String key) {
        if (key == null) return;
        if (!config.isConfigurationSection("containers")) return;

        for (String uuid : config.getConfigurationSection("containers").getKeys(false)) {
            String path = "containers." + uuid;

            List<String> list = config.getStringList(path);
            if (!list.remove(key)) continue;

            config.set(path, list);
            save();
            return;
        }
    }

    public static UUID getOwner(String key) {
        if (key == null) return null;
        if (!config.isConfigurationSection("containers")) return null;

        for (String uuid : config.getConfigurationSection("containers").getKeys(false)) {
            List<String> list = config.getStringList("containers." + uuid);
            if (list.contains(key)) {
                return UUID.fromString(uuid);
            }
        }
        return null;
    }

    public static List<Location> getContainersPerPlayer(UUID uuid) {
        if (config == null) return List.of();
        return config.getStringList("containers." + uuid).stream()
                .filter(s -> s.startsWith("block:"))
                .map(ContainerHandler::fromKey)
                .collect(Collectors.toList());
    }

    public static List<UUID> getEntityContainersPerPlayer(UUID uuid) {
        if (config == null) return List.of();
        return config.getStringList("containers." + uuid).stream()
                .filter(s -> s.startsWith("entity:"))
                .map(s -> UUID.fromString(s.substring("entity:".length())))
                .collect(Collectors.toList());
    }

    public static Set<UUID> getAllOwners() {
        if (config == null || !config.isConfigurationSection("containers")) return Set.of();

        return config.getConfigurationSection("containers").getKeys(false).stream()
                .map(UUID::fromString)
                .collect(Collectors.toSet());
    }

    public static String keyFor(Object holderOrLocationSource) {
        if (holderOrLocationSource instanceof Location loc) {
            return keyFor(loc);
        }
        if (holderOrLocationSource instanceof BlockState blockState) {
            return keyFor(blockState.getLocation());
        }
        if (holderOrLocationSource instanceof Entity entity) {
            return "entity:" + entity.getUniqueId();
        }
        return null;
    }

    public static String keyFor(Location loc) {
        return "block:" + loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    private static Location fromKey(String s) {
        String[] p = s.substring("block:".length()).split(":");
        return new Location(Bukkit.getWorld(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]), Integer.parseInt(p[3]));
    }

    private static void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save containers.yml: " + e.getMessage());
        }
    }
}