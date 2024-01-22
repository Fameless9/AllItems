package net.fameless.allitems.game;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fameless.allitems.AllItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataFile {

    private static final File jsonFile = new File(AllItems.getInstance().getDataFolder(), "data.json");

    public static void init() throws IOException {
        List<String> toExclude = getToExclude();

        if (!jsonFile.exists()) {
            jsonFile.createNewFile();
            JsonObject initData = new JsonObject();
            initData.addProperty("Author", "Fameless9 (https://github.com/Fameless9)");
            saveJsonFile(initData);
        }

        JsonObject finalObject = getRootObject();
        JsonObject itemObject = getItemObject();
        JsonObject playerObject = getPlayerObject();

        List<Material> materials = new ArrayList<>();

        for (Material material : Material.values()) {
            if (toExclude.contains(material.name())) continue;
            materials.add(material);
        }

        Collections.shuffle(materials);

        for (Material material : materials) {
            if (!itemObject.has(material.name())) {
                itemObject.addProperty(material.name(), false);
            }
        }

        for (String s : toExclude) {
            if (itemObject.has(s)) {
                itemObject.remove(s);
            }
        }

        finalObject.add("items", itemObject);
        finalObject.add("players", playerObject);
        saveJsonFile(finalObject);saveJsonFile(finalObject);
    }

    private static List<String> getToExclude() {
        List<String> toExclude = new ArrayList<>();
        List<String> section = AllItems.getInstance().getConfig().getStringList("exclude.items");
        for (Material material : Material.values()) {
            if (section.contains(material.name())) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().endsWith("SPAWN_EGG")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().endsWith("BANNER_PATTERN")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().endsWith("CANDLE_CAKE")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().startsWith("POTTED")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("TORCH")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("SIGN")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("HEAD")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("CORAL")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("BANNER")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("SKULL")) {
                toExclude.add(material.name());
                continue;
            }
            if (material.name().endsWith("STEM")) {
                toExclude.add(material.name());
            }
        }
        return toExclude;
    }

    public static JsonObject getItemObject() {
        if (getRootObject().has("items")) {
            return getRootObject().getAsJsonObject("items");
        }
        return new JsonObject();
    }

    public static JsonObject getPlayerObject() {
        if (getRootObject().has("players")) {
            return getRootObject().getAsJsonObject("players");
        }
        return new JsonObject();
    }

    public static void saveJsonFile(JsonObject finalObject) {
        try (FileWriter writer = new FileWriter(jsonFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(finalObject, writer);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save file. Shutting down.");
            Bukkit.getPluginManager().disablePlugin(AllItems.getInstance());
        }
    }

    public static JsonObject getRootObject() {
        JsonParser parser = new JsonParser();
        try {
            return parser.parse(new FileReader(jsonFile)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().severe("Failed to read file. Shutting down.");
            Bukkit.getPluginManager().disablePlugin(AllItems.getInstance());
        }
        return new JsonObject();
    }
}
