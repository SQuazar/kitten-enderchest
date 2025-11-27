package com.github.kitten_java.enderchest.manager;

import com.github.kitten_java.enderchest.EnderChestAPI;
import com.github.kitten_java.enderchest.config.Configuration;
import com.github.kitten_java.enderchest.config.InventorySettings;
import com.github.kitten_java.enderchest.economy.EconomyProvider;
import com.github.kitten_java.enderchest.inventory.AdvancedEnderChest;
import com.github.kitten_java.enderchest.inventory.ChestSlot;
import com.github.kitten_java.enderchest.persistent.SerializableArrayDataType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class EnderChestManager {
    public static final PersistentDataType<byte[], ChestSlot[]> CHEST_SLOT_DATA_TYPE =
            new SerializableArrayDataType<>(ChestSlot[].class);

    private final NamespacedKey key;
    private final Configuration configuration;
    private final EconomyProvider economy;
    private final Map<UUID, AdvancedEnderChest> loadedEnderChests = new HashMap<>();

    public EnderChestManager(EnderChestAPI plugin, EconomyProvider economy, Configuration configuration) {
        this.key = new NamespacedKey(plugin, "enderchest");
        this.economy = economy;
        this.configuration = configuration;
    }

    public AdvancedEnderChest loadEnderChest(Player player) {
        if (loadedEnderChests.containsKey(player.getUniqueId()))
            return loadedEnderChests.get(player.getUniqueId());
        PersistentDataContainer container = player.getPersistentDataContainer();
        AdvancedEnderChest chest;
        InventorySettings settings = configuration.getInventorySettings();
        if (container.has(key, CHEST_SLOT_DATA_TYPE)) {
            ChestSlot[] slots = container.get(key, CHEST_SLOT_DATA_TYPE);
            if (slots == null) slots = new ChestSlot[settings.maxSize()];

            if (slots.length != settings.maxSize() - 1)
                slots = Arrays.copyOf(slots, settings.maxSize());

            for (int i = 0; i < slots.length; i++) {
                if (slots[i] == null && i < settings.startSize()) slots[i] = new ChestSlot(new ItemStack(Material.AIR), 0, false);
                if (slots[i] != null && slots[i].isLocked())
                    slots[i] = configuration.getLockItemForSlot(i);
                if (slots[i] == null && i >= settings.startSize()) slots[i] = configuration.getLockItemForSlot(i);
            }
            chest = new AdvancedEnderChest(slots, this, settings);
        } else {
            ChestSlot[] slots = new ChestSlot[settings.maxSize()];
            for (int i = 0; i < settings.startSize(); i++)
                slots[i] = new ChestSlot(new ItemStack(Material.AIR), 0, false);
            for (int i = settings.startSize(); i < settings.maxSize(); i++)
                slots[i] = configuration.getLockItemForSlot(i);

            container.set(key, CHEST_SLOT_DATA_TYPE, slots);
            chest = new AdvancedEnderChest(slots, this, settings);
        }

        loadedEnderChests.put(player.getUniqueId(), chest);
        return chest;
    }

    public void saveEnderChest(Player player) {
        Optional.ofNullable(loadedEnderChests.get(player.getUniqueId())).ifPresent(ec ->
                player.getPersistentDataContainer().set(key, CHEST_SLOT_DATA_TYPE, ec.getSlots()));
    }

    public void removeFromCache(Player player) {
        saveEnderChest(player);
        loadedEnderChests.remove(player.getUniqueId());
    }

    public boolean purchase(Player player, double price) {
        if (economy.takeIfHas(player, price)) {
            player.sendMessage(configuration.getMessages().purchaseSuccessful());
            return true;
        } else {
            player.sendMessage(configuration.getMessages().purchaseFailed());
            return false;
        }
    }

    public void uploadCachedData() {
        loadedEnderChests.forEach((uuid, ec) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) saveEnderChest(player);
        });

        loadedEnderChests.clear();
    }
}
