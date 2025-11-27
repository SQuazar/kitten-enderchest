package com.github.kitten_java.enderchest.inventory;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class ChestSlot implements ConfigurationSerializable {
    private ItemStack stack;
    private final double price;

    private boolean locked;

    public ChestSlot(ItemStack stack, double price, boolean locked) {
        this.stack = stack;
        this.price = price;
        this.locked = locked;
    }

    public ChestSlot(ItemStack stack, double price) {
        this.stack = stack;
        this.price = price;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of(
                "stack", stack.serialize(),
                "price", price,
                "locked", locked
        );
    }

    public static ChestSlot deserialize(Map<String, Object> map) {
        ItemStack stack = ItemStack.deserialize((Map<String, Object>) map.get("stack"));
        double price = ((Number) map.get("price")).doubleValue();
        boolean locked = (boolean) map.get("locked");
        return new ChestSlot(stack, price, locked);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChestSlot slot = (ChestSlot) o;
        return Double.compare(price, slot.price) == 0 && locked == slot.locked && Objects.equals(stack, slot.stack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack, price, locked);
    }
}
