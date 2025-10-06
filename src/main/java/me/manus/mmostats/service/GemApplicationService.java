package me.manus.mmostats.service;

import me.manus.mmostats.MMOStats;
import me.manus.mmostats.config.GemConfig;
import me.manus.mmostats.util.ItemUtil;
import me.manus.mmostats.util.PDCUtil;
import me.manus.mmostats.util.TextUtil;
// MMOItems imports not available at compile time
// import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
// import net.Indyuce.mmoitems.api.player.PlayerData;
// import net.Indyuce.mmoitems.api.stat.StatMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Particle;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GemApplicationService {

    private final MMOStats plugin;

    public GemApplicationService(MMOStats plugin) {
        this.plugin = plugin;
    }

    public void attemptToApplyGem(Player player, ItemStack gemItemStack, ItemStack targetItem) {
        if (gemItemStack == null || targetItem == null || gemItemStack.getType() == Material.AIR || targetItem.getType() == Material.AIR) {
            return;
        }

        String gemId = PDCUtil.getGemId(gemItemStack);
        if (gemId == null) {
            return; // Not a valid gem
        }

        GemConfig gemConfig = plugin.getGemRegistry().getGem(gemId);
        if (gemConfig == null) {
            return; // Should not happen if PDC is correct
        }

        // 1. Compatibility Check
        if (!isCompatible(gemConfig, targetItem)) {
            String requiredType = gemConfig.getType();
            plugin.getMessageManager().sendMessage(player, "not-compatible",
                    "gem", TextUtil.getItemName(gemItemStack),
                           "item", TextUtil.getItemName(targetItem),
                           "type", requiredType);
            playSound(player, plugin.getConfigManager().getFailSound());
            playParticle(player, plugin.getConfigManager().getFailParticle());
            return;
        }

        // 2. Duplicate Check
        if (plugin.getConfigManager().isPreventDuplicates() && PDCUtil.hasGemApplied(targetItem, gemId)) {
            plugin.getMessageManager().sendMessage(player, "duplicate-disallowed",
                    "gem", TextUtil.getItemName(gemItemStack),
                           "item", TextUtil.getItemName(targetItem));
            playSound(player, plugin.getConfigManager().getFailSound());
            playParticle(player, plugin.getConfigManager().getFailParticle());
            return;
        }

        // 3. Stat Cap Check
        Map<String, Double> existingPDCStats = PDCUtil.getAppliedStats(targetItem);
        
        for (Map.Entry<String, Double> entry : gemConfig.getStats().entrySet()) {
            String stat = entry.getKey();
            double gemValue = entry.getValue();
            double currentValue = existingPDCStats.getOrDefault(stat, 0.0);
            double cap = plugin.getConfigManager().getStatCap(stat);

            if (cap != -1 && (currentValue + gemValue) > cap) {
                plugin.getMessageManager().sendMessage(player, "exceeds-cap",
                        "gem", TextUtil.getItemName(gemItemStack),
                               "stats", stat + " (Current: " + String.format("%.1f", currentValue) + ", Gem: " + String.format("%.1f", gemValue) + ", Cap: " + String.format("%.1f", cap) + ")");
                playSound(player, plugin.getConfigManager().getFailSound());
                playParticle(player, plugin.getConfigManager().getFailParticle());
                return;
            }
        }

        // All checks passed, apply the gem
        applyGem(player, gemConfig, targetItem);
        gemItemStack.setAmount(gemItemStack.getAmount() - 1);
    }

    private void applyGem(Player player, GemConfig gemConfig, ItemStack targetItem) {
        ItemStack newTargetItem = targetItem.clone();

        // Apply stats via MMOItems or PDC
        if (plugin.getMmoItemsHook().isHooked()) {
            newTargetItem = plugin.getMmoItemsHook().addStats(newTargetItem, gemConfig.getStats());
        }

        // Always store data in PDC for consistency and vanilla item support
        PDCUtil.addAppliedGem(newTargetItem, gemConfig.getId());
        PDCUtil.addStatsToPDC(newTargetItem, gemConfig.getStats());

        // Update Lore
        ItemMeta meta = newTargetItem.getItemMeta();
        List<Component> lore = meta.hasLore() ? meta.lore() : new java.util.ArrayList<>();
        if (lore == null) lore = new java.util.ArrayList<>(); // Ensure lore is not null
        
        String statsString = plugin.getMmoItemsHook().isHooked()
            ? plugin.getMmoItemsHook().getStatsString(gemConfig.getStats())
            : gemConfig.getStats().entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));

        lore.add(TextUtil.parse("<dark_gray>---</dark_gray>"));
        lore.add(TextUtil.parse("<green>Gem: " + gemConfig.getName()));
        lore.add(TextUtil.parse("<gray>" + statsString));
        meta.lore(lore);
        newTargetItem.setItemMeta(meta);

        targetItem.setItemMeta(newTargetItem.getItemMeta()); // Apply changes to the original item stack

        // Success feedback
        plugin.getMessageManager().sendMessage(player, "applied-success",
                "gem", gemConfig.getName(),
                       "item", TextUtil.getItemName(targetItem),
                       "stats", statsString);
        playSound(player, plugin.getConfigManager().getSuccessSound());
        playParticle(player, plugin.getConfigManager().getSuccessParticle());
    }

    private boolean isCompatible(GemConfig gemConfig, ItemStack targetItem) {
        List<String> allowedTypes = plugin.getConfigManager().getTypeMapping().get(gemConfig.getType().toUpperCase());
        if (allowedTypes == null || allowedTypes.isEmpty()) {
            return false;
        }
        if (allowedTypes.contains("*")) {
            return true;
        }

        // Check MMOItems type first
        if (plugin.getMmoItemsHook().isHooked()) {
            String mmoType = plugin.getMmoItemsHook().getItemType(targetItem);
            if (mmoType != null) {
                // MMOItems types are often like "SWORD", "BOW", etc. We need to check if any allowed type matches
                // the MMOItem type, potentially prefixed with "MMO_"
                for (String allowed : allowedTypes) {
                    if (allowed.equalsIgnoreCase(mmoType) || allowed.equalsIgnoreCase("MMO_" + mmoType)) {
                        return true;
                    }
                }
            }
        }

        // Fallback to vanilla material check
        String materialName = targetItem.getType().name();
        for (String allowed : allowedTypes) {
            // Check if the material name contains the allowed type (e.g., DIAMOND_SWORD contains SWORD)
            if (materialName.contains(allowed)) {
                return true;
            }
        }

        return false;
    }

    private void playSound(Player player, Sound sound) {
        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }

    private void playParticle(Player player, String particleName) {
        if (particleName != null && !particleName.isEmpty()) {
            try {
                Particle particle = Particle.valueOf(particleName.toUpperCase());
                player.spawnParticle(particle, player.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.05);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid particle name in config.yml: " + particleName + ".");
            }
        }
    }
}

