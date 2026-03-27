package com.breakinblocks.neovitae.datagen.book.rituals;

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

public class RitualStonesEntry extends EntryProvider {

    public RitualStonesEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Stones");
        this.pageText("[#](8B0000)Ritual Stones[#]() are the foundation upon which every [#](4A0080)ritual circle[#]() is inscribed. Each stone serves as a vessel for elemental resonance, channeling the forces that give your rituals form."
                + "\\\n\\\nThe [#](8B0000)Elemental Inscription Tools[#]() can be used to paint stones by hand; they never break, so feel free to use them for decoration as well as function.");

        this.page("recipes", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual_stone_blank"))
                .withRecipeId2(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual_stone_master")));
    }

    @Override
    protected String entryName() {
        return "Ritual Stones";
    }

    @Override
    protected String entryDescription() {
        return "The foundation of every ritual circle.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.WATER_RITUAL_STONE.asItem());
    }

    @Override
    protected String entryId() {
        return "ritual_stones";
    }
}
