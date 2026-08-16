package wueffi.resourcerush.utils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class GameModeHandler {
    private static JavaPlugin plugin;
    private static BukkitTask tickTask;
    private static World world;
    private static World nether;
    private static World end;
    private static World lobby;

    private GameModeHandler() {}

    public static void init(JavaPlugin javaPlugin) {
        plugin = javaPlugin;

        startTickTask();
        world = Bukkit.getWorld("world");
        nether = Bukkit.getWorld("world_nether");
        end = Bukkit.getWorld("world_the_end");
        lobby = Bukkit.getWorld("lobby");
    }

    public static void shutdown() {
        if (tickTask != null) {
            tickTask.cancel();
            tickTask = null;
        }
    }

    private static void startTickTask() {
        tickTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : world.getPlayers()){
                if (ModManager.isModerator(player.getName())) continue;
                if (player.getGameMode() != GameMode.SURVIVAL) player.setGameMode(GameMode.SURVIVAL);
            }
            for (Player player : nether.getPlayers()){
                if (ModManager.isModerator(player.getName())) continue;
                if (player.getGameMode() != GameMode.SURVIVAL) player.setGameMode(GameMode.SURVIVAL);
            }
            for (Player player : end.getPlayers()){
                if (ModManager.isModerator(player.getName())) continue;
                if (player.getGameMode() != GameMode.SURVIVAL) player.setGameMode(GameMode.SURVIVAL);
            }

            for (Player player : lobby.getPlayers()){
                if (ModManager.isModerator(player.getName())) continue;
                if (player.getGameMode() != GameMode.ADVENTURE) player.setGameMode(GameMode.ADVENTURE);
            }
        }, 1, 1);
    }
}
