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

public class ReinforcedAugCapacityRuneEntry extends EntryProvider {

    public ReinforcedAugCapacityRuneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Reinforced Rune of Aug. Capacity");
        this.pageText("With some [#](8B0000)Netherite Scrap[#]() and some [#](8B0000)Intricate Hellforged Parts[#]() looted from the "
                + "[#](8B0000)Demon Realm[#](), you can double the power of your [#](8B0000)Rune of Augmented Capacity[#](), increasing "
                + "the capacity of the [#](8B0000)Ara Vitae[#]() by a multiplicative +15%% per rune.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "rune_2_capacity_augmented")));

        this.page("reversion", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("If you change your mind, you can revert the upgraded rune back to its base version "
                + "in the Athanor .");
    }

    @Override
    protected String entryName() {
        return "Reinforced Rune of Aug. Capacity";
    }

    @Override
    protected String entryDescription() {
        return "A more powerful Rune of Augmented Capacity.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.RUNE_2_CAPACITY_AUGMENTED.asItem());
    }

    @Override
    protected String entryId() {
        return "rune_2_aug_capacity";
    }
}
