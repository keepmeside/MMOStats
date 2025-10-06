package me.manus.mmostats.config;

import java.util.List;
import java.util.Map;

public class GemConfig {
    private final String id;
    private final String name;
    private final String material;
    private final String type;
    private final Map<String, Double> stats;
    private final List<String> lore;

    public GemConfig(String id, String name, String material, String type, Map<String, Double> stats, List<String> lore) {
        this.id = id;
        this.name = name;
        this.material = material;
        this.type = type;
        this.stats = stats;
        this.lore = lore;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getMaterial() { return material; }
    public String getType() { return type; }
    public Map<String, Double> getStats() { return stats; }
    public List<String> getLore() { return lore; }
}
