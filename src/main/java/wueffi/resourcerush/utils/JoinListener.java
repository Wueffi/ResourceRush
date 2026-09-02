package wueffi.resourcerush.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        World lobby = Bukkit.getWorld("lobby");
        Player player = event.getPlayer();

        long firstPlayed = player.getFirstPlayed();
        long lastLogin = player.getLastLogin();

        if (firstPlayed == 0 || (lastLogin - firstPlayed) < 500) {
            player.sendMessage(Component.text("Welcome to Resource Rush! Good Luck & Have Fun!").color(NamedTextColor.GOLD));
            assert lobby != null;
            player.teleport(lobby.getSpawnLocation());
        }
    }
}
