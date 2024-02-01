package net.fameless.allitems.util;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    public static ItemStack buildItem(ItemStack itemStack, Component name, boolean hideAttributes, Component ...lore) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(name);

        if (hideAttributes) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        List<Component> lores = new ArrayList<>(Arrays.asList(lore));

        meta.lore(lores);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
