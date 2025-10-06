package me.manus.mmostats.hook;

import me.manus.mmostats.MMOStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.StatTexture;
import net.Indyuce.mmoitems.api.util.MMOItemReforger;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.StatData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

public class MMOItemsHook {

    private final MMOStats plugin;
    private boolean hooked = false;

    public MMOItemsHook(MMOStats plugin) {
        this.plugin = plugin;
        if (Bukkit.getPluginManager().getPlugin("MMOItems") != null) {
            this.hooked = true;
            plugin.getLogger().info("Hooked into MMOItems!");
        } else {
            plugin.getLogger().info("MMOItems not found, skipping integration.");
        }
    }

    public boolean isHooked() {
        return hooked;
    }

    public String getItemType(ItemStack item) {
        if (!hooked) return null;
        MMOItem mmoItem = MMOItem.get(item);
        return (mmoItem != null) ? mmoItem.getType().getId() : null;
    }

    public ItemStack addStats(ItemStack item, Map<String, Double> statsToAdd) {
        if (!hooked) return item;

        LiveMMOItem mmoItem = new LiveMMOItem(item);
        if (!mmoItem.hasMMOItem()) {
            // If it's a vanilla item, we can't directly add MMOItems stats.
            // We could potentially convert it to an MMOItem, but that's complex.
            // For now, we'll just return the original item, and PDC will handle it.
            return item;
        }

        MMOItemReforger reforger = new MMOItemReforger(mmoItem);

        for (Map.Entry<String, Double> entry : statsToAdd.entrySet()) {
            String statId = entry.getKey().toUpperCase();
            Double value = entry.getValue();

            ItemStat stat = MMOItems.plugin.getStats().get(statId);
            if (stat == null) {
                plugin.getLogger().warning("MMOItems stat '" + statId + "' not found. Skipping.");
                continue;
            }

            if (stat instanceof DoubleStat) {
                // Get current stat data
                Optional<StatData> currentData = mmoItem.getData().getStatData(stat);
                DoubleData doubleData = (DoubleData) currentData.orElse(new DoubleData(0.0));

                // Add the new value
                doubleData.add(value);

                // Set the new stat data
                reforger.setStat(stat, doubleData);
            } else {
                plugin.getLogger().warning("MMOItems stat '" + statId + "' is not a DoubleStat. Cannot add numerical value. Skipping.");
            }
        }

        return reforger.reforge(mmoItem.getNBT().getItem());
    }

    public String getStatsString(Map<String, Double> stats) {
        if (!hooked) {
            return stats.entrySet().stream()
                    .map(e -> e.getKey() + ": " + e.getValue())
                    .collect(java.util.stream.Collectors.joining(", "));
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> entry : stats.entrySet()) {
            String statId = entry.getKey().toUpperCase();
            Double value = entry.getValue();

            ItemStat stat = MMOItems.plugin.getStats().get(statId);
            if (stat == null) {
                sb.append(statId).append(": ").append(value).append(", ");
                continue;
            }

            // Use MMOItems' own formatting for stats if possible
            // This is a simplified approach, MMOItems has complex stat formatting
            // For full formatting, one would need to simulate MMOItems' stat display logic
            String formattedValue = (value > 0 ? "+" : "") + value;
            sb.append(stat.getName()).append(": ").append(formattedValue).append(", ");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2); // Remove trailing ", "
        }
        return sb.toString();
    }

    public MMOItem getMMOItem(ItemStack item) {
        if (!hooked) return null;
        return MMOItem.get(item);
    }
}
