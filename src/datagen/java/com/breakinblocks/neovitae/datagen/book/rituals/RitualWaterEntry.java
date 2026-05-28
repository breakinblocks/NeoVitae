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

public class RitualWaterEntry extends EntryProvider {

    public RitualWaterEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/water"))
                .withMultiblockName("Ritual of the Full Spring")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("water")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Spring Eternal");
        this.pageText("Among the first rituals a young Vitaemancer learns, this circle conjures water from nothing, placing source blocks above the [#](8B0000)Master Ritual Stone[#](). Though humble in its purpose, a spring that never runs dry is a gift not to be underestimated.");

        this.page("tank_fill", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tank Filling");
        this.pageText("While any [#](8B0000)Raw Spiritus[#]() aura is present in the chunk, the ritual also fills "
                + "any [#](8B0000)fluid tank[#]() placed directly above the Master Ritual Stone with water, [#](8B0000)1,000 mB per refresh[#](). "
                + "A small expansion that turns the spring from a decoration into a quiet supply line for "
                + "alchemy, brewing, or anything else that drinks water.");
    }

    @Override
    protected String entryName() {
        return "Ritual of the Full Spring";
    }

    @Override
    protected String entryDescription() {
        return "Conjures water from the aether itself.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.WATER_BUCKET);
    }

    @Override
    protected String entryId() {
        return "ritual_water";
    }
}
