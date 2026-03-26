package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookFlaskRecipePageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class InstantDamageFlaskEntry extends EntryProvider {

    public InstantDamageFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Instant Damage");
        this.pageText("**Instant Damage** inflicts 6 points of **Magic Damage** to the target per level. "
                + "If the target is **Undead**, they will be healed for 6 points per level instead.\\\n\\\n"
                + "It's made from a potion of either **Instant Health** or **Poison**.");

        this.page("recipe1", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/health_to_harm")
                .withRecipeId2("neovitae:flask/poison_to_harm"));
        this.page("recipe2", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/potency_harm")
                .withRecipeId2("neovitae:flask/potency_average_harm"));
    }

    @Override
    protected String entryName() {
        return "Instant Damage";
    }

    @Override
    protected String entryDescription() {
        return "Inflicts magic damage instantly.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.IRON_SWORD);
    }

    @Override
    protected String entryId() {
        return "flask_instant_damage";
    }
}
