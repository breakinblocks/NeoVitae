package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class RitualZephyrEntry extends EntryProvider {

    public RitualZephyrEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/zephyr"))
                .withMultiblockName("Call of the Zephyr")
                .withText(this.context().pageText()));
        this.pageText("Use a [#](8B0000)Ritual Diviner[#]() for easier construction.");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withTitle("Ritual Stats")
                .withText(RitualStatsHelper.generateStats("zephyr")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("This ritual collects dropped items within its area of effect and deposits them into a nearby chest. Acts as an area-of-effect item vacuum.");
    }

    @Override
    protected String entryName() {
        return "Call of the Zephyr";
    }

    @Override
    protected String entryDescription() {
        return "Collects dropped items into a chest.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.HOPPER);
    }

    @Override
    protected String entryId() {
        return "ritual_zephyr";
    }
}
