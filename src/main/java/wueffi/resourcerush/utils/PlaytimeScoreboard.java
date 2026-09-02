package wueffi.resourcerush.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import wueffi.resourcerush.commands.EventCommands;

import java.util.*;

public final class PlaytimeScoreboard {
    private static final long TICK_INTERVAL = 60*20L;

    private static JavaPlugin plugin;
    private static BukkitTask updateTask;

    private PlaytimeScoreboard() {
    }

    public static void init(JavaPlugin javaPlugin) {
        plugin = javaPlugin;
        startUpdateTask();
    }

    public static void shutdown() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    private static void startUpdateTask() {
        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                update(player);
            }
        }, TICK_INTERVAL, TICK_INTERVAL);
    }

    public static void update(Player player) {
        Scoreboard board = player.getScoreboard();
        if (board.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            board = Bukkit.getScoreboardManager().getNewScoreboard();
        }

        new ArrayList<>(board.getObjectives()).forEach(Objective::unregister);

        Component title = Component.text("Leaderboard").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD);

        Objective obj = board.registerNewObjective("playtime", Criteria.DUMMY, title);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        long secondsLeft = Math.max(0L, 7200 - PlaytimeManager.getSecondsToday(player));
        double pts = PlayerPointsStore.get(player.getUniqueId());
        String ptsStr = String.format("%.2f", pts);

        obj.getScore(" ").setScore(0);
        obj.getScore("§a" + EventCommands.formatSeconds(secondsLeft)).setScore(1);
        obj.getScore("§7Time left:").setScore(2);
        obj.getScore("  ").setScore(3);
        obj.getScore("§e" + ptsStr + " pts").setScore(4);
        obj.getScore("§7Your points:").setScore(5);
        obj.getScore("§8──────────────").setScore(6);

        buildLeaderboard(obj, 7);

        player.setScoreboard(board);
    }

    private static void buildLeaderboard(Objective obj, int base) {
        List<Map.Entry<UUID, Double>> top = PlayerPointsStore.getTopN(5);
        int count = top.size();

        String[] prefix = {"§6#1 ", "§7#2 ", "§c#3 ", "§f#4 ", "§f#5 "};

        for (int i = 0; i < count; i++) {
            Map.Entry<UUID, Double> e = top.get(i);
            String name = PlayerPointsStore.getName(e.getKey());
            obj.getScore(prefix[i] + "§f" + name + " §e" + String.format("%.2f", e.getValue())).setScore(base + count - 1 - i);
        }
    }
}