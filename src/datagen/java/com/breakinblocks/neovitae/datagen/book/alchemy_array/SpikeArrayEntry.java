package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class SpikeArrayEntry extends EntryProvider {

    public SpikeArrayEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spike Array");
        this.pageText("The [#](8B0000)Spike Array[#]() is a cruel but effective ward. Any living creature that "
                + "steps onto it suffers a full heart of damage. The array is nearly invisible once inscribed, "
                + "making it ideal for defending passages or feeding certain... automated arrangements.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Inscribe an [#](8B0000)Alchemy Array[#]() and apply the appropriate base and catalyst "
                + "to form the spike glyph.\\\n\\\n"
                + "[#](4A0080)The floor remembers every trespass.[#]()");
    }

    @Override
    protected String entryName() {
        return "Spike Array";
    }

    @Override
    protected String entryDescription() {
        return "An invisible ward that punishes those who tread upon it.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.IRON_INGOT);
    }

    @Override
    protected String entryId() {
        return "spike_array";
    }
}
