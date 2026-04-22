package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class RitualExpulsionEntry extends EntryProvider {

    public RitualExpulsionEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/expulsion"))
                .withMultiblockName("Aura of Expulsion")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("expulsion")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Warding Gale");
        this.pageText("Where the Ritual of Binding imprisons, this circle repels. A wave of [#](4A0080)vitaemantic force[#]() radiates outward from the [#](8B0000)Master Ritual Stone[#](), driving all creatures beyond its boundaries. Practitioners remain unaffected, allowing you to ward your sanctum against uninvited guests.");
    }

    @Override
    protected String entryName() {
        return "Aura of Expulsion";
    }

    @Override
    protected String entryDescription() {
        return "Drives all creatures from your sanctum.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SHIELD);
    }

    @Override
    protected String entryId() {
        return "ritual_expulsion";
    }
}
