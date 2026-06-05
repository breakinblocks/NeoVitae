package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class RitualLavaEntry extends EntryProvider {

    public RitualLavaEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/lava"))
                .withMultiblockName("Serenade of the Nether")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("lava")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Infernal Invocation");
        this.pageText("This ritual tears open a seam to the molten depths, conjuring lava source blocks above the [#](8B0000)Master Ritual Stone[#](). The heat it radiates is palpable; handle with the respect due to the Nether's own blood.");

        this.page("tank_fill", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Channeled Reservoir");
        this.pageText("Place any fluid container (a [#](8B0000)Blood Tank[#]() or compatible tank from another tradition) directly above the [#](8B0000)Master Ritual Stone[#]() and the ritual will pipe one bucket of lava into it each cycle instead of spilling a source block into the world."
                + "\n\nWhen the vessel has no room for another bucket, the ritual grows patient: it probes the container less and less frequently, doubling the wait between attempts, so a sealed tank never burdens the server. The moment space reappears it ramps straight back up to its normal cadence of one bucket every ten ticks.");

        this.page("spiritus_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spiritus Resonance");
        this.pageText("- [#](8B0000)Raw Spiritus[#](): Reduces the [#](8B0000)Essentia Vitae[#]() cost of each lava block or bucket conjured."
                + "\n\n- [#](8B0000)Spiritus Ruina[#](): Sets nearby hostile creatures ablaze."
                + "\n\n- [#](8B0000)Spiritus Vindicta[#](): Marks hostile mobs with [#](8B0000)Fire Fuse[#](). When it expires, they detonate spectacularly."
                + "\n\n- [#](8B0000)Spiritus Invictus[#](): Wraps nearby practitioners in [#](8B0000)Fire Resistance[#]().");
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
    protected GuiSprite entryBackground() {
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
