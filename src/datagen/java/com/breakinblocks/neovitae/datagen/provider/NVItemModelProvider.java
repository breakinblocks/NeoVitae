package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.MaterialItem;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.ItemAnointmentProvider;
import com.breakinblocks.neovitae.common.material.MaterialDefinition;
import com.breakinblocks.neovitae.common.material.MaterialRegistry;

import java.util.function.Supplier;

public class NVItemModelProvider extends ItemModelProvider {
    public NVItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, NeoVitae.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Register basic items, excluding items that need custom layered models
        NVItems.BASIC_ITEMS.getEntries().stream()
                .filter(holder -> holder != NVItems.THROWING_DAGGER_TIPPED)
                .filter(holder -> holder != NVItems.ALCHEMY_FLASK)
                .filter(holder -> holder != NVItems.ALCHEMY_FLASK_THROWABLE)
                .filter(holder -> holder != NVItems.ALCHEMY_FLASK_LINGERING)
                .filter(holder -> !(holder.get() instanceof ItemAnointmentProvider))
                .filter(holder -> !(holder.get() instanceof MaterialItem))
                .filter(holder -> holder != NVItems.SIGIL_BLOOD_LIGHT)
                .filter(holder -> !(holder.get() instanceof com.breakinblocks.neovitae.common.item.BloodOrbItem))
                .filter(holder -> !(holder.get() instanceof com.breakinblocks.neovitae.common.item.soul.ISentientTool))
                .filter(holder -> !(holder.get() instanceof net.minecraft.world.item.SpawnEggItem))
                .map(Supplier::get)
                .forEach(this::basicItem);

        // Spawn eggs use the vanilla template_spawn_egg parent model (tinted by egg colors)
        NVItems.BASIC_ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof net.minecraft.world.item.SpawnEggItem)
                .forEach(holder -> getBuilder(holder.getId().getPath())
                        .parent(new ModelFile.UncheckedModelFile("minecraft:item/template_spawn_egg")));
        NVItems.TAB_REQ.getEntries().stream().map(Supplier::get).forEach(this::basicItem);

        // Tipped throwing dagger - 2 layers for potion tint effect
        // Layer 0: dagger texture (untinted)
        // Layer 1: potion overlay (tinted by TippedDaggerColor)
        getBuilder("tipped_throwing_dagger")
                .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
                .texture("layer0", modLoc("item/amethyst_throwing_dagger_partial"))
                .texture("layer1", modLoc("item/dagger_potion"));

        getBuilder("alchemy_flask")
                .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
                .texture("layer0", modLoc("item/potionflask_underlay"))
                .texture("layer1", modLoc("item/potionflask_underlay_top"))
                .texture("layer2", modLoc("item/potionflask_outline"))
                .texture("layer3", modLoc("item/potionflask_overlay"));

        getBuilder("alchemy_flask_throwable")
                .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
                .texture("layer0", modLoc("item/potionflask_underlay"))
                .texture("layer1", modLoc("item/potionflask_underlay_top"))
                .texture("layer2", modLoc("item/potionflask_outline_throwable"))
                .texture("layer3", modLoc("item/potionflask_overlay_throwable"));

        getBuilder("alchemy_flask_lingering")
                .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
                .texture("layer0", modLoc("item/potionflask_underlay"))
                .texture("layer1", modLoc("item/potionflask_underlay_top"))
                .texture("layer2", modLoc("item/potionflask_outline_lingering"))
                .texture("layer3", modLoc("item/potionflask_overlay_lingering"));

        // Anointment items - 3 layers for colored liquid effect
        // Layer 0: alchemic_liquid (tinted by AnointmentColor based on anointment type)
        // Layer 1: alchemic_vial (untinted - varies by tier: base, labeled for _l, greenlabeled for _xl)
        // Layer 2: alchemic_ribbon (untinted - decoration)
        NVItems.BASIC_ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof ItemAnointmentProvider)
                .forEach(holder -> {
                    String path = holder.getId().getPath();
                    // Determine vial texture based on tier suffix
                    String vialTexture;
                    if (path.endsWith("_xl")) {
                        vialTexture = "item/greenlabeledalchemic_vial";
                    } else if (path.endsWith("_l")) {
                        vialTexture = "item/labeledalchemic_vial";
                    } else {
                        vialTexture = "item/alchemic_vial";
                    }
                    getBuilder(path)
                            .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
                            .texture("layer0", modLoc("item/alchemic_liquid"))
                            .texture("layer1", modLoc(vialTexture))
                            .texture("layer2", modLoc("item/alchemic_ribbon"));
                });

        // Blood lamp sigil - layer0 is base sigil (untinted), layer1 is symbol (tinted by dye color)
        getBuilder("sigil_blood_light")
                .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
                .texture("layer0", modLoc("item/base_sigil"))
                .texture("layer1", modLoc("item/base_blood_lamp"));

        // Material items - tinted greyscale base textures from MaterialRegistry
        for (MaterialDefinition mat : MaterialRegistry.getAllMaterials()) {
            for (String stage : mat.getStages()) {
                String itemId = mat.getItemId(stage);
                String baseTexture = switch (stage) {
                    case "fragment" -> "item/base_fragment";
                    case "gravel" -> "item/base_gravel";
                    default -> "item/base_dust";
                };
                getBuilder(itemId)
                        .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
                        .texture("layer0", modLoc(baseTexture));
            }
        }

        // Process WILL_ITEMS - only items that need will type variants (not already-typed monster souls)
        NVItems.WILL_ITEMS.getEntries().forEach(item -> {
            String path = item.getId().getPath();
            // Monster souls are already typed, just use basic item model
            if (path.startsWith("base_spiritus_soul")) {
                basicItem(item.get());
                return;
            }
            // Other will items get variants for each will type
            ItemModelBuilder builder = getBuilder(path).parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"))
                    .texture("layer0", modLoc("item/" + path + "_default"));
            for (SpiritusType type : SpiritusType.values()) {
                ModelFile modelFile = singleTexture(String.format("item/variant/%s_%s", path, type.getSerializedName()), mcLoc("item/handheld"), "layer0", modLoc(String.format("item/%s_%s", path, type.getSerializedName())));
                builder.override().predicate(NeoVitae.TYPE_PROPERTY, type.ordinal()).model(modelFile).end();
            }
        });

        // Blood Stained Glass Pane - flat texture for inventory
        singleTexture("blood_stained_glass_pane", mcLoc("item/generated"), "layer0", modLoc("block/blood_stained_glass")).renderType("translucent");

        // Blood orbs with fill level overlays
        for (var orb : java.util.List.of(NVItems.ORB_WEAK, NVItems.ORB_APPRENTICE, NVItems.ORB_MAGICIAN,
                NVItems.ORB_MASTER, NVItems.ORB_ARCHMAGE, NVItems.ORB_TRANSCENDENT)) {
            String path = orb.getId().getPath();
            ModelFile emptyModel = singleTexture("item/variant/" + path + "_empty",
                    mcLoc("item/generated"), "layer0", modLoc("item/" + path));
            ModelFile[] fillModels = new ModelFile[5];
            for (int i = 0; i < 5; i++) {
                fillModels[i] = getBuilder("item/variant/" + path + "_fill_" + (i + 1))
                        .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
                        .texture("layer0", modLoc("item/" + path))
                        .texture("layer1", modLoc("item/orb_fill_" + (i + 1)));
            }
            ItemModelBuilder orbBuilder = getBuilder(path);
            orbBuilder.override().predicate(NeoVitae.rl("fill_level"), 0).model(emptyModel).end();
            orbBuilder.override().predicate(NeoVitae.rl("fill_level"), 0.01f).model(fillModels[0]).end();
            orbBuilder.override().predicate(NeoVitae.rl("fill_level"), 0.2f).model(fillModels[1]).end();
            orbBuilder.override().predicate(NeoVitae.rl("fill_level"), 0.4f).model(fillModels[2]).end();
            orbBuilder.override().predicate(NeoVitae.rl("fill_level"), 0.6f).model(fillModels[3]).end();
            orbBuilder.override().predicate(NeoVitae.rl("fill_level"), 0.8f).model(fillModels[4]).end();
        }

        ModelFile normalDagger = singleTexture("item/variant/sacrificial_dagger_normal", mcLoc("item/handheld"), "layer0", modLoc("item/sacrificial_dagger"));
        ModelFile chargedDagger = singleTexture("item/variant/sacrificial_dagger_charged", mcLoc("item/handheld"), "layer0", modLoc("item/sacrificial_dagger_charged"));
        ModelFile alternateOrb = singleTexture("item/variant/sacrificial_dagger_alternate", mcLoc("item/generated"), "layer0", modLoc("item/glass_orb"));
        ItemModelBuilder builder = getBuilder(NVItems.SACRIFICIAL_DAGGER.getId().getPath());
        builder.override().predicate(NeoVitae.rl("alternate"), 1).model(alternateOrb).end();
        builder.override().predicate(NeoVitae.INCENSE_PROPERTY, 0).model(normalDagger).end();
        builder.override().predicate(NeoVitae.INCENSE_PROPERTY, 1).model(chargedDagger).end();

        // Sentient tools - use handheld parent for correct held rendering
        singleTexture("sentient_sword", mcLoc("item/handheld"), "layer0", modLoc("item/sentient_sword"));
        singleTexture("sentient_axe", mcLoc("item/handheld"), "layer0", modLoc("item/sentient_axe"));
        singleTexture("sentient_pickaxe", mcLoc("item/handheld"), "layer0", modLoc("item/sentient_pickaxe"));
        singleTexture("sentient_shovel", mcLoc("item/handheld"), "layer0", modLoc("item/sentient_shovel"));
        singleTexture("sentient_scythe", mcLoc("item/handheld"), "layer0", modLoc("item/sentient_scythe"));

        // Array effect dummy items - use their alchemy array textures
        singleTexture("array_bounce", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/bouncearray"));
        singleTexture("array_spike", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/spikearray"));
        singleTexture("array_updraft", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/updraftarray"));
        singleTexture("array_movement", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/movementarray"));
        singleTexture("array_day", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/sunarray"));
        singleTexture("array_night", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/moonarray"));
        singleTexture("array_elevator", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/elevatorarray"));
        singleTexture("array_repulsion", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/repulsionarray"));
        singleTexture("array_collection", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/collectionarray"));
        singleTexture("array_light", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/lightarray"));
        singleTexture("array_furnace", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/furnacearray"));
        singleTexture("array_rain", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/rainarray"));
        singleTexture("array_growth", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/growtharray"));
        singleTexture("array_freeze", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/freezearray"));
        singleTexture("array_signal", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/signalarray"));
        singleTexture("array_trigger", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/triggerarray"));
        singleTexture("array_spirit_siphon", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/spiritsiphonarray"));
        singleTexture("array_deflection", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/deflectionarray"));
        singleTexture("array_endless_fountain", mcLoc("item/generated"), "layer0", modLoc("models/alchemyarrays/fountainarray"));
    }
}
