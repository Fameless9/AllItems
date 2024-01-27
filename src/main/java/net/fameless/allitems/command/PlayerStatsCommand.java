package net.fameless.allitems.command;

import net.fameless.allitems.AllItems;
import net.fameless.allitems.game.DataFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerStatsCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command.");
            return false;
        }
        if (!AllItems.getInstance().getConfig().getBoolean("enable_playerstats")) {
            sender.sendMessage(ChatColor.RED + "Player stats are disabled.");
            return false;
        }
        new GUI((Player) sender, 1);
        return false;
    }

    static class GUI implements InventoryHolder {
        private Inventory gui;
        public GUI(Player player, int page) {
            gui = Bukkit.createInventory(this, 54, "Player Stats | Page " + page);

            List<UUID> playerList = new ArrayList<>();

            for (Map.Entry entry : DataFile.getPlayerObject().entrySet()) {
                playerList.add(UUID.fromString(entry.getKey().toString()));
            }

            ItemStack left;
            ItemMeta leftMeta;
            if (PageUtil.isPageValid(playerList, page - 1, 52)) {
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

            if (PageUtil.isPageValid(playerList, page + 1, 52)) {
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

            for (ItemStack itemStack : PageUtil.getPageItems(playerList, page, 52)) {
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
        public static List<ItemStack> getPageItems(List<UUID> players, int page, int spaces) {
            int startIndex = (page - 1) * spaces;
            int endIndex = Math.min(startIndex + spaces, players.size());

            List<ItemStack> newObjectives = new ArrayList<>();

            for (int i = startIndex; i < endIndex; i++) {
                if (i >= 0 && i < players.size()) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(players.get(i));
                    ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) skull.getItemMeta();
                    meta.setOwningPlayer(player);
                    meta.setDisplayName(ChatColor.BLUE + player.getName());
                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add(ChatColor.GRAY + "Completed items: " + ChatColor.BLUE + DataFile.getPlayerObject().get(players.get(i).toString()));
                    meta.setLore(lore);
                    skull.setItemMeta(meta);

                    newObjectives.add(skull);
                }
            }
            return newObjectives;
        }

        public static boolean isPageValid(List<UUID> players, int page, int spaces) {
            if (page <= 0) return false;

            int upperBound = page * spaces;
            int lowerBound = upperBound - spaces;

            return players.size() > lowerBound;
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
