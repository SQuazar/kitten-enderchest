package com.github.kitten_java.enderchest.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VaultProvider implements EconomyProvider {
    private final Economy economy;

    public VaultProvider(){
        economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }

    @Override
    public boolean takeIfHas(Player player, double amount) {
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }
}
