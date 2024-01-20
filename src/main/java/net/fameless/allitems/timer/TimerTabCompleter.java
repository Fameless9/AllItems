package net.fameless.allitems.timer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimerTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            String[] array = new String[]{"toggle", "set", "gradient", "gradientspeed"};
            return StringUtil.copyPartialMatches(args[0], Arrays.asList(array), new ArrayList<>());
        }
        return null;
    }
}
