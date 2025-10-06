package me.manus.mmostats.command;

import me.manus.mmostats.MMOStats;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final MMOStats plugin;

    public ReloadCommand(MMOStats plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (plugin.reload()) {
            plugin.getMessageManager().sendMessage(sender, "reload-ok");
        } else {
            plugin.getMessageManager().sendMessage(sender, "reload-fail");
        }
        return true;
    }
}
