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

public class RitualSphereCreateEntry extends EntryProvider {

    public RitualSphereCreateEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/sphere"))
                .withMultiblockName("Dawn of the New Moon")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("sphere")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Phantom Moon");
        this.pageText("This ritual conjures a hollow sphere of [#](4A0080)phantom matter[#]() centered above the [#](8B0000)Master Ritual Stone[#](), a translucent shell that hangs in the air like a captured moon. The sphere's radius can be adjusted with the [#](8B0000)Ritual Tinkerer[#](), though the [#](8B0000)Essentia Vitae[#]() cost scales accordingly.");
    }

    @Override
    protected String entryName() {
        return "Dawn of the New Moon";
    }

    @Override
    protected String entryDescription() {
        return "Conjures a hollow sphere of spectral matter.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.ENDER_PEARL);
    }

    @Override
    protected String entryId() {
        return "ritual_sphere_create";
    }
}
