package com.github.kitten_java.enderchest;

import com.github.kitten_java.enderchest.config.Configuration;
import com.github.kitten_java.enderchest.economy.EconomyRealization;
import com.github.kitten_java.enderchest.economy.PlayerPointsRealization;
import com.github.kitten_java.enderchest.economy.VaultRealization;
import com.google.common.collect.HashBiMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public final class EnderChestAPI extends JavaPlugin {

    @Getter private static EnderChestAPI instance;

    @Getter private Configuration conf;
    @Getter @Setter private EconomyRealization economy;
    @Getter private Map<UUID, EnderChest> enderchests = HashBiMap.create();

    public void onEnable(){
        instance = this;
        conf = new Configuration();
        if (conf.isVault()){
            economy = new VaultRealization();
        }else economy = new PlayerPointsRealization();
        Bukkit.getPluginManager().registerEvents(new Listener(), this);
    }

    public boolean takeIfHas(Player player, double amount){
        return economy.takeIfHas(player, amount);
    }
}
