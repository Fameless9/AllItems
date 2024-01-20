package net.fameless.allitems.game;

import com.google.gson.JsonPrimitive;
import net.fameless.allitems.AllItems;
import net.fameless.allitems.manager.BossbarManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StatsCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command.");
            return false;
        }
        new GUI((Player) sender, 1);

        return false;
    }


    static class GUI {
        public GUI(Player player, int page) {

            Inventory gui = Bukkit.createInventory(null, 54, "Results | Page " + page);

            List<Material> finishedItems = new ArrayList<>();

            for (Map.Entry entry : ItemFile.getItemObject().entrySet()) {
                if (((JsonPrimitive) entry.getValue()).getAsBoolean()) {
                    finishedItems.add(Material.getMaterial(entry.getKey().toString()));
                }
            }

            ItemStack left;
            ItemMeta leftMeta;
            if (PageUtil.isPageValid(finishedItems, page - 1, 52)) {
                left = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                leftMeta = left.getItemMeta();
                leftMeta.setDisplayName(ChatColor.GREEN + "Go page left!");
            } else {
                left = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                leftMeta = left.getItemMeta();
                leftMeta.setDisplayName(ChatColor.RED + "Can't go left!");
            }

            leftMeta.setLocalizedName(page + "");
            left.setItemMeta(leftMeta);

            ItemStack right;
            ItemMeta rightMeta;

            if (PageUtil.isPageValid(finishedItems, page + 1, 52)) {
                right = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                rightMeta = right.getItemMeta();
                rightMeta.setDisplayName(ChatColor.GREEN + "Go page right!");
            } else {
                right = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                rightMeta = right.getItemMeta();
                rightMeta.setDisplayName(ChatColor.RED + "Can't go right!");
            }

            right.setItemMeta(rightMeta);
            gui.setItem(0, left);
            gui.setItem(8, right);

            for (ItemStack itemStack : PageUtil.getPageItems(finishedItems, page, 52)) {
                gui.addItem(itemStack);
            }
            player.openInventory(gui);
        }
    }

    static class PageUtil {
        public static List<ItemStack> getPageItems(List<Material> items, int page, int spaces) {
            int startIndex = (page - 1) * spaces;
            int endIndex = Math.min(startIndex + spaces, items.size());

            List<ItemStack> newObjectives = new ArrayList<>();

            for (int i = startIndex; i < endIndex; i++) {
                if (i >= 0 && i < items.size()) {
                    Object object = items.get(i);

                    if (object instanceof Material) {
                        Material material = (Material) object;
                        newObjectives.add(buildItem(new ItemStack(material),
                                ChatColor.GRAY + "Item: " + ChatColor.BLUE +
                                        BossbarManager.formatItemName(material.name().replace("_", " ")),
                                        "", ChatColor.DARK_GRAY + "Time: " + (AllItems.getInstance().getConfig().get(
                                                "ignore." + material.name()) != null ? ChatColor.BLUE + toFormatted(AllItems.getInstance().getConfig().getInt(
                                                        "ignore." + material.name())) : ChatColor.GRAY + "N/A")));
                    }
                }
            }
            return newObjectives;
        }

        public static boolean isPageValid(List<Material> items, int page, int spaces) {
            if (page <= 0) return false;

            int upperBound = page * spaces;
            int lowerBound = upperBound - spaces;

            return items.size() > lowerBound;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().contains("Results")) return;
        if (event.getCurrentItem() == null) return;

        int page = Integer.parseInt(event.getInventory().getItem(0).getItemMeta().getLocalizedName());

        if (event.getRawSlot() == 0 && event.getCurrentItem().getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
            new GUI((Player) event.getWhoClicked(), page - 1);
        } else if (event.getRawSlot() == 8 && event.getCurrentItem().getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
            new GUI((Player) event.getWhoClicked(), page + 1);
        }
        event.setCancelled(true);
    }

    private static ItemStack buildItem(ItemStack item, String name, String... lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        List<String> lores = new ArrayList<>();
        Collections.addAll(lores, lore);

        meta.setLore(lores);
        item.setItemMeta(meta);
        return item;
    }

    private static String toFormatted(int time) {
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
}