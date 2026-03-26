package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class RitualMagneticEntry extends EntryProvider {

    public RitualMagneticEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/magnetism"))
                .withMultiblockName("Ritual of Magnetism")
                .withText(this.context().pageText()));
        this.pageText("Use a [#](8B0000)Ritual Diviner[#]() for easier construction.");

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("By default, the [#](8B0000)Ritual of Magnetism[#]() searches down to bedrock in a radius of 3 blocks out from the [#](8B0000)Master Ritual Stone[#]() for ores to collect. This can be augmented by placing an expensive block directly underneath the MRS, as follows:"
                + "\n- [#](8B0000)Block of Iron[#]() - 7 blocks."
                + "\n- [#](8B0000)Block of Gold[#]() - 15 blocks."
                + "\n- [#](8B0000)Block of Diamond[#]() - 31 blocks.");
    }

    @Override
    protected String entryName() {
        return "Ritual of Magnetism";
    }

    @Override
    protected String entryDescription() {
        return "Pulls ores up from underground.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.IRON_ORE);
    }

    @Override
    protected String entryId() {
        return "ritual_magnetic";
    }
}
