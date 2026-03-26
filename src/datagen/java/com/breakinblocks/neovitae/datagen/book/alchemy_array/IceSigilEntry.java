package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.datagen.book.page.BookAlchemyArrayRecipePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class IceSigilEntry extends EntryProvider {

    public IceSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sigil of the Frozen Lake");
        this.pageText("The [#](8B0000)Sigil of the Frozen Lake[#]() is a toggleable sigil that freezes water "
                + "beneath your feet as you walk, similar to the Frost Walker enchantment.\\\n\\\n"
                + "While active, any water source blocks within a 2-block radius below you will be "
                + "converted to ice. This drains LP from your soul network while active.");

        this.page("recipe", () -> BookAlchemyArrayRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "array/frost_sigil")));
    }

    @Override
    protected String entryName() {
        return "Sigil of the Frozen Lake";
    }

    @Override
    protected String entryDescription() {
        return "Freeze water beneath your feet.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_FROST.get());
    }

    @Override
    protected String entryId() {
        return "sigil_ice";
    }
}
