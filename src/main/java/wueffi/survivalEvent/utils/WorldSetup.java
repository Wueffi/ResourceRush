package wueffi.survivalEvent.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

public class WorldSetup {
   public static boolean setup() {
        World lobby = Bukkit.getWorld("world2");
        if (lobby == null) {
            lobby = Bukkit.createWorld(new WorldCreator("lobby"));

            World overworld = Bukkit.getWorld("world");
            World nether = Bukkit.getWorld("world_nether");
            World end = Bukkit.getWorld("world_the_end");

            if (overworld != null) {
                WorldBorder border = overworld.getWorldBorder();
                border.setCenter(overworld.getSpawnLocation());
                border.setSize(2048);
            }

            if (nether != null) {
                WorldBorder netherBorder = nether.getWorldBorder();
                netherBorder.setCenter(lobby.getSpawnLocation());
                netherBorder.setSize(2048);
            }

            if (end != null) {
                WorldBorder endBorder = end.getWorldBorder();
                endBorder.setCenter(lobby.getSpawnLocation());
                endBorder.setSize(4096);
            }
        }

        if (lobby == null) {
            return false;
        }

        return true;
    }

    public static boolean unload() {
        World lobby = Bukkit.getWorld("lobby");

        if (lobby == null) {
            return false;
        }

        World world = Bukkit.getWorld("world");

        if (world == null) {
            return false;
        }

        for (Player player : world.getPlayers()) {
            player.teleport(lobby.getSpawnLocation());
        }
        return true;
    }
}
