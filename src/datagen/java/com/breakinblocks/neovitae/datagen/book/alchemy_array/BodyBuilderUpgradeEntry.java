package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientUpgradeTablePageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class BodyBuilderUpgradeEntry extends EntryProvider {

    public BodyBuilderUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookSentientUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Body Builder");
        this.pageText("The armor observes your habits of nourishment and responds in kind, hardening your "
                + "frame against impact. Grants an [#](4A0080)anchored stance[#]() that resists being thrown and "
                + "bolsters your vitality considerably.\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Eating food.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 5");
    }

    @Override
    protected String entryName() {
        return "Body Builder";
    }

    @Override
    protected String entryDescription() {
        return "Feed yourself well and the armor fortifies your body in turn.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.COOKED_BEEF);
    }

    @Override
    protected String entryId() {
        return "upgrade_body_builder";
    }
}
