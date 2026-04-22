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
import net.minecraft.resources.Identifier;
import com.mojang.datafixers.util.Pair;

public class RitualSimpleDungeonEntry extends EntryProvider {

    public RitualSimpleDungeonEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/simple_dungeon"))
                .withMultiblockName("Edge of the Hidden Realm")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("simple_dungeon")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("A Crack in the Veil");
        this.pageText("This ritual tears a narrow fissure in the fabric of reality, opening a gateway to the [#](4A0080)Demon Realm[#](), a treacherous dimension teeming with hostile entities and coveted resources. This lesser portal provides an introductory trial for those who dare to test their strength against what lurks beyond.");
    }

    @Override
    protected String entryName() {
        return "Edge of the Hidden Realm";
    }

    @Override
    protected String entryDescription() {
        return "Tears a narrow fissure into the Demon Realm.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIMPLE_KEY.get());
    }

    @Override
    protected String entryId() {
        return "ritual_simple_dungeon";
    }
}
