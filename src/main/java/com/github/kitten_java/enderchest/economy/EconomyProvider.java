package com.github.kitten_java.enderchest.economy;

import org.bukkit.entity.Player;

public interface EconomyProvider {
    boolean takeIfHas(Player player, double amount);
}
