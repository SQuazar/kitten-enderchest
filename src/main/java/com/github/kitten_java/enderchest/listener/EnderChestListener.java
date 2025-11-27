package com.github.kitten_java.enderchest.listener;

import com.github.kitten_java.enderchest.inventory.AdvancedEnderChest;
import com.github.kitten_java.enderchest.manager.EnderChestManager;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EnderChestListener implements Listener {
    private final EnderChestManager manager;

    public EnderChestListener(EnderChestManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        manager.removeFromCache(player);
    }

    @EventHandler
    public void onEnderChestOpen(InventoryOpenEvent e) {
        if (e.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
            e.setCancelled(true);
            openAdvancedEnderChest((Player) e.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof AdvancedEnderChest) {
            manager.saveEnderChest((Player) e.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof AdvancedEnderChest ec) {
            ec.handleClick(e);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getState() instanceof EnderChest) {
            e.setCancelled(true);
            openAdvancedEnderChest(e.getPlayer());
        }
    }

    void openAdvancedEnderChest(Player player) {
        AdvancedEnderChest chest = manager.loadEnderChest(player);
        player.openInventory(chest.getInventory());
    }
}
