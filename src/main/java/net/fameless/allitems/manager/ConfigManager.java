package net.fameless.allitems.manager;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class ConfigManager {

    public ConfigManager(FileConfiguration configuration) {
        if (!configuration.contains("enable_playerstats")) {
            configuration.set("enable_playerstats", true);
        }
        if (!configuration.contains("enable_stats")) {
            configuration.set("enable_stats", true);
        }
        if (!configuration.contains("ignore.time")) {
            configuration.set("ignore.time", 0);
        }
        if (!configuration.contains("ignore.gradient_enabled")) {
            configuration.set("ignore.gradient_enabled", true);
        }
        if (!configuration.contains("ignore.gradient_speed")) {
            configuration.set("ignore.gradient_speed", 3);
        }
        if (!configuration.contains("exclude.items")) {
            configuration.set("exclude.items", new ArrayList<>());
        }
    }
}
