package net.fameless.allitems;

import net.fameless.allitems.command.PlayerStatsCommand;
import net.fameless.allitems.command.SettingsCommand;
import net.fameless.allitems.command.SkipCommand;
import net.fameless.allitems.command.StatsCommand;
import net.fameless.allitems.game.DataFile;
import net.fameless.allitems.game.GameListener;
import net.fameless.allitems.manager.ConfigManager;
import net.fameless.allitems.manager.ItemManager;
import net.fameless.allitems.timer.Timer;
import net.fameless.allitems.timer.TimerTabCompleter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.time.Duration;
import java.util.Locale;
import java.util.ResourceBundle;

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
        saveResource("lang/Bundle_de_DE.properties", true);
        saveResource("lang/Bundle_fr_FR.properties", true);

        try {
            DataFile.init();
        } catch (IOException e) {
            Bukkit.getLogger().severe("[All Items] Failed to create File. Shutting down.");
            Bukkit.getLogger().severe("[All Items] Please contact me on Discord for help.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        ItemManager.loadToList();

        int time = getConfig().get("ignore.time") != null ? getConfig().getInt("ignore.time") : 0;
        boolean gradientEnabled = getConfig().get("ignore.gradient_enabled") == null || getConfig().getBoolean("ignore.gradient_enabled");
        int gradientSpeed = getConfig().get("ignore.gradient_speed") != null ? getConfig().getInt("ignore.gradient_speed") : 3;

        new ConfigManager(getConfig());
        timer = new Timer(false, time, gradientEnabled, gradientSpeed);
        StatsCommand statsCommand = new StatsCommand();
        SettingsCommand settingsCommand = new SettingsCommand();
        PlayerStatsCommand playerStatsCommand = new PlayerStatsCommand();

        TranslationRegistry germanRegistry = TranslationRegistry.create(Key.key("de"));
        TranslationRegistry frenchRegistry = TranslationRegistry.create(Key.key("fr"));
        ResourceBundle germanBundle = ResourceBundle.getBundle("Bundle", Locale.GERMANY, UTF8ResourceBundleControl.get());
        ResourceBundle frenchBundle = ResourceBundle.getBundle("Bundle", Locale.FRANCE, UTF8ResourceBundleControl.get());
        germanRegistry.registerAll(Locale.GERMANY, germanBundle, true);
        frenchRegistry.registerAll(Locale.FRANCE, frenchBundle, true);
        GlobalTranslator.translator().addSource(germanRegistry);
        GlobalTranslator.translator().addSource(frenchRegistry);

        Bukkit.getPluginManager().registerEvents(new GameListener(), this);
        Bukkit.getPluginManager().registerEvents(settingsCommand, this);
        Bukkit.getPluginManager().registerEvents(statsCommand, this);
        Bukkit.getPluginManager().registerEvents(playerStatsCommand, this);

        getCommand("skip").setExecutor(new SkipCommand());
        getCommand("playerstats").setExecutor(playerStatsCommand);
        getCommand("timer").setExecutor(timer);
        getCommand("stats").setExecutor(statsCommand);
        getCommand("settings").setExecutor(settingsCommand);

        getCommand("timer").setTabCompleter(new TimerTabCompleter());

        UpdateChecker checker = new UpdateChecker(114608, Duration.ofHours(2));
        checker.checkForUpdates();

        new Metrics(this, 20782);

        Bukkit.getLogger().info("[All Items] Successfully started.");
        Bukkit.getLogger().info("[All Items] Thanks for using my plugin.");
    }

    public static AllItems getInstance() { return instance; }
    public Timer getTimer() { return timer; }
}
