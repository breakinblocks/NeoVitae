package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class SuppressionSigilEntry extends EntryProvider {

    public SuppressionSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sigil of Suppression");
        this.pageText("The [#](8B0000)Sigil of Suppression[#]() exerts your will upon the elements themselves, "
                + "temporarily banishing all fluids within roughly six blocks of you. The liquid vanishes as "
                + "though it never existed, returning only after you move away.\\\n\\\n"
                + "[#](2E8B57)A powerful tool for traversing Nether lava oceans. But be wary of deep "
                + "water; deactivating above a chasm invites a swift end.[#]()");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Forge the [#](8B0000)Suppression Reagent[#]() in the [#](8B0000)Tabula Vitae[#](), then inscribe "
                + "an [#](8B0000)Alchemy Array[#]() with the reagent as base and a slate as catalyst.\\\n\\\n"
                + "[#](4A0080)The elements themselves bow before sufficient will.[#]()");
    }

    @Override
    protected String entryName() {
        return "Sigil of Suppression";
    }

    @Override
    protected String entryDescription() {
        return "Command the elements to part; fluid yields to your presence.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_SUPPRESSION.get());
    }

    @Override
    protected String entryId() {
        return "sigil_suppression";
    }
}
