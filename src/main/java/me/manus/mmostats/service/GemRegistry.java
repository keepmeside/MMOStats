package me.manus.mmostats.service;

import me.manus.mmostats.MMOStats;
import me.manus.mmostats.config.GemConfig;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GemRegistry {

    private final MMOStats plugin;
    private final Map<String, GemConfig> gems = new HashMap<>();

    public GemRegistry(MMOStats plugin) {
        this.plugin = plugin;
    }

    public void loadGems() {
        gems.clear();
        Set<String> gemIds = plugin.getConfigManager().getGemIds();
        for (String id : gemIds) {
            ConfigurationSection section = plugin.getConfigManager().getGemSection(id);
            if (section == null) continue;

            try {
                String name = section.getString("name", id);
                String material = section.getString("material", "STONE");
                String type = section.getString("type", "ALL");
                Map<String, Double> stats = new HashMap<>();
                ConfigurationSection statsSection = section.getConfigurationSection("stats");
                if (statsSection != null) {
                    for (String statKey : statsSection.getKeys(false)) {
                        stats.put(statKey, statsSection.getDouble(statKey));
                    }
                }
                List<String> lore = section.getStringList("lore");

                GemConfig gemConfig = new GemConfig(id, name, material, type, stats, lore);
                gems.put(id.toLowerCase(), gemConfig);
                plugin.getLogger().fine("Loaded gem: " + id);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load gem '" + id + "' from config.yml: " + e.getMessage());
                e.printStackTrace();
            }
        }
        plugin.getLogger().info("Loaded " + gems.size() + " gems.");
    }

    public GemConfig getGem(String id) {
        return gems.get(id.toLowerCase());
    }

    public Set<String> getGemIds() {
        return Collections.unmodifiableSet(gems.keySet());
    }
}
