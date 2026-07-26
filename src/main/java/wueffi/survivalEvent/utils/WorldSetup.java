package wueffi.survivalEvent.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

public class WorldSetup {
    public static boolean setup() {
        World overworld = Bukkit.getWorld("world2");
        if (overworld == null) {
            overworld = Bukkit.createWorld(new WorldCreator("world2"));

            World nether = Bukkit.createWorld(new WorldCreator("world2_nether").environment(World.Environment.NETHER));
            World end = Bukkit.createWorld(new WorldCreator("world2_the_end").environment(World.Environment.THE_END));

            WorldBorder border = overworld.getWorldBorder();
            border.setCenter(overworld.getSpawnLocation());
            border.setSize(2048);

            WorldBorder netherBorder = nether.getWorldBorder();
            netherBorder.setCenter(overworld.getSpawnLocation());
            netherBorder.setSize(2048);

            WorldBorder endBorder = end.getWorldBorder();
            endBorder.setCenter(overworld.getSpawnLocation());
            endBorder.setSize(4096);
        }

        if (overworld == null) {
            return false;
        }

        return true;
    }

    public static boolean unload() {
        World world2 = Bukkit.getWorld("world2");

        if (world2 == null) {
            return false;
        }

        World world = Bukkit.getWorld("world2");

        if (world == null) {
            return false;
        }

        for (Player player : world.getPlayers()) {
            player.teleport(world.getSpawnLocation());
        }
        return true;
    }
}
