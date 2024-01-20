package net.fameless.allitems.timer;

import net.fameless.allitems.AllItems;
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

    public Timer(boolean running, int time) {
        this.running = running;
        this.time = time;
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

    public void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                sendActionbar();
                if (isRunning()) {
                    setTime(getTime() + 1);
                    AllItems.getInstance().getConfig().set("ignore.time", getTime());
                    AllItems.getInstance().saveConfig();
                }
            }
        }.runTaskTimer(AllItems.getInstance(), 0, 20);
    }

    private void sendActionbar() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!isRunning()) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.BLUE.toString() + ChatColor.ITALIC + ChatColor.BOLD + toFormatted(getTime())));
            } else {
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
            sender.sendMessage(ChatColor.GRAY + "Usage: /timer <toggle|set> <time>.");
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
                    sender.sendMessage(ChatColor.GRAY + "Usage: /timer <toggle|set> <time>.");
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
        }

        return false;
    }
}
