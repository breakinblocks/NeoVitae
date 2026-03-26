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
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import com.mojang.datafixers.util.Pair;

public class PoisonFlaskEntry extends EntryProvider {

    public PoisonFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Poison");
        this.pageText("[#](8B0000)Poison[#]() deals damage over time to the target, but cannot kill them on its own. "
                + "Higher levels deal faster damage.");

        this.page("recipe1", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/poison")
                .withRecipeId2("neovitae:flask/length_poison"));
        this.page("recipe2", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/potency_poison")
                .withRecipeId2("neovitae:flask/potency_average_poison"));
        this.page("recipe3", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/length_average_poison"));
    }

    @Override
    protected String entryName() {
        return "Poison";
    }

    @Override
    protected String entryDescription() {
        return "Deals damage over time.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        ItemStack flask = new ItemStack(NVItems.ALCHEMY_FLASK.get());
        ItemAlchemyFlask.setFlaskEffects(flask, FlaskEffects.single(EffectHolder.create(MobEffects.POISON, 3600, 0)));
        return BookIconModel.create(flask);
    }

    @Override
    protected String entryId() {
        return "flask_poison";
    }
}
