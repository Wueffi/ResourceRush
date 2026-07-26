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

    private record TrackedItem(String key, Material material, int weight) {}
    private static final List<TrackedItem> TRACKED = new ArrayList<>();
    private static final Map<Material, TrackedItem> TRACKED_MAP = new HashMap<>();

    static {
        TRACKED.add(new TrackedItem("Amethyst Shard", Material.AMETHYST_SHARD, 1));
        TRACKED.add(new TrackedItem("Coal", Material.COAL, 1));
        TRACKED.add(new TrackedItem("Copper Ingot", Material.COPPER_INGOT, 1));
        TRACKED.add(new TrackedItem("Diamond", Material.DIAMOND, 1));
        TRACKED.add(new TrackedItem("Emerald", Material.EMERALD, 1));
        TRACKED.add(new TrackedItem("Gold Ingot", Material.GOLD_INGOT, 1));
        TRACKED.add(new TrackedItem("Iron Ingot", Material.IRON_INGOT, 1));
        TRACKED.add(new TrackedItem("Lapis Lazuli", Material.LAPIS_LAZULI, 1));
        TRACKED.add(new TrackedItem("Netherite Ingot", Material.NETHERITE_INGOT, 1));
        TRACKED.add(new TrackedItem("Prismarine Crystals", Material.PRISMARINE_CRYSTALS, 1));
        TRACKED.add(new TrackedItem("Quartz", Material.QUARTZ, 1));
        TRACKED.add(new TrackedItem("Redstone", Material.REDSTONE, 1));
        TRACKED.add(new TrackedItem("Glowstone Dust", Material.GLOWSTONE_DUST, 1));
        TRACKED.add(new TrackedItem("Resin Brick", Material.RESIN_BRICK, 1));

        TRACKED.add(new TrackedItem("Amethyst Shard", Material.AMETHYST_BLOCK, 4));
        TRACKED.add(new TrackedItem("Coal", Material.COAL_BLOCK, 9));
        TRACKED.add(new TrackedItem("Copper Ingot", Material.COPPER_BLOCK, 9));
        TRACKED.add(new TrackedItem("Diamond", Material.DIAMOND_BLOCK, 9));
        TRACKED.add(new TrackedItem("Emerald", Material.EMERALD_BLOCK, 9));
        TRACKED.add(new TrackedItem("Gold Ingot", Material.GOLD_BLOCK, 9));
        TRACKED.add(new TrackedItem("Iron Ingot", Material.IRON_BLOCK, 9));
        TRACKED.add(new TrackedItem("Lapis Lazuli", Material.LAPIS_BLOCK, 9));
        TRACKED.add(new TrackedItem("Netherite Ingot", Material.NETHERITE_BLOCK, 9));
        TRACKED.add(new TrackedItem("Redstone", Material.REDSTONE_BLOCK, 9));
        TRACKED.add(new TrackedItem("Quartz", Material.QUARTZ_BLOCK, 4));
        TRACKED.add(new TrackedItem("Glowstone Dust", Material.GLOWSTONE, 4));

        TRACKED.add(new TrackedItem("Raw Iron", Material.RAW_IRON, 1));
        TRACKED.add(new TrackedItem("Raw Gold", Material.RAW_GOLD, 1));
        TRACKED.add(new TrackedItem("Raw Copper", Material.RAW_COPPER, 1));

        TRACKED.add(new TrackedItem("Raw Iron", Material.RAW_IRON_BLOCK, 9));
        TRACKED.add(new TrackedItem("Raw Gold", Material.RAW_GOLD_BLOCK, 9));
        TRACKED.add(new TrackedItem("Raw Copper", Material.RAW_COPPER_BLOCK, 9));

        TRACKED.add(new TrackedItem("Coal", Material.COAL_ORE, 1));
        TRACKED.add(new TrackedItem("Coal", Material.DEEPSLATE_COAL_ORE, 1));
        TRACKED.add(new TrackedItem("Copper Ingot", Material.COPPER_ORE, 1));
        TRACKED.add(new TrackedItem("Copper Ingot", Material.DEEPSLATE_COPPER_ORE, 1));
        TRACKED.add(new TrackedItem("Diamond", Material.DIAMOND_ORE, 1));
        TRACKED.add(new TrackedItem("Diamond", Material.DEEPSLATE_DIAMOND_ORE, 1));
        TRACKED.add(new TrackedItem("Emerald", Material.EMERALD_ORE, 1));
        TRACKED.add(new TrackedItem("Emerald", Material.DEEPSLATE_EMERALD_ORE, 1));
        TRACKED.add(new TrackedItem("Gold Ingot", Material.GOLD_ORE, 1));
        TRACKED.add(new TrackedItem("Gold Ingot", Material.DEEPSLATE_GOLD_ORE, 1));
        TRACKED.add(new TrackedItem("Gold Ingot", Material.NETHER_GOLD_ORE, 1));
        TRACKED.add(new TrackedItem("Iron Ingot", Material.IRON_ORE, 1));
        TRACKED.add(new TrackedItem("Iron Ingot", Material.DEEPSLATE_IRON_ORE, 1));
        TRACKED.add(new TrackedItem("Lapis Lazuli", Material.LAPIS_ORE, 6));
        TRACKED.add(new TrackedItem("Lapis Lazuli", Material.DEEPSLATE_LAPIS_ORE, 6));
        TRACKED.add(new TrackedItem("Redstone", Material.REDSTONE_ORE, 4));
        TRACKED.add(new TrackedItem("Redstone", Material.DEEPSLATE_REDSTONE_ORE, 4));
        TRACKED.add(new TrackedItem("Quartz", Material.NETHER_QUARTZ_ORE, 1));
        TRACKED.add(new TrackedItem("Netherite Ingot", Material.ANCIENT_DEBRIS, 1));

        TRACKED.add(new TrackedItem("Iron Ingot", Material.IRON_NUGGET, 1));
        TRACKED.add(new TrackedItem("Gold Ingot", Material.GOLD_NUGGET, 1));

        TRACKED.add(new TrackedItem("Apple", Material.APPLE, 1));
        TRACKED.add(new TrackedItem("Baked Potato", Material.BAKED_POTATO, 1));
        TRACKED.add(new TrackedItem("Beef", Material.BEEF, 1));
        TRACKED.add(new TrackedItem("Beetroot", Material.BEETROOT, 1));
        TRACKED.add(new TrackedItem("Beetroot Soup", Material.BEETROOT_SOUP, 1));
        TRACKED.add(new TrackedItem("Bread", Material.BREAD, 1));
        TRACKED.add(new TrackedItem("Cake", Material.CAKE, 1));
        TRACKED.add(new TrackedItem("Carrot", Material.CARROT, 1));
        TRACKED.add(new TrackedItem("Chicken", Material.CHICKEN, 1));
        TRACKED.add(new TrackedItem("Chorus Fruit", Material.CHORUS_FRUIT, 1));
        TRACKED.add(new TrackedItem("Cod", Material.COD, 1));
        TRACKED.add(new TrackedItem("Cooked Chicken", Material.COOKED_CHICKEN, 1));
        TRACKED.add(new TrackedItem("Cooked Cod", Material.COOKED_COD, 1));
        TRACKED.add(new TrackedItem("Cooked Mutton", Material.COOKED_MUTTON, 1));
        TRACKED.add(new TrackedItem("Cooked Porkchop", Material.COOKED_PORKCHOP, 1));
        TRACKED.add(new TrackedItem("Cooked Rabbit", Material.COOKED_RABBIT, 1));
        TRACKED.add(new TrackedItem("Cooked Salmon", Material.COOKED_SALMON, 1));
        TRACKED.add(new TrackedItem("Cookie", Material.COOKIE, 1));
        TRACKED.add(new TrackedItem("Dried Kelp", Material.DRIED_KELP, 1));
        TRACKED.add(new TrackedItem("Enchanted Golden Apple", Material.ENCHANTED_GOLDEN_APPLE, 1));
        TRACKED.add(new TrackedItem("Glow Berries", Material.GLOW_BERRIES, 1));
        TRACKED.add(new TrackedItem("Golden Apple", Material.GOLDEN_APPLE, 1));
        TRACKED.add(new TrackedItem("Golden Carrot", Material.GOLDEN_CARROT, 1));
        TRACKED.add(new TrackedItem("Honey Bottle", Material.HONEY_BOTTLE, 1));
        TRACKED.add(new TrackedItem("Melon Slice", Material.MELON_SLICE, 1));
        TRACKED.add(new TrackedItem("Milk Bucket", Material.MILK_BUCKET, 1));
        TRACKED.add(new TrackedItem("Mushroom Stew", Material.MUSHROOM_STEW, 1));
        TRACKED.add(new TrackedItem("Mutton", Material.MUTTON, 1));
        TRACKED.add(new TrackedItem("Poisonous Potato", Material.POISONOUS_POTATO, 1));
        TRACKED.add(new TrackedItem("Porkchop", Material.PORKCHOP, 1));
        TRACKED.add(new TrackedItem("Potato", Material.POTATO, 1));
        TRACKED.add(new TrackedItem("Pufferfish", Material.PUFFERFISH, 1));
        TRACKED.add(new TrackedItem("Pumpkin Pie", Material.PUMPKIN_PIE, 1));
        TRACKED.add(new TrackedItem("Rabbit", Material.RABBIT, 1));
        TRACKED.add(new TrackedItem("Rabbit Stew", Material.RABBIT_STEW, 1));
        TRACKED.add(new TrackedItem("Rotten Flesh", Material.ROTTEN_FLESH, 1));
        TRACKED.add(new TrackedItem("Salmon", Material.SALMON, 1));
        TRACKED.add(new TrackedItem("Spider Eye", Material.SPIDER_EYE, 1));
        TRACKED.add(new TrackedItem("Steak", Material.COOKED_BEEF, 1));
        TRACKED.add(new TrackedItem("Suspicious Stew", Material.SUSPICIOUS_STEW, 1));
        TRACKED.add(new TrackedItem("Sweet Berries", Material.SWEET_BERRIES, 1));
        TRACKED.add(new TrackedItem("Tropical Fish", Material.TROPICAL_FISH, 1));
        TRACKED.add(new TrackedItem("Armadillo Scute", Material.ARMADILLO_SCUTE, 1));
        TRACKED.add(new TrackedItem("Arrow", Material.ARROW, 1));
        TRACKED.add(new TrackedItem("Blaze Rod", Material.BLAZE_ROD, 1));
        TRACKED.add(new TrackedItem("Bone", Material.BONE, 1));
        TRACKED.add(new TrackedItem("Bone Meal", Material.BONE_MEAL, 1));
        TRACKED.add(new TrackedItem("Breeze Rod", Material.BREEZE_ROD, 1));
        TRACKED.add(new TrackedItem("Egg", Material.EGG, 1));
        TRACKED.add(new TrackedItem("Ender Pearl", Material.ENDER_PEARL, 1));
        TRACKED.add(new TrackedItem("Feather", Material.FEATHER, 1));
        TRACKED.add(new TrackedItem("Ghast Tear", Material.GHAST_TEAR, 1));
        TRACKED.add(new TrackedItem("Glass Bottle", Material.GLASS_BOTTLE, 1));
        TRACKED.add(new TrackedItem("Glow Ink Sac", Material.GLOW_INK_SAC, 1));
        TRACKED.add(new TrackedItem("Goat Horn", Material.GOAT_HORN, 1));
        TRACKED.add(new TrackedItem("Gunpowder", Material.GUNPOWDER, 1));
        TRACKED.add(new TrackedItem("Ink Sac", Material.INK_SAC, 1));
        TRACKED.add(new TrackedItem("Lead", Material.LEAD, 1));
        TRACKED.add(new TrackedItem("Leather", Material.LEATHER, 1));
        TRACKED.add(new TrackedItem("Magma Cream", Material.MAGMA_CREAM, 1));
        TRACKED.add(new TrackedItem("Nautilus Shell", Material.NAUTILUS_SHELL, 1));
        TRACKED.add(new TrackedItem("Nether Star", Material.NETHER_STAR, 1));
        TRACKED.add(new TrackedItem("Ominous Bottle", Material.OMINOUS_BOTTLE, 1));
        TRACKED.add(new TrackedItem("Phantom Membrane", Material.PHANTOM_MEMBRANE, 1));
        TRACKED.add(new TrackedItem("Pitcher Pod", Material.PITCHER_POD, 1));
        TRACKED.add(new TrackedItem("Prismarine Shard", Material.PRISMARINE_SHARD, 1));
        TRACKED.add(new TrackedItem("Rabbit Foot", Material.RABBIT_FOOT, 1));
        TRACKED.add(new TrackedItem("Rabbit Hide", Material.RABBIT_HIDE, 1));
        TRACKED.add(new TrackedItem("Saddle", Material.SADDLE, 1));
        TRACKED.add(new TrackedItem("Sculk Catalyst", Material.SCULK_CATALYST, 1));
        TRACKED.add(new TrackedItem("Seagrass", Material.SEAGRASS, 1));
        TRACKED.add(new TrackedItem("Shulker Shell", Material.SHULKER_SHELL, 1));
        TRACKED.add(new TrackedItem("Slime Ball", Material.SLIME_BALL, 1));
        TRACKED.add(new TrackedItem("Snowball", Material.SNOWBALL, 1));
        TRACKED.add(new TrackedItem("Stick", Material.STICK, 1));
        TRACKED.add(new TrackedItem("String", Material.STRING, 1));
        TRACKED.add(new TrackedItem("Sugar", Material.SUGAR, 1));
        TRACKED.add(new TrackedItem("Torchflower Seeds", Material.TORCHFLOWER_SEEDS, 1));
        TRACKED.add(new TrackedItem("Totem Of Undying", Material.TOTEM_OF_UNDYING, 1));
        TRACKED.add(new TrackedItem("Trident", Material.TRIDENT, 1));
        TRACKED.add(new TrackedItem("Turtle Scute", Material.TURTLE_SCUTE, 1));
        TRACKED.add(new TrackedItem("Wet Sponge", Material.WET_SPONGE, 1));
        TRACKED.add(new TrackedItem("Wither Skeleton Skull", Material.WITHER_SKELETON_SKULL, 1));
        TRACKED.add(new TrackedItem("Zombie Head", Material.ZOMBIE_HEAD, 1));

        TRACKED.add(new TrackedItem("White Wool", Material.WHITE_WOOL, 1));
        TRACKED.add(new TrackedItem("Orange Wool", Material.ORANGE_WOOL, 1));
        TRACKED.add(new TrackedItem("Magenta Wool", Material.MAGENTA_WOOL, 1));
        TRACKED.add(new TrackedItem("Light Blue Wool", Material.LIGHT_BLUE_WOOL, 1));
        TRACKED.add(new TrackedItem("Yellow Wool", Material.YELLOW_WOOL, 1));
        TRACKED.add(new TrackedItem("Lime Wool", Material.LIME_WOOL, 1));
        TRACKED.add(new TrackedItem("Pink Wool", Material.PINK_WOOL, 1));
        TRACKED.add(new TrackedItem("Gray Wool", Material.GRAY_WOOL, 1));
        TRACKED.add(new TrackedItem("Light Gray Wool", Material.LIGHT_GRAY_WOOL, 1));
        TRACKED.add(new TrackedItem("Cyan Wool", Material.CYAN_WOOL, 1));
        TRACKED.add(new TrackedItem("Purple Wool", Material.PURPLE_WOOL, 1));
        TRACKED.add(new TrackedItem("Blue Wool", Material.BLUE_WOOL, 1));
        TRACKED.add(new TrackedItem("Brown Wool", Material.BROWN_WOOL, 1));
        TRACKED.add(new TrackedItem("Green Wool", Material.GREEN_WOOL, 1));
        TRACKED.add(new TrackedItem("Red Wool", Material.RED_WOOL, 1));
        TRACKED.add(new TrackedItem("Black Wool", Material.BLACK_WOOL, 1));

        for (TrackedItem item : TRACKED) {
            TRACKED_MAP.put(item.material(), item);
        }
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

        for (TrackedItem tracked : TRACKED) {
            String key = tracked.key();

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

        for (TrackedItem tracked : TRACKED) {
            counts.putIfAbsent(tracked.key(), 0);
        }

        return counts;
    }

    private static void addCounts(Inventory inv, Map<String, Integer> counts) {
        Map<String, Integer> nuggetBuffer = new HashMap<>();

        for (ItemStack item : inv.getContents()) {
            if (item == null) continue;

            TrackedItem tracked = TRACKED_MAP.get(item.getType());
            if (tracked == null) continue;

            if (item.getType() == Material.IRON_NUGGET) {
                nuggetBuffer.merge("Iron Ingot", item.getAmount(), Integer::sum);
            } else if (item.getType() == Material.GOLD_NUGGET) {
                nuggetBuffer.merge("Gold Ingot", item.getAmount(), Integer::sum);
            } else {
                counts.merge(tracked.key(), item.getAmount() * tracked.weight(), Integer::sum);
            }
        }

        counts.merge("Iron Ingot", nuggetBuffer.getOrDefault("Iron Ingot", 0) / 9, Integer::sum);
        counts.merge("Gold Ingot", nuggetBuffer.getOrDefault("Gold Ingot", 0) / 9, Integer::sum);
    }

    private static void writeHeader() throws IOException {
        try (FileWriter fw = new FileWriter(csvFile, true)) {
            String header = TRACKED.stream().map(TrackedItem::key).distinct().reduce((a, b) -> a + "," + b).orElse("");

            fw.write("id,playername,timestamp,points," + header + "\n");
        }
    }

    private static void writeRow(int id, String name, String timestamp, double points, Map<String, Integer> counts) {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",").append(name).append(",").append(timestamp).append(",").append(points);

        for (String key : counts.keySet()) {
            sb.append(",").append(counts.getOrDefault(key, 0));
        }

        sb.append("\n");

        try (FileWriter fw = new FileWriter(csvFile, true)) {
            fw.write(sb.toString());
        } catch (IOException e) {
            plugin.getLogger().severe("Could not write to item_report.csv: " + e.getMessage());
        }
    }
}