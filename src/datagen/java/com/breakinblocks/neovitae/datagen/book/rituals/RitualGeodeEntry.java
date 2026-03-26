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

public class RitualGeodeEntry extends EntryProvider {

    public RitualGeodeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/geode"))
                .withMultiblockName("Ritual of the Geode's Bounty")
                .withText(this.context().pageText()));
        this.pageText("Use a [#](8B0000)Ritual Diviner[#]() for easier construction.");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("geode")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("This ritual interacts with amethyst geodes, accelerating crystal growth and harvesting mature clusters.");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Will Effects");
        this.pageText("- [#](8B0000)Raw Will[#](): Increases growth speed."
                + "\n\n- [#](8B0000)Corrosive Will[#](): Enables harvest of mature clusters."
                + "\n\n- [#](8B0000)Vengeful Will[#](): Damages nearby mobs."
                + "\n\n- [#](8B0000)Destructive Will[#](): Increases Fortune effect on harvested clusters."
                + "\n\n- [#](8B0000)Steadfast Will[#](): Deposits items into a nearby chest.");
    }

    @Override
    protected String entryName() {
        return "Ritual of the Geode's Bounty";
    }

    @Override
    protected String entryDescription() {
        return "Grows and harvests amethyst clusters.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.AMETHYST_CLUSTER);
    }

    @Override
    protected String entryId() {
        return "ritual_geode";
    }
}
