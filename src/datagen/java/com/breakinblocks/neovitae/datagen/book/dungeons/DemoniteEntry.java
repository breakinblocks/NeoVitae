package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.Identifier;

public class DemoniteEntry extends EntryProvider {

    public DemoniteEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demonite");
        this.pageText("[#](8B0000)Demonite Ore[#]() exists only within [#](8B0000)The Mines[#]() of the Endless Realm, "
                + "accessible after locating a [#](8B0000)Foreman's Key[#]() and unsealing the [#](4A0080)Spatial Distortion[#]() "
                + "that guards the passage. This ore yields [#](8B0000)Hellforged Ingots[#]() or "
                + "[#](8B0000)Hellforged Sand[#]() through various processing methods.\\\n\\\n"
                + "[#](2E8B57)A Silk Touch enchantment harvests the ore block itself. Without it, you receive "
                + "clumps of Raw Demonite.[#]()");

        this.page("smelting", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Hellforged Ingot");
        this.pageText("Both the raw ore and its mined clumps can be smelted into [#](8B0000)Hellforged Ingots[#](), "
                + "the refined essence of demonic metallurgy. Consult JEI for these smelting recipes.");

        this.page("athanor_dust", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Hellforged Sand");
        this.pageText("[#](8B0000)Hellforged Sand[#]() can be produced within the [#](8B0000)Athanor[#]() from gravel, "
                + "ore, or raw demonite. This powdered form is essential for advanced potion-craft.");

        this.page("athanor_processing", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demonite Processing");
        this.pageText("The [#](8B0000)Athanor[#]() can further process demonite into fragments and gravel, "
                + "offering multiple avenues to extract value from each piece of ore you wrest from the mines.");

        this.page("hellforged_block", () -> BookCraftingRecipePageModel.create()
                .withTitle1("Hellforged Block")
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "hellforged_block_from_ingots"))
                .withText(this.context().pageText()));
        this.pageText("Consult JEI for the Raw Hellforged Block recipe.");

        this.page("decorative", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Decorative Variants");
        this.pageText("Hellforged Blocks can be shaped in a [#](8B0000)stonecutter[#]() into variants faintly "
                + "tainted with [#](4A0080)Corrosive[#](), [#](4A0080)Destructive[#](), [#](4A0080)Steadfast[#](), "
                + "or [#](4A0080)Vengeful[#]() Spiritus. Each decorative variant can be deconstructed back into "
                + "9 [#](8B0000)Hellforged Ingots[#]().");
    }

    @Override
    protected String entryName() {
        return "Demonite";
    }

    @Override
    protected String entryDescription() {
        return "A dark ore found only in the deepest tunnels of the Demon Realm.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(DungeonBlocks.DUNGEON_ORE.asItem());
    }

    @Override
    protected String entryId() {
        return "demonite";
    }
}
