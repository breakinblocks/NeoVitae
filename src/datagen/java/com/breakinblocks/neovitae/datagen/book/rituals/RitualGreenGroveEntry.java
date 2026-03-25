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

public class RitualGreenGroveEntry extends EntryProvider {

    public RitualGreenGroveEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/green_grove"))
                .withMultiblockName("Ritual of the Green Grove")
                .withText(this.context().pageText()));
        this.pageText("Use a **Ritual Diviner** for easier construction.");

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("This ritual accelerates the growth of crops and plants within its area of effect, similar to using bone meal repeatedly.");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Will Effects");
        this.pageText("- **Raw Will**: Increases the growth rate."
                + "\n- **Corrosive Will**: Hydrates nearby farmland."
                + "\n- **Vengeful Will**: Causes nearby plants to spread."
                + "\n- **Destructive Will**: Increases effect potency at higher will concentrations."
                + "\n- **Steadfast Will**: Causes nearby saplings to grow into trees.");
    }

    @Override
    protected String entryName() {
        return "Ritual of the Green Grove";
    }

    @Override
    protected String entryDescription() {
        return "Accelerates plant growth.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.BONE_MEAL);
    }

    @Override
    protected String entryId() {
        return "ritual_green_grove";
    }
}
