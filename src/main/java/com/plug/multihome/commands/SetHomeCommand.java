package com.plug.multihome.commands;

import com.plug.multihome.MultiHomePlugin;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SetHomeCommand implements CommandExecutor, TabCompleter {
    private final MultiHomePlugin plugin;
    private static final int MAX_HOMES = 3;

    public SetHomeCommand(MultiHomePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Tylko gracze mogą ustawiać domy.");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage("Użycie: /sethome <nazwa>");
            return true;
        }
        Player player = (Player) sender;
        String name = args[0].toLowerCase();
        FileConfiguration cfg = plugin.getHomesConfig();
        String base = "players." + player.getUniqueId();
        ConfigurationSection section = cfg.getConfigurationSection(base);
        int count = (section == null ? 0 : section.getKeys(false).size());

        if (!cfg.contains(base + "." + name) && count >= MAX_HOMES) {
            player.sendMessage("Osiągnięto limit " + MAX_HOMES + " domów. Usuń któryś za pomocą /delhome <nazwa>.");
            return true;
        }

        Location loc = player.getLocation();
        String path = base + "." + name;
        cfg.set(path + ".world", loc.getWorld().getName());
        cfg.set(path + ".x", loc.getX());
        cfg.set(path + ".y", loc.getY());
        cfg.set(path + ".z", loc.getZ());
        cfg.set(path + ".yaw", loc.getYaw());
        cfg.set(path + ".pitch", loc.getPitch());
        plugin.saveHomesConfig();

        player.sendMessage("Ustawiono dom '" + name + "'. (" + (section==null?1:count + 1) + "/" + MAX_HOMES + ")");
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
