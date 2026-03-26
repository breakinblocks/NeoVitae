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

public class RitualGroundingEntry extends EntryProvider {

    public RitualGroundingEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/grounding"))
                .withMultiblockName("The Sinner's Burden")
                .withText(this.context().pageText()));
        this.pageText("Use a [#](8B0000)Ritual Diviner[#]() for easier construction.");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("grounding")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("This ritual prevents flight within its area, grounding any flying entities.");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Will Effects");
        this.pageText("- [#](8B0000)Raw Will[#](): Increases the grounding effect potency."
                + "\n\n- [#](8B0000)Corrosive Will[#](): Behaves similarly to the [#](8B0000)Suspended[#]() potion effect."
                + "\n\n- [#](8B0000)Vengeful Will[#](): Applies [#](8B0000)Levitation[#]() to entities."
                + "\n\n- [#](8B0000)Destructive Will[#](): Applies the [#](8B0000)Heavy Heart[#]() effect."
                + "\n\n- [#](8B0000)Steadfast Will[#](): Increases the area of effect.");
    }

    @Override
    protected String entryName() {
        return "The Sinner's Burden";
    }

    @Override
    protected String entryDescription() {
        return "Prevents flight within the ritual area.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.DIRT);
    }

    @Override
    protected String entryId() {
        return "ritual_grounding";
    }
}
