package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.util.Pair;

public class RitualStandardDungeonEntry extends EntryProvider {

    public RitualStandardDungeonEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/standard_dungeon"))
                .withMultiblockName("Pathway to the Endless Realm")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("standard_dungeon")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Beyond the Threshold");
        this.pageText("This ritual rends the veil wide, opening a full passage into the depths of the [#](4A0080)Demon Realm[#](). What awaits is not a trial; it is a war. The creatures within are far more dangerous than those encountered through the lesser gateway. Arm yourself well, apprentice. Many who step through do not return.");
    }

    @Override
    protected String entryName() {
        return "Pathway to the Endless Realm";
    }

    @Override
    protected String entryDescription() {
        return "Rends the veil wide into the Demon Realm's depths.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.HELLFORGED_INGOT.get());
    }

    @Override
    protected String entryId() {
        return "ritual_standard_dungeon";
    }
}
