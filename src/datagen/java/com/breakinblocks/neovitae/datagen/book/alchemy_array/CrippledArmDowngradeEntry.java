package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientDowngradeRecipePageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class CrippledArmDowngradeEntry extends EntryProvider {

    public CrippledArmDowngradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crippled Arm");
        this.pageText("The armour seizes your off-hand in a rigid grip, preventing you from using whatever "
                + "you hold there. No torches, no shields, no secondary tools.\\\n\\\n"
                + "[#](4A0080)The armour demands your undivided attention.[#]()");

        this.page("recipe", () -> BookSentientDowngradeRecipePageModel.create()
                .withRecipeId1("neovitae:downgrade/crippled_arm"));
    }

    @Override
    protected String entryName() {
        return "Crippled Arm";
    }

    @Override
    protected String entryDescription() {
        return "The armour locks your off-hand; one arm must suffice.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SHIELD);
    }

    @Override
    protected String entryId() {
        return "downgrade_crippled_arm";
    }
}
