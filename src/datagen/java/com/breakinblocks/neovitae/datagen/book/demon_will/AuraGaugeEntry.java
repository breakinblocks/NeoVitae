package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookImagePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class AuraGaugeEntry extends EntryProvider {

    public AuraGaugeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Aura Gauge");
        this.pageText("The [#](4A0080)Aura[#]() is invisible to the naked eye, but the trained practitioner need not "
                + "rely on guesswork. While the [#](8B0000)Demon Will Aura Gauge[#]() rests in your inventory, it projects "
                + "a spectral reading onto your vision.\\\n\\\n"
                + "From top to bottom, the bars measure:\n\n"
                + "- Raw\n\n"
                + "- Corrosive\n\n"
                + "- Steadfast\n\n"
                + "- Destructive\n\n"
                + "- Vengeful");

        this.page("recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Aura Gauge");
        this.pageText("See overleaf for an image of the gauge's spectral overlay.");

        this.page("hud_image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/demon_will/demon_will_aura_gauge.png"))
                .withTitle("Aura Gauge Overlay")
                .withBorder(true));

        this.page("details", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The overlay appears at the top-left of your vision. The colored bars offer a quick "
                + "estimate of each [#](4A0080)Aspect's[#]() concentration in the current chunk.\\\n\\\n"
                + "[#](2E8B57)Hold sneak for a precise numerical reading -- values range from 0 to 100 for each "
                + "Aspect. 100 is the maximum a single chunk can contain.[#]()");
    }

    @Override
    protected String entryName() {
        return "Aura Gauge";
    }

    @Override
    protected String entryDescription() {
        return "A spectral instrument for reading the Aura's hidden currents.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.DEMON_WILL_GAUGE.get());
    }

    @Override
    protected String entryId() {
        return "aura_gauge";
    }
}
