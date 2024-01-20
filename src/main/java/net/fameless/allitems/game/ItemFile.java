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

public class ItemFile {

    private static final File jsonFile = new File(AllItems.getInstance().getDataFolder(), "data.json");

    public static void init() throws IOException {

        if (!jsonFile.exists()) {
            jsonFile.createNewFile();
            JsonObject initData = new JsonObject();
            initData.addProperty("Author", "Fameless9 (https://github.com/Fameless9)");
            saveJsonFile(initData);

            JsonObject finalObject = new JsonObject();
            JsonObject itemObject = new JsonObject();

            List<String> toExclude = new ArrayList<>();
            List<Material> materials = new ArrayList<>();

            for (Material material : Material.values()) {
                toExclude.add("WATER");
                toExclude.add("DECORATED_POT");
                toExclude.add("POWDER_SNOW");
                toExclude.add("CHAIN_COMMAND_BLOCK");
                toExclude.add("WATER_CAULDRON");
                toExclude.add("LAVA_CAULDRON");
                toExclude.add("COMMAND_BLOCK_MINECART");
                toExclude.add("COMMAND_BLOCK");
                toExclude.add("POWDER_SNOW_CAULDRON");
                toExclude.add("REPEATING_COMMAND_BLOCK");
                toExclude.add("BEDROCK");
                toExclude.add("AIR");
                toExclude.add("FIRE");
                toExclude.add("SOUL_FIRE");
                toExclude.add("END_PORTAL_FRAME");
                toExclude.add("NETHER_PORTAL");
                toExclude.add("END_PORTAL");
                toExclude.add("CHAINMAIL_HELMET");
                toExclude.add("CHAINMAIL_LEGGINGS");
                toExclude.add("CHAINMAIL_CHESTPLATE");
                toExclude.add("CHAINMAIL_BOOTS");
                toExclude.add("LIGHT");
                toExclude.add("END_GATEWAY");
                toExclude.add("DRAGON_EGG");
                toExclude.add("TALL_SEAGRASS");
                toExclude.add("VOID_AIR");
                toExclude.add("PISTON_HEAD");
                toExclude.add("MOVING_PISTON");
                toExclude.add("WRITTEN_BOOK");
                toExclude.add("DAMAGED_ANVIL");
                toExclude.add("TORCHFLOWER_SEEDS");
                toExclude.add("JIGSAW");
                toExclude.add("TIPPED_ARROW");
                toExclude.add("LAVA");
                toExclude.add("WATER_CAULDRON");
                toExclude.add("STRUCTURE_VOID");
                toExclude.add("BARRIER");
                toExclude.add("DEBUG_STICK");
                toExclude.add("BUNDLE");
                toExclude.add("FARMLAND");
                toExclude.add("STRUCTURE_BLOCK");
                toExclude.add("BUBBLE_COLUMN");
                toExclude.add("CAVE_AIR");
                toExclude.add("KNOWLEDGE_BOOK");
                toExclude.add("DIRT_PATH");

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

            for (Material material : Material.values()) {
                if (toExclude.contains(material.name())) continue;
                materials.add(material);
            }

            Collections.shuffle(materials);

            for (Material material : materials) {
                itemObject.addProperty(material.name(), false);
            }

            finalObject.add("items", itemObject);
            saveJsonFile(finalObject);
        }
    }

    public static JsonObject getItemObject() {
        if (getRootObject().has("items")) {
            return getRootObject().getAsJsonObject("items");
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
