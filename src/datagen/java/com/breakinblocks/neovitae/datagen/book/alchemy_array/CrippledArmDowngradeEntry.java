package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookLivingDowngradeRecipePageModel;
import com.mojang.datafixers.util.Pair;
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
        this.pageText("Effect: Prevents you from using your offhand item. This includes placing torches, "
                + "raising your shield, etc.");

        this.page("recipe", () -> BookLivingDowngradeRecipePageModel.create()
                .withRecipeId1("neovitae:downgrade/crippled_arm"));
    }

    @Override
    protected String entryName() {
        return "Crippled Arm";
    }

    @Override
    protected String entryDescription() {
        return "Downgrade: disables offhand use.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
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
