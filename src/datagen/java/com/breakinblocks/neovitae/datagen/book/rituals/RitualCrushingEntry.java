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
        this.pageText("Use a **Ritual Diviner [Dusk]** for easier construction.");

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("This ritual breaks blocks within its area of effect and deposits the drops into a nearby chest.");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Will Effects");
        this.pageText("- **Raw Will**: Increases the number of blocks broken per tick."
                + "\n- **Corrosive Will**: Applies **Silk Touch** to broken blocks. Requires **Cutting Fluid** in the input chest."
                + "\n- **Vengeful Will**: Compresses items - turns Coal into Blocks of Coal, Redstone Dust into Blocks of Redstone, etc."
                + "\n- **Destructive Will**: Applies **Fortune III** to broken blocks."
                + "\n- **Steadfast Will**: Applies both **Silk Touch** and **Fortune** simultaneously.");
    }

    @Override
    protected String entryName() {
        return "Ritual of the Crusher";
    }

    @Override
    protected String entryDescription() {
        return "Breaks blocks and collects drops.";
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
