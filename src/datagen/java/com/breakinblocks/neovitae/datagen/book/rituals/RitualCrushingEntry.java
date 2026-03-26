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

public class RitualCrushingEntry extends EntryProvider {

    public RitualCrushingEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/crushing"))
                .withMultiblockName("Ritual of the Crusher")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("crushing")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Grinding Pressure");
        this.pageText("This ritual exerts an invisible, crushing force upon the blocks within its domain, reducing them to their component drops and depositing the results into a nearby chest. A methodical, relentless destroyer.");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Will Resonance");
        this.pageText("- [#](8B0000)Raw Will[#](): Increases the number of blocks shattered per tick."
                + "\n\n- [#](8B0000)Corrosive Will[#](): Applies [#](8B0000)Silk Touch[#]() to broken blocks. Requires [#](8B0000)Cutting Fluid[#]() in the input chest."
                + "\n\n- [#](8B0000)Vengeful Will[#](): Compresses drops — coal becomes blocks, dust becomes ingots."
                + "\n\n- [#](8B0000)Destructive Will[#](): Applies [#](8B0000)Fortune III[#]() to all broken blocks."
                + "\n\n- [#](8B0000)Steadfast Will[#](): Applies both [#](8B0000)Silk Touch[#]() and [#](8B0000)Fortune[#]() simultaneously — a potent combination.");
    }

    @Override
    protected String entryName() {
        return "Ritual of the Crusher";
    }

    @Override
    protected String entryDescription() {
        return "Grinds blocks to dust and collects the spoils.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.IRON_PICKAXE);
    }

    @Override
    protected String entryId() {
        return "ritual_crushing";
    }
}
