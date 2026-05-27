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

public class RitualMagneticEntry extends EntryProvider {

    public RitualMagneticEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/magnetism"))
                .withMultiblockName("Ritual of Magnetism")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("magnetism")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Deep Earth Communion");
        this.pageText("A persistent pulling field. Every loose [#](8B0000)item entity[#]() within a [#](B8860B)21x7x21[#]() "
                + "box centered on the [#](8B0000)Master Ritual Stone[#]() is dragged toward the stone, letting "
                + "you funnel mob drops, ore-processing outputs, or any item rain into a single collection point.\\\n\\\n"
                + "The area can be widened with the [#](8B0000)Ritual Reader[#]() (Define Area mode), at proportional EV cost.");
    }

    @Override
    protected String entryName() {
        return "Ritual of Magnetism";
    }

    @Override
    protected String entryDescription() {
        return "Pulls loose items toward the ritual stone.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.IRON_INGOT);
    }

    @Override
    protected String entryId() {
        return "ritual_magnetic";
    }
}
