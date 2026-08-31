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

public class RitualPenanceEntry extends EntryProvider {

    public RitualPenanceEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/penance"))
                .withMultiblockName("Ritual of Sentient Penance")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Tenebrae] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("penance")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Willing Burden");
        this.pageText("Strength has a price, and here you choose to pay it in advance. This rite "
                + "inscribes [#](8B0000)Downgrades[#]() onto worn Sentient Armor: deliberate curses "
                + "that weaken you in one respect and, in exchange, [#](4A0080)free Upgrade "
                + "Points[#]() for the abilities you value more.\\\n\\\n"
                + "Every curse and its catalyst item is cataloged under [#](8B0000)Downgrades[#]() "
                + "in the Alchemy Arrays chapter.");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Stand upon the [#](8B0000)Master Ritual Stone[#]() wearing your Sentient "
                + "chestplate and throw a downgrade [#](8B0000)catalyst[#]() onto the small "
                + "[#](B8860B)5x2x5[#]() zone above the stone. Each pulse consumes one catalyst and "
                + "inscribes one level of the matching curse; keep standing there with more "
                + "catalysts to deepen it further.\\\n\\\n"
                + "To repent, run the chestplate through the [#](8B0000)Sentient Extraction[#]() "
                + "ritual; the curse departs as a tome, taking its freed points with it.");
    }

    @Override
    protected String entryName() {
        return "Ritual of Sentient Penance";
    }

    @Override
    protected String entryDescription() {
        return "Inscribes chosen downgrades onto Sentient Armor, freeing Upgrade Points.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.WITHER_ROSE);
    }

    @Override
    protected String entryId() {
        return "ritual_penance";
    }
}
