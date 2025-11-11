package com.github.kitten_java.enderchest;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Listener implements org.bukkit.event.Listener {

    private EnderChestAPI api;
    private File folder;

    public Listener(){
        api = EnderChestAPI.getInstance();
        folder = new File(api.getDataFolder(), "data");
        if (!folder.exists()) folder.mkdirs();
        ConfigurationSerialization.registerClass(EnderChest.class);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(api, () -> {
            File file = new File(folder, uuid+".yml");
            if (!file.exists()){
                addEnderChest(uuid, new EnderChest(player));
                return;
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            EnderChest enderchest = config.getSerializable("data", EnderChest.class);
            enderchest.setOwner(player);
            addEnderChest(uuid, enderchest);
        });
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(api, () -> {
            File file = new File(folder, uuid+".yml");
            if (!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    api.getLogger().severe("An exception occured while saving player "+uuid+"! Error: "+e.getStackTrace());
                    return;
                }
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            EnderChest enderchest = api.getEnderchests().get(uuid);
            config.set("data", enderchest);
            try {
                config.save(file);
            } catch (IOException e) {
                api.getLogger().severe("An exception occured while saving player "+uuid+"! Error: "+e.getStackTrace());
            }
        });
    }

    @EventHandler
    public void onEnderChestOpen(InventoryOpenEvent event){
        if (event.getInventory().getType().equals(InventoryType.ENDER_CHEST)){
            event.setCancelled(true);
            HumanEntity entity = event.getPlayer();
            EnderChest enderchest = api.getEnderchests().get(entity.getUniqueId());
            if (enderchest != null) {
                entity.openInventory(enderchest.getInventory());
            }
            else {
                entity.sendMessage(api.getConf().getNotLoadedMessage());
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (pdc.has(api.getConf().getNbtKey(), PersistentDataType.DOUBLE)){
            event.setCancelled(true);
            double price = pdc.get(api.getConf().getNbtKey(), PersistentDataType.DOUBLE);
            Player player = (Player) event.getWhoClicked();
            if (api.takeIfHas(player, price)){
                api.getEnderchests().get(player.getUniqueId()).getInventory().setItem(event.getSlot(), null);
                player.sendMessage(api.getConf().getSuccessfulBuyMessage());
            }else {
                player.sendMessage(api.getConf().getNoPointsMessage());
            }
        }
    }

    private void addEnderChest(UUID uuid, EnderChest enderchest){
        Bukkit.getScheduler().runTask(api, () -> {
            if (Bukkit.getPlayer(uuid) != null){
                api.getEnderchests().put(uuid, enderchest);
            }
        });
    }
}
