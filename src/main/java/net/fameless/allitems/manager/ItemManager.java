package net.fameless.allitems.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.fameless.allitems.game.DataFile;
import net.fameless.allitems.util.Format;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemManager {

    private static final List<Material> materials = new ArrayList<>();
    private static int pos = 0;
    private static Material currentItem = null;

    public static void loadToList() {
        for (Map.Entry entry : DataFile.getItemObject().entrySet()) {
            if (!((JsonPrimitive) entry.getValue()).getAsBoolean() && Material.valueOf(entry.getKey().toString()) != null) {
                materials.add(Material.valueOf(entry.getKey().toString()));
            }
        }
        try {
            currentItem = materials.get(pos);
        } catch (IndexOutOfBoundsException e) {
            currentItem = null;
        }
    }

    public static void updateItem() {
        pos++;
        try {
            currentItem = materials.get(pos);
        } catch (IndexOutOfBoundsException e) {
            currentItem = null;
        }
    }

    public static Material getCurrentItem() {
        return currentItem;
    }

    public static String getNextItem() {
        String nextItem;
        try {
            nextItem = Format.formatItemName(materials.get(pos + 1).name().replace("_", " "));
        } catch (IndexOutOfBoundsException e) {
            nextItem = "None";
        }
        return nextItem;
    }

    public static void markedAsFinished(Material material) {
        if (DataFile.getItemObject().has(material.name())) {
            JsonObject newFinalObject = DataFile.getRootObject();
            JsonObject newItemObject = DataFile.getItemObject();
            newItemObject.addProperty(material.name(), true);
            newFinalObject.add("items", newItemObject);
            DataFile.saveJsonFile(newFinalObject);
        }
    }

    public static int getItemAmount() {
        int i = 0;
        for (Map.Entry ignored : DataFile.getItemObject().entrySet()) {
            i++;
        }
        return i;
    }

    public static int getFinishedAmount() {
        int i = 0;
        for (Map.Entry entry : DataFile.getItemObject().entrySet()) {
            if (((JsonPrimitive) entry.getValue()).getAsBoolean()) {
                i++;
            }
        }
        return i;
    }

    public static void removeMaterial(Material material) {
        if (DataFile.getItemObject().has(material.name())) {
            JsonObject newFinalObject = DataFile.getRootObject();
            JsonObject newItemObject = DataFile.getItemObject();
            newItemObject.remove(material.name());
            newFinalObject.add("items", newItemObject);
            DataFile.saveJsonFile(newFinalObject);
        }
    }
}
