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

public class ChargingRuneEntry extends EntryProvider {

    public ChargingRuneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Charging Rune");
        this.pageText("The **Charging Rune** is a unique Rune upgrade. When the **Blood Altar** is not crafting "
                + "nor filling a **Blood Orb**, it will syphon **LP** from the Altar to charge an internal buffer. "
                + "When an item is next placed inside of the Altar, it will instantaneously consume the stored "
                + "charge and apply it to the crafting of the item at a 1:1 ratio.");

        this.page("formula", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The Blood Altar does a charging tick once per 20 in-game ticks, which is reduced by 1 "
                + "per **Acceleration Rune**.\n\nThe speed that the Blood Altar charges at per charging tick is: "
                + "[**10LP** x **Charging Runes** x (1 + **Speed Runes**/10)]\n\nThe maximum charge that a Blood "
                + "Altar can hold is **1000 LP** per **Charging Rune**, which is then multiplied by: "
                + "[(capacity of the main Blood Altar tank)/20000] if that value is above 1.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "blood_rune_charging")));

        this.page("recipe2", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "blood_rune_charging_2"))
                .withText(this.context().pageText()));
        this.pageText("With some **Netherite Scrap** and some **Intricate Hellforged Parts** looted from the "
                + "**Demon Realm**, you can double the power of your **Charging Rune**, both in terms of "
                + "capacity and speed.");

        this.page("reversion", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("If you change your mind, you can revert the upgraded rune back to its base version "
                + "in the Alchemical Reaction Chamber (ARC).");
    }

    @Override
    protected String entryName() {
        return "Charging Rune";
    }

    @Override
    protected String entryDescription() {
        return "Pre-charges LP for instant crafting when items are inserted.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.RUNE_CHARGING.asItem());
    }

    @Override
    protected String entryId() {
        return "rune_charging";
    }
}
