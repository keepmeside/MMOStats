package me.manus.mmostats.hook;

import me.manus.mmostats.MMOStats;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
// import com.arcaniax.headatabase.api.HeadDatabaseAPI; // Not available at compile time

public class HeadDatabaseHook {

    private final MMOStats plugin;
    private Object api; // HeadDatabaseAPI - not available at compile time
    private boolean hooked = false;

    public HeadDatabaseHook(MMOStats plugin) {
        this.plugin = plugin;
        if (Bukkit.getPluginManager().getPlugin("HeadDatabase") != null) {
            try {
                // Use reflection to create HeadDatabaseAPI instance
                Class<?> apiClass = Class.forName("com.arcaniax.headatabase.api.HeadDatabaseAPI");
                this.api = apiClass.getDeclaredConstructor().newInstance();
                this.hooked = true;
                plugin.getLogger().info("Hooked into HeadDatabase!");
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to hook into HeadDatabase: " + e.getMessage());
                this.hooked = false;
            }
        } else {
            plugin.getLogger().info("HeadDatabase not found, skipping integration.");
        }
    }

    public boolean isHooked() {
        return hooked;
    }

    public ItemStack getHead(String id) {
        if (!hooked || api == null) {
            return null;
        }
        try {
            // Use reflection to call getItemHead method
            return (ItemStack) api.getClass().getMethod("getItemHead", String.class).invoke(api, id);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get head with ID " + id + " from HeadDatabase: " + e.getMessage());
            return null;
        }
    }
}
