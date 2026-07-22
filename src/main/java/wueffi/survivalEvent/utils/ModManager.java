package wueffi.survivalEvent.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class ModManager {
    private static final String CONFIG_FILE_NAME = "moderators.yml";
    private static JavaPlugin plugin;
    private static final List<String> playerNames  = new ArrayList<>();
    private static File configFile;
    private static FileConfiguration config;

    private ModManager() {}

    public static void init(JavaPlugin javaPlugin) {
        plugin = javaPlugin;

        loadConfig();
    }

    public static boolean isModerator(String playerName) {
        return playerNames.contains(playerName);
    }

    private static void loadConfig() {
        configFile = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);

        if (!configFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create moderators.yml: " + e.getMessage());
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        if (!config.isConfigurationSection("moderators")) return;

        for (String name : config.getConfigurationSection("moderators").getKeys(false)) {
            try {
                playerNames.add(name);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }
}
