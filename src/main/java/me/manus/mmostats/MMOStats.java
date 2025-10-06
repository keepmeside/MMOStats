package me.manus.mmostats;

import me.manus.mmostats.command.CommandManager;
import me.manus.mmostats.config.ConfigManager;
import me.manus.mmostats.config.MessageManager;
import me.manus.mmostats.hook.HeadDatabaseHook;
import me.manus.mmostats.hook.MMOItemsHook;
import me.manus.mmostats.listener.InventoryListener;
import me.manus.mmostats.service.GemApplicationService;
import me.manus.mmostats.service.GemRegistry;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class MMOStats extends JavaPlugin {

    private ConfigManager configManager;
    private MessageManager messageManager;
    private GemRegistry gemRegistry;
    private GemApplicationService gemApplicationService;
    private MMOItemsHook mmoItemsHook;
    private HeadDatabaseHook headDatabaseHook;

    @Override
    public void onEnable() {
        // Initialize hooks first
        mmoItemsHook = new MMOItemsHook(this);
        headDatabaseHook = new HeadDatabaseHook(this);

        // Initialize managers and services
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        gemRegistry = new GemRegistry(this);
        gemApplicationService = new GemApplicationService(this);

        // Load configurations
        reload();

        // Register listeners and commands
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        Objects.requireNonNull(getCommand("mmostats")).setExecutor(new CommandManager(this));
        Objects.requireNonNull(getCommand("mmostats")).setTabCompleter(new CommandManager(this));

        getLogger().info("MMOStats has been enabled successfully.");
    }

    @Override
    public void onDisable() {
        getLogger().info("MMOStats has been disabled.");
    }

    public boolean reload() {
        try {
            configManager.reload();
            messageManager.reload();
            gemRegistry.loadGems();
            return true;
        } catch (Exception e) {
            getLogger().severe("Failed to reload MMOStats configuration.");
            e.printStackTrace();
            return false;
        }
    }

    // --- Getters ---
    public ConfigManager getConfigManager() { return configManager; }
    public MessageManager getMessageManager() { return messageManager; }
    public GemRegistry getGemRegistry() { return gemRegistry; }
    public GemApplicationService getGemApplicationService() { return gemApplicationService; }
    public MMOItemsHook getMmoItemsHook() { return mmoItemsHook; }
    public HeadDatabaseHook getHeadDatabaseHook() { return headDatabaseHook; }
}
