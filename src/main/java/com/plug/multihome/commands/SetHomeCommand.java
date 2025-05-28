package com.plug.multihome.commands;
import com.plug.multihome.MultiHomePlugin;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

public class SetHomeCommand implements CommandExecutor {
    private final MultiHomePlugin plugin;

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
            sender.sendMessage("Użycie: /setmultihome <nazwa>");
            return true;
        }
        Player player = (Player) sender;
        String homeName = args[0].toLowerCase();
        Location loc = player.getLocation();

        FileConfiguration cfg = plugin.getHomesConfig();
        String path = "players." + player.getUniqueId() + "." + homeName;
        cfg.set(path + ".world", loc.getWorld().getName());
        cfg.set(path + ".x", loc.getX());
        cfg.set(path + ".y", loc.getY());
        cfg.set(path + ".z", loc.getZ());
        cfg.set(path + ".yaw", loc.getYaw());
        cfg.set(path + ".pitch", loc.getPitch());
        plugin.saveHomesConfig();

        player.sendMessage("Ustawiono dom '" + homeName + "'.");
        return true;
    }
}
