package me.manus.mmostats.util;

import me.manus.mmostats.MMOStats;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PDCUtil {

    private static final NamespacedKey GEM_ID_KEY = new NamespacedKey(MMOStats.getPlugin(MMOStats.class), "mmostats_gem_id");
    private static final NamespacedKey APPLIED_GEMS_KEY = new NamespacedKey(MMOStats.getPlugin(MMOStats.class), "mmostats_applied_gems");
    private static final NamespacedKey APPLIED_STATS_KEY = new NamespacedKey(MMOStats.getPlugin(MMOStats.class), "mmostats_applied_stats");

    public static void setGemId(ItemStack item, String gemId) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(GEM_ID_KEY, PersistentDataType.STRING, gemId);
            item.setItemMeta(meta);
        }
    }

    public static String getGemId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        return item.getItemMeta().getPersistentDataContainer().get(GEM_ID_KEY, PersistentDataType.STRING);
    }

    public static boolean isGem(ItemStack item) {
        return getGemId(item) != null;
    }

    public static void addAppliedGem(ItemStack item, String gemId) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            String currentAppliedGems = pdc.get(APPLIED_GEMS_KEY, PersistentDataType.STRING);
            Set<String> appliedGems = new HashSet<>();
            if (currentAppliedGems != null && !currentAppliedGems.isEmpty()) {
                appliedGems.addAll(Set.of(currentAppliedGems.split(",")));
            }
            appliedGems.add(gemId);
            pdc.set(APPLIED_GEMS_KEY, PersistentDataType.STRING, String.join(",", appliedGems));
            item.setItemMeta(meta);
        }
    }

    public static boolean hasGemApplied(ItemStack item, String gemId) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        String currentAppliedGems = pdc.get(APPLIED_GEMS_KEY, PersistentDataType.STRING);
        if (currentAppliedGems == null || currentAppliedGems.isEmpty()) {
            return false;
        }
        return Set.of(currentAppliedGems.split(",")).contains(gemId);
    }

    public static void addStatsToPDC(ItemStack item, Map<String, Double> stats) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            String currentStatsString = pdc.get(APPLIED_STATS_KEY, PersistentDataType.STRING);
            Map<String, Double> existingStats = new HashMap<>();
            if (currentStatsString != null && !currentStatsString.isEmpty()) {
                for (String entry : currentStatsString.split(";")) {
                    String[] parts = entry.split(":");
                    if (parts.length == 2) {
                        try {
                            existingStats.put(parts[0], Double.parseDouble(parts[1]));
                        } catch (NumberFormatException e) {
                            // Ignore malformed entries
                        }
                    }
                }
            }

            for (Map.Entry<String, Double> newStat : stats.entrySet()) {
                existingStats.merge(newStat.getKey(), newStat.getValue(), Double::sum);
            }

            String updatedStatsString = existingStats.entrySet().stream()
                    .map(entry -> entry.getKey() + ":" + entry.getValue())
                    .collect(Collectors.joining(";"));
            pdc.set(APPLIED_STATS_KEY, PersistentDataType.STRING, updatedStatsString);
            item.setItemMeta(meta);
        }
    }

    public static Map<String, Double> getAppliedStats(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return new HashMap<>();
        }
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        String currentStatsString = pdc.get(APPLIED_STATS_KEY, PersistentDataType.STRING);
        Map<String, Double> stats = new HashMap<>();
        if (currentStatsString != null && !currentStatsString.isEmpty()) {
            for (String entry : currentStatsString.split(";")) {
                String[] parts = entry.split(":");
                if (parts.length == 2) {
                    try {
                        stats.put(parts[0], Double.parseDouble(parts[1]));
                    } catch (NumberFormatException e) {
                        // Ignore malformed entries
                    }
                }
            }
        }
        return stats;
    }
}
