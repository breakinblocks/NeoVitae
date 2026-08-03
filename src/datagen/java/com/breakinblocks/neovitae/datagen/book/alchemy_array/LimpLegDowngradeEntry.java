package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientDowngradeRecipePageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class LimpLegDowngradeEntry extends EntryProvider {

    public LimpLegDowngradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Limp Leg");
        this.pageText("The armor stiffens around your legs, hobbling your stride. Reduces movement "
                + "speed by up to 70%%.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 10\\\n\\\n"
                + "[#](4A0080)Every step is a negotiation with the armor's weight.[#]()");

        this.page("recipe", () -> BookSentientDowngradeRecipePageModel.create()
                .withRecipeId1("neovitae:downgrade/speed_decrease"));
    }

    @Override
    protected String entryName() {
        return "Limp Leg";
    }

    @Override
    protected String entryDescription() {
        return "The armor hobbles your stride; each step heavier than the last.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SOUL_SAND);
    }

    @Override
    protected String entryId() {
        return "downgrade_limp_leg";
    }
}
