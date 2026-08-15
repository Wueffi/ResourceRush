package wueffi.resourcerush.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class DroppedItemHandler {
    private static final String CONFIG_FILE_NAME = "dropped_items.yml";
    private static JavaPlugin plugin;
    private static File configFile;
    private static FileConfiguration config;

    private DroppedItemHandler() {}

    public static void init(JavaPlugin javaPlugin) {
        plugin = javaPlugin;
        configFile = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);

        if (!configFile.exists()) {
            plugin.getDataFolder().mkdirs();

            try {
                configFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create dropped_items.yml: " + e.getMessage());
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static void addItem(UUID itemUUID, UUID ownerUUID) {
        config.set("items." + itemUUID, ownerUUID.toString());
        save();
    }

    public static UUID getOwner(UUID itemUUID) {
        if (config == null) return null;

        String raw = config.getString("items." + itemUUID);
        if (raw == null) return null;

        return UUID.fromString(raw);
    }

    public static Set<UUID> getAllOwners() {
        if (config == null || !config.isConfigurationSection("items")) return Set.of();

        Set<UUID> owners = new HashSet<>();
        for (String key : config.getConfigurationSection("items").getKeys(false)) {
            String raw = config.getString("items." + key);
            if (raw != null) {
                owners.add(UUID.fromString(raw));
            }
        }
        return owners;
    }

    public static void removeItem(UUID itemUUID) {
        if (config == null) return;

        if (config.get("items." + itemUUID) == null) return;
        config.set("items." + itemUUID, null);

        save();
    }

    private static void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save dropped_items.yml: " + e.getMessage());
        }
    }
}