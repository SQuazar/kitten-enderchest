package com.github.kitten_java.enderchest;

import com.github.kitten_java.enderchest.config.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnderChest implements InventoryHolder, ConfigurationSerializable {

    @Getter @Setter private Player owner;
    @Getter private Inventory inventory;

    private EnderChest(Map<String, Object> s){
        Configuration config = EnderChestAPI.getInstance().getConf();
        inventory = Bukkit.createInventory(this, config.getMaxSize(), config.getTitle());
        for (int slot = 0;slot < inventory.getSize();slot++){
            Object something = s.get("item."+slot);
            if (something instanceof ItemStack item){
                inventory.setItem(slot, item);
            }
        }
        if (s.get("locked-slots") instanceof List<?> lockedSlots) {
            for (Object slot : lockedSlots) {
                int i = (int)slot;
                inventory.setItem(i, EnderChestAPI.getInstance().getConf().getLockItemForSlot(i));
            }
        }
    }

    public EnderChest(Player owner){
        this.owner = owner;
        Configuration config = EnderChestAPI.getInstance().getConf();
        inventory = Bukkit.createInventory(this, config.getMaxSize(), config.getTitle());
        for (int slot = config.getStartSize(); slot < inventory.getSize(); slot++){
            inventory.setItem(slot, config.getLockItemForSlot(slot));
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        NamespacedKey key = EnderChestAPI.getInstance().getConf().getNbtKey();
        List<Integer> slots = new ArrayList<>();
        for (int slot = 0;slot < inventory.getSize();slot++){
            ItemStack item = inventory.getItem(slot);
            if (item != null && !item.getType().isAir()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.getPersistentDataContainer().has(key, PersistentDataType.DOUBLE)){
                    slots.add(slot);
                }
                else result.put("item."+slot, item);
            }
        }
        result.put("locked-slots", slots);
        return result;
    }

    public static EnderChest deserialize(Map<String, Object> s){
        return new EnderChest(s);
    }
}
