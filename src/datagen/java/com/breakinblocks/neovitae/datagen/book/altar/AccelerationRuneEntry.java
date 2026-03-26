package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.util.Pair;

public class AccelerationRuneEntry extends EntryProvider {

    public AccelerationRuneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Acceleration Rune");
        this.pageText("The [#](8B0000)Acceleration Rune[#]() collapses time around certain altar operations. "
                + "The [#](8B0000)Charging Rune[#]() and [#](8B0000)Displacement Rune[#]() normally pulse once every 20 ticks; "
                + "each Acceleration Rune shaves one tick from that delay, down to a minimum of a single tick "
                + "between pulses. The altar's secondary rhythms become a frantic heartbeat.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "rune_acceleration")));

    }

    @Override
    protected String entryName() {
        return "Acceleration Rune";
    }

    @Override
    protected String entryDescription() {
        return "Collapses the interval between Charging and Displacement pulses.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.RUNE_ACCELERATION.asItem());
    }

    @Override
    protected String entryId() {
        return "rune_acceleration";
    }
}
