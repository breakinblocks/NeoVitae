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

public class SelfSacrificeRuneEntry extends EntryProvider {

    public SelfSacrificeRuneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Rune of Self Sacrifice");
        this.pageText("The **Rune of Self Sacrifice** increases the amount of **Life Essence** gained in the "
                + "Blood Altar through means that use a player's health. Each rune gives a bonus of +10% "
                + "additively per rune.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "blood_rune_self_sacrifice")));

        this.page("recipe2", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "blood_rune_self_sac_2"))
                .withText(this.context().pageText()));
        this.pageText("With some **Netherite Scrap** and some **Intricate Hellforged Parts** looted from the "
                + "**Demon Realm**, you can double the power of your **Rune of Self Sacrifice**, increasing "
                + "bonus to an additive +20% per rune.");

        this.page("reversion", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("If you change your mind, you can revert the upgraded rune back to its base version "
                + "in the Alchemical Reaction Chamber (ARC).");
    }

    @Override
    protected String entryName() {
        return "Rune of Self Sacrifice";
    }

    @Override
    protected String entryDescription() {
        return "Increases LP gained from self-sacrifice with the Sacrificial Knife.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.RUNE_SELF_SACRIFICE.asItem());
    }

    @Override
    protected String entryId() {
        return "rune_self_sacrifice";
    }
}
