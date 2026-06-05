package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.datagen.book.page.BookAlchemyArrayRecipePageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;

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
        this.pageText("The [#](8B0000)Sigil of the Frozen Lake[#]() chills the water beneath your feet as you walk, "
                + "crystallizing it into solid ice within a two-block radius. The effect is reminiscent of "
                + "ancient frost enchantments, but drawn from [#](4A0080)Essentia Vitae[#]() rather than arcane "
                + "inscriptions.\\\n\\\n"
                + "Toggle it with sneak and [Use]. The ice endures only as long as you remain near.");

        this.page("recipe", () -> BookAlchemyArrayRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "array/frost_sigil")));
    }

    @Override
    protected String entryName() {
        return "Sigil of the Frozen Lake";
    }

    @Override
    protected String entryDescription() {
        return "Walk upon water made solid by the chill of your Anima.";
    }

    @Override
    protected GuiSprite entryBackground() {
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
