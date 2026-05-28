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

public class RitualSimpleDungeonEntry extends EntryProvider {

    public RitualSimpleDungeonEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/simple_dungeon"))
                .withMultiblockName("Breaching the Edge of Demon Realm")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("simple_dungeon")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("A Crack in the Veil");
        this.pageText("A [#](2E8B57)one-shot[#]() rite. It consumes a large pool of [#](8B0000)EV[#]() and "
                + "assembles a complete [#](8B0000)Starter tier dungeon[#]() structure at the Master Ritual Stone, "
                + "the entry tier of the Demon Realm experience.\\\n\\\n"
                + "Choose your spot carefully; the dungeon is placed in the world, not opened as a portal. "
                + "Once assembled, the ritual ends and the Master Ritual Stone deactivates.");
    }

    @Override
    protected String entryName() {
        return "Breaching the Edge of Demon Realm";
    }

    @Override
    protected String entryDescription() {
        return "Generates a Starter tier dungeon at the ritual stone.";
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
