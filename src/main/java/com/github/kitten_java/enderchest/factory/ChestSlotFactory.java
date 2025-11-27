package com.github.kitten_java.enderchest.factory;

import com.github.kitten_java.enderchest.inventory.ChestSlot;
import com.github.kitten_java.enderchest.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ChestSlotFactory {
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    public ChestSlot create(ConfigurationSection s) {
        Material material = Material.matchMaterial(s.getString("material", "barrier").toUpperCase());
        ItemStack stack = ItemBuilder.builder(material)
                .displayName(SERIALIZER.deserialize(s.getString("displayname", "")))
                .lore(s.getStringList("lore").stream().map(st -> (Component) SERIALIZER.deserialize(st)).toList())
                .model(s.getInt("model", 0))
                .build();
        double price = s.getDouble("price", 0);

        return new ChestSlot(stack, price, true);
    }
}
