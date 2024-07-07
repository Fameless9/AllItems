package net.fameless.allitems.command;

import com.google.gson.JsonElement;
import net.fameless.allitems.AllItems;
import net.fameless.allitems.game.DataFile;
import net.fameless.allitems.util.Format;
import net.fameless.allitems.util.ItemBuilder;
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
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatsCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command.");
            return false;
        }
        if (!AllItems.getInstance().getConfig().getBoolean("enable_stats")) {
            sender.sendMessage(ChatColor.RED + "Stats are disabled.");
            return false;
        }
        new GUI((Player) sender, 1);
        return false;
    }

    static class GUI implements InventoryHolder {
        private final Inventory gui;

        public GUI(Player player, int page) {
            gui = Bukkit.createInventory(this, 54, "Results | Page " + page);

            List<Material> finishedItems = new ArrayList<>();

            for (Map.Entry<String, JsonElement> entry : DataFile.getItemObject().entrySet()) {
                if (entry.getValue().getAsBoolean()) {
                    finishedItems.add(Material.getMaterial(entry.getKey()));
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

            leftMeta.getPersistentDataContainer().set(AllItems.pageKey, PersistentDataType.INTEGER, page);
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
            player.closeInventory();
            player.openInventory(gui);
        }

        @NotNull
        @Override
        public Inventory getInventory() {
            return gui;
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

                    if (object instanceof Material material) {
                        newObjectives.add(ItemBuilder.buildItem(new ItemStack(material),
                                ChatColor.GRAY + "Item: " + ChatColor.BLUE +
                                        Format.formatItemName(material.name().replace("_", " ")),
                                        true,"", ChatColor.DARK_GRAY + "Time: " + (AllItems.getInstance().getConfig().get(
                                                "ignore." + material.name()) != null ? ChatColor.BLUE + Format.formatTime(AllItems.getInstance().getConfig().getInt(
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

    @SuppressWarnings("ConstantConditions") // Ignore the null check for page key, as item at slot 0 contains page
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof GUI)) return;
        if (event.getCurrentItem() == null) return;

        int page = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(AllItems.pageKey, PersistentDataType.INTEGER);

        if (event.getRawSlot() == 0 && event.getCurrentItem().getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
            new GUI((Player) event.getWhoClicked(), page - 1);
        } else if (event.getRawSlot() == 8 && event.getCurrentItem().getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
            new GUI((Player) event.getWhoClicked(), page + 1);
        }
        event.setCancelled(true);
    }
}