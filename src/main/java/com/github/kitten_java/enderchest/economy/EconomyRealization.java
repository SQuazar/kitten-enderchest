package com.github.kitten_java.enderchest.economy;

import org.bukkit.entity.Player;

public interface EconomyRealization {

    public boolean takeIfHas(Player player, double amount);
}
