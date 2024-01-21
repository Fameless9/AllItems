package net.fameless.allitems.util;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    public static ItemStack buildItem(ItemStack itemStack, String name, boolean hideAttributes, String ...lore) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);

        if (hideAttributes) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        List<String> lores = new ArrayList<>();
        lores.addAll(Arrays.asList(lore));

        meta.setLore(lores);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
