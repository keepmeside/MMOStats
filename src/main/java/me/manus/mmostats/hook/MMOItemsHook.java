package me.manus.mmostats.hook;

import me.manus.mmostats.MMOStats;
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
        // Since MMOItems API is not available at compile time, we'll use reflection
        // or return null for now. Users will need to add MMOItems dependency manually.
        plugin.getLogger().info("MMOItems integration requires the MMOItems plugin and API to be present.");
        return null;
    }

    public ItemStack addStats(ItemStack item, Map<String, Double> statsToAdd) {
        if (!hooked) return item;
        
        // Since MMOItems API is not available at compile time, we can't directly modify stats
        // The plugin will rely on PDC storage for vanilla items
        plugin.getLogger().info("MMOItems stat modification requires the MMOItems plugin to be present.");
        return item;
    }

    public String getStatsString(Map<String, Double> stats) {
        // Fallback formatting when MMOItems is not available
        return stats.entrySet().stream()
                .map(e -> "+" + e.getValue() + " " + formatStatName(e.getKey()))
                .collect(java.util.stream.Collectors.joining(", "));
    }
    
    private String formatStatName(String statKey) {
        // Convert stat keys to readable names
        switch (statKey.toUpperCase()) {
            case "DAMAGE": return "Damage";
            case "ATTACK_SPEED": return "Attack Speed";
            case "MAX_HEALTH": return "Max Health";
            case "DEFENSE": return "Defense";
            case "CRITICAL_STRIKE_CHANCE": return "Critical Strike Chance";
            case "MINING_SPEED": return "Mining Speed";
            default: return statKey.replace("_", " ");
        }
    }

    public Object getMMOItem(ItemStack item) {
        if (!hooked) return null;
        // Return null since MMOItems API is not available at compile time
        return null;
    }
}
