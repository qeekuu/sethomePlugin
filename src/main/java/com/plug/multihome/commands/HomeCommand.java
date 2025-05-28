package com.plug.multihome.commands;

import com.plug.multihome.MultiHomePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HomeCommand implements CommandExecutor, TabCompleter {
    private final MultiHomePlugin plugin;

    public HomeCommand(MultiHomePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Tylko gracze mogą używać tej komendy.");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage("Użycie: /home <nazwa>");
            return true;
        }
        Player player = (Player) sender;
        String name = args[0].toLowerCase();
        FileConfiguration cfg = plugin.getHomesConfig();
        String path = "players." + player.getUniqueId() + "." + name;
        if (!cfg.contains(path)) {
            player.sendMessage("Dom o nazwie '" + name + "' nie istnieje.");
            return true;
        }
        Location loc = new Location(
                Bukkit.getWorld(cfg.getString(path + ".world")),
                cfg.getDouble(path + ".x"),
                cfg.getDouble(path + ".y"),
                cfg.getDouble(path + ".z"),
                (float)cfg.getDouble(path + ".yaw"),
                (float)cfg.getDouble(path + ".pitch")
        );
        player.teleport(loc);
        player.sendMessage("Teleportowano do domu '" + name + "'.");
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
