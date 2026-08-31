package wueffi.resourcerush.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class Holograms {
    private static NamespacedKey holoKey;
    private static final Map<String, TextDisplay> activeHolograms = new HashMap<>();

    private Holograms() {}

    public static void init(JavaPlugin plugin) {
        holoKey = new NamespacedKey(plugin, "resourcerush_hologram");

        for (TextDisplay entity : Bukkit.getWorld("lobby").getEntitiesByClass(TextDisplay.class)) {
            if (entity.getPersistentDataContainer().has(holoKey, PersistentDataType.STRING)) {
                String id = entity.getPersistentDataContainer().get(holoKey, PersistentDataType.STRING);
                activeHolograms.put(id, entity);
            }
        }
    }

    public static void setHologram(Player player, String id) {
        System.out.println("setHologram()");
        removeHologram(id);

        List<Component> lines = buildLines(id);
        if (lines.isEmpty()) {
            return;
        }

        Component text = Component.empty();
        for (int i = 0; i < lines.size(); i++) {
            text = text.append(lines.get(i));
            if (i < lines.size() - 1) {
                text = text.append(Component.newline());
            }
        }

        Location loc = centerOnBlock(player.getLocation());
        loc.setY(loc.getY() + 2.3);
        Component finalText = text;

        TextDisplay display = loc.getWorld().spawn(loc, TextDisplay.class, td -> {
            td.text(finalText);
            td.setBillboard(Display.Billboard.CENTER);
            td.setPersistent(true);
            td.setInvulnerable(true);
            td.getPersistentDataContainer().set(holoKey, PersistentDataType.STRING, player.getUniqueId().toString());
        });

        activeHolograms.put(id, display);
    }

    public static void removeHologram(String id) {
        TextDisplay display = activeHolograms.remove(id);
        if (display != null && !display.isDead()) {
            display.remove();
        }
    }

    public static void shutdown() {
        activeHolograms.clear();
    }

    private static Location centerOnBlock(Location loc) {
        return new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getY(), loc.getBlockZ() + 0.5);
    }

    private static List<Component> buildLines(String id) {
        return switch (id) {
            case "1" -> buildLeaderboardLines();
            case "2" -> buildMostCollectedLines();
            case "3" -> buildLeastCollectedLines();
            default -> List.of();
        };
    }

    private static List<Component> buildLeaderboardLines() {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.text("Leaderboard").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));

        List<Map.Entry<UUID, Double>> top = PlayerPointsStore.getTopN(10);
        NamedTextColor[] rankColors = {NamedTextColor.GOLD, NamedTextColor.GRAY, NamedTextColor.RED};

        for (int i = 0; i < top.size(); i++) {
            Map.Entry<UUID, Double> entry = top.get(i);
            String name = PlayerPointsStore.getName(entry.getKey());
            NamedTextColor rankColor = i < rankColors.length ? rankColors[i] : NamedTextColor.WHITE;

            lines.add(Component.text("#" + (i + 1) + " ", rankColor)
                    .append(Component.text(name, NamedTextColor.WHITE))
                    .append(Component.text(" " + String.format("%.2f", entry.getValue()), NamedTextColor.YELLOW)));
        }
        return lines;
    }

    private static List<Component> buildMostCollectedLines() {
        return buildItemLines("Top Items", NamedTextColor.AQUA, true);
    }

    private static List<Component> buildLeastCollectedLines() {
        return buildItemLines("Rare Items", NamedTextColor.RED, false);
    }

    private static List<Component> buildItemLines(String title, NamedTextColor color, boolean descending) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.text(title).color(color).decorate(TextDecoration.BOLD));

        Map<String, Integer> totals = ItemReportTask.scanAllWorlds();
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(totals.entrySet());
        if (descending) {
            sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        } else {
            sorted.sort(Map.Entry.comparingByValue());
        }

        int count = Math.min(sorted.size(), 5);
        for (int i = 0; i < count; i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            lines.add(Component.text("#" + (i + 1) + " ", color)
                    .append(Component.text(entry.getKey() + ": ", NamedTextColor.GRAY))
                    .append(Component.text(entry.getValue(), NamedTextColor.WHITE)));
        }
        return lines;
    }
}