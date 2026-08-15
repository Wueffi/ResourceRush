package wueffi.resourcerush.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Nameable;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.InventoryHolder;

public class ContainerListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (!(block.getState() instanceof Container container)) return;

        Player player = event.getPlayer();

        container.customName(Component.text(player.getName()));
        container.update();

        ContainerHandler.addContainer(player.getUniqueId(), ContainerHandler.keyFor(block.getLocation()));
        player.sendMessage("§aYou claimed this container!");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!(block.getState() instanceof Container)) return;

        ContainerHandler.removeContainer(ContainerHandler.keyFor(block.getLocation()));
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof DoubleChest doubleChest) {
            InventoryHolder left = doubleChest.getLeftSide();
            if (left != null) holder = left;
        }

        String key;
        Nameable nameable;

        if (holder instanceof Container container) {
            key = ContainerHandler.keyFor(container);
            nameable = container;
        } else if (holder instanceof Entity entity) {
            key = ContainerHandler.keyFor(entity);
            nameable = entity;
        } else {
            return;
        }

        if (key == null) return;
        if (ContainerHandler.getOwner(key) != null) return;

        ContainerHandler.addContainer(player.getUniqueId(), key);
        nameable.customName(Component.text(player.getName()));

        if (holder instanceof Container container) {
            container.update();
        }

        player.sendMessage("§aYou claimed this container!");
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (!(event.getVehicle() instanceof InventoryHolder)) return;
        ContainerHandler.removeContainer(ContainerHandler.keyFor(event.getVehicle()));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof InventoryHolder)) return;
        ContainerHandler.removeContainer(ContainerHandler.keyFor(event.getEntity()));
    }
}