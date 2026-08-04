package wueffi.survivalEvent.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class InventoryCacheHandler {

    private static final String FILE_NAME = "inventoryCache.yml";

    private static JavaPlugin plugin;
    private static File file;
    private static FileConfiguration config;

    private InventoryCacheHandler() {}

    public static void init(JavaPlugin javaPlugin) {
        plugin = javaPlugin;

        file = new File(plugin.getDataFolder(), FILE_NAME);

        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();

            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create inventoryCache.yml: " + e.getMessage());
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public static void saveInventory(Player player) {
        String path = "inventories." + player.getUniqueId();

        config.set(path, player.getInventory().getContents());

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save inventory for " + player.getName() + ": " + e.getMessage());
        }
    }

    public static ItemStack[] getInventory(UUID uuid) {
        return getInventory(uuid.toString());
    }

    public static ItemStack[] getInventory(String uuid) {
        config = YamlConfiguration.loadConfiguration(file);

        Object value = config.get("inventories." + uuid);

        if (value instanceof ItemStack[] items) {
            return items;
        }

        return new ItemStack[0];
    }

    public static Set<UUID> getAllOwners() {
        if (config == null || !config.isConfigurationSection("inventories")) {
            return Set.of();
        }

        Set<UUID> owners = new HashSet<>();

        for (String key : config.getConfigurationSection("inventories").getKeys(false)) {
            try {
                owners.add(UUID.fromString(key));
            } catch (IllegalArgumentException ignored) {
                plugin.getLogger().warning("Invalid UUID in inventory cache: " + key);
            }
        }

        return owners;
    }

    public static void removeCache(Player player) {
        config.set("inventories." + player.getUniqueId(), null);

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not remove inventory cache: " + e.getMessage());
        }
    }
}