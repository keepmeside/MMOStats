package me.manus.mmostats.util;

import me.manus.mmostats.MMOStats;
import me.manus.mmostats.config.GemConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class ItemUtil {

    public static ItemStack createGemItem(MMOStats plugin, GemConfig gemConfig, int amount) {
        ItemStack item;
        String materialString = gemConfig.getMaterial().toUpperCase();

        if (materialString.startsWith("HDB:") && plugin.getHeadDatabaseHook().isHooked()) {
            item = plugin.getHeadDatabaseHook().getHead(materialString.substring(4));
            if (item == null) { // Fallback if ID is invalid or HDB not found
                plugin.getLogger().warning("HeadDatabase head with ID " + materialString.substring(4) + " not found or HDB not hooked. Defaulting to STONE for gem " + gemConfig.getId() + ".");
                item = new ItemStack(Material.STONE);
            }
        } else {
            try {
                item = new ItemStack(Material.valueOf(materialString));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material \'" + gemConfig.getMaterial() + "\' for gem \'" + gemConfig.getId() + "\'. Defaulting to STONE.");
                item = new ItemStack(Material.STONE);
            }
        }

        item.setAmount(amount);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(TextUtil.parse(gemConfig.getName()));

        String statsString = plugin.getMmoItemsHook().isHooked()
                ? plugin.getMmoItemsHook().getStatsString(gemConfig.getStats())
                : gemConfig.getStats().entrySet().stream()
                    .map(e -> e.getKey() + ": " + e.getValue())
                    .collect(Collectors.joining(", "));

        List<Component> lore = gemConfig.getLore().stream()
                .map(line -> line.replace("{stats}", statsString).replace("{type}", gemConfig.getType()))
                .map(TextUtil::parse)
                .collect(Collectors.toList());
        meta.lore(lore);

        item.setItemMeta(meta);

        // Add PDC data to identify it as a gem
        PDCUtil.setGemId(item, gemConfig.getId());

        return item;
    }

    public static String getItemName(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return "null";
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return TextUtil.serialize(meta.displayName());
        } else {
            return item.getType().name().replace("_", " ").toLowerCase();
        }
    }
}
