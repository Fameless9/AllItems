package net.fameless.allitems;

import net.fameless.allitems.game.GameListener;
import net.fameless.allitems.game.ItemFile;
import net.fameless.allitems.game.SkipCommand;
import net.fameless.allitems.game.StatsCommand;
import net.fameless.allitems.manager.ItemManager;
import net.fameless.allitems.timer.Timer;
import net.fameless.allitems.timer.TimerTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class AllItems extends JavaPlugin {

    private static AllItems instance;
    Timer timer;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {

        saveDefaultConfig();

        try {
            ItemFile.init();
        } catch (IOException e) {
            Bukkit.getLogger().severe("[All Items] Failed to create File. Shutting down.");
            Bukkit.getLogger().severe("[All Items] Please contact me on Discord for help.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        ItemManager.loadToList();

        timer = new Timer(false, getConfig().getInt("ignore.time"));
        StatsCommand statsCommand = new StatsCommand();

        Bukkit.getPluginManager().registerEvents(new GameListener(), this);
        Bukkit.getPluginManager().registerEvents(statsCommand, this);

        getCommand("skip").setExecutor(new SkipCommand());
        getCommand("timer").setExecutor(timer);
        getCommand("stats").setExecutor(statsCommand);

        getCommand("timer").setTabCompleter(new TimerTabCompleter());

        Bukkit.getLogger().info("[All Items] Successfully started.");
        Bukkit.getLogger().info("[All Items] Thanks for using my plugin.");
    }

    @Override
    public void onDisable() {
    }

    public static AllItems getInstance() { return instance; }
    public Timer getTimer() { return timer; }
}
