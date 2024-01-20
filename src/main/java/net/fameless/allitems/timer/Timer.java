package net.fameless.allitems.timer;

import net.fameless.allitems.AllItems;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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

    private int progress = 1;
    private boolean inverted = false;
    private int speed;
    private boolean isGradientEnabled;

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
                    progress = progress + speed;
                }
            }
        }.runTaskTimer(AllItems.getInstance(), 0, 1);
    }

    private final MiniMessage serializer = MiniMessage.miniMessage();

    private void sendActionbar() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Audience audience = (Audience) p;
            if (!isRunning()) {
                if (isGradientEnabled) {
                    Component parsed;
                    if (inverted) {
                        parsed = serializer.deserialize("<bold><italic><gradient:#8a4fff:blue:" + (double) progress / 100 + ">Timer paused</gradient>");
                    } else {
                        parsed = serializer.deserialize("<bold><italic><gradient:blue:#8a4fff:" + (double) progress / 100 + ">Timer paused</gradient>");
                    }
                    audience.sendActionBar(parsed);
                    return;
                }
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.BLUE.toString() + ChatColor.BOLD + ChatColor.ITALIC + "Timer paused"));
            } else {
                if (isGradientEnabled) {
                    Component parsed;
                    if (inverted) {
                        parsed = serializer.deserialize("<bold><gradient:#8a4fff:blue:" + (double) progress / 100 + ">" + toFormatted(getTime()) + "</gradient>");
                    } else {
                        parsed = serializer.deserialize("<bold><gradient:blue:#8a4fff:" + (double) progress / 100 + ">" + toFormatted(getTime()) + "</gradient>");
                    }
                    audience.sendActionBar(parsed);
                    return;
                }
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.BLUE.toString() + ChatColor.BOLD + toFormatted(getTime())));
            }
        }
    }


    private String toFormatted(int time) {
        int days = time / 86400;
        int hours = time / 3600 % 24;
        int minutes = time / 60 % 60;
        int seconds = time % 60;

        StringBuilder message = new StringBuilder();

        if (days >= 1) {
            message.append(days).append("d ");
        }
        if (hours >= 1) {
            message.append(hours).append("h ");
        }
        if (minutes >= 1) {
            message.append(minutes).append("m ");
        }
        if (seconds >= 1) {
            message.append(seconds).append("s ");
        }
        if (time == 0) {
            message.append("0s");
        }
        return String.valueOf(message);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.GRAY + "Usage: /timer <toggle|set|gradientspeed|gradient> <time|speed>.");
            return false;
        }

        switch (args[0]) {
            case "toggle": {
                if (isRunning()) {
                    setRunning(false);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendTitle(ChatColor.BLUE + "Timer paused.", "", 10, 40, 10);
                    }
                } else {
                    setRunning(true);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendTitle(ChatColor.BLUE + "Timer started.", "", 10, 40, 10);
                    }
                }
                break;
            }
            case "set": {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.GRAY + "Usage: /timer <toggle|set|gradientspeed|gradient> <time|speed>.");
                    return false;
                }
                int newTime;
                try {
                    newTime = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.GRAY + "Time must be a number.");
                    return false;
                }
                setRunning(false);
                setTime(newTime);
                Bukkit.broadcastMessage(ChatColor.BLUE + "Timer set to " + newTime + " seconds.");
                break;
            }
            case "gradientspeed": {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.GRAY + "Usage: /timer <toggle|set|gradientspeed|gradient> <time|speed>.");
                    return false;
                }
                int newSpeed;
                try {
                    newSpeed = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.GRAY + "Speed must be a number between 1 and 5.");
                    return false;
                }
                if (newSpeed < 1 || newSpeed > 5) {
                    sender.sendMessage(ChatColor.GRAY + "Usage: /timer <toggle|set|gradientspeed|gradient> <time|speed>.");
                    return false;
                }
                speed = newSpeed;
                AllItems.getInstance().getConfig().set("ignore.gradient_speed", newSpeed);
                AllItems.getInstance().saveConfig();
                break;
            }
            case "gradient": {
                isGradientEnabled = !isGradientEnabled;
                if (isGradientEnabled) {
                    Bukkit.broadcastMessage(ChatColor.BLUE + "Gradient has been toggled on.");
                    AllItems.getInstance().getConfig().set("ignore.gradient_enabled", true);
                    AllItems.getInstance().saveConfig();
                    return false;
                }
                Bukkit.broadcastMessage(ChatColor.BLUE + "Gradient has been toggled off.");
                AllItems.getInstance().getConfig().set("ignore.gradient_enabled", false);
                AllItems.getInstance().saveConfig();
                break;
            }
            default: {
                sender.sendMessage(ChatColor.GRAY + "Usage: /timer <toggle|set|gradientspeed|gradient> <time|speed>.");
            }
        }

        return false;
    }
}
