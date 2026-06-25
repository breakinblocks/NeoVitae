package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.datagen.book.page.BookAlchemyArrayRecipePageModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

public class ImprisonmentArrayEntry extends EntryProvider {

    public ImprisonmentArrayEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Array of Imprisonment");
        this.pageText("The [#](8B0000)Array of Imprisonment[#]() binds a wandering soul into stone. Inscribe it "
                + "directly atop a [#](8B0000)Monster Spawner[#](), then slay a creature within an "
                + "[#](8B0000)11x11x11[#]() region centred on the array.\\\n\\\nThe spawner forgets its old charge and "
                + "takes on the dying creature's nature instead. The glyph, its purpose fulfilled, crumbles to nothing.");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Pair it with a [#](4A0080)Torment Nexus[#]() to grow a captive harvest of whatever quarry you "
                + "choose. Reconfiguring a spawner is a one-time rite: to change it again, inscribe a fresh array.\\\n\\\n"
                + "A few mighty creatures are too willful to be bound and the array ignores their passing.\\\n\\\n"
                + "[#](4A0080)What dies near the cage becomes the cage.[#]()");

        this.page("recipe", () -> BookAlchemyArrayRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "array/imprisonment")));
    }

    @Override
    protected String entryName() {
        return "Array of Imprisonment";
    }

    @Override
    protected String entryDescription() {
        return "Rebinds a monster spawner to a creature slain beside it.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SPAWNER);
    }

    @Override
    protected String entryId() {
        return "imprisonment_array";
    }
}
