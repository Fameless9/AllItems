package net.fameless.allitems.game;

import net.fameless.allitems.AllItems;
import net.fameless.allitems.manager.BossbarManager;
import net.fameless.allitems.manager.ItemManager;
import net.fameless.allitems.util.Format;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class GameListener implements Listener {

    public GameListener() {
        run();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setResourcePack("https://drive.usercontent.google.com/download?id=1K5On0YGYJlknv9p2Wgdz9qGyrChWn8fl&export=download&authuser=1&confirm=t&uuid=b67aa88a-90e7-42ad-ab70-deaa2eea4f9e&at=APZUnTVJb5KuZWw3nzyYMd434CfL:1693104909215");
        BossbarManager.updateBossbar();
        event.setJoinMessage(ChatColor.BLUE + event.getPlayer().getName() + ChatColor.GRAY + " joined the game");
    }

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Bukkit.broadcastMessage(ChatColor.BLUE + event.getPlayer().getName() + ChatColor.GRAY + ": " + event.getMessage());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (event.getStatus().equals(PlayerResourcePackStatusEvent.Status.DECLINED)) {
            event.getPlayer().sendMessage(ChatColor.RED + "Allow resource packs to hide the bossbar!");
            return;
        }
        if (event.getStatus().equals(PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD)) {
            event.getPlayer().sendMessage(ChatColor.RED + "Failed to download resource pack!");
        }
    }

    public static void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player players : Bukkit.getOnlinePlayers()) {
                    Inventory playerInv = players.getInventory();
                    for (ItemStack stack : playerInv.getContents()) {
                        if (stack != null && stack.getType().equals(ItemManager.getCurrentItem())) {
                            Bukkit.broadcastMessage(ChatColor.BLUE + players.getName() + ChatColor.GRAY + " completed " +
                                    Format.formatItemName(stack.getType().name().replace("_", " "))
                                    + ChatColor.GRAY + " (" + ChatColor.GREEN + (ItemManager.getFinishedAmount() + 1) + ChatColor.GRAY + "/" +
                                    ItemManager.getItemAmount() + ")");
                            players.playSound(players.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1, 20);
                            AllItems.getInstance().getConfig().set("ignore." + stack.getType().name(), AllItems.getInstance().getTimer().getTime());
                            AllItems.getInstance().saveConfig();
                            ItemManager.markedAsFinished(stack.getType());
                            ItemManager.updateItem();
                            BossbarManager.updateBossbar();
                        }
                    }
                }
            }
        }.runTaskTimer(AllItems.getInstance(), 0, 2);
    }
}
