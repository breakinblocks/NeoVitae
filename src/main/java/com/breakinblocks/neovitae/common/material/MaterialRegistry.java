package com.breakinblocks.neovitae.common.material;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.MaterialItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import com.breakinblocks.neovitae.util.helper.OreDiscoveryHelper;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MaterialRegistry {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(NeoVitae.MODID);
    private static final List<MaterialDefinition> MATERIALS = new ArrayList<>();
    private static final Map<String, Map<String, DeferredHolder<Item, Item>>> ITEM_MAP = new LinkedHashMap<>();
    private static InMemoryPack GENERATED_PACK;
    private static boolean firstRun = false;
    private static boolean pendingRestartNotice = false;

    static {
        loadAndRegister();
    }

    private static void loadAndRegister() {
        List<MaterialDefinition> definitions = loadConfig();
        MATERIALS.addAll(definitions);

        for (MaterialDefinition mat : MATERIALS) {
            Map<String, DeferredHolder<Item, Item>> stageMap = new LinkedHashMap<>();
            for (String stage : mat.getStages()) {
                String itemId = mat.getItemId(stage);
                int color = mat.getColorInt();
                DeferredHolder<Item, Item> holder = ITEMS.register(itemId, () -> new MaterialItem(color));
                stageMap.put(stage, holder);
            }
            ITEM_MAP.put(mat.getName(), stageMap);
        }

        populatePack();
    }

    private static void populatePack() {
        var locationInfo = new PackLocationInfo(
                "neovitae_materials",
                Component.literal("NeoVitae Materials"),
                PackSource.BUILT_IN,
                Optional.empty()
        );
        GENERATED_PACK = new InMemoryPack(locationInfo);
        JsonObject lang = new JsonObject();

        for (MaterialDefinition mat : MATERIALS) {
            String displayName = mat.getDisplayName();

            for (String stage : mat.getStages()) {
                String itemId = mat.getItemId(stage);
                String stageName = stage.substring(0, 1).toUpperCase() + stage.substring(1);

                String baseTexture = switch (stage) {
                    case "fragment" -> "neovitae:item/base_fragment";
                    case "gravel" -> "neovitae:item/base_gravel";
                    default -> "neovitae:item/base_dust";
                };

                JsonObject model = new JsonObject();
                model.addProperty("parent", "minecraft:item/generated");
                JsonObject textures = new JsonObject();
                textures.addProperty("layer0", baseTexture);
                model.add("textures", textures);

                GENERATED_PACK.putJson(PackType.CLIENT_RESOURCES,
                        ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "models/item/" + itemId + ".json"),
                        GSON.toJson(model));

                lang.addProperty("item.neovitae." + itemId, displayName + " " + stageName);
            }

            if (mat.getSmeltTo() != null && !mat.getSmeltTo().isEmpty() && mat.hasStage("dust")) {
                String dustId = NeoVitae.MODID + ":" + mat.getItemId("dust");
                addSmeltingRecipe(mat.getName(), dustId, mat.getSmeltTo(), mat.getSmeltXp(), 200, "smelting");
                addSmeltingRecipe(mat.getName(), dustId, mat.getSmeltTo(), mat.getSmeltXp(), 100, "blasting");
            }

            addMaterialTags(mat);
            addAthanorRecipes(mat);
            addAlchemyTableRecipes(mat);
        }

        GENERATED_PACK.putJson(PackType.CLIENT_RESOURCES,
                ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "lang/en_us.json"),
                GSON.toJson(lang));

        NeoVitae.LOGGER.info("Populated in-memory resource pack for {} materials", MATERIALS.size());
    }

    private static void addSmeltingRecipe(String materialName, String input, String result, float xp, int cookTime, String recipeType) {
        JsonObject root = new JsonObject();

        JsonObject condition = new JsonObject();
        condition.addProperty("type", "neoforge:item_exists");
        condition.addProperty("item", result);
        JsonArray conditions = new JsonArray();
        conditions.add(condition);
        root.add("neoforge:conditions", conditions);

        root.addProperty("type", "minecraft:" + recipeType);
        root.addProperty("category", "misc");

        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("item", input);
        root.add("ingredient", ingredient);

        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("count", 1);
        resultObj.addProperty("id", result);
        root.add("result", resultObj);

        root.addProperty("experience", xp);
        root.addProperty("cookingtime", cookTime);

        GENERATED_PACK.putJson(PackType.SERVER_DATA,
                ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "recipe/materials/" + recipeType + "_" + materialName + "_dust.json"),
                GSON.toJson(root));
    }

    private static void addMaterialTags(MaterialDefinition mat) {
        Map<String, String> stageToTagPrefix = Map.of(
                "fragment", "fragments",
                "gravel", "gravels",
                "dust", "dusts"
        );

        for (String stage : mat.getStages()) {
            String tagPrefix = stageToTagPrefix.get(stage);
            if (tagPrefix == null) continue;

            String itemId = NeoVitae.MODID + ":" + mat.getItemId(stage);

            JsonObject tag = new JsonObject();
            JsonArray values = new JsonArray();
            values.add(itemId);
            tag.add("values", values);

            GENERATED_PACK.putJson(PackType.SERVER_DATA,
                    ResourceLocation.fromNamespaceAndPath("c", "tags/item/" + tagPrefix + "/" + mat.getName() + ".json"),
                    GSON.toJson(tag));
        }
    }

    private static void addAlchemyTableRecipes(MaterialDefinition mat) {
        String name = mat.getName();
        String dustId = mat.hasStage("dust") ? NeoVitae.MODID + ":" + mat.getItemId("dust") : null;
        String gravelId = mat.hasStage("gravel") ? NeoVitae.MODID + ":" + mat.getItemId("gravel") : null;

        // Ore + cutting fluid = 2 dust
        if (dustId != null && mat.getOreTag() != null) {
            addAlchemyTableRecipe("dust_" + name, dustId, 2, 400, 200, 1,
                    new String[]{"tag:" + mat.getOreTag(), "tag:neovitae:athanor_tool/cutting_fluids"});
        }

        // Fragment + corrupted dust = 2 gravel
        if (gravelId != null && mat.hasStage("fragment")) {
            addAlchemyTableRecipe("corrupted_" + name, gravelId, 2, 200, 50, 3,
                    new String[]{"tag:c:fragments/" + name, "item:neovitae:corrupted_dust"});
        }
    }

    private static void addAlchemyTableRecipe(String recipeName, String outputItem, int outputCount,
                                               int syphon, int ticks, int tier, String[] inputs) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "neovitae:alchemytable");

        JsonArray inputArray = new JsonArray();
        for (String input : inputs) {
            JsonObject entry = new JsonObject();
            if (input.startsWith("tag:")) {
                entry.addProperty("tag", input.substring(4));
            } else {
                entry.addProperty("item", input.substring(5));
            }
            inputArray.add(entry);
        }
        recipe.add("input", inputArray);

        JsonObject output = new JsonObject();
        output.addProperty("count", outputCount);
        output.addProperty("id", outputItem);
        recipe.add("output", output);

        recipe.addProperty("syphon", syphon);
        recipe.addProperty("ticks", ticks);
        recipe.addProperty("upgradeLevel", tier);

        GENERATED_PACK.putJson(PackType.SERVER_DATA,
                ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "recipe/alchemytable/" + recipeName + ".json"),
                GSON.toJson(recipe));
    }

    private static void addAthanorRecipes(MaterialDefinition mat) {
        String name = mat.getName();
        String fragmentId = mat.hasStage("fragment") ? NeoVitae.MODID + ":" + mat.getItemId("fragment") : null;
        String gravelId = mat.hasStage("gravel") ? NeoVitae.MODID + ":" + mat.getItemId("gravel") : null;
        String dustId = mat.hasStage("dust") ? NeoVitae.MODID + ":" + mat.getItemId("dust") : null;

        // Raw material + explosive = 2 fragments + 25% bonus
        if (fragmentId != null && mat.getRawTag() != null) {
            addAthanorRecipe("explosives/fragments_" + name,
                    mat.getRawTag(), "neovitae:athanor_tool/explosives",
                    fragmentId, 2, fragmentId, 0.25);

            // Ore + explosive = 4 fragments
            if (mat.getOreTag() != null) {
                addAthanorRecipe("explosives/fragments_from_ore_" + name,
                        mat.getOreTag(), "neovitae:athanor_tool/explosives",
                        fragmentId, 4, null, 0);
            }
        }

        // Fragment + resonator = 1 gravel + 50% corrupted dust
        if (fragmentId != null && gravelId != null) {
            addAthanorRecipe("resonator/gravels_" + name,
                    "c:fragments/" + name, "neovitae:athanor_tool/resonator",
                    gravelId, 1, "neovitae:corrupted_tiny_dust", 0.5);
        }

        // Gravel + cutting fluid = 1 dust
        if (gravelId != null && dustId != null) {
            addAthanorRecipe("cutting_fluids/dusts_from_gravel_" + name,
                    "c:gravels/" + name, "neovitae:athanor_tool/cutting_fluids",
                    dustId, 1, null, 0);
        }

        // Ore + cutting fluid = 3 dust
        if (dustId != null && mat.getOreTag() != null) {
            addAthanorRecipe("cutting_fluids/dusts_from_ore_" + name,
                    mat.getOreTag(), "neovitae:athanor_tool/cutting_fluids",
                    dustId, 3, null, 0);
        }

        // Ingot + cutting fluid = 1 dust
        if (dustId != null && mat.getIngotTag() != null) {
            addAthanorRecipe("cutting_fluids/dusts_from_ingot_" + name,
                    mat.getIngotTag(), "neovitae:athanor_tool/cutting_fluids",
                    dustId, 1, null, 0);
        }

        // Raw material + cutting fluid = 1 dust + 33% bonus
        if (dustId != null && mat.getRawTag() != null) {
            addAthanorRecipe("cutting_fluids/dusts_from_raw_" + name,
                    mat.getRawTag(), "neovitae:athanor_tool/cutting_fluids",
                    dustId, 1, dustId, 0.33);
        }
    }

    private static void addAthanorRecipe(String recipePath, String inputTag, String toolTag,
                                          String outputItem, int outputCount,
                                          String chanceItem, double chance) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "neovitae:athanor");

        JsonObject input = new JsonObject();
        input.addProperty("tag", inputTag);
        JsonArray inputs = new JsonArray();
        inputs.add(input);
        recipe.add("inputs", inputs);

        JsonObject tool = new JsonObject();
        tool.addProperty("tag", toolTag);
        recipe.add("tool", tool);

        JsonArray guaranteed = new JsonArray();
        JsonObject mainOutput = new JsonObject();
        mainOutput.addProperty("count", outputCount);
        mainOutput.addProperty("id", outputItem);
        guaranteed.add(mainOutput);
        recipe.add("guaranteed_outputs", guaranteed);

        JsonArray chanced = new JsonArray();
        if (chanceItem != null && chance > 0) {
            JsonObject chanceEntry = new JsonObject();
            chanceEntry.addProperty("chance", chance);
            JsonObject chanceItemObj = new JsonObject();
            chanceItemObj.addProperty("count", 1);
            chanceItemObj.addProperty("id", chanceItem);
            chanceEntry.add("item", chanceItemObj);
            chanced.add(chanceEntry);
        }
        recipe.add("chance_outputs", chanced);

        GENERATED_PACK.putJson(PackType.SERVER_DATA,
                ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "recipe/athanor/" + recipePath + ".json"),
                GSON.toJson(recipe));
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
        modBus.addListener(MaterialRegistry::onAddPackFinders);
        NeoForge.EVENT_BUS.addListener(MaterialRegistry::onPlayerLoggedIn);
    }

    private static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!firstRun) return;
        if (event.getEntity().getServer() == null || event.getEntity().getServer().isDedicatedServer()) return;
        firstRun = false;

        ServerLevel level = event.getEntity().getServer().overworld();

        List<MaterialDefinition> newMaterials = OreDiscoveryHelper.discoverNewMaterials(level);

        if (newMaterials.isEmpty()) {
            NeoVitae.LOGGER.info("[MaterialRegistry] First run: no additional ores found beyond defaults");
            return;
        }

        appendAndSave(newMaterials);
        pendingRestartNotice = true;
        List<String> names = newMaterials.stream().map(MaterialDefinition::getName).toList();
        NeoVitae.LOGGER.info("[MaterialRegistry] First run auto-discovery: added {} materials: {}. Restart for items to appear.",
                newMaterials.size(), String.join(", ", names));
    }

    public static boolean hasPendingRestartNotice() {
        return pendingRestartNotice;
    }

    public static void clearRestartNotice() {
        pendingRestartNotice = false;
    }

    private static void onAddPackFinders(AddPackFindersEvent event) {
        if (GENERATED_PACK == null) return;
        NeoVitae.LOGGER.info("Registering material pack for {}", event.getPackType());

        event.addRepositorySource(consumer -> {
            var locationInfo = new PackLocationInfo(
                    "neovitae_materials",
                    Component.literal("NeoVitae Materials"),
                    PackSource.BUILT_IN,
                    Optional.empty()
            );
            var selectionConfig = new PackSelectionConfig(true, Pack.Position.TOP, false);

            Pack.ResourcesSupplier supplier = new Pack.ResourcesSupplier() {
                @Override
                public PackResources openPrimary(PackLocationInfo info) {
                    return GENERATED_PACK;
                }

                @Override
                public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
                    return GENERATED_PACK;
                }
            };

            Pack pack = Pack.readMetaAndCreate(locationInfo, supplier, event.getPackType(), selectionConfig);
            if (pack != null) {
                consumer.accept(pack);
            }
        });
    }

    private static List<MaterialDefinition> loadConfig() {
        List<MaterialDefinition> defaults = createDefaults();
        Path configFile = FMLPaths.CONFIGDIR.get().resolve("neovitae/materials.json");

        if (!Files.exists(configFile)) {
            firstRun = true;
            writeConfig(configFile, defaults);
            return defaults;
        }

        List<MaterialDefinition> loaded = readConfig(configFile);
        if (loaded != null && !loaded.isEmpty()) {
            return loaded;
        }

        NeoVitae.LOGGER.warn("Materials config was empty or unreadable, using defaults");
        return defaults;
    }

    @Nullable
    private static List<MaterialDefinition> readConfig(Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            Type listType = new TypeToken<List<MaterialDefinition>>() {}.getType();
            List<MaterialDefinition> result = GSON.fromJson(reader, listType);
            NeoVitae.LOGGER.info("Loaded {} materials from config", result != null ? result.size() : 0);
            return result;
        } catch (Exception e) {
            NeoVitae.LOGGER.error("Failed to parse materials config", e);
            return null;
        }
    }

    private static void writeConfig(Path path, List<MaterialDefinition> materials) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(materials, writer);
            }
            NeoVitae.LOGGER.info("Generated default materials config with {} materials", materials.size());
        } catch (Exception e) {
            NeoVitae.LOGGER.error("Failed to write materials config", e);
        }
    }

    public static List<MaterialDefinition> getAllMaterials() {
        return Collections.unmodifiableList(MATERIALS);
    }

    @Nullable
    public static MaterialDefinition getMaterial(String name) {
        return MATERIALS.stream().filter(m -> m.getName().equals(name)).findFirst().orElse(null);
    }

    @Nullable
    public static DeferredHolder<Item, Item> getItem(String materialName, String stage) {
        Map<String, DeferredHolder<Item, Item>> stageMap = ITEM_MAP.get(materialName);
        return stageMap != null ? stageMap.get(stage) : null;
    }

    @Nullable
    public static DeferredHolder<Item, Item> getDust(String materialName) {
        return getItem(materialName, "dust");
    }

    @Nullable
    public static DeferredHolder<Item, Item> getGravel(String materialName) {
        return getItem(materialName, "gravel");
    }

    @Nullable
    public static DeferredHolder<Item, Item> getFragment(String materialName) {
        return getItem(materialName, "fragment");
    }

    public static Collection<DeferredHolder<Item, Item>> getAllItems() {
        List<DeferredHolder<Item, Item>> all = new ArrayList<>();
        ITEM_MAP.values().forEach(map -> all.addAll(map.values()));
        return all;
    }

    public static boolean hasMaterial(String name) {
        return MATERIALS.stream().anyMatch(m -> m.getName().equals(name));
    }

    public static void appendAndSave(List<MaterialDefinition> newMaterials) {
        List<MaterialDefinition> all = new ArrayList<>(MATERIALS);
        all.addAll(newMaterials);
        Path configFile = FMLPaths.CONFIGDIR.get().resolve("neovitae/materials.json");
        writeConfig(configFile, all);
    }

    private static List<MaterialDefinition> createDefaults() {
        List<MaterialDefinition> defaults = new ArrayList<>();
        defaults.add(new MaterialDefinition("iron", "#D8AF93",
                List.of("fragment", "gravel", "dust"),
                "minecraft:iron_ingot", 0.7f,
                "c:ores/iron", "c:raw_materials/iron", "c:ingots/iron",
                null, null));
        defaults.add(new MaterialDefinition("gold", "#FCEE4B",
                List.of("fragment", "gravel", "dust"),
                "minecraft:gold_ingot", 1.0f,
                "c:ores/gold", "c:raw_materials/gold", "c:ingots/gold",
                null, null));
        defaults.add(new MaterialDefinition("copper", "#E77C56",
                List.of("fragment", "gravel", "dust"),
                "minecraft:copper_ingot", 0.7f,
                "c:ores/copper", "c:raw_materials/copper", "c:ingots/copper",
                null, null));
        defaults.add(new MaterialDefinition("coal", "#404040",
                List.of("dust"),
                "minecraft:coal", 0.1f,
                "c:ores/coal", null, null,
                null, null));
        defaults.add(new MaterialDefinition("netherite_scrap", "#755045",
                List.of("fragment", "gravel", "dust"),
                "minecraft:netherite_scrap", 2.0f,
                null, null, null,
                "Ancient Debris",
                Map.of("fragment", "fragment_netherite_scrap", "gravel", "gravel_netherite_scrap")));
        defaults.add(new MaterialDefinition("demonite", "#8CBFB5",
                List.of("fragment", "gravel"),
                null, 0f,
                null, "c:raw_materials/hellforged", null,
                null, null));
        defaults.add(new MaterialDefinition("hellforged", "#99D6CB",
                List.of("dust"),
                "neovitae:ingot_hellforged", 1.0f,
                null, null, "c:ingots/hellforged",
                null, null));
        return defaults;
    }
}
