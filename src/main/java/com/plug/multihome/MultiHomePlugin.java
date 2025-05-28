package com.plug.multihome;

import com.plug.multihome.commands.SetHomeCommand;
import com.plug.multihome.commands.HomeCommand;
import com.plug.multihome.commands.DeleteHomeCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class MultiHomePlugin extends JavaPlugin {
    private File homesFile;
    private FileConfiguration homesConfig;

    @Override
    public void onEnable() {
		// Utwórz plik homes.yml, jeśli nie istnieje
		if (!getDataFolder().exists()) getDataFolder().mkdirs();
			homesFile = new File(getDataFolder(), "homes.yml");
			if (!homesFile.exists()) {
				saveResource("homes.yml", false);
			}
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);

        // rejestracja komend
        SetHomeCommand set = new SetHomeCommand(this);
        getCommand("sethome").setExecutor(set);
        // getCommand("sethome").setTabCompleter(set);

        HomeCommand home = new HomeCommand(this);
        getCommand("home").setExecutor(home);
        getCommand("home").setTabCompleter(home);

        DeleteHomeCommand del = new DeleteHomeCommand(this);
        getCommand("delhome").setExecutor(del);
        getCommand("delhome").setTabCompleter(del);

        getLogger().info("MultiHomePlugin włączony!");
    }

    @Override
    public void onDisable() {
        // Zapisz konfigurację przy wyłączaniu
        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getHomesConfig() {
        return homesConfig;
    }

    public void saveHomesConfig() {
        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
