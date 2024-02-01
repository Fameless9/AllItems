package net.fameless.allitems.command;

import net.fameless.allitems.AllItems;
import net.fameless.allitems.manager.BossbarManager;
import net.fameless.allitems.manager.ItemManager;
import net.fameless.allitems.util.Format;
import net.fameless.allitems.util.Head;
import net.fameless.allitems.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SettingsCommand implements CommandExecutor, Listener, InventoryHolder {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("allitems.settings")) {
            sender.sendMessage(Component.text("Lacking permission: 'allitems.settings'", NamedTextColor.RED));
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players may use this command.", NamedTextColor.RED));
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
                event.getWhoClicked().sendMessage(Component.text("Lacking permission: 'allitems.settings.settings'", NamedTextColor.RED));
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
                case 8: {
                    event.getWhoClicked().sendMessage(Component.text("Report any bugs here: ", NamedTextColor.GRAY)
                            .append(Component.text("https://github.com/Fameless9/AllItems/issues", NamedTextColor.BLUE)
                                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Fameless9/AllItems/issues"))
                                    .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Github page", NamedTextColor.BLUE)))));
                    event.getWhoClicked().closeInventory();
                }
            }
            return;
        }
        if (event.getView().getTitle().contains("Timer settings")) {
            if (!event.getWhoClicked().hasPermission("allitems.settings.timer")) {
                event.getWhoClicked().sendMessage(Component.text("Lacking permission: 'allitems.settings.timer'", NamedTextColor.RED));
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
                event.getWhoClicked().sendMessage(Component.text("Lacking permission: 'allitems.settings.game'", NamedTextColor.RED));
                event.getWhoClicked().closeInventory();
                return;
            }
            event.setCancelled(true);
            switch (event.getSlot()) {
                case 0: {
                    if (ItemManager.getNextItem().equals("None")) {
                        event.getWhoClicked().sendMessage(Component.text("No item left to skip.", NamedTextColor.RED));
                        return;
                    }
                    Bukkit.broadcast(Component.text(event.getWhoClicked().getName(), NamedTextColor.BLUE).append(Component.text(" skipped ",
                            NamedTextColor.GRAY)).append(Component.text(Format.formatItemName(ItemManager.getCurrentItem().name().replace("_", " "))
                            , NamedTextColor.GRAY)));

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
        Inventory inventory = Bukkit.createInventory(this, 9, Component.text("Settings", NamedTextColor.BLUE, TextDecoration.BOLD));
        inventory.setItem(0, ItemBuilder.buildItem(new ItemStack(Material.CLOCK), Component.text("Timer", NamedTextColor.BLUE), true,
                Component.text("Open Timer settings", NamedTextColor.GRAY)));
        inventory.setItem(1, ItemBuilder.buildItem(new ItemStack(Material.ARROW), Component.text("Game", NamedTextColor.BLUE), true,
                Component.text("Open Game settings", NamedTextColor.GRAY)));
        inventory.setItem(8, ItemBuilder.buildItem(Head.INFO.getAsItemStack(), Component.text("Help", NamedTextColor.BLUE), true,
                Component.text("Click to go to the github issues page,", NamedTextColor.GRAY, TextDecoration.ITALIC),
                Component.text("or contact me on Discord: ", NamedTextColor.GRAY)
                        .append(Component.text("fameless9", NamedTextColor.DARK_PURPLE))));
        return inventory;
    }

    private Inventory getTimerInv() {
        Inventory inventory = Bukkit.createInventory(this, 9, Component.text("Timer settings", NamedTextColor.BLUE, TextDecoration.BOLD));

        inventory.setItem(0, ItemBuilder.buildItem(new ItemStack(Material.CLOCK), Component.text("Toggle", NamedTextColor.BLUE), true,
                Component.text("Click to toggle the timer", NamedTextColor.GRAY, TextDecoration.ITALIC),
                Component.text("Timer is currently ", NamedTextColor.GRAY)
                        .append(Component.text(AllItems.getInstance().getTimer().isRunning() ? "running" : "paused", NamedTextColor.BLUE))));
        inventory.setItem(1, ItemBuilder.buildItem(new ItemStack(Material.PURPLE_DYE), Component.text("Gradient", NamedTextColor.BLUE), true,
                Component.text("Click to toggle the timer gradient", NamedTextColor.GRAY, TextDecoration.ITALIC),
                Component.text("Currently set to: ", NamedTextColor.GRAY)
                        .append(Component.text(AllItems.getInstance().getTimer().isGradientEnabled(), NamedTextColor.BLUE))));
        inventory.setItem(2, ItemBuilder.buildItem(new ItemStack(Material.PURPLE_DYE), Component.text("Gradient Speed", NamedTextColor.BLUE), true,
                Component.text("Click to cycle through the speeds", NamedTextColor.GRAY, TextDecoration.ITALIC),
                Component.text("Current speed: ", NamedTextColor.GRAY)
                        .append(Component.text(AllItems.getInstance().getTimer().getSpeed(), NamedTextColor.BLUE))));

        return inventory;
    }

    private Inventory getGameInv() {
        Inventory inventory = Bukkit.createInventory(this, 9, Component.text("Game settings", NamedTextColor.BLUE, TextDecoration.BOLD));

        inventory.setItem(0, ItemBuilder.buildItem(new ItemStack(Material.BARRIER), Component.text("Skip", NamedTextColor.BLUE), true,
                Component.text("Click to skip the current item", NamedTextColor.GRAY, TextDecoration.ITALIC),
                Component.text("Current item: ", NamedTextColor.GRAY)
                        .append(Component.text(ItemManager.getCurrentItem() != null ?
                                Format.formatItemName(ItemManager.getCurrentItem().name().replace("_", " ")) : "None", NamedTextColor.BLUE))));
        inventory.setItem(1, ItemBuilder.buildItem(new ItemStack(Material.BOOK), Component.text("Stats", NamedTextColor.BLUE), true,
                Component.text("Click to open the stats", NamedTextColor.GRAY, TextDecoration.ITALIC)));

        return inventory;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return getSettingsInv();
    }
}
