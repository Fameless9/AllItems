package net.fameless.allitems.game;

import com.google.gson.JsonObject;
import net.fameless.allitems.AllItems;
import net.fameless.allitems.manager.BossbarManager;
import net.fameless.allitems.manager.ItemManager;
import net.fameless.allitems.util.Format;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
        if (!DataFile.getPlayerObject().has(event.getPlayer().getUniqueId().toString())) {
            JsonObject rootObject = DataFile.getRootObject();
            JsonObject playerObject = DataFile.getPlayerObject();
            playerObject.addProperty(event.getPlayer().getUniqueId().toString(), 0);
            rootObject.add("players", playerObject);
            DataFile.saveJsonFile(rootObject);
        }
        event.joinMessage(Component.text(event.getPlayer().getName(), NamedTextColor.BLUE).append(Component.text(" joined the game", NamedTextColor.GRAY)));
        BossbarManager.updateBossbar();
    }


    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Bukkit.broadcast(Component.text(event.getPlayer().getName(), NamedTextColor.BLUE).append(Component.text(": " + event.getMessage(), NamedTextColor.GRAY)));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (event.getStatus().equals(PlayerResourcePackStatusEvent.Status.DECLINED)) {
            event.getPlayer().sendMessage(Component.text("Allow resource packs to hide the bossbar!", NamedTextColor.RED));
            return;
        }
        if (event.getStatus().equals(PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD)) {
            event.getPlayer().sendMessage(Component.text("Failed to download resource pack!", NamedTextColor.RED));
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
                            Material material = ItemManager.getCurrentItem();
                            String key = material.getItemTranslationKey();
                            Bukkit.broadcast(Component.text(players.getName(), NamedTextColor.BLUE)
                                    .append(Component.text(" completed ", NamedTextColor.GRAY).append(Component.translatable(key,
                                                    Format.formatItemName(material.name().replace("_", " "))))
                                    .append(Component.text(" (", NamedTextColor.GRAY)).append(Component.text(ItemManager.getFinishedAmount() + 1, NamedTextColor.GREEN))
                                    .append(Component.text("/" + ItemManager.getItemAmount() + ")", NamedTextColor.GRAY))));
                            players.playSound(players.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1, 20);
                            AllItems.getInstance().getConfig().set("ignore." + stack.getType().name(), AllItems.getInstance().getTimer().getTime());
                            AllItems.getInstance().saveConfig();
                            JsonObject rootObject = DataFile.getRootObject();
                            JsonObject playerObject = DataFile.getPlayerObject();
                            int finished = DataFile.getPlayerObject().get(players.getUniqueId().toString()).getAsInt();
                            int newFinished = finished + 1;
                            playerObject.addProperty(players.getUniqueId().toString(), newFinished);
                            rootObject.add("players", playerObject);
                            DataFile.saveJsonFile(rootObject);
                            ItemManager.markedAsFinished(stack.getType());
                            ItemManager.updateItem();
                            BossbarManager.updateBossbar();
                            if (ItemManager.getCurrentItem() == null) {
                                Bukkit.broadcast(Component.text("You have collected every item.", NamedTextColor.GREEN, TextDecoration.BOLD));
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(AllItems.getInstance(), 0, 2);
    }
}
