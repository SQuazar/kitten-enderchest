package com.github.kitten_java.enderchest.inventory;

import com.github.kitten_java.enderchest.config.InventorySettings;

import com.github.kitten_java.enderchest.manager.EnderChestManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AdvancedEnderChest implements InventoryHolder {
    private final ChestSlot[] slots;
    private final EnderChestManager manager;
    private final Inventory inventory;

    public AdvancedEnderChest(ChestSlot[] slots, EnderChestManager manager, InventorySettings settings) {
        this.slots = slots;
        this.manager = manager;

        this.inventory = Bukkit.createInventory(this, settings.maxSize(), settings.title());
        for (int i = 0; i < settings.maxSize(); i++)
            inventory.setItem(i, slots[i].getStack());
    }

    public void handleClick(InventoryClickEvent event) {
        if (event.getSlot() >= slots.length) return;
        ChestSlot slot = slots[event.getSlot()];
        if (slot != null && slot.isLocked()) {
            event.setCancelled(true);
            if (manager.purchase((Player) event.getWhoClicked(), slot.getPrice())) {
                slot.setLocked(false);
                slot.setStack(new ItemStack(Material.AIR));
                event.setCurrentItem(null);
                manager.saveEnderChest((Player) event.getWhoClicked());
            }
        }
        if (slot != null)
            slot.setStack(Optional.ofNullable(event.getCurrentItem())
                    .orElse(new ItemStack(Material.AIR)));
    }

    public ChestSlot[] getSlots() {
        for (int i = 0; i < slots.length; i++) {
            ChestSlot slot = slots[i];
            if (slot != null && !slot.isLocked())
                slot.setStack(Optional.ofNullable(inventory.getItem(i))
                        .orElse(new ItemStack(Material.AIR)));
        }

        return slots;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
