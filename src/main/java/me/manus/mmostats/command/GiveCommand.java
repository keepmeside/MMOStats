package me.manus.mmostats.command;

import me.manus.mmostats.MMOStats;
import me.manus.mmostats.config.GemConfig;
import me.manus.mmostats.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class GiveCommand implements CommandExecutor, TabCompleter {

    private final MMOStats plugin;

    public GiveCommand(MMOStats plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 4) {
            plugin.getMessageManager().sendMessage(sender, "invalid-usage-give");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            plugin.getMessageManager().sendMessage(sender, "player-not-found", "player", args[1]);
            return true;
        }

        String gemId = args[2].toLowerCase(Locale.ROOT);
        GemConfig gemConfig = plugin.getGemRegistry().getGem(gemId);
        if (gemConfig == null) {
            plugin.getMessageManager().sendMessage(sender, "gem-not-found", "id", gemId);
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
            if (amount <= 0) {
                plugin.getMessageManager().sendMessage(sender, "invalid-amount");
                return true;
            }
        } catch (NumberFormatException e) {
            plugin.getMessageManager().sendMessage(sender, "invalid-amount");
            return true;
        }

        ItemStack gemItem = ItemUtil.createGemItem(plugin, gemConfig, amount);
        targetPlayer.getInventory().addItem(gemItem);

        plugin.getMessageManager().sendMessage(sender, "given-gem",
                "amount", String.valueOf(amount),
                "gem", gemConfig.getName(),
                "player", targetPlayer.getName());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        }
        if (args.length == 3) {
            return plugin.getGemRegistry().getGemIds().stream()
                    .filter(id -> id.toLowerCase(Locale.ROOT).startsWith(args[2].toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        }
        if (args.length == 4) {
            return List.of("1", "5", "10", "64");
        }
        return new ArrayList<>();
    }
}
