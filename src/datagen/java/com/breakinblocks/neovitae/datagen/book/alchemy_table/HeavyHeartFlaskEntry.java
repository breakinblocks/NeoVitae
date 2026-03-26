package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookFlaskRecipePageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class HeavyHeartFlaskEntry extends EntryProvider {

    public HeavyHeartFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Heavy Heart");
        this.pageText("**Heavy Heart** drags the target steadily downwards, making flying and swimming "
                + "significantly more difficult.\\\n\\\n"
                + "It's created from a flask containing both the **Gravity** and **Instant Health** effects.");

        this.page("recipe1", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/gravity_to_heart")
                .withRecipeId2("neovitae:flask/length_heavy_heart"));
        this.page("recipe2", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/potency_heavy_heart")
                .withRecipeId2("neovitae:flask/potency_average_heavy_heart"));
        this.page("recipe3", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/length_average_heavy_heart"));
    }

    @Override
    protected String entryName() {
        return "Heavy Heart";
    }

    @Override
    protected String entryDescription() {
        return "Drags the target downwards.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SLIME_BALL);
    }

    @Override
    protected String entryId() {
        return "flask_heavy_heart";
    }
}
