package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookFlaskRecipePageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class GravityFlaskEntry extends EntryProvider {

    public GravityFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Gravity");
        this.pageText("**Gravity** causes the target to fall faster and take more damage on landing. "
                + "It does not reduce jump height, however.\\\n\\\n"
                + "It's created from a flask containing both the **Grounded** and **Slow Falling** effects.");

        this.page("recipe1", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/gravity")
                .withRecipeId2("neovitae:flask/length_gravity"));
        this.page("recipe2", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/potency_gravity")
                .withRecipeId2("neovitae:flask/potency_average_gravity"));
        this.page("recipe3", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/length_average_gravity"));
    }

    @Override
    protected String entryName() {
        return "Gravity";
    }

    @Override
    protected String entryDescription() {
        return "Fall faster and take more fall damage.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.TROPICAL_FISH);
    }

    @Override
    protected String entryId() {
        return "flask_gravity";
    }
}
