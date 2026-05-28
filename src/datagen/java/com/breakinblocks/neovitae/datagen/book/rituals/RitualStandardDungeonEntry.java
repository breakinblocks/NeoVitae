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
                .withMultiblockName("Highway to Hell")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("standard_dungeon")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Beyond the Threshold");
        this.pageText("This rite forges a [#](8B0000)permanent gateway[#]() to the [#](8B0000)Endless tier "
                + "dungeon[#](), a vast procedural realm complete with [#](8B0000)The Mines[#](), the Foreman "
                + "fight, and aspected loot. Unlike the lesser breach, the Endless dungeon [#](8B0000)goes on "
                + "forever[#](); you may return to it as often as you wish, and the deeper you delve, the more "
                + "the realm reveals.\\\n\\\n"
                + "The ritual consumes a tremendous pool of [#](8B0000)EV[#]() to tear and stabilise the "
                + "gateway. Arm yourself well, apprentice; what waits within rewards the prepared and ruins "
                + "the rest.");
    }

    @Override
    protected String entryName() {
        return "Highway to Hell";
    }

    @Override
    protected String entryDescription() {
        return "Opens a permanent gateway to the Endless tier dungeon.";
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
