package com.plug.multihome.commands;

import com.plug.multihome.MultiHomePlugin;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DeleteHomeCommand implements CommandExecutor, TabCompleter {
    private final MultiHomePlugin plugin;

    public DeleteHomeCommand(MultiHomePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Tylko gracze mogą usuwać domy.");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage("Użycie: /delhome <nazwa>");
            return true;
        }
        Player player = (Player) sender;
        String name = args[0].toLowerCase();
        FileConfiguration cfg = plugin.getHomesConfig();
        String base = "players." + player.getUniqueId();
        String path = base + "." + name;
        if (!cfg.contains(path)) {
            player.sendMessage("Dom '" + name + "' nie istnieje.");
            return true;
        }
        cfg.set(path, null);
        plugin.saveHomesConfig();
        player.sendMessage("Usunięto dom '" + name + "'.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player) || args.length != 1) return Collections.emptyList();
        Player p = (Player) sender;
        FileConfiguration cfg = plugin.getHomesConfig();
        String base = "players." + p.getUniqueId();
        ConfigurationSection sec = cfg.getConfigurationSection(base);
        if (sec == null) return Collections.emptyList();
        String pref = args[0].toLowerCase();
        return sec.getKeys(false).stream().filter(k -> k.startsWith(pref)).collect(Collectors.toList());
    }
}
