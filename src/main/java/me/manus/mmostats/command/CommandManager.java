package me.manus.mmostats.command;

import me.manus.mmostats.MMOStats;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final MMOStats plugin;

    public CommandManager(MMOStats plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mmostats.admin")) {
            plugin.getMessageManager().sendMessage(sender, "no-permission");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessageManager().sendMessage(sender, "invalid-usage-main");
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "give":
                return new GiveCommand(plugin).onCommand(sender, command, label, args);
            case "reload":
                return new ReloadCommand(plugin).onCommand(sender, command, label, args);
            default:
                plugin.getMessageManager().sendMessage(sender, "invalid-usage-main");
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("mmostats.admin")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            return Arrays.asList("give", "reload").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        }

        if (args.length > 1) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "give":
                    return new GiveCommand(plugin).onTabComplete(sender, command, alias, args);
                // Reload command has no further arguments for tab completion
            }
        }
        return new ArrayList<>();
    }
}
