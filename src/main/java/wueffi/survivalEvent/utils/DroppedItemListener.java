package wueffi.survivalEvent.utils;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DroppedItemListener implements Listener {
    private static final long ENTITY_DROP_WINDOW_MILLIS = 3000L;
    private static final double ENTITY_DROP_RADIUS = 3.0;

    private final Map<String, UUID> pendingBlockOwners = new HashMap<>();
    private final List<PendingEntityDrop> pendingEntityDrops = new ArrayList<>();

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        DroppedItemHandler.addItem(event.getItemDrop().getUniqueId(), event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        String key = ContainerHandler.keyFor(event.getBlock().getLocation());
        UUID owner = ContainerHandler.getOwner(key);

        if (owner != null) {
            pendingBlockOwners.put(key, owner);
        }
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        String key = ContainerHandler.keyFor(event.getBlockState().getLocation());
        UUID owner = pendingBlockOwners.remove(key);

        if (owner == null) {
            owner = event.getPlayer().getUniqueId();
        }

        for (Item item : event.getItems()) {
            DroppedItemHandler.addItem(item.getUniqueId(), owner);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent event) {
        String key = ContainerHandler.keyFor(event.getEntity());
        UUID owner = ContainerHandler.getOwner(key);

        if (owner != null) {
            pendingEntityDrops.add(new PendingEntityDrop(event.getEntity().getLocation(), owner, System.currentTimeMillis()));
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Iterator<PendingEntityDrop> iterator = pendingEntityDrops.iterator();
        Location itemLocation = event.getLocation();
        long now = System.currentTimeMillis();

        while (iterator.hasNext()) {
            PendingEntityDrop drop = iterator.next();

            if (now - drop.timestamp > ENTITY_DROP_WINDOW_MILLIS) {
                iterator.remove();
                continue;
            }

            if (!drop.location.getWorld().equals(itemLocation.getWorld())) continue;
            if (drop.location.distanceSquared(itemLocation) > ENTITY_DROP_RADIUS * ENTITY_DROP_RADIUS) continue;

            DroppedItemHandler.addItem(event.getEntity().getUniqueId(), drop.owner);
            return;
        }
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        DroppedItemHandler.removeItem(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent event) {
        UUID sourceOwner = DroppedItemHandler.getOwner(event.getEntity().getUniqueId());
        if (sourceOwner == null) return;

        UUID targetOwner = DroppedItemHandler.getOwner(event.getTarget().getUniqueId());
        if (targetOwner == null) {
            DroppedItemHandler.addItem(event.getTarget().getUniqueId(), sourceOwner);
        }

        DroppedItemHandler.removeItem(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        DroppedItemHandler.removeItem(event.getItem().getUniqueId());
    }

    private record PendingEntityDrop(Location location, UUID owner, long timestamp) {
    }
}