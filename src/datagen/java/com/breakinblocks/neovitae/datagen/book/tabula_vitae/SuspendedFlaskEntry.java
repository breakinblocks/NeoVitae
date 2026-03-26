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

public class SuspendedFlaskEntry extends EntryProvider {

    public SuspendedFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Suspended");
        this.pageText("[#](8B0000)Suspended[#]() prevents the target from jumping, falling, or being affected by gravity "
                + "in any way whatsoever. They can still move, however, sliding about as though on a flat, "
                + "never-ending plane of ice.\\\n\\\n"
                + "It's made from a potion of [#](8B0000)Gravity[#]().");

        this.page("recipe1", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/gravity_to_suspended")
                .withRecipeId2("neovitae:flask/length_suspended"));
        this.page("recipe2", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/length_average_suspended"));
    }

    @Override
    protected String entryName() {
        return "Suspended";
    }

    @Override
    protected String entryDescription() {
        return "Unaffected by gravity in any way.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        ItemStack flask = new ItemStack(NVItems.ALCHEMY_FLASK.get());
        ItemAlchemyFlask.setFlaskEffects(flask, FlaskEffects.single(EffectHolder.create(NVMobEffects.SUSPENDED.getDelegate(), 3600, 0)));
        return BookIconModel.create(flask);
    }

    @Override
    protected String entryId() {
        return "flask_suspended";
    }
}
