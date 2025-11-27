package com.github.kitten_java.enderchest.config;

import net.kyori.adventure.text.Component;

public record InventorySettings(
        Component title,
        int maxSize,
        int startSize
) {
}
