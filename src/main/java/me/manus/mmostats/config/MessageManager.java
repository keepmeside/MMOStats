package me.manus.mmostats.config;

import me.manus.mmostats.MMOStats;
import me.manus.mmostats.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MessageManager {

    private final MMOStats plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;
    private String prefix;

    public MessageManager(MMOStats plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        this.prefix = messagesConfig.getString("prefix", "<gray>[<aqua>MMOStats</aqua>]</gray> ");
    }

    public void sendMessage(CommandSender sender, String key, Object... placeholders) {
        String message = messagesConfig.getString(key);
        if (message == null) {
            plugin.getLogger().warning("Message key '" + key + "' not found in messages.yml");
            return;
        }

        // Replace prefix placeholder first
        message = message.replace("{prefix}", prefix);

        // Replace custom placeholders
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                String placeholder = "{" + placeholders[i].toString() + "}";
                String value = placeholders[i+1].toString();
                message = message.replace(placeholder, value);
            }
        }

        sender.sendMessage(TextUtil.parse(message));
    }

    public Component getComponent(String key, Object... placeholders) {
        String message = messagesConfig.getString(key);
        if (message == null) {
            plugin.getLogger().warning("Message key '" + key + "' not found in messages.yml");
            return Component.text("Error: Message key not found");
        }

        // Replace prefix placeholder first
        message = message.replace("{prefix}", prefix);

        // Replace custom placeholders
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                String placeholder = "{" + placeholders[i].toString() + "}";
                String value = placeholders[i+1].toString();
                message = message.replace(placeholder, value);
            }
        }
        return TextUtil.parse(message);
    }
}
