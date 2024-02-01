package net.fameless.allitems.command;

import com.google.gson.JsonElement;
import net.fameless.allitems.AllItems;
import net.fameless.allitems.game.DataFile;
import net.fameless.allitems.util.Format;
import net.fameless.allitems.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatsCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players may use this command.", NamedTextColor.RED));
            return false;
        }
        if (!AllItems.getInstance().getConfig().getBoolean("enable_stats")) {
            sender.sendMessage(Component.text("Stats are disabled.", NamedTextColor.RED));
            return false;
        }
        new GUI((Player) sender, 1);
        return false;
    }

    static class GUI implements InventoryHolder {
        private final Inventory gui;

        public GUI(Player player, int page) {
            gui = Bukkit.createInventory(this, 54, Component.text("Results | Page " + page));

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
                leftMeta.displayName(Component.text("Go page left!", NamedTextColor.GREEN));
            } else {
                left = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                leftMeta = left.getItemMeta();
                leftMeta.displayName(Component.text("Can't go left!", NamedTextColor.RED));
            }

            leftMeta.setLocalizedName(page + "");
            left.setItemMeta(leftMeta);

            ItemStack right;
            ItemMeta rightMeta;

            if (PageUtil.isPageValid(finishedItems, page + 1, 52)) {
                right = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                rightMeta = right.getItemMeta();
                rightMeta.displayName(Component.text("Go page right!", NamedTextColor.GREEN));
            } else {
                right = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                rightMeta = right.getItemMeta();
                rightMeta.displayName(Component.text("Can't go right!", NamedTextColor.RED));
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
                    Material object = items.get(i);

                    if (object != null) {
                        ItemStack stack = ItemBuilder.buildItem(new ItemStack(object),
                                Component.text("Item: ", NamedTextColor.GRAY).append(Component.text(Format.formatItemName(object.name().replace("_", " ")))),
                                true,Component.text(""), Component.text( "Time: ", NamedTextColor.DARK_GRAY).append(
                                (AllItems.getInstance().getConfig().get("ignore." + object.name()) != null ?
                                        Component.text(Format.formatTime(AllItems.getInstance().getConfig().getInt("ignore." + object.name())), NamedTextColor.BLUE)
                                        : Component.text("N/A", NamedTextColor.GRAY))));
                        ItemMeta meta = stack.getItemMeta();
                        if (meta != null) for (ItemFlag flag : ItemFlag.values()) {
                            meta.addItemFlags(flag);
                        }
                        stack.setItemMeta(meta);
                        newObjectives.add(stack);
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
        if (!(event.getInventory().getHolder() instanceof GUI)) return;
        if (event.getCurrentItem() == null) return;

        int page = Integer.parseInt(event.getInventory().getItem(0).getItemMeta().getLocalizedName());

        if (event.getRawSlot() == 0 && event.getCurrentItem().getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
            new GUI((Player) event.getWhoClicked(), page - 1);
        } else if (event.getRawSlot() == 8 && event.getCurrentItem().getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
            new GUI((Player) event.getWhoClicked(), page + 1);
        }
        event.setCancelled(true);
    }
}