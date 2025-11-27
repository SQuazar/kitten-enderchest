package com.github.kitten_java.enderchest.config;

import com.github.kitten_java.enderchest.EnderChestAPI;
import com.github.kitten_java.enderchest.factory.ChestSlotFactory;
import com.github.kitten_java.enderchest.inventory.ChestSlot;
import lombok.AccessLevel;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand;

@Getter
public class Configuration {
    private final Messages messages;
    private final boolean isVault;
    private final InventorySettings inventorySettings;

    @Getter(AccessLevel.PRIVATE)
    private final Map<Integer, Supplier<ChestSlot>> lockedSlots = new HashMap<>();

    public Configuration(EnderChestAPI plugin, ChestSlotFactory lockedSlotFactory) {
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) plugin.saveResource("config.yml", false);

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        Component title = legacyAmpersand().deserialize(config.getString("title", ""));
        int maxSize = config.getInt("max-size", 6) * 9;
        int startSize = config.getInt("start-size", 27);

        this.inventorySettings = new InventorySettings(title, maxSize, startSize);

        messages = new Messages(
                legacyAmpersand().deserialize(config.getString("messages.not-loaded", "")),
                legacyAmpersand().deserialize(config.getString("messages.successfull-buy", "")),
                legacyAmpersand().deserialize(config.getString("messages.no-points", ""))
        );
        isVault = config.getString("economy-type", "Vault").equalsIgnoreCase("vault");

        ConfigurationSection lockItemsSection = config.getConfigurationSection("lock-items");
        for (String key : lockItemsSection.getKeys(false)) {
            lockedSlots.put(Integer.parseInt(key), () -> lockedSlotFactory.create(lockItemsSection.getConfigurationSection(key)));
        }
    }

    public ChestSlot getLockItemForSlot(int slot) {
        Supplier<ChestSlot> result;
        do {
            result = lockedSlots.get(slot);
            slot--;
        } while (result == null && slot > 0);
        return result.get();
    }

    public record Messages(
            Component notLoaded,
            Component purchaseSuccessful,
            Component purchaseFailed
    ) {

    }
}
