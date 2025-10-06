package me.manus.mmostats.config;

import me.manus.mmostats.MMOStats;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigManager {

    private final MMOStats plugin;
    private FileConfiguration config;

    private String language;
    private String colorMode;
    private Sound successSound;
    private Sound failSound;
    private String successParticle;
    private String failParticle;
    private boolean preventDuplicates;
    private final Map<String, Double> statCaps = new HashMap<>();
    private final Map<String, List<String>> typeMapping = new HashMap<>();

    public ConfigManager(MMOStats plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        this.language = config.getString("language", "en_US");
        this.colorMode = config.getString("color-mode", "mini_message");
        this.preventDuplicates = config.getBoolean("prevent-duplicates", true);

        try {
            this.successSound = Sound.valueOf(config.getString("sounds.success", "ENTITY_PLAYER_LEVELUP").toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid success sound in config.yml: " + config.getString("sounds.success") + ". Defaulting to ENTITY_PLAYER_LEVELUP.");
            this.successSound = Sound.ENTITY_PLAYER_LEVELUP;
        }
        try {
            this.failSound = Sound.valueOf(config.getString("sounds.fail", "ENTITY_VILLAGER_NO").toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid fail sound in config.yml: " + config.getString("sounds.fail") + ". Defaulting to ENTITY_VILLAGER_NO.");
            this.failSound = Sound.ENTITY_VILLAGER_NO;
        }

        this.successParticle = config.getString("particles.success", "HAPPY_VILLAGER");
        this.failParticle = config.getString("particles.fail", "SMOKE");

        loadStatCaps();
        loadTypeMapping();
    }

    private void loadStatCaps() {
        statCaps.clear();
        ConfigurationSection section = config.getConfigurationSection("stat-caps");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                statCaps.put(key.toUpperCase(), section.getDouble(key));
            }
        }
    }

    private void loadTypeMapping() {
        typeMapping.clear();
        ConfigurationSection section = config.getConfigurationSection("type-mapping");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                List<String> materials = section.getStringList(key).stream()
                        .map(String::toUpperCase)
                        .collect(Collectors.toList());
                typeMapping.put(key.toUpperCase(), materials);
            }
        }
    }

    public String getLanguage() { return language; }
    public String getColorMode() { return colorMode; }
    public Sound getSuccessSound() { return successSound; }
    public Sound getFailSound() { return failSound; }
    public boolean isPreventDuplicates() { return preventDuplicates; }
    public String getSuccessParticle() { return successParticle; }
    public String getFailParticle() { return failParticle; }

    public double getStatCap(String stat) {
        return statCaps.getOrDefault(stat.toUpperCase(), -1.0);
    }

    public Map<String, List<String>> getTypeMapping() { return typeMapping; }

    public Set<String> getGemIds() {
        ConfigurationSection gemsSection = config.getConfigurationSection("gems");
        return gemsSection != null ? gemsSection.getKeys(false) : new java.util.HashSet<>();
    }

    public ConfigurationSection getGemSection(String gemId) {
        return config.getConfigurationSection("gems." + gemId);
    }
}
