package net.fameless.allitems.timer;

import net.fameless.allitems.AllItems;
import net.fameless.allitems.util.Format;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class Timer implements CommandExecutor {

    public Timer(boolean running, int time, boolean gradientEnabled, int gradientSpeed) {
        this.running = running;
        this.time = time;
        this.isGradientEnabled = gradientEnabled;
        this.speed = gradientSpeed;
        run();
    }

    private boolean running;
    private int time;
    private int progress = 1;
    private boolean inverted = false;
    private int speed;
    private boolean isGradientEnabled;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isGradientEnabled() {
        return isGradientEnabled;
    }

    public void setGradientEnabled(boolean gradientEnabled) {
        isGradientEnabled = gradientEnabled;
    }

    public void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().isEmpty()) return;
                if (isRunning()) {
                    setTime(getTime() + 1);
                    AllItems.getInstance().getConfig().set("ignore.time", getTime());
                    AllItems.getInstance().saveConfig();
                }
            }
        }.runTaskTimer(AllItems.getInstance(), 0, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().isEmpty()) return;
                sendActionbar();
                if (progress + speed > 100) {
                    progress = 1;
                    inverted = !inverted;
                } else {
                    progress += speed;
                }
            }
        }.runTaskTimer(AllItems.getInstance(), 0, 1);
    }

    private final MiniMessage serializer = MiniMessage.miniMessage();

    private void sendActionbar() {
        Component parsed;

        if (!isRunning()) {
            if (isGradientEnabled) {
                parsed = inverted
                        ? serializer.deserialize("<bold><italic><gradient:#8a4fff:blue:" + (double) progress / 100 + ">Timer paused</gradient>")
                        : serializer.deserialize("<bold><italic><gradient:blue:#8a4fff:" + (double) progress / 100 + ">Timer paused</gradient>");
            } else {
                parsed = Component.text("Timer paused", NamedTextColor.BLUE, TextDecoration.BOLD, TextDecoration.ITALIC);
            }
        } else {
            if (isGradientEnabled) {
                parsed = inverted
                        ? serializer.deserialize("<bold><gradient:#8a4fff:blue:" + (double) progress / 100 + ">" + Format.formatTime(getTime()) + "</gradient>")
                        : serializer.deserialize("<bold><gradient:blue:#8a4fff:" + (double) progress / 100 + ">" + Format.formatTime(getTime()) + "</gradient>");
            } else {
                parsed = Component.text(Format.formatTime(getTime()), NamedTextColor.BLUE, TextDecoration.BOLD);
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(parsed);
        }
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /timer <toggle|set|gradientspeed|gradient> <time|speed>.", NamedTextColor.GRAY));
            return false;
        }
        if (!sender.hasPermission("allitems.timer")) {
            sender.sendMessage(Component.text("Lacking permission: 'allitems.timer'", NamedTextColor.RED));
            return false;
        }

        switch (args[0]) {
            case "toggle": {
                toggle();
                break;
            }
            case "set": {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("Usage: /timer <toggle|set|gradientspeed|gradient> <time|speed>.", NamedTextColor.GRAY));
                    return false;
                }
                int newTime;
                try {
                    newTime = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(Component.text("Time must be a number.", NamedTextColor.GRAY));
                    return false;
                }
                setRunning(false);
                setTime(newTime);
                Bukkit.broadcast(Component.text("Timer set to " + newTime + " seconds.", NamedTextColor.BLUE));
                break;
            }
            case "gradientspeed": {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("Usage: /timer <toggle|set|gradientspeed|gradient> <time|speed>.", NamedTextColor.GRAY));
                    return false;
                }
                int newSpeed;
                try {
                    newSpeed = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(Component.text("Speed must be a number between 1 and 5.", NamedTextColor.GRAY));
                    return false;
                }
                if (newSpeed < 1 || newSpeed > 5) {
                    sender.sendMessage(Component.text("Usage: /timer <toggle|set|gradientspeed|gradient> <time|speed>.", NamedTextColor.GRAY));
                    return false;
                }
                speed = newSpeed;
                AllItems.getInstance().getConfig().set("ignore.gradient_speed", newSpeed);
                AllItems.getInstance().saveConfig();
                break;
            }
            case "gradient": {
                toggleGradient();
                break;
            }
            default: {
                sender.sendMessage(Component.text("Usage: /timer <toggle|set|gradientspeed|gradient> <time|speed>.", NamedTextColor.GRAY));
            }
        }

        return false;
    }

    public void toggle() {
        if (isRunning()) {
            setRunning(false);
            for (Player player : Bukkit.getOnlinePlayers()) {
                Title title = Title.title(Component.text("Timer paused", NamedTextColor.BLUE), Component.empty(), Title.Times.times(
                        Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1)));
                player.showTitle(title);
            }
        } else {
            setRunning(true);
            for (Player player : Bukkit.getOnlinePlayers()) {
                Title title = Title.title(Component.text("Timer started", NamedTextColor.BLUE), Component.empty(), Title.Times.times(
                        Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1)));
                player.showTitle(title);
            }
        }
    }

    public void toggleGradient() {
        setGradientEnabled(!isGradientEnabled());
        if (isGradientEnabled()) {
            Bukkit.broadcast(Component.text("Gradient has been toggled on.", NamedTextColor.BLUE));
            AllItems.getInstance().getConfig().set("ignore.gradient_enabled", true);
            AllItems.getInstance().saveConfig();
            return;
        }
        Bukkit.broadcast(Component.text("Gradient has been toggled off.", NamedTextColor.BLUE));
        AllItems.getInstance().getConfig().set("ignore.gradient_enabled", false);
        AllItems.getInstance().saveConfig();
    }
}
