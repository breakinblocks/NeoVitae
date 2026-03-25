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

public class RitualYawningVoidEntry extends EntryProvider {

    public RitualYawningVoidEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/yawning_void"))
                .withMultiblockName("Yawning of the Void")
                .withText(this.context().pageText()));
        this.pageText("Use a **Ritual Diviner [Dusk]** for easier construction.");

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("This ritual acts as a quarry, breaking blocks within its area and collecting the drops.");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Will Effects");
        this.pageText("- **Raw Will**: Increases quarrying speed."
                + "\n- **Corrosive Will**: Enables a block filter. To set a filter, place any form of **Item Filter** into the linked chest. The ritual will only use the first filter it finds, but will accept Standard, Tag, Mod, and Composite Item Filters. Blacklisting works too!"
                + "\n- **Steadfast Will**: Mined blocks are placed above the ritual instead of being destroyed, creating a surface-level copy of the mined area.");
    }

    @Override
    protected String entryName() {
        return "Yawning of the Void";
    }

    @Override
    protected String entryDescription() {
        return "Quarries blocks from underground.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.BLACK_STAINED_GLASS);
    }

    @Override
    protected String entryId() {
        return "ritual_yawning_void";
    }
}
