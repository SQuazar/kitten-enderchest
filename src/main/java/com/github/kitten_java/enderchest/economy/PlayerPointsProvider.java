package com.github.kitten_java.enderchest.economy;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerPointsProvider implements EconomyProvider {
    private final PlayerPointsAPI economy;

    public PlayerPointsProvider(){
        economy = PlayerPoints.getInstance().getAPI();
    }

    @Override
    public boolean takeIfHas(Player player, double amount) {
        return economy.take(player.getUniqueId(), (int) amount);
    }
}
