package net.fameless.allitems.manager;

import net.fameless.allitems.AllItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class BossbarManager {

    private static final HashMap<Player, BossBar> bossBarHashMap = new HashMap<>();

    public static void updateBossbar() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (bossBarHashMap.containsKey(player)) {
                bossBarHashMap.get(player).removePlayer(player);
            }
            getBossbar(player);
        }
    }

    private static void getBossbar(Player player) {
        BossBar bossbar;
        if (ItemManager.getCurrentItem() == null) {
            bossbar = Bukkit.createBossBar(ChatColor.GRAY + "Completed " + ChatColor.GREEN + ChatColor.BOLD + ItemManager.getFinishedAmount() +
                    ChatColor.RESET + ChatColor.GRAY + " out of " + ChatColor.GREEN + ChatColor.BOLD + ItemManager.getItemAmount() +
                    ChatColor.RESET + ChatColor.GRAY + " items", BarColor.PURPLE, BarStyle.SOLID);
            AllItems.getInstance().getTimer().setRunning(false);
            Bukkit.broadcastMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "You have collected every item.");
        } else {
            bossbar = Bukkit.createBossBar(ChatColor.GRAY + "(" + ChatColor.GREEN + ItemManager.getFinishedAmount() +
                            ChatColor.GRAY + "/" + ItemManager.getItemAmount() + ")" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Item" +
                            ChatColor.DARK_GRAY + ": " + ChatColor.BLUE + formatItemName(ItemManager.getCurrentItem().name().replace("_", " ")) +
                            ChatColor.GRAY + " » " + ChatColor.BLUE + ItemManager.getNextItem(),
                    BarColor.PURPLE, BarStyle.SOLID);
        }
        bossbar.addPlayer(player);
        bossBarHashMap.put(player, bossbar);
    }

    public static String formatItemName(String input) {
        String[] words = input.split(" ");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            String formattedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
            formatted.append(formattedWord).append(" ");
        }
        return formatted.toString().trim();
    }
}
