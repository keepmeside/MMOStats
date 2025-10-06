package me.manus.mmostats.util;

import me.manus.mmostats.MMOStats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TextUtil {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().character('&').build();

    public static Component parse(String text) {
        if (MMOStats.getPlugin(MMOStats.class).getConfigManager().getColorMode().equalsIgnoreCase("mini_message")) {
            return miniMessage.deserialize(text);
        } else {
            return legacySerializer.deserialize(text);
        }
    }

    public static String serialize(Component component) {
        if (MMOStats.getPlugin(MMOStats.class).getConfigManager().getColorMode().equalsIgnoreCase("mini_message")) {
            return miniMessage.serialize(component);
        } else {
            return legacySerializer.serialize(component);
        }
    }

    public static String getItemName(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return item.getType().name().replace("_", " ").toLowerCase();
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
            return serialize(meta.displayName());
        } else {
            return item.getType().name().replace("_", " ").toLowerCase();
        }
    }
}
