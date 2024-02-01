package net.fameless.allitems.command;

import net.fameless.allitems.manager.BossbarManager;
import net.fameless.allitems.manager.ItemManager;
import net.fameless.allitems.util.Format;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SkipCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("allitems.skip")) {
            sender.sendMessage(Component.text("Lacking permission: 'allitems.skip'", NamedTextColor.RED));
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players may use this command.", NamedTextColor.RED));
            return false;
        }
        if (ItemManager.getNextItem().equals("None")) {
            sender.sendMessage(Component.text("No item left to skip.", NamedTextColor.RED));
            return false;
        }
        Bukkit.broadcast(Component.text(sender.getName(), NamedTextColor.BLUE).append(Component.text(" skipped " +
                Format.formatItemName(ItemManager.getCurrentItem().name().replace("_", " ")), NamedTextColor.GRAY)));
        ItemManager.removeMaterial(ItemManager.getCurrentItem());
        ItemManager.updateItem();
        BossbarManager.updateBossbar();

        return false;
    }
}
