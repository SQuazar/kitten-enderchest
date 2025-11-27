package com.github.kitten_java.enderchest;

import com.github.kitten_java.enderchest.config.Configuration;
import com.github.kitten_java.enderchest.economy.EconomyProvider;
import com.github.kitten_java.enderchest.economy.PlayerPointsProvider;
import com.github.kitten_java.enderchest.economy.VaultProvider;
import com.github.kitten_java.enderchest.factory.ChestSlotFactory;
import com.github.kitten_java.enderchest.inventory.AdvancedEnderChest;
import com.github.kitten_java.enderchest.listener.EnderChestListener;
import com.github.kitten_java.enderchest.manager.EnderChestManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnderChestAPI extends JavaPlugin {
    private EnderChestManager enderChestManager;

    @Override
    public void onEnable() {
        ChestSlotFactory slotFactory = new ChestSlotFactory();
        Configuration conf = new Configuration(this, slotFactory);

        EconomyProvider economyProvider = conf.isVault() ?
                new VaultProvider()
                : new PlayerPointsProvider();

        this.enderChestManager = new EnderChestManager(this, economyProvider, conf);

        Bukkit.getPluginManager().registerEvents(new EnderChestListener(enderChestManager), this);
    }

    @Override
    public void onDisable() {
        enderChestManager.uploadCachedData();
        for (Player player : Bukkit.getOnlinePlayers()) {
            InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
            if (holder != null && holder.getClass().getName().equals(AdvancedEnderChest.class.getName())) {
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            }
        }
    }
}
