package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class DemonFoodEntry extends EntryProvider {

    public DemonFoodEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tainted Provisions");
        this.pageText("The dungeons offer little in the way of wholesome sustenance, but a resourceful "
                + "vitaemancer can make do with what the dead leave behind.");

        this.page("tainted_flesh", () -> BookSpotlightPageModel.create()
                .withItem(NVItems.TAINTED_FLESH.get())
                .withTitle("Tainted Flesh")
                .withText(this.context().pageText()));
        this.pageText("Dropped by most Daemonium, this dark meat restores [#](2E8B57)4 hunger[#]() but "
                + "carries a [#](8B0000)30%% chance of inflicting Wither I[#]() for 5 seconds. Desperation "
                + "food at best; useful as a crafting base at worst.");

        this.page("vitae_morsel", () -> BookSpotlightPageModel.create()
                .withItem(NVItems.VITAE_MORSEL.get())
                .withTitle("Vitae Morsel")
                .withText(this.context().pageText()));
        this.pageText("Tainted Flesh refined through the [#](8B0000)Hellfire Forge[#]() with a "
                + "[#](8B0000)Weak Blood Shard[#]() and [#](8B0000)Gore-Clotted Fang[#](). Restores "
                + "[#](2E8B57)6 hunger[#]() with high saturation, grants [#](2E8B57)Regeneration I[#]() "
                + "for 10 seconds, and can be eaten at any time. A reliable combat ration.");

        this.page("bottled_spite", () -> BookSpotlightPageModel.create()
                .withItem(NVItems.BOTTLED_SPITE.get())
                .withTitle("Bottled Spite")
                .withText(this.context().pageText()));
        this.pageText("A vile draught brewed from [#](8B0000)Ectoplasmic Residue[#](), "
                + "[#](8B0000)Venomgland Sac[#](), and [#](8B0000)Hollow Gut[#](). Grants "
                + "[#](8B0000)Strength II[#]() for 30 seconds but inflicts [#](B8860B)Mining Fatigue I[#]() "
                + "for 15 seconds. A combat stimulant with a bitter cost.");
    }

    @Override
    protected String entryName() {
        return "Tainted Provisions";
    }

    @Override
    protected String entryDescription() {
        return "Sustenance harvested from the Demon Realm, potent but perilous.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.VITAE_MORSEL.get());
    }

    @Override
    protected String entryId() {
        return "demon_food";
    }
}
