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

public class RitualMeteorEntry extends EntryProvider {

    public RitualMeteorEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/meteor"))
                .withMultiblockName("Ritual of Meteo")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("meteor")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Heaven's Wrath");
        this.pageText("With enough [#](8B0000)Essentia Vitae[#]() and ambition, you can tear a celestial body from the void above and hurl it earthward. Drop a suitable offering atop the [#](8B0000)Master Ritual Stone[#](); the ritual consumes it along with an enormous expenditure of [#](8B0000)Essentia Vitae[#](), then calls down a meteor from the sky.\\\n\\\n"
                + "The meteor settles in the air directly above the ritual, the [#](8B0000)lowest block of its sphere resting one block above the Master Ritual Stone[#](). The exact altitude is set by the meteor's outermost sphere radius - heavier offerings call larger meteors that hang higher overhead. When it materialises it detonates with a thunderous shock, leaving a crater of layered resources to mine and process.");

        this.page("offerings", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Offerings to the Sky");
        this.pageText("The composition and scale of the meteor vary with the offering. Standard offerings include [#](8B0000)Diamond[#](), [#](8B0000)Block of Iron[#](), [#](8B0000)Glowstone Dust[#](), and [#](8B0000)Stone[#]()."
                + "\\\n\\\nExotic offerings from other disciplines are also recognized: [#](8B0000)Dragon Bones[#](), [#](8B0000)Certus Quartz[#](), [#](8B0000)Andesite Alloy[#](), [#](8B0000)Copper Wire Coil[#](), [#](8B0000)Prosperity Shard[#](), [#](8B0000)RF Coil[#](), and [#](8B0000)Advanced Alloy[#](), among others.");
    }

    @Override
    protected String entryName() {
        return "Ritual of Meteo";
    }

    @Override
    protected String entryDescription() {
        return "Tears a celestial body from the void above.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.FIRE_CHARGE);
    }

    @Override
    protected String entryId() {
        return "ritual_meteor";
    }
}
