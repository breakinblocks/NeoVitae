package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class RitualEllipseEntry extends EntryProvider {

    public RitualEllipseEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/ellipse"))
                .withMultiblockName("Focus of the Ellipsoid")
                .withText(this.context().pageText()));
        this.pageText("Use a **Ritual Diviner [Dusk]** for easier construction.");

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("This ritual is purely aesthetic in nature, designed for the budding, yet lazy, builder that resides in all of us. It's particularly good for building complicated shapes, such as the mighty gold dome above your Evil Lair. ...You do have one of those, right?"
                + "\\\n\\\nThe ritual takes blocks from a nearby chest and places them in an ellipsoidal shape.");
    }

    @Override
    protected String entryName() {
        return "Focus of the Ellipsoid";
    }

    @Override
    protected String entryDescription() {
        return "Builds ellipsoidal shapes from blocks.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.BUCKET);
    }

    @Override
    protected String entryId() {
        return "ritual_ellipse";
    }
}
