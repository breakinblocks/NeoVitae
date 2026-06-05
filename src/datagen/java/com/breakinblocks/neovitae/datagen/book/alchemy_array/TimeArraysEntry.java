package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class TimeArraysEntry extends EntryProvider {

    public TimeArraysEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Day/Night Arrays");
        this.pageText("Among the most audacious workings in [#](4A0080)Vitaemancy[#]() are these arrays that "
                + "bend the passage of time itself. Once activated, the inscribed components are consumed "
                + "as the sky obeys.\\\n\\\n"
                + "The [#](8B0000)Day[#]() array advances the world to the next sunrise. "
                + "The [#](8B0000)Night[#]() array draws it forward to the next sunset.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](8B0000)New Dawn[#](): Inscribed via [#](8B0000)Alchemy Array[#](). Commands the sun "
                + "to rise.\\\n\\\n"
                + "[#](8B0000)True Twilight[#](): Inscribed via [#](8B0000)Alchemy Array[#](). Beckons the fall "
                + "of night.\\\n\\\n"
                + "[#](4A0080)Time is not yours to own, only to borrow.[#]()");
    }

    @Override
    protected String entryName() {
        return "Day/Night Arrays";
    }

    @Override
    protected String entryDescription() {
        return "Bend the sky's clock, summon dawn or hasten dusk.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.CLOCK);
    }

    @Override
    protected String entryId() {
        return "time_arrays";
    }
}
