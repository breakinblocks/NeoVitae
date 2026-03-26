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

public class NightVisionFlaskEntry extends EntryProvider {

    public NightVisionFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Night Vision");
        this.pageText("[#](8B0000)Night Vision[#]() increases the target's ability to see in darkness and underwater.");

        this.page("recipe1", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/night_vision")
                .withRecipeId2("neovitae:flask/length_night_vision"));
        this.page("recipe2", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/length_average_night_vision"));
    }

    @Override
    protected String entryName() {
        return "Night Vision";
    }

    @Override
    protected String entryDescription() {
        return "See in darkness and underwater.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        ItemStack flask = new ItemStack(NVItems.ALCHEMY_FLASK.get());
        ItemAlchemyFlask.setFlaskEffects(flask, FlaskEffects.single(EffectHolder.create(MobEffects.NIGHT_VISION, 3600, 0)));
        return BookIconModel.create(flask);
    }

    @Override
    protected String entryId() {
        return "flask_night_vision";
    }
}
