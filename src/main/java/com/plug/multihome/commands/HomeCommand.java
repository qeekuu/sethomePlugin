package com.plug.multihome.commands;

import com.plug.multihome.MultiHomePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
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
        String homeName = args[0].toLowerCase();

        FileConfiguration cfg = plugin.getHomesConfig();
        String path = "players." + player.getUniqueId() + "." + homeName;
        if (!cfg.contains(path)) {
            player.sendMessage("Dom o nazwie '" + homeName + "' nie istnieje.");
            return true;
        }

        String world = cfg.getString(path + ".world");
        double x = cfg.getDouble(path + ".x");
        double y = cfg.getDouble(path + ".y");
        double z = cfg.getDouble(path + ".z");
        float yaw = (float) cfg.getDouble(path + ".yaw");
        float pitch = (float) cfg.getDouble(path + ".pitch");

        Location loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        player.teleport(loc);
        player.sendMessage("Teleportowano do domu '" + homeName + "'.");
        return true;
    }

	// podpowiedzi
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player) || args.length != 1) {
            return Collections.emptyList();
        }

        Player p = (Player) sender;
        FileConfiguration cfg = plugin.getHomesConfig();
        ConfigurationSection sec = cfg.getConfigurationSection("players." + p.getUniqueId());
        if (sec == null) {
            return Collections.emptyList();
        }

        String prefix = args[0].toLowerCase();
        return sec.getKeys(false).stream()
                  .filter(name -> name.startsWith(prefix))
                  .collect(Collectors.toList());
    }
}
