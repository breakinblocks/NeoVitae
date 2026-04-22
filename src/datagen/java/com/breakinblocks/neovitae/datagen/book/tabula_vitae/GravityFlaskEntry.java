package com.breakinblocks.neovitae.datagen.book.tabula_vitae;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookFlaskRecipePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;
import com.breakinblocks.neovitae.common.datacomponent.FlaskEffects;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import net.minecraft.world.item.ItemStack;
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
        this.pageText("The [#](8B0000)Gravity[#]() elixir intensifies the earth's pull upon the drinker, "
                + "causing them to plummet faster and suffer greater harm on impact. Jump height "
                + "remains unchanged, small comfort on the way down.\\\n\\\n"
                + "To synthesize it, brew both [#](8B0000)Grounded[#]() and [#](8B0000)Slow Falling[#]() into a single flask, "
                + "then fuse them with a [#](8B0000)Combinational Catalyst[#]() at the Tabula Vitae.");

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
        return "Intensifies the earth's pull to crushing force.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        ItemStack flask = new ItemStack(NVItems.ALCHEMY_FLASK.get());
        ItemAlchemyFlask.setFlaskEffects(flask, FlaskEffects.single(EffectHolder.create(NVMobEffects.GRAVITY.getDelegate(), 3600, 0)));
        return BookIconModel.create(com.breakinblocks.neovitae.common.item.NVItems.ALCHEMY_FLASK.get());
    }

    @Override
    protected String entryId() {
        return "flask_gravity";
    }
}
