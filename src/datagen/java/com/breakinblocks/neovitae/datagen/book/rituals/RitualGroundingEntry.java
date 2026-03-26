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
        this.pageText("[#](2E8B57)Use a Ritual Diviner for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("grounding")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Weight of Guilt");
        this.pageText("This circle enforces a cruel gravity upon all within its domain. Wings fail. Enchantments falter. Any entity that dares take flight is dragged back to the earth, as though the world itself refuses to release them.");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Will Resonance");
        this.pageText("- [#](8B0000)Raw Will[#](): Strengthens the grounding effect."
                + "\n\n- [#](8B0000)Corrosive Will[#](): Applies the [#](8B0000)Suspended[#]() effect, halting vertical movement entirely."
                + "\n\n- [#](8B0000)Vengeful Will[#](): Inflicts [#](8B0000)Levitation[#]() — a cruel irony, dragging entities skyward against their will."
                + "\n\n- [#](8B0000)Destructive Will[#](): Applies the [#](8B0000)Heavy Heart[#]() affliction."
                + "\n\n- [#](8B0000)Steadfast Will[#](): Expands the area of influence.");
    }

    @Override
    protected String entryName() {
        return "The Sinner's Burden";
    }

    @Override
    protected String entryDescription() {
        return "Denies the sky to all within its domain.";
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
