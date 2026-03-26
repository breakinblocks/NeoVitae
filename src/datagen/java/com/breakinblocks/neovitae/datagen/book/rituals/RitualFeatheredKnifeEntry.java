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

public class RitualFeatheredKnifeEntry extends EntryProvider {

    public RitualFeatheredKnifeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/feathered_knife"))
                .withMultiblockName("Ritual of the Feathered Knife")
                .withText(this.context().pageText()));
        this.pageText("Use a [#](8B0000)Ritual Diviner [Dusk][#]() for easier construction.");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withTitle("Ritual Stats")
                .withText(RitualStatsHelper.generateStats("feathered_knife")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("This ritual drains health from nearby players and converts it into [#](8B0000)LP[#]() deposited into a nearby [#](8B0000)Blood Altar[#](). The efficiency is affected by [#](8B0000)Runes of Self Sacrifice[#]() and the [#](8B0000)Tough Palms[#]() Living Armor upgrade.");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Will Effects");
        this.pageText("- [#](8B0000)Raw Will[#](): Increases the LP gained per health point."
                + "\n- [#](8B0000)Corrosive Will[#](): Applies the [#](8B0000)Incense Bonus[#]() from a nearby [#](8B0000)Incense Altar[#]()."
                + "\n- [#](8B0000)Vengeful Will[#](): When combined with [#](8B0000)Steadfast[#]() will, increases drain rate."
                + "\n- [#](8B0000)Destructive Will[#](): Increases the maximum health that can be drained per tick."
                + "\n- [#](8B0000)Steadfast Will[#](): Prevents the ritual from killing the player.");
    }

    @Override
    protected String entryName() {
        return "Ritual of the Feathered Knife";
    }

    @Override
    protected String entryDescription() {
        return "Drains player health for LP.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SACRIFICIAL_DAGGER.get());
    }

    @Override
    protected String entryId() {
        return "ritual_feathered_knife";
    }
}
