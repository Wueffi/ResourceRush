package wueffi.resourcerush.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class EnderChestCacheHandler {

    private static final String FILE_NAME = "enderChestCache.yml";

    private static JavaPlugin plugin;
    private static File file;
    private static FileConfiguration config;

    private EnderChestCacheHandler() {}

    public static void init(JavaPlugin javaPlugin) {
        plugin = javaPlugin;

        file = new File(plugin.getDataFolder(), FILE_NAME);

        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();

            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create enderChestCache.yml: " + e.getMessage());
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public static void saveEnderChest(Player player) {
        String path = "enderchests." + player.getUniqueId();

        config.set(path, Arrays.asList(player.getEnderChest().getContents()));

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save ender chest for " + player.getName() + ": " + e.getMessage());
        }
    }

    public static ItemStack[] getEnderChest(UUID uuid) {
        return getEnderChest(uuid.toString());
    }

    @SuppressWarnings("unchecked")
    public static ItemStack[] getEnderChest(String uuid) {
        config = YamlConfiguration.loadConfiguration(file);

        List<?> list = config.getList("enderchests." + uuid);

        if (list == null) {
            return new ItemStack[0];
        }

        ItemStack[] items = new ItemStack[list.size()];

        for (int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);

            if (obj instanceof ItemStack item) {
                items[i] = item;
            } else if (obj instanceof Map<?, ?> map) {
                try {
                    items[i] = ItemStack.deserialize((Map<String, Object>) map);
                } catch (Exception e) {
                    plugin.getLogger().warning(e.getMessage());
                    items[i] = null;
                }
            }
        }

        return items;
    }

    public static Set<UUID> getAllOwners() {
        config = YamlConfiguration.loadConfiguration(file);

        if (!config.isConfigurationSection("enderchests")) {
            return Set.of();
        }

        Set<UUID> owners = new HashSet<>();

        for (String key : config.getConfigurationSection("enderchests").getKeys(false)) {
            try {
                owners.add(UUID.fromString(key));
            } catch (IllegalArgumentException ignored) {
                plugin.getLogger().warning("Invalid UUID in ender chest cache: " + key);
            }
        }

        return owners;
    }

    public static void removeCache(Player player) {
        config.set("enderchests." + player.getUniqueId(), null);

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not remove ender chest cache: " + e.getMessage());
        }
    }
}