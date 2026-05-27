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

public class RitualSentientDowngradeEntry extends EntryProvider {

    public RitualSentientDowngradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/downgrade"))
                .withMultiblockName("Penance of the Leaden Soul")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("downgrade")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Price of Power");
        this.pageText("Throw a piece of [#](8B0000)Sentient Armor[#]() onto the small [#](B8860B)5x2x5[#]() zone "
                + "above the [#](8B0000)Master Ritual Stone[#](). The ritual extracts every upgrade currently "
                + "inscribed on it as a separate [#](8B0000)Upgrade Tome[#](), preserving the accumulated "
                + "experience inside each, then strips the armor piece clean.\\\n\\\n"
                + "This is the [#](2E8B57)preservation[#]() rite. Use it when you want to reshape a chestplate "
                + "without losing the training you have invested.");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Drop one armor piece at a time. The ritual processes whichever piece it finds first, "
                + "extracts every upgrade on it, then deactivates the master stone. To strip another piece, "
                + "reactivate the ritual.\\\n\\\n"
                + "The armor itself is left intact: its enchantments and binding survive. Only the upgrades, "
                + "and their used point budget, are removed.");
    }

    @Override
    protected String entryName() {
        return "Penance of the Leaden Soul";
    }

    @Override
    protected String entryDescription() {
        return "Extracts upgrades from a sentient armor piece as reusable tomes.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.UPGRADE_TOME.get());
    }

    @Override
    protected String entryId() {
        return "ritual_sentient_downgrade";
    }
}
