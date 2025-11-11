package com.github.kitten_java.enderchest.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VaultRealization implements EconomyRealization{

    private Economy economy;

    public VaultRealization(){
        economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }

    @Override
    public boolean takeIfHas(Player player, double amount) {
        if (economy.has(player, amount)){
            economy.withdrawPlayer(player, amount);
            return true;
        }
        return false;
    }
}
