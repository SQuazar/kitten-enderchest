package com.github.kitten_java.enderchest.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class ItemBuilder {
    private final Material material;

    private Component displayName;
    private List<Component> lore;
    private int model;

    ItemBuilder(Material material) {
        this.material = material;
    }

    public static ItemBuilder builder(Material material) {
        return new ItemBuilder(material);
    }

    public ItemBuilder displayName(Component displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemBuilder lore(List<Component> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder model(int model) {
        this.model = model;
        return this;
    }

    public ItemStack build() {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(displayName);
        meta.lore(lore);
        meta.setCustomModelData(model);
        stack.setItemMeta(meta);
        return stack;
    }

}
