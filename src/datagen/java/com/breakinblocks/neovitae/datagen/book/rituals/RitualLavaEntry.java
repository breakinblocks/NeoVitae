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

public class RitualLavaEntry extends EntryProvider {

    public RitualLavaEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/lava"))
                .withMultiblockName("Serenade of the Nether")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("lava")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Infernal Invocation");
        this.pageText("This ritual tears open a seam to the molten depths, conjuring lava source blocks above the [#](8B0000)Master Ritual Stone[#](). The heat it radiates is palpable — handle with the respect due to the Nether's own blood.");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Will Resonance");
        this.pageText("- [#](8B0000)Raw Will[#](): Channels lava directly into a tank placed above the ritual, reducing [#](8B0000)Essentia Vitae[#]() cost."
                + "\n\n- [#](8B0000)Corrosive Will[#](): Sets nearby hostile creatures ablaze."
                + "\n\n- [#](8B0000)Vengeful Will[#](): Marks hostile mobs with [#](8B0000)Fire Fuse[#](). When it expires, they detonate spectacularly."
                + "\n\n- [#](8B0000)Destructive Will[#](): Accelerates the rate of lava conjuration."
                + "\n\n- [#](8B0000)Steadfast Will[#](): Wraps nearby practitioners in [#](8B0000)Fire Resistance[#]().");
    }

    @Override
    protected String entryName() {
        return "Serenade of the Nether";
    }

    @Override
    protected String entryDescription() {
        return "Tears open a seam to the molten depths.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.LAVA_BUCKET);
    }

    @Override
    protected String entryId() {
        return "ritual_lava";
    }
}
