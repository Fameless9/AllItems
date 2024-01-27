package net.fameless.allitems.command;

import net.fameless.allitems.AllItems;
import net.fameless.allitems.manager.BossbarManager;
import net.fameless.allitems.manager.ItemManager;
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
import org.jetbrains.annotations.NotNull;

public class SettingsCommand implements CommandExecutor, Listener, InventoryHolder {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("allitems.settings")) {
            sender.sendMessage(ChatColor.RED + "Lacking permission: 'allitems.settings'");
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command.");
            return false;
        }

        ((Player) sender).openInventory(getSettingsInv());
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SettingsCommand)) return;
        if (event.getView().getTitle().contains("Settings")) {
            if (!event.getWhoClicked().hasPermission("allitems.settings.settings")) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Lacking permission: 'allitems.settings.settings'");
                event.getWhoClicked().closeInventory();
                return;
            }
            event.setCancelled(true);
            switch (event.getSlot()) {
                case 0: {
                    event.getWhoClicked().openInventory(getTimerInv());
                    break;
                }
                case 1: {
                    event.getWhoClicked().openInventory(getGameInv());
                    break;
                }
            }
            return;
        }
        if (event.getView().getTitle().contains("Timer settings")) {
            if (!event.getWhoClicked().hasPermission("allitems.settings.timer")) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Lacking permission: 'allitems.settings.timer'");
                event.getWhoClicked().closeInventory();
                return;
            }
            event.setCancelled(true);
            switch (event.getSlot()) {
                case 0: {
                    AllItems.getInstance().getTimer().toggle();
                    break;
                }
                case 1: {
                    AllItems.getInstance().getTimer().toggleGradient();
                    break;
                }
                case 2: {
                    int speed = AllItems.getInstance().getTimer().getSpeed();
                    int newSpeed;
                    if (speed + 1 > 5) {
                        newSpeed = 1;
                    } else {
                        newSpeed = speed + 1;
                    }
                    AllItems.getInstance().getTimer().setSpeed(newSpeed);
                    break;
                }
            }
            event.getWhoClicked().openInventory(getTimerInv());
            return;
        }
        if (event.getView().getTitle().contains("Game settings")) {
            if (!event.getWhoClicked().hasPermission("allitems.settings.game")) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Lacking permission: 'allitems.settings.game'");
                event.getWhoClicked().closeInventory();
                return;
            }
            event.setCancelled(true);
            switch (event.getSlot()) {
                case 0: {
                    if (ItemManager.getNextItem().equals("None")) {
                        event.getWhoClicked().sendMessage(ChatColor.RED + "No item left to skip.");
                        return;
                    }
                    Bukkit.broadcastMessage(ChatColor.BLUE + event.getWhoClicked().getName() + ChatColor.GRAY + " skipped " +
                            Format.formatItemName(ItemManager.getCurrentItem().name().replace("_", " ")));
                    ItemManager.removeMaterial(ItemManager.getCurrentItem());
                    ItemManager.updateItem();
                    BossbarManager.updateBossbar();
                    event.getWhoClicked().openInventory(getGameInv());
                    break;
                }
                case 1: {
                    new StatsCommand.GUI((Player) event.getWhoClicked(), 1);
                    break;
                }
            }
        }
    }

    private Inventory getSettingsInv() {
        Inventory inventory = Bukkit.createInventory(this, 9, ChatColor.BLUE.toString() + ChatColor.BOLD + "Settings");
        inventory.setItem(0, ItemBuilder.buildItem(new ItemStack(Material.CLOCK), ChatColor.BLUE + "Timer", true,
                ChatColor.GRAY + "Open Timer settings"));
        inventory.setItem(1, ItemBuilder.buildItem(new ItemStack(Material.ARROW), ChatColor.BLUE + "Game", true,
                ChatColor.GRAY + "Open Game settings"));
        return inventory;
    }

    private Inventory getTimerInv() {
        Inventory inventory = Bukkit.createInventory(this, 9, ChatColor.BLUE.toString() + ChatColor.BOLD + "Timer settings");
        inventory.setItem(0, ItemBuilder.buildItem(new ItemStack(Material.CLOCK), ChatColor.BLUE + "Toggle", true,
                ChatColor.GRAY + "Click to toggle the timer", "", ChatColor.GRAY + "Timer is currently " + ChatColor.BLUE +
                        (AllItems.getInstance().getTimer().isRunning() ? "running" : "paused")));
        inventory.setItem(1, ItemBuilder.buildItem(new ItemStack(Material.PURPLE_DYE), ChatColor.BLUE + "Gradient", true,
                ChatColor.GRAY + "Click to toggle the timer gradient", "", ChatColor.GRAY + "Currently set to: " + ChatColor.BLUE +
                AllItems.getInstance().getTimer().isGradientEnabled()));
        inventory.setItem(2, ItemBuilder.buildItem(new ItemStack(Material.PURPLE_DYE), ChatColor.BLUE + "Gradient Speed", true,
                ChatColor.GRAY + "Click to cycle through the speeds", "", ChatColor.GRAY + "Current speed: " + ChatColor.BLUE +
                        AllItems.getInstance().getTimer().getSpeed()));
        return inventory;
    }

    private Inventory getGameInv() {
        Inventory inventory = Bukkit.createInventory(this, 9, ChatColor.BLUE.toString() + ChatColor.BOLD + "Game settings");
        inventory.setItem(0, ItemBuilder.buildItem(new ItemStack(Material.BARRIER), ChatColor.BLUE + "Skip", true,
                ChatColor.GRAY + "Click to skip the current item", "", ChatColor.GRAY + "Current item: " + ChatColor.BLUE +
                        (ItemManager.getCurrentItem() != null ? Format.formatItemName(ItemManager.getCurrentItem().name().replace("_", " "))
                                : "None")));
        inventory.setItem(1, ItemBuilder.buildItem(new ItemStack(Material.BOOK), ChatColor.BLUE + "Stats", true,
                ChatColor.GRAY + "Click to open the stats"));
        return inventory;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return getSettingsInv();
    }
}
