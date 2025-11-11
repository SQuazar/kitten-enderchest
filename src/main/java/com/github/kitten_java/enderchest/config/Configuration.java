package com.github.kitten_java.enderchest.config;

import com.github.kitten_java.enderchest.EnderChestAPI;
import com.google.common.collect.HashBiMap;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.Map;

@Getter
public class Configuration {

    private Map<Integer, ItemStack> lockItems = HashBiMap.create();
    private NamespacedKey nbtKey;
    private String title;
    private int maxSize, startSize;
    private String notLoadedMessage, successfulBuyMessage, noPointsMessage;
    private boolean isVault;

    public Configuration(){
        File file = new File(EnderChestAPI.getInstance().getDataFolder(), "config.yml");
        if (!file.exists()){
            EnderChestAPI.getInstance().saveResource("config.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        title = config.getString("title", "");
        maxSize = config.getInt("max-size", 6)*9;
        startSize = config.getInt("start-size", 27);
        nbtKey = NamespacedKey.fromString(config.getString("namespaced", "not-defined-and-using-default"));
        notLoadedMessage = config.getString("messages.not-loaded", "");
        successfulBuyMessage = config.getString("messages.successfull-buy", "");
        noPointsMessage = config.getString("messages.no-points", "");
        isVault = config.getString("economy-type", "Vault").toLowerCase().equals("vault");

        ConfigurationSection lockItemsSection = config.getConfigurationSection("lock-items");
        for (String key : lockItemsSection.getKeys(false)){
            ItemStack i = readItem(lockItemsSection.getConfigurationSection(key));
            lockItems.put(Integer.parseInt(key), i);
        }
    }

    public ItemStack getLockItemForSlot(int slot){
        ItemStack result;
        do{
            result = lockItems.get(slot);
            slot--;
        } while (result == null);
        return result;
    }

    private ItemStack readItem(ConfigurationSection s){
        ItemStack result = new ItemStack(Material.valueOf(s.getString("material", "barrier").toUpperCase()), s.getInt("amount", 1));
        ItemMeta meta = result.getItemMeta();
        meta.setDisplayName(s.getString("displayname", ""));
        meta.setLore(s.getStringList("lore"));
        meta.setCustomModelData(s.getInt("model", 0));
        meta.getPersistentDataContainer().set(nbtKey, PersistentDataType.DOUBLE, s.getDouble("price", 1.0));
        result.setItemMeta(meta);
        return result;
    }
}
