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
        this.pageText("Use a **Ritual Diviner** for easier construction.");

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("This ritual prevents flight within its area, grounding any flying entities.");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Will Effects");
        this.pageText("- **Raw Will**: Increases the grounding effect potency."
                + "\n- **Corrosive Will**: Behaves similarly to the **Suspended** potion effect."
                + "\n- **Vengeful Will**: Applies **Levitation** to entities."
                + "\n- **Destructive Will**: Applies the **Heavy Heart** effect."
                + "\n- **Steadfast Will**: Increases the area of effect.");
    }

    @Override
    protected String entryName() {
        return "The Sinner's Burden";
    }

    @Override
    protected String entryDescription() {
        return "Prevents flight within the ritual area.";
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
