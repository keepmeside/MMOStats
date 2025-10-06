package me.manus.mmostats.hook;

import me.manus.mmostats.MMOStats;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import com.arcaniax.headatabase.api.HeadDatabaseAPI;

public class HeadDatabaseHook {

    private final MMOStats plugin;
    private HeadDatabaseAPI api;
    private boolean hooked = false;

    public HeadDatabaseHook(MMOStats plugin) {
        this.plugin = plugin;
        if (Bukkit.getPluginManager().getPlugin("HeadDatabase") != null) {
            this.api = new HeadDatabaseAPI();
            this.hooked = true;
            plugin.getLogger().info("Hooked into HeadDatabase!");
        } else {
            plugin.getLogger().info("HeadDatabase not found, skipping integration.");
        }
    }

    public boolean isHooked() {
        return hooked;
    }

    public ItemStack getHead(String id) {
        if (!hooked) {
            return null;
        }
        try {
            return api.getItemHead(id);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get head with ID " + id + " from HeadDatabase: " + e.getMessage());
            return null;
        }
    }
}
