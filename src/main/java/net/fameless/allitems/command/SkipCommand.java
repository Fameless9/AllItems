package net.fameless.allitems.command;

import net.fameless.allitems.manager.BossbarManager;
import net.fameless.allitems.manager.ItemManager;
import net.fameless.allitems.util.Format;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkipCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("allitems.skip")) {
            sender.sendMessage(ChatColor.RED + "Lacking permission: 'allitems.skip'");
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command.");
            return false;
        }
        if (ItemManager.getNextItem().equals("None")) {
            sender.sendMessage(ChatColor.RED + "No item left to skip.");
            return false;
        }
        Bukkit.broadcastMessage(ChatColor.BLUE + sender.getName() + ChatColor.GRAY + " skipped " +
                Format.formatItemName(ItemManager.getCurrentItem().name().replace("_", " ")));
        ItemManager.removeMaterial(ItemManager.getCurrentItem());
        ItemManager.updateItem();
        BossbarManager.updateBossbar();

        return false;
    }
}
