package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonVariant;
import com.mojang.datafixers.util.Pair;

public class DemonStoneEntry extends EntryProvider {

    public DemonStoneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Stone");
        this.pageText("The [#](4A0080)Demon Realm[#]() is built from a pale-blue material unknown to our "
                + "world, a substance practitioners have taken to calling [#](8B0000)Demon Stone[#](). "
                + "Its creation is deceptively simple: an infusion of ordinary stone with a measure "
                + "of [#](4A0080)Spiritus[#](). Yet the result is something fundamentally alien.");

        this.page("recipe_stone", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Consult JEI for the Demon Stone recipe.");

        this.page("recipe_spread", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("A troubling property of this material: it is [#](4A0080)aggressively transmutative[#](). "
                + "Any attempt to combine Demon Stone with ordinary stone simply yields more Demon Stone, "
                + "as though the material hungers to convert all it touches. Consult JEI for the spread recipes.");

        this.page("variants_info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Coloured Variants");
        this.pageText("Introducing stone to [#](4A0080)Ruina[#](), [#](4A0080)Nihilum[#](), "
                + "[#](4A0080)Invictus[#](), or [#](4A0080)Vindicta[#]() Spiritus produces coloured variants "
                + "of Demon Stone, each carrying the faintest resonance of its aspect. All variants "
                + "can be shaped in a stonecutter into various decorative patterns. Consult JEI for "
                + "variant and spread recipes.");
    }

    @Override
    protected String entryName() {
        return "Demon Stone";
    }

    @Override
    protected String entryDescription() {
        return "The pale foundation of a realm not meant for mortal eyes.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(DungeonBlocks.DUNGEON_EYE.get(DungeonVariant.RAW).asItem());
    }

    @Override
    protected String entryId() {
        return "demon_stone";
    }
}
