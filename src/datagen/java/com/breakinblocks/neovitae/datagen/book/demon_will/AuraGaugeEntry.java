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
        this.pageTitle("Demon Will Aura Gauge");
        this.pageText("While in the player's inventory, the **Demon Will Aura Gauge** will display a HUD element "
                + "to show how much Demon Will is in the local **Aura**.\n\n"
                + "From top to bottom, the measured Will aspects are:\n"
                + "- Raw\n"
                + "- Corrosive\n"
                + "- Steadfast\n"
                + "- Destructive\n"
                + "- Vengeful");

        this.page("recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Will Aura Gauge");
        this.pageText("Craft the Demon Will Aura Gauge in the Hellfire Forge.\n\n"
                + "See overleaf for an image of the Gauge's HUD Element.");

        this.page("hud_image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/demon_will/demon_will_aura_gauge.png"))
                .withTitle("Aura Gauge HUD Element")
                .withBorder(true));

        this.page("details", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("This gauge will appear in the top left of your screen. The coloured bars will give you "
                + "a good estimate of how much of each **Will Aspect** is in the current chunk.\n\n"
                + "You can hold sneak to get a numerical value for each Aspect, between 1 and 100 Will for "
                + "each. 100 is the maximum amount of any one aspect of Will that a chunk can have in it.");
    }

    @Override
    protected String entryName() {
        return "Demon Will Aura Gauge";
    }

    @Override
    protected String entryDescription() {
        return "A HUD element that measures Demon Will in the local aura.";
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
