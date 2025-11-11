package com.github.kitten_java.enderchest.economy;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerPointsRealization implements EconomyRealization{

    private PlayerPointsAPI economy;

    public PlayerPointsRealization(){
        economy = PlayerPoints.getInstance().getAPI();
    }

    @Override
    public boolean takeIfHas(Player player, double amount) {
        UUID uuid = player.getUniqueId();
        if (economy.look(uuid) >= amount){
            return economy.give(uuid, (int) -amount);
        }
        return false;
    }
}
