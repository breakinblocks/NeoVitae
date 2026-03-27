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

public class RitualMagneticEntry extends EntryProvider {

    public RitualMagneticEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/magnetism"))
                .withMultiblockName("Ritual of Magnetism")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("magnetism")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Deep Earth Communion");
        this.pageText("This ritual reaches deep beneath the earth, calling ores upward from the stone to the surface. By default it searches a 3-block radius down to bedrock. Place a precious block beneath the [#](8B0000)Master Ritual Stone[#]() to widen its grasp:"
                + "\n\n- [#](8B0000)Block of Iron[#](): 7 blocks."
                + "\n\n- [#](8B0000)Block of Gold[#](): 15 blocks."
                + "\n\n- [#](8B0000)Block of Diamond[#](): 31 blocks.");
    }

    @Override
    protected String entryName() {
        return "Ritual of Magnetism";
    }

    @Override
    protected String entryDescription() {
        return "Summons ores from the deep earth to the surface.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.IRON_ORE);
    }

    @Override
    protected String entryId() {
        return "ritual_magnetic";
    }
}
