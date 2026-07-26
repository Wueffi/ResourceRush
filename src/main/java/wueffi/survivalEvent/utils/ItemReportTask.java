package wueffi.survivalEvent.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class ItemReportTask {
    private static final long INTERVAL_TICKS = 20L * 300;
    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final LinkedHashMap<String, Material> TRACKED = new LinkedHashMap<>();

    static {
        TRACKED.put("Amethyst Shard", Material.AMETHYST_SHARD);
        TRACKED.put("Coal", Material.COAL);
        TRACKED.put("Copper Ingot", Material.COPPER_INGOT);
        TRACKED.put("Diamond", Material.DIAMOND);
        TRACKED.put("Emerald", Material.EMERALD);
        TRACKED.put("Gold Ingot", Material.GOLD_INGOT);
        TRACKED.put("Iron Ingot", Material.IRON_INGOT);
        TRACKED.put("Lapis Lazuli", Material.LAPIS_LAZULI);
        TRACKED.put("Netherite Ingot", Material.NETHERITE_INGOT);
        TRACKED.put("Prismarine Crystals", Material.PRISMARINE_CRYSTALS);
        TRACKED.put("Quartz", Material.QUARTZ);
        TRACKED.put("Redstone", Material.REDSTONE);
        TRACKED.put("Glowstone Dust", Material.GLOWSTONE_DUST);
        TRACKED.put("Resin Brick", Material.RESIN_BRICK);

        TRACKED.put("Apple", Material.APPLE);
        TRACKED.put("Baked Potato", Material.BAKED_POTATO);
        TRACKED.put("Beef", Material.BEEF);
        TRACKED.put("Beetroot", Material.BEETROOT);
        TRACKED.put("Beetroot Soup", Material.BEETROOT_SOUP);
        TRACKED.put("Bread", Material.BREAD);
        TRACKED.put("Cake", Material.CAKE);
        TRACKED.put("Carrot", Material.CARROT);
        TRACKED.put("Chicken", Material.CHICKEN);
        TRACKED.put("Chorus Fruit", Material.CHORUS_FRUIT);
        TRACKED.put("Cod", Material.COD);
        TRACKED.put("Cooked Chicken", Material.COOKED_CHICKEN);
        TRACKED.put("Cooked Cod", Material.COOKED_COD);
        TRACKED.put("Cooked Mutton", Material.COOKED_MUTTON);
        TRACKED.put("Cooked Porkchop", Material.COOKED_PORKCHOP);
        TRACKED.put("Cooked Rabbit", Material.COOKED_RABBIT);
        TRACKED.put("Cooked Salmon", Material.COOKED_SALMON);
        TRACKED.put("Cookie", Material.COOKIE);
        TRACKED.put("Dried Kelp", Material.DRIED_KELP);
        TRACKED.put("Enchanted Golden Apple", Material.ENCHANTED_GOLDEN_APPLE);
        TRACKED.put("Glow Berries", Material.GLOW_BERRIES);
        TRACKED.put("Golden Apple", Material.GOLDEN_APPLE);
        TRACKED.put("Golden Carrot", Material.GOLDEN_CARROT);
        TRACKED.put("Honey Bottle", Material.HONEY_BOTTLE);
        TRACKED.put("Melon Slice", Material.MELON_SLICE);
        TRACKED.put("Milk Bucket", Material.MILK_BUCKET);
        TRACKED.put("Mushroom Stew", Material.MUSHROOM_STEW);
        TRACKED.put("Mutton", Material.MUTTON);
        TRACKED.put("Poisonous Potato", Material.POISONOUS_POTATO);
        TRACKED.put("Porkchop", Material.PORKCHOP);
        TRACKED.put("Potato", Material.POTATO);
        TRACKED.put("Pufferfish", Material.PUFFERFISH);
        TRACKED.put("Pumpkin Pie", Material.PUMPKIN_PIE);
        TRACKED.put("Rabbit", Material.RABBIT);
        TRACKED.put("Rabbit Stew", Material.RABBIT_STEW);
        TRACKED.put("Rotten Flesh", Material.ROTTEN_FLESH);
        TRACKED.put("Salmon", Material.SALMON);
        TRACKED.put("Spider Eye", Material.SPIDER_EYE);
        TRACKED.put("Steak", Material.COOKED_BEEF);
        TRACKED.put("Suspicious Stew", Material.SUSPICIOUS_STEW);
        TRACKED.put("Sweet Berries", Material.SWEET_BERRIES);
        TRACKED.put("Tropical Fish", Material.TROPICAL_FISH);
        TRACKED.put("Armadillo Scute", Material.ARMADILLO_SCUTE);
        TRACKED.put("Arrow", Material.ARROW);
        TRACKED.put("Blaze Rod", Material.BLAZE_ROD);
        TRACKED.put("Bone", Material.BONE);
        TRACKED.put("Bone Meal", Material.BONE_MEAL);
        TRACKED.put("Breeze Rod", Material.BREEZE_ROD);
        TRACKED.put("Egg", Material.EGG);
        TRACKED.put("Ender Pearl", Material.ENDER_PEARL);
        TRACKED.put("Feather", Material.FEATHER);
        TRACKED.put("Ghast Tear", Material.GHAST_TEAR);
        TRACKED.put("Glass Bottle", Material.GLASS_BOTTLE);
        TRACKED.put("Glow Ink Sac", Material.GLOW_INK_SAC);
        TRACKED.put("Goat Horn", Material.GOAT_HORN);
        TRACKED.put("Gunpowder", Material.GUNPOWDER);
        TRACKED.put("Ink Sac", Material.INK_SAC);
        TRACKED.put("Lead", Material.LEAD);
        TRACKED.put("Leather", Material.LEATHER);
        TRACKED.put("Magma Cream", Material.MAGMA_CREAM);
        TRACKED.put("Nautilus Shell", Material.NAUTILUS_SHELL);
        TRACKED.put("Nether Star", Material.NETHER_STAR);
        TRACKED.put("Ominous Bottle", Material.OMINOUS_BOTTLE);
        TRACKED.put("Phantom Membrane", Material.PHANTOM_MEMBRANE);
        TRACKED.put("Pitcher Pod", Material.PITCHER_POD);
        TRACKED.put("Prismarine Shard", Material.PRISMARINE_SHARD);
        TRACKED.put("Rabbit Foot", Material.RABBIT_FOOT);
        TRACKED.put("Rabbit Hide", Material.RABBIT_HIDE);
        TRACKED.put("Saddle", Material.SADDLE);
        TRACKED.put("Sculk Catalyst", Material.SCULK_CATALYST);
        TRACKED.put("Seagrass", Material.SEAGRASS);
        TRACKED.put("Shulker Shell", Material.SHULKER_SHELL);
        TRACKED.put("Slime Ball", Material.SLIME_BALL);
        TRACKED.put("Snowball", Material.SNOWBALL);
        TRACKED.put("Stick", Material.STICK);
        TRACKED.put("String", Material.STRING);
        TRACKED.put("Sugar", Material.SUGAR);
        TRACKED.put("Torchflower Seeds", Material.TORCHFLOWER_SEEDS);
        TRACKED.put("Totem Of Undying", Material.TOTEM_OF_UNDYING);
        TRACKED.put("Trident", Material.TRIDENT);
        TRACKED.put("Turtle Scute", Material.TURTLE_SCUTE);
        TRACKED.put("Wet Sponge", Material.WET_SPONGE);
        TRACKED.put("Wither Skeleton Skull", Material.WITHER_SKELETON_SKULL);
        TRACKED.put("Zombie Head", Material.ZOMBIE_HEAD);

        TRACKED.put("White Wool", Material.WHITE_WOOL);
        TRACKED.put("Orange Wool", Material.ORANGE_WOOL);
        TRACKED.put("Magenta Wool", Material.MAGENTA_WOOL);
        TRACKED.put("Light Blue Wool", Material.LIGHT_BLUE_WOOL);
        TRACKED.put("Yellow Wool", Material.YELLOW_WOOL);
        TRACKED.put("Lime Wool", Material.LIME_WOOL);
        TRACKED.put("Pink Wool", Material.PINK_WOOL);
        TRACKED.put("Gray Wool", Material.GRAY_WOOL);
        TRACKED.put("Light Gray Wool", Material.LIGHT_GRAY_WOOL);
        TRACKED.put("Cyan Wool", Material.CYAN_WOOL);
        TRACKED.put("Purple Wool", Material.PURPLE_WOOL);
        TRACKED.put("Blue Wool", Material.BLUE_WOOL);
        TRACKED.put("Brown Wool", Material.BROWN_WOOL);
        TRACKED.put("Green Wool", Material.GREEN_WOOL);
        TRACKED.put("Red Wool", Material.RED_WOOL);
        TRACKED.put("Black Wool", Material.BLACK_WOOL);
    }

    private static JavaPlugin plugin;
    private static BukkitTask task;
    private static File csvFile;
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    private ItemReportTask() {}

    public static void init(JavaPlugin javaPlugin) {
        plugin = javaPlugin;
        csvFile = new File(plugin.getDataFolder(), "item_report.csv");

        if (!csvFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                csvFile.createNewFile();
                writeHeader();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create item_report.csv: " + e.getMessage());
            }
        }

        task = Bukkit.getScheduler().runTaskTimer(plugin, ItemReportTask::run, INTERVAL_TICKS, INTERVAL_TICKS);
    }

    public static void shutdown() {
        if (task == null) return;

        task.cancel();
        task = null;
    }

    private static void run() {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);

        Map<UUID, Map<String, Integer>> playerCounts = new LinkedHashMap<>();
        Map<UUID, String> playerNames = new LinkedHashMap<>();

        for (String worldName : List.of("world", "world_nether", "world_the_end" )) {
            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;

            for (Player player : world.getPlayers()) {
                if (ModManager.isModerator(player.getName())) {
                    continue;
                }

                UUID uuid = player.getUniqueId();

                Map<String, Integer> counts = playerCounts.computeIfAbsent(
                        uuid,
                        k -> zeroCounts()
                );

                playerNames.put(uuid, player.getName());

                addCounts(player.getInventory(), counts);

                for (Location loc : ContainerHandler.getContainersPerPlayer(uuid)) {
                    Block block = loc.getBlock();

                    if (!(block.getState() instanceof Container container)) {
                        continue;
                    }

                    addCounts(container.getInventory(), counts);
                }
            }
        }

        Map<String, Map<UUID, Integer>> itemMatrix = new LinkedHashMap<>();

        for (String key : TRACKED.keySet()) {
            Map<UUID, Integer> row = new LinkedHashMap<>();

            for (Map.Entry<UUID, Map<String, Integer>> entry : playerCounts.entrySet()) {
                row.put(entry.getKey(), entry.getValue().get(key));
            }

            itemMatrix.put(key, row);
        }

        Map<UUID, Double> scores = calculateScores(itemMatrix);

        for (Map.Entry<UUID, Map<String, Integer>> entry : playerCounts.entrySet()) {
            UUID uuid = entry.getKey();

            double pts = scores.getOrDefault(uuid, 0.0);
            String name = playerNames.getOrDefault(uuid, "Unknown");

            PlayerPointsStore.set(uuid, name, pts);

            writeRow(idCounter.getAndIncrement(), name, timestamp, pts, entry.getValue());
        }

        PlayerPointsStore.save();
    }

    private static Map<UUID, Double> calculateScores(Map<String, Map<UUID, Integer>> items) {
        Map<UUID, Double> scores = new LinkedHashMap<>();

        for (Map.Entry<String, Map<UUID, Integer>> itemEntry : items.entrySet()) {
            Map<UUID, Integer> playerAmounts = itemEntry.getValue();
            double S_k = playerAmounts.values().stream().mapToDouble(Integer::doubleValue).sum();

            if (S_k == 0) continue;

            double weightedTotal = Math.pow(S_k, 0.5);

            for (Map.Entry<UUID, Integer> e : playerAmounts.entrySet()) {
                double w_i_k = e.getValue() / S_k;
                scores.merge(e.getKey(), w_i_k * weightedTotal, Double::sum);
            }
        }
        return scores;
    }

    static Map<String, Integer> scanAllWorlds() {
        Map<String, Integer> totals = zeroCounts();

        for (String worldName : List.of( "world", "world_nether", "world_the_end")) {
            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;

            Map<String, Integer> worldTotals = scanWorld(world);

            for (Map.Entry<String, Integer> e : worldTotals.entrySet()) {
                totals.merge(e.getKey(), e.getValue(), Integer::sum);
            }
        }

        return totals;
    }

    static Map<String, Integer> scanWorld(World world) {
        Map<String, Integer> totals = zeroCounts();

        for (Player player : world.getPlayers()) {
            if (ModManager.isModerator(player.getName())) {
                continue;
            }

            addCounts(player.getInventory(), totals);

            for (Location loc : ContainerHandler.getContainersPerPlayer(player.getUniqueId())) {
                Block block = loc.getBlock();

                if (!(block.getState() instanceof Container container)) continue;

                addCounts(container.getInventory(), totals);
            }
        }
        return totals;
    }

    private static Map<String, Integer> zeroCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();

        for (String key : TRACKED.keySet()) counts.put(key, 0);
        return counts;
    }

    private static void addCounts(Inventory inv, Map<String, Integer> counts) {
        for (ItemStack item : inv.getContents()) {
            if (item == null) continue;

            for (Map.Entry<String, Material> entry : TRACKED.entrySet()) {
                if (entry.getValue() != item.getType()) continue;
                counts.merge(entry.getKey(), item.getAmount(), Integer::sum);
            }
        }
    }

    private static void writeHeader() throws IOException {
        try (FileWriter fw = new FileWriter(csvFile, true)) {
            fw.write("id,playername,timestamp,points," + String.join(",", TRACKED.keySet()) + "\n");
        }
    }

    private static void writeRow(int id, String name, String timestamp, double points, Map<String, Integer> counts) {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",").append(name).append(",").append(timestamp).append(",").append(points);

        for (int count : counts.values()) sb.append(",").append(count);

        sb.append("\n");

        try (FileWriter fw = new FileWriter(csvFile, true)) {
            fw.write(sb.toString());
        } catch (IOException e) {
            plugin.getLogger().severe("Could not write to item_report.csv: " + e.getMessage());
        }
    }
}