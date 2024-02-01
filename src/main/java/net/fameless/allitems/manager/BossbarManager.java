package net.fameless.allitems.manager;

import net.fameless.allitems.AllItems;
import net.fameless.allitems.util.Format;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class BossbarManager {

    private static final HashMap<Player, BossBar> bossBarHashMap = new HashMap<>();

    public static void updateBossbar() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (bossBarHashMap.containsKey(player)) {
                bossBarHashMap.get(player).removeViewer(player);
            }
            getBossbar(player);
        }
    }

    private static void getBossbar(Player player) {
        BossBar bossbar;
        Material material = ItemManager.getCurrentItem();
        String key = material != null ? material.getItemTranslationKey() : null;
        Material nextItem = ItemManager.getNextItem();
        String nextKey = nextItem != null ? ItemManager.getNextItem().getItemTranslationKey() : null;
        if (ItemManager.getCurrentItem() == null) {
            bossbar = BossBar.bossBar(
                    Component.text("Completed ", NamedTextColor.GRAY)
                            .append(Component.text(ItemManager.getFinishedAmount(), NamedTextColor.GREEN, TextDecoration.BOLD))
                            .append(Component.text(" out of ", NamedTextColor.GRAY))
                            .append(Component.text(ItemManager.getItemAmount(), NamedTextColor.GREEN, TextDecoration.BOLD))
                            .append(Component.text(" items", NamedTextColor.GRAY)), 1,
                    BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);
            AllItems.getInstance().getTimer().setRunning(false);
        } else {
            bossbar = BossBar.bossBar(Component.text("(", NamedTextColor.GRAY)
                            .append(Component.text(ItemManager.getFinishedAmount(), NamedTextColor.GREEN))
                            .append(Component.text("/" + ItemManager.getItemAmount() + ")", NamedTextColor.GRAY))
                            .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Item: ", NamedTextColor.GRAY))
                            .append(key != null ? Component.translatable(key, Format.formatItemName(material.name().replace("_", " ")),
                                    NamedTextColor.BLUE) : Component.text(material != null ? Format.formatItemName(material.name().replace("_", " ")) :
                                    "None", NamedTextColor.BLUE))
                            .append(nextItem != null && nextKey != null ? Component.text(" » ", NamedTextColor.GRAY) : Component.empty())
                            .append(nextItem != null && nextKey != null ? Component.translatable(nextKey,
                                    Format.formatItemName(nextItem.name().replace("_", " ")),NamedTextColor.BLUE) : Component.empty()),
                    1, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);
        }
        bossbar.addViewer(player);
        bossBarHashMap.put(player, bossbar);
    }
}
