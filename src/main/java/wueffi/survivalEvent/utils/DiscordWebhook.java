package wueffi.survivalEvent.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class DiscordWebhook {

    private static JavaPlugin plugin;
    private static String webhookUrl;
    private static BukkitTask task;

    private static String messageId;

    private DiscordWebhook() {}

    public static void init(JavaPlugin javaPlugin) {
        plugin = javaPlugin;

        plugin.saveDefaultConfig();
        webhookUrl = plugin.getConfig().getString("discord.webhook-url");
        messageId = plugin.getConfig().getString("discord.webhook-msg");

        if (webhookUrl == null || webhookUrl.isBlank()) {
            plugin.getLogger().warning("Discord webhook URL missing!");
            return;
        }

        startLeaderboardTask();
    }

    private static void startLeaderboardTask() {
        task = Bukkit.getScheduler().runTaskTimer(
                plugin,
                DiscordWebhook::sendLeaderboard,
                20L,
                20L * 60L * 5L
        );
    }

    public static void shutdown() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private static void sendLeaderboard() {
        List<Map.Entry<UUID, Double>> top = PlayerPointsStore.getTopN(10);

        StringBuilder description = new StringBuilder();

        String[] medals = {
                "🥇","🥈","🥉", "4️⃣","5️⃣","6️⃣", "7️⃣","8️⃣","9️⃣","🔟"
        };

        for (int i = 0; i < top.size(); i++) {
            Map.Entry<UUID, Double> entry = top.get(i);

            description.append(medals[i])
                    .append(" **")
                    .append(PlayerPointsStore.getName(entry.getKey()))
                    .append("**\n↳ ")
                    .append(String.format("%.2f", entry.getValue()))
                    .append(" points\n\n");
        }

        sendEmbed("Current Leaderboard", description.toString(), 0xFFD700);
    }

    private static void sendEmbed(String title, String description, int color) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                boolean edit = messageId != null;

                String url = edit ? webhookUrl + "/messages/" + messageId : webhookUrl + "?wait=true";

                String json = """
                {
                  "embeds": [
                    {
                      "title": "%s",
                      "description": "%s",
                      "color": %d,
                      "footer": {
                        "text": "Survival Event"
                      },
                      "timestamp": "%s"
                    }
                  ]
                }
                """.formatted(escape(title), escape(description), color, java.time.Instant.now());

                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .method(edit ? "PATCH" : "POST", HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (!edit) {
                    messageId = extractMessageId(response.body());
                }

                if (response.statusCode() != 200 && response.statusCode() != 204) {
                    plugin.getLogger().warning("Discord webhook returned HTTP " + response.statusCode() + ": " + response.body());
                }

            } catch (Exception e) {
                plugin.getLogger().warning("Discord webhook failed: " + e.getMessage());
            }
        });
    }

    private static String extractMessageId(String json) {
        int index = json.indexOf("\"id\":\"");

        if (index == -1) {
            return null;
        }

        int start = index + 6;
        int end = json.indexOf("\"", start);

        return json.substring(start, end);
    }

    private static String escape(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}