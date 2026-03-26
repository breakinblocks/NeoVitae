package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.ItemAnointmentProvider;

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

        // Alchemy flask - 3 layers for potion tint effect
        // Layer 0: underlay (tinted by FlaskColor)
        // Layer 1: outline (untinted)
        // Layer 2: overlay (untinted)
        getBuilder("alchemy_flask")
                .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
                .texture("layer0", modLoc("item/potionflask_underlay"))
                .texture("layer1", modLoc("item/potionflask_outline"))
                .texture("layer2", modLoc("item/potionflask_overlay"));

        // Throwable flask - same 3 layer pattern with throwable variants
        getBuilder("alchemy_flask_throwable")
                .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
                .texture("layer0", modLoc("item/potionflask_underlay"))
                .texture("layer1", modLoc("item/potionflask_outline_throwable"))
                .texture("layer2", modLoc("item/potionflask_overlay_throwable"));

        // Lingering flask - same 3 layer pattern with lingering variants
        getBuilder("alchemy_flask_lingering")
                .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
                .texture("layer0", modLoc("item/potionflask_underlay"))
                .texture("layer1", modLoc("item/potionflask_outline_lingering"))
                .texture("layer2", modLoc("item/potionflask_overlay_lingering"));

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

        // Process WILL_ITEMS - only items that need will type variants (not already-typed monster souls)
        NVItems.WILL_ITEMS.getEntries().forEach(item -> {
            String path = item.getId().getPath();
            // Monster souls are already typed, just use basic item model
            if (path.startsWith("basemonstersoul")) {
                basicItem(item.get());
                return;
            }
            // Other will items get variants for each will type
            ItemModelBuilder builder = getBuilder(path);
            for (EnumWillType type : EnumWillType.values()) {
                ModelFile modelFile = singleTexture(String.format("item/variant/%s_%s", path, type.getSerializedName()), mcLoc("item/handheld"), "layer0", modLoc(String.format("item/%s_%s", path, type.getSerializedName())));
                builder.override().predicate(NeoVitae.TYPE_PROPERTY, type.ordinal()).model(modelFile).end();
            }
        });

        ItemModelBuilder builder = getBuilder(NVItems.LAMINA_MALEFICUS.getId().getPath());
        ModelFile normalDagger = singleTexture("item/variant/lamina_maleficus_normal", mcLoc("item/handheld"), "layer0", modLoc("item/lamina_maleficus"));
        ModelFile chargedDagger = singleTexture("item/variant/lamina_maleficus_charged", mcLoc("item/handheld"), "layer0", modLoc("item/lamina_maleficus_charged"));
        builder.override().predicate(NeoVitae.INCENSE_PROPERTY, 0).model(normalDagger).end();
        builder.override().predicate(NeoVitae.INCENSE_PROPERTY, 1).model(chargedDagger).end();
    }
}
